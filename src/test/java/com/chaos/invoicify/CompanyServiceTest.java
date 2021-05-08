package com.chaos.invoicify;

import com.chaos.invoicify.dto.CompanyDto;
import com.chaos.invoicify.entity.CompanyEntity;
import com.chaos.invoicify.repository.CompanyRepository;
import com.chaos.invoicify.services.CompanyService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class CompanyServiceTest {
    @InjectMocks
    CompanyService companyService;

    @Mock
    CompanyRepository companyRepository;

    CompanyEntity companyEntity;
    CompanyDto companyDto;
    @BeforeEach
    public void setup() {
        companyDto=new CompanyDto("Comapany1","Adress 123","Samik",
                "Account Payable","467-790-0128");
        companyEntity= new CompanyEntity("Comapany1","Adress 123","Samik","Account Payable","467-790-0128");


    }

    @Test
    public void createCompanyTest() {

        companyService.createCompany(companyDto);

        verify(companyRepository).save(companyEntity);

    }
}
