package com.chaos.invoicify.service;

import com.chaos.invoicify.dto.CompanyDto;
import com.chaos.invoicify.entity.CompanyEntity;
import com.chaos.invoicify.helper.StatusCode;
import com.chaos.invoicify.repository.CompanyRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CompanyService {
    private CompanyRepository companyRepository;

    public CompanyService(CompanyRepository companyRepository) {
        this.companyRepository = companyRepository;
    }

    public StatusCode createCompany(CompanyDto companyDto) {
        if (companyDto.getName() == null || companyDto.getName().isEmpty()){
            return StatusCode.NONAME;
        }
        CompanyEntity companyEntity = companyRepository.findByName(companyDto.getName());
        if (companyEntity == null) {
            companyRepository.save(new CompanyEntity(companyDto.getName(),
                    companyDto.getAddress(),
                    companyDto.getContactName(),
                    companyDto.getContactTitle(),
                    companyDto.getContactPhoneNumber()));
            return StatusCode.SUCCESS;

        }else{
            return StatusCode.DUPLICATE;
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


    public StatusCode updateCompany(CompanyDto companyDto) {
        CompanyEntity companyEntity = companyRepository.findByName(companyDto.getName());
        StatusCode statusCode = StatusCode.OTHER;

        if (companyEntity != null) {
            companyEntity.setAddress(companyDto.getAddress());
            companyEntity.setContactName(companyDto.getContactName());
            companyEntity.setContactTitle(companyDto.getContactTitle());
            companyEntity.setContactPhoneNumber(companyDto.getContactPhoneNumber());
            companyRepository.save(companyEntity);
            statusCode = StatusCode.SUCCESS;
        }

        return statusCode;

    }

    public StatusCode updateCompany(String companyName, CompanyDto companyDto) {
        CompanyEntity companyEntity = companyRepository.findByName(companyName);
        StatusCode statusCode = StatusCode.OTHER;

        if (companyEntity != null) {
            companyEntity.setName(companyDto.getName());
            companyEntity.setAddress(companyDto.getAddress());
            companyEntity.setContactName(companyDto.getContactName());
            companyEntity.setContactTitle(companyDto.getContactTitle());
            companyEntity.setContactPhoneNumber(companyDto.getContactPhoneNumber());
            companyRepository.save(companyEntity);
            statusCode = StatusCode.SUCCESS;
        }

        return statusCode;
    }
}
