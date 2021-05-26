package com.kakaopay.investment.repository;

import com.kakaopay.investment.entity.ProductEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Date;
import java.util.List;

public interface ProductRepository extends JpaRepository<ProductEntity, Long> {
    List<ProductEntity> findByStartedAtBeforeAndFinishedAtAfter(Date todayForStartedAt, Date todayForFinishedAt);
}
