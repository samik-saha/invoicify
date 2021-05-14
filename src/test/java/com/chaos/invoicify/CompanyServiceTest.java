package com.chaos.invoicify;

import com.chaos.invoicify.dto.CompanyDto;
import com.chaos.invoicify.entity.CompanyEntity;
import com.chaos.invoicify.helper.StatusCode;
import com.chaos.invoicify.repository.CompanyRepository;
import com.chaos.invoicify.service.CompanyService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CompanyServiceTest {
  @InjectMocks CompanyService companyService;

  @Mock CompanyRepository companyRepository;

  CompanyEntity companyEntity;
  CompanyDto companyDto;
  CompanyDto companyDto2;

  @BeforeEach
  public void setup() {
    companyDto =
        new CompanyDto("Comapany1", "Adress 123", "Samik", "Account Payable", "467-790-0128");
    companyDto2 =
        new CompanyDto("Comapany1", "Adress 123", "Samik", "Account Payable", "467-790-0128");
    companyEntity =
        new CompanyEntity("Comapany1", "Adress 123", "Samik", "Account Payable", "467-790-0128");
  }

  @Test
  public void createCompanyTest() {
    companyService.createCompany(companyDto);
    verify(companyRepository).save(companyEntity);
  }

  @Test
  public void getCompanyListTest() {
    when(companyRepository.findAll()).thenReturn(List.of(companyEntity));
    List<CompanyDto> companyDtoList = companyService.getCompanyList();
    assertThat(companyDtoList).isEqualTo(List.of(companyDto));
  }

  @Test
  public void uniqueCompanyTest(){
    when(companyRepository.findByName("Comapany1")).thenReturn(companyEntity);
    StatusCode s = companyService.createCompany(companyDto2);
    assertThat(s).isEqualTo(StatusCode.DUPLICATE);
  }

}