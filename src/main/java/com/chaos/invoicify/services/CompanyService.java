package com.chaos.invoicify.services;

import com.chaos.invoicify.dto.CompanyDto;
import com.chaos.invoicify.entity.CompanyEntity;
import com.chaos.invoicify.helper.StatusCodes;
import com.chaos.invoicify.repository.CompanyRepository;
import lombok.Setter;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CompanyService {
    private CompanyRepository companyRepository;

    public CompanyService(CompanyRepository companyRepository) {
        this.companyRepository = companyRepository;
    }
    public StatusCodes createCompany(CompanyDto companyDto) {
        CompanyEntity companyEntity = companyRepository.findByName(companyDto.getName());
        if (companyEntity == null) {
            companyRepository.save(new CompanyEntity(companyDto.getName(),
                    companyDto.getAddress(),
                    companyDto.getContactName(),
                    companyDto.getContactTitle(),
                    companyDto.getContactPhoneNumber()));
            return StatusCodes.SUCCESS;

        }else{
            return StatusCodes.DUPLICATE;
        }
    }

    public List<CompanyDto> getCompanyList() {
        return companyRepository.findAll().stream().map(companyEntity -> {
            return new CompanyDto(companyEntity.getName(),
                    companyEntity.getAddress(),
                    companyEntity.getContactName(),
                    companyEntity.getContactTitle(),
                    companyEntity.getContactPhoneNumber());}).collect(Collectors.toList());
        }


}
