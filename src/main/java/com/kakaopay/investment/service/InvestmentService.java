package com.kakaopay.investment.service;

import com.kakaopay.investment.controller.dto.InvestmentRequest;
import com.kakaopay.investment.entity.InvestmentEntity;
import com.kakaopay.investment.entity.InvestmentPrimaryKey;
import com.kakaopay.investment.entity.MemberEntity;
import com.kakaopay.investment.entity.ProductEntity;
import com.kakaopay.investment.exception.ErrorCodeInfo;
import com.kakaopay.investment.exception.InvestmentException;
import com.kakaopay.investment.repository.InvestmentRepository;
import com.kakaopay.investment.repository.MemberRepository;
import com.kakaopay.investment.repository.ProductRepository;
import com.kakaopay.investment.repository.redis.InvestmentRedisRepository;
import com.kakaopay.investment.type.ProductStatusType;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;
import java.util.Calendar;
import java.util.Date;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class InvestmentService {

    private final InvestmentRepository investmentRepository;
    private final MemberRepository memberRepository;
    private final ProductRepository productRepository;
    private final InvestmentRedisRepository investmentRedisRepository;

    public InvestmentService(InvestmentRepository investmentRepository, MemberRepository memberRepository, ProductRepository productRepository, InvestmentRedisRepository investmentRedisRepository) {
        this.investmentRepository = investmentRepository;
        this.memberRepository = memberRepository;
        this.productRepository = productRepository;
        this.investmentRedisRepository = investmentRedisRepository;
    }


    /**
     * 투자하기
     *
     * @param investmentRequest
     * @throws InvestmentException
     */
    @Transactional
    public void invest(InvestmentRequest investmentRequest) throws InvestmentException{
        /* 유저 상태 확인 */
        MemberEntity memberEntity = this.checkMemberStatus(investmentRequest);

        /* process점유 */
        Optional.of(investmentRedisRepository.lockInvestment(investmentRequest.getProductId()))
                .filter(s -> s == Boolean.TRUE)
                .orElseThrow(() ->  new InvestmentException(ErrorCodeInfo.INVESTMENT_SYNC));

        try {
            /* 투자상품 상태 확인 */
            ProductEntity productEntity = this.checkProductStatus(investmentRequest);

            /* 기존 투자내역 확인 */
            InvestmentPrimaryKey investmentPrimaryKey = new InvestmentPrimaryKey();
            investmentPrimaryKey.setMemberId(investmentRequest.getMemberId());
            investmentPrimaryKey.setProductId(investmentRequest.getProductId());
            this.alreadyInvest(investmentPrimaryKey);

            /* 투자 */
            InvestmentEntity investment = new InvestmentEntity();
            investment.setInvestmentPrimaryKey(investmentPrimaryKey);
            investment.setInvestingAmount(investmentRequest.getInvestingAmount());
            investment.setCreatedBy("API");
            investmentRepository.save(investment);

            /* 문닫고 들어온 투자자인지 */
            if(productEntity.getRemainAmount().equals(investmentRequest.getInvestingAmount())) {
                productEntity.setStatus(ProductStatusType.SOLDOUT);
                productRepository.save(productEntity);
            }

            /* 고객 잔액에서 투자금액 차감 */
            Long remainBalance = memberEntity.getBalance() - investmentRequest.getInvestingAmount();
            memberEntity.setBalance(remainBalance);
            memberRepository.save(memberEntity);

            /* 투자 가능금액 빼주고 데이터 한번 더 확인 */
            Long redisDecrResult = investmentRedisRepository.decreaseProductRemainAmount(investmentRequest.getProductId(),investmentRequest.getInvestingAmount());
            if(redisDecrResult < 0) {
                throw new InvestmentException(ErrorCodeInfo.INVESTMENT_REDIS_REAMINAMOUNT);
            }
        } catch (Exception e) {
            throw e;
        } finally {
            /* 점유 release */
            investmentRedisRepository.releaseInvestmentLock(investmentRequest.getProductId());
        }
    }

    /**
     * 이미 투자한 상품 여부 확인
     *
     * @param investmentPrimaryKey
     * @return
     * @throws InvestmentException
     */
    private void alreadyInvest(InvestmentPrimaryKey investmentPrimaryKey) throws InvestmentException{
        Optional<InvestmentEntity> investmentEntityOptional = investmentRepository.findById(investmentPrimaryKey);
        if(investmentEntityOptional.isPresent()) {
            throw new InvestmentException(ErrorCodeInfo.ALREADY_INVESTED);
        }
    }
    /**
     * 회원의 투자가능 여부 확인
     *
     * @param investmentRequest
     * @return
     * @throws InvestmentException
     */
    private MemberEntity checkMemberStatus(InvestmentRequest investmentRequest) throws InvestmentException{
        Optional<MemberEntity> memberEntityOptional = memberRepository.findById(investmentRequest.getMemberId());
        if(memberEntityOptional != null && memberEntityOptional.isPresent()) {
            MemberEntity memberEntity = memberEntityOptional.get();
            if(memberEntity.getBalance() < investmentRequest.getInvestingAmount()) {
                throw new InvestmentException(ErrorCodeInfo.USER_BALANCE);
            } else {
                return memberEntity;
            }
        } else {
            throw new InvestmentException(ErrorCodeInfo.USER_VAILDATION);
        }
    }

    /**
     * 상품의 투자 가능여부 확인
     *
     * @param investmentRequest
     * @return
     * @throws InvestmentException
     */
    private ProductEntity checkProductStatus(InvestmentRequest investmentRequest) throws InvestmentException {
        Optional<ProductEntity> productEntityOptional = productRepository.findById(investmentRequest.getProductId());
        if(!productEntityOptional.isPresent()) {
            throw new InvestmentException(ErrorCodeInfo.PRODUCT_VALIDATION);
        }

        ProductEntity productEntity = productEntityOptional.get();
        if(!this.isAvailablePeriod(productEntity)) {
            throw new InvestmentException(ErrorCodeInfo.PRODUCT_VALIDATION_PERIOD);
        }

        if(productEntity.getStatus() == ProductStatusType.SOLDOUT) {
            throw new InvestmentException(ErrorCodeInfo.PRODUCT_VALIDATION_STATUS);
        }

//        Long investedAmount = productEntity.getInvestmentEntityList().stream().mapToLong(InvestmentEntity::getInvestingAmount).sum();
//        Long remainAmount = productEntity.getTotalInvestingAmount() - investedAmount;
        Long remainAmount = investmentRedisRepository.getProductRemainAmount(investmentRequest.getProductId());

        if(remainAmount == null) {
            //TODO:REDIS데이터 이상 flush 필요 Alarm to 개발자.
            throw new InvestmentException(ErrorCodeInfo.PRODUCT_VALIDATION_REDIS);
        } else if(remainAmount < investmentRequest.getInvestingAmount()) {
            throw new InvestmentException(ErrorCodeInfo.PRODUCT_VALIDATION_AMOUNT);
        } else {
            productEntity.setRemainAmount(remainAmount);
        }
        return productEntity;
    }

    /**
     * 상품의 투자 가능한 기간여부 확인
     *
     * @param productEntity
     * @return
     */
    private boolean isAvailablePeriod(ProductEntity productEntity) {
        Date today = Calendar.getInstance().getTime();
        return (today.before(productEntity.getFinishedAt()) && today.after(productEntity.getStartedAt()));
    }

}
