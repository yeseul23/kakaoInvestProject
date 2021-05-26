package com.kakaopay.investment.service;

import com.kakaopay.investment.controller.dto.ProductInfo;
import com.kakaopay.investment.entity.InvestmentEntity;
import com.kakaopay.investment.entity.ProductEntity;
import com.kakaopay.investment.repository.ProductRepository;
import com.kakaopay.investment.repository.redis.InvestmentRedisRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class ProductService {
    private final ProductRepository productRepository;
    private final InvestmentRedisRepository investmentRedisRepository;

    public ProductService(ProductRepository productRepository, InvestmentRedisRepository investmentRedisRepository) {
        this.productRepository = productRepository;
        this.investmentRedisRepository = investmentRedisRepository;
    }

    /**
     * 현재 투자가능 상품 조회
     *
     * @return
     */
    public List<ProductInfo> getAllAvailableProduct() {
        //상품 모집기간으로 으로 조회
        Date today = new Date();
        List<ProductEntity> productEntities =  productRepository.findByStartedAtBeforeAndFinishedAtAfter(today, today);
        List<ProductInfo> products = productEntities.stream()
                .map(entity -> ProductInfo.builder()
                        .productId(entity.getId())
                        .productTitle(entity.getTitle())
                        .productTotalInvestingAmount(entity.getTotalInvestingAmount())
                        .investedAmount(entity.getInvestmentEntityList().stream().mapToLong(InvestmentEntity::getInvestingAmount).sum())
                        .investorCount(entity.getInvestmentEntityList().size())
                        .status(entity.getStatus())
                        .startedAt(entity.getStartedAt())
                        .finishedAt(entity.getFinishedAt())
                        .build())
                .collect(Collectors.toList());
        return products;
    }

    /**
     * 투자가능 상품 Redis 적재
     */
    public String addProductListToRedis(){
        Date today = new Date();
        List<ProductEntity> productEntities =  productRepository.findByStartedAtBeforeAndFinishedAtAfter(today, today);
        if(CollectionUtils.isEmpty(productEntities)) {
            return "투자가능한 상품 없음";
        }
        for (ProductEntity productEntity:productEntities) {
            Long investedAmount = productEntity.getInvestmentEntityList().stream().mapToLong(InvestmentEntity::getInvestingAmount).sum();
            Long remainAmount = productEntity.getTotalInvestingAmount() - investedAmount;
            investmentRedisRepository.setProductRemainAmount(productEntity, remainAmount);
        }
        return "완료";
    }
}
