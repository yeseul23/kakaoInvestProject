package com.kakaopay.investment.service;

import com.kakaopay.investment.controller.dto.InvestmentRequest;
import com.kakaopay.investment.entity.InvestmentEntity;
import com.kakaopay.investment.entity.MemberEntity;
import com.kakaopay.investment.entity.ProductEntity;
import com.kakaopay.investment.repository.InvestmentRepository;
import com.kakaopay.investment.repository.MemberRepository;
import com.kakaopay.investment.repository.ProductRepository;
import com.kakaopay.investment.repository.redis.InvestmentRedisRepository;
import com.kakaopay.investment.type.ProductStatusType;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Calendar;
import java.util.Date;
import java.util.Optional;

import static org.junit.Assert.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;


@RunWith(SpringRunner.class)
@SpringBootTest
public class InvestmentServiceTest {

    private  InvestmentRepository investmentRepository;
    private  MemberRepository memberRepository;
    private  ProductRepository productRepository;
    private  InvestmentRedisRepository investmentRedisRepository;
    private InvestmentService investmentService;

    @Before
    public void setup() {
        investmentRepository = Mockito.mock(InvestmentRepository.class);
        memberRepository = Mockito.mock(MemberRepository.class);
        productRepository = Mockito.mock(ProductRepository.class);
        investmentRedisRepository = Mockito.mock(InvestmentRedisRepository.class);
        investmentService = new InvestmentService(investmentRepository,memberRepository,productRepository,investmentRedisRepository);
    }

    @Test
    public void  투자하기_정상 ()  {
        InvestmentRequest investmentRequest = new InvestmentRequest();
        investmentRequest.setMemberId(1L);
        investmentRequest.setProductId(1L);
        investmentRequest.setInvestingAmount(3000L);

        MemberEntity memberEntity = new MemberEntity();
        memberEntity.setId(1L);
        memberEntity.setBalance(10000L);

        Calendar cal = Calendar.getInstance();
        Date startDate = cal.getTime();
        cal.add(Calendar.DATE, 10);
        Date finishDate = cal.getTime();

        ProductEntity productEntity = new ProductEntity();
        productEntity.setId(1L);
        productEntity.setStatus(ProductStatusType.AVAILABLE);
        productEntity.setStartedAt(startDate);
        productEntity.setFinishedAt(finishDate);
        productEntity.setTotalInvestingAmount(15000L);

        InvestmentEntity investmentEntity = new InvestmentEntity();

        when(investmentRedisRepository.lockInvestment(any())).thenReturn(true);
        when(memberRepository.findById(any())).thenReturn(Optional.of(memberEntity));
        when(productRepository.findById(any())).thenReturn(Optional.of(productEntity));
        when(investmentRedisRepository.getProductRemainAmount(any())).thenReturn(3000L);
        when(investmentRepository.save(any())).thenReturn(investmentEntity);
        when(memberRepository.save(any())).thenReturn(memberEntity);
        when(investmentRedisRepository.decreaseProductRemainAmount(any(),any())).thenReturn(1000L);
//        doNothing().when(investmentRedisRepository).releaseInvestmentLock(any());
        when(investmentRedisRepository.releaseInvestmentLock(any())).thenReturn(true);

        try {
            investmentService.invest(investmentRequest);
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }


}