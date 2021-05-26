package com.kakaopay.investment.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.kakaopay.investment.type.ProductStatusType;
import lombok.Data;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Data
@Table(name ="product")
public class ProductEntity extends BaseEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id")
    private Long id;

    @Column(name="title")
    private String title;

    @Column(name="total_investing_amount")
    private Long totalInvestingAmount;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:sss", timezone = "Asia/Seoul")
    @Column(name="started_at", nullable = false)
    private Date startedAt = new Date();

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:sss", timezone = "Asia/Seoul")
    @Column(name="finished_at", nullable = false)
    private Date finishedAt = new Date();

    @Column(name="status")
    @Enumerated(EnumType.STRING)
    private ProductStatusType status;

    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", insertable = false, updatable = false)
    private List<InvestmentEntity> investmentEntityList = new ArrayList<>();

    @Transient
    private Long remainAmount ;
}
