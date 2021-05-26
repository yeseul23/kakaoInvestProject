package com.kakaopay.investment.controller;

import com.kakaopay.investment.controller.dto.MyInvestInfo;
import com.kakaopay.investment.exception.InvestmentException;
import com.kakaopay.investment.service.MemberService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.NotNull;
import java.util.List;

@RestController
@RequestMapping("/api/member")
@Slf4j
public class MemberRestController {
    private final MemberService memberService;

    public MemberRestController(MemberService memberService) {
        this.memberService = memberService;
    }

    /**
     * 나의 투자상품 조회
     *
     * @param memberId
     * @return
     */
    @GetMapping(value = "/myList",produces = { MediaType.APPLICATION_JSON_VALUE })
    public ResponseEntity<List<MyInvestInfo>> getMyInvestment(@RequestHeader(value="X-USER-ID") @NotNull Long memberId) throws InvestmentException {
        /* 정상유저 확인 */
        memberService.isNormalUser(memberId);

        /* 투자 List */
        List<MyInvestInfo> myInvestInfos = memberService.getMyInvestment(memberId);
        return new ResponseEntity<List<MyInvestInfo>>(myInvestInfos, HttpStatus.OK);
    }
}
