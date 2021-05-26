package com.kakaopay.investment.repository;

import com.kakaopay.investment.entity.InvestmentEntity;
import com.kakaopay.investment.entity.InvestmentPrimaryKey;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InvestmentRepository extends JpaRepository<InvestmentEntity, InvestmentPrimaryKey> {

}
