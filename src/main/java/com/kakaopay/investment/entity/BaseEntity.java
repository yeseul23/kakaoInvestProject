package com.kakaopay.investment.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.util.Date;

@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
@Data
abstract class BaseEntity {
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:sss", timezone = "Asia/Seoul")
    @CreatedDate
    @Column(name="created_at", nullable = false)
    private Date createdAt = new Date();

    @Column(name="created_by")
    private String createdBy;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:sss", timezone = "Asia/Seoul")
    @LastModifiedDate
    @Column(name="updated_at", nullable = false)
    private Date updatedAt = new Date();

    @Column(name="updated_by")
    private String updatedBy;
}