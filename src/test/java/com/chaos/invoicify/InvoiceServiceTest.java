package com.chaos.invoicify;

import com.chaos.invoicify.dto.CompanyDto;
import com.chaos.invoicify.dto.InvoiceDto;
import com.chaos.invoicify.entity.CompanyEntity;
import com.chaos.invoicify.entity.InvoiceEntity;
import com.chaos.invoicify.helper.Address;
import com.chaos.invoicify.repository.CompanyRepository;
import com.chaos.invoicify.repository.InvoicesRepository;
import com.chaos.invoicify.service.CompanyService;
import com.chaos.invoicify.service.InvoiceService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;


import java.time.LocalDate;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class InvoiceServiceTest {
    @Mock
    InvoicesRepository invoicesRepository;
    @Mock
    CompanyRepository companyRepository;

    @InjectMocks
    InvoiceService invoiceService;
    @InjectMocks
    CompanyService companyService;



    InvoiceEntity invoiceEntity;
    CompanyEntity companyEntity;
    Address address1, address2;
    private CompanyDto companyDto, companyDto2;

    @BeforeEach
    public void setup() throws Exception {
        companyDto =
                new CompanyDto("Company1", address1, "Samik", "Account Payable", "467-790-0128");
        companyDto2 =
                new CompanyDto("Company2", address2, "Rajendra", "Account Payable", "123-456-7890");
        companyEntity =
                new CompanyEntity("Company1", address1, "Samik", "Account Payable", "467-790-0128");
        invoiceEntity =
                new InvoiceEntity(companyEntity);

    }

    @Test
    public void addInvoicesWithNoItems() {
        when(companyRepository.findByName(companyDto.getName()))
                .thenReturn(companyEntity);
        InvoiceDto invoiceDto = new InvoiceDto(companyDto.getName());
        invoiceService.addInvoices(invoiceDto);
        verify(invoicesRepository).save(invoiceEntity);
    }
}
