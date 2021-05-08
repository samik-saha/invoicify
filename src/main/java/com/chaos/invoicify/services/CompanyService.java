package com.chaos.invoicify.services;

import com.chaos.invoicify.dto.CompanyDto;
import com.chaos.invoicify.entity.CompanyEntity;
import com.chaos.invoicify.repository.CompanyRepository;
import lombok.Setter;
import org.springframework.stereotype.Service;

@Service
public class CompanyService {
    private CompanyRepository companyRepository;

    public CompanyService(CompanyRepository companyRepository) {
        this.companyRepository = companyRepository;
    }
    public void createCompany(CompanyDto companyDto){
        companyRepository.save(new CompanyEntity(companyDto.getName(),
                companyDto.getAddress(),
                companyDto.getContactName(),
                companyDto.getContactTitle(),
                companyDto.getContactPhoneNumber()));
    }
}
