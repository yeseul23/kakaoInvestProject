package com.kakaopay.investment.controller.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

@Data
public class InvestmentRequest {
    private Long memberId;
    @NotNull
    private Long productId;
    @NotNull
    private Long investingAmount;
}
