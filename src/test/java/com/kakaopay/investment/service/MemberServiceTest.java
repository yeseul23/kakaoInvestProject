package com.kakaopay.investment.service;

import com.kakaopay.investment.entity.MemberEntity;
import com.kakaopay.investment.exception.ErrorCodeInfo;
import com.kakaopay.investment.exception.InvestmentException;
import com.kakaopay.investment.repository.MemberRepository;
import org.assertj.core.api.AssertionsForClassTypes;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Optional;

import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;


@RunWith(MockitoJUnitRunner.class)
public class MemberServiceTest {

    @InjectMocks
    private MemberService memberService;
    @Mock
    private MemberRepository memberRepository;

    @Test
    public void  Member_정상유저(){
        MemberEntity memberEntity = new MemberEntity();
        memberEntity.setId(1L);
        when(memberRepository.findById(anyLong())).thenReturn(Optional.of(memberEntity));

        try {
            memberService.isNormalUser(1L);
//            assertTrue(true);
        } catch (Exception e) {
            assertTrue(false);
        }
    }

    @Test
    public void  Member_유저미존재(){
        when(memberRepository.findById(anyLong())).thenReturn(Optional.empty());

        final Throwable thrown = AssertionsForClassTypes.catchThrowable(() -> memberService.isNormalUser(1L));
        InvestmentException investmentException = (InvestmentException) thrown;
        Assert.assertEquals(investmentException.getErrorCodeInfo(), ErrorCodeInfo.USER_VAILDATION);

    }
}