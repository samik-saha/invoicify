package com.chaos.invoicify.repository;

import com.chaos.invoicify.entity.CompanyEntity;
import com.chaos.invoicify.entity.InvoiceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InvoicesRepository extends JpaRepository<InvoiceEntity, Long> {
}
