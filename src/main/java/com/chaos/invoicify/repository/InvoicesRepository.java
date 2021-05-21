package com.chaos.invoicify.repository;

import com.chaos.invoicify.entity.CompanyEntity;
import com.chaos.invoicify.entity.InvoiceEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Arrays;
import java.util.List;

@Repository
public interface InvoicesRepository extends JpaRepository<InvoiceEntity, Long> {
    List<InvoiceEntity> findByCompany_Name(Pageable paging, String companyName);
}
