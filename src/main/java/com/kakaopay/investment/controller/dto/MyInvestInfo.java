package com.kakaopay.investment.controller.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Data;

import java.util.Date;

@Data
@Builder
public class MyInvestInfo {
    private Long productId;
    private String productTitle;
    private Long productTotalInvestingAmount;
    private Long myInvestingAmount;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:sss", timezone = "Asia/Seoul")
    private Date createdAt;
}
