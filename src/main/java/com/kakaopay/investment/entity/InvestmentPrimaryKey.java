package com.kakaopay.investment.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;

@Embeddable
@NoArgsConstructor
@AllArgsConstructor
@Data
public class InvestmentPrimaryKey implements Serializable {

    private static final long serialVersionUID = 1L;

    @NonNull
    @Column(name="member_id")
    private Long memberId;

    @NonNull
    @Column(name="product_id")
    private Long productId;

}
