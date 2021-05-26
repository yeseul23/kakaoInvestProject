package com.kakaopay.investment.repository;


import com.kakaopay.investment.entity.ProductEntity;
import com.kakaopay.investment.repository.redis.InvestmentRedisRepository;
import org.junit.Assert;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Calendar;
import java.util.Date;

import static junit.framework.Assert.assertFalse;
import static junit.framework.TestCase.assertTrue;

@RunWith(SpringRunner.class)
@SpringBootTest
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class RedisRepositoryTest {

    @Autowired
    private InvestmentRedisRepository investmentRedisRepository;


    @Test
    public void A_LOCK() {
        Boolean lockResult = investmentRedisRepository.lockInvestment(1L);
        assertTrue(lockResult);
    }

    @Test
    public void B_OCKED_BUT_TRY() {
        Boolean lockResult = investmentRedisRepository.lockInvestment(1L);
        assertFalse(lockResult);
    }

    @Test
    public void C_RELEASE() {
        Boolean releaseResult = investmentRedisRepository.releaseInvestmentLock(1L);
        assertTrue(releaseResult);
    }

    @Test
    public void D_RELEASED_AND_TRY() {
        Boolean lockResult = investmentRedisRepository.lockInvestment(1L);
        assertTrue(lockResult);
    }


    @Test
    public void E_Redis_ProductInfo추가() {
        Calendar cal = Calendar.getInstance();
        Date startDate = cal.getTime();
        cal.add(Calendar.DATE, 10);
        Date finishDate = cal.getTime();

        ProductEntity productEntity = new ProductEntity();
        productEntity.setId(1L);
        productEntity.setStartedAt(startDate);
        productEntity.setFinishedAt(finishDate);
        Long remainAmount = 3000L;

        investmentRedisRepository.setProductRemainAmount(productEntity, remainAmount);
    }

    @Test
    public void F_Redis_ProductInfo확인 () {
        Long remainAmount = investmentRedisRepository.getProductRemainAmount(1L);
        Assert.assertEquals(remainAmount.longValue(), 3000L);
    }
}