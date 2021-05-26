package com.kakaopay.investment.service;

import com.kakaopay.investment.controller.dto.MyInvestInfo;
import com.kakaopay.investment.entity.InvestmentEntity;
import com.kakaopay.investment.entity.MemberEntity;
import com.kakaopay.investment.exception.ErrorCodeInfo;
import com.kakaopay.investment.exception.InvestmentException;
import com.kakaopay.investment.repository.MemberRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class MemberService {
    private final MemberRepository memberRepository;

    public MemberService(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    /**
     * 유저 존재 여부
     *
     * @param memberId
     * @throws InvestmentException
     */
    public void isNormalUser(Long memberId) throws InvestmentException {
        Optional<MemberEntity> memberEntityOptional = memberRepository.findById(memberId);
        if(!memberEntityOptional.isPresent()) {
            throw new InvestmentException(ErrorCodeInfo.USER_VAILDATION);
        }
    }

    /**
     * 나의 투자 내역
     *
     * @param memberId
     * @return
     */
    public List<MyInvestInfo> getMyInvestment(Long memberId) {
        List<InvestmentEntity> investmentEntityList =  memberRepository.getById(memberId).getInvestmentEntityList();

        List<MyInvestInfo> myInvestInfos = investmentEntityList.stream()
                .map(entity -> MyInvestInfo.builder()
                        .productId(entity.getInvestmentPrimaryKey().getProductId())
                        .productTitle(entity.getProductEntity().getTitle())
                        .productTotalInvestingAmount(entity.getProductEntity().getTotalInvestingAmount())
                        .myInvestingAmount(entity.getInvestingAmount())
                        .createdAt(entity.getCreatedAt())
                        .build())
                .collect(Collectors.toList());
        return myInvestInfos;
    }
}
