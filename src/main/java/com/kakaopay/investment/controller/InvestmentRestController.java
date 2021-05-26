package com.kakaopay.investment.controller;

import com.kakaopay.investment.controller.dto.InvestmentRequest;
import com.kakaopay.investment.exception.InvestmentException;
import com.kakaopay.investment.service.InvestmentService;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

@RestController
@RequestMapping("/api/invest")
@Slf4j
public class InvestmentRestController {

    private final InvestmentService investmentService;

    public InvestmentRestController(InvestmentService investmentService) {
        this.investmentService = investmentService;
    }

    /**
     * 투자하기
     *
     * @param investmentRequest
     * @return
     */
    @PostMapping(produces = { MediaType.APPLICATION_JSON_VALUE })
    public ResponseEntity invest(@RequestHeader(value="X-USER-ID") @NotNull Long memberId,
                                 @RequestBody @Valid InvestmentRequest investmentRequest) throws InvestmentException{
        investmentRequest.setMemberId(memberId);
        investmentService.invest(investmentRequest);
        return new ResponseEntity<>(HttpStatus.OK.getReasonPhrase(),HttpStatus.OK);
    }


}
