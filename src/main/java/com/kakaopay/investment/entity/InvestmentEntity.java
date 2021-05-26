package com.kakaopay.investment.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import javax.persistence.*;
import java.util.Date;

@Entity
@Data
@Table(name ="investment")
public class InvestmentEntity extends BaseEntity{

    @EmbeddedId
    private InvestmentPrimaryKey investmentPrimaryKey;

    @Column(name="investing_amount")
    private Long investingAmount;

    @ManyToOne
    @JoinColumn( name = "product_id", insertable = false, updatable = false)
    private ProductEntity productEntity;

}
