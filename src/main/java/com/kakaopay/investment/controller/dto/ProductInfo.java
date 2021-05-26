package com.kakaopay.investment.controller.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.kakaopay.investment.type.ProductStatusType;
import lombok.Builder;
import lombok.Data;

import java.util.Date;

@Data
@Builder
public class ProductInfo {
    private Long productId;
    private String productTitle;
    private Long productTotalInvestingAmount;
    private Long investedAmount;
    private int investorCount;
    private ProductStatusType status;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:sss", timezone = "Asia/Seoul")
    private Date startedAt;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:sss", timezone = "Asia/Seoul")
    private Date finishedAt;
}
