package com.chaos.invoicify.service;

import com.chaos.invoicify.dto.CompanyDto;
import com.chaos.invoicify.entity.CompanyEntity;
import com.chaos.invoicify.helper.Address;
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
        if (companyDto.getName() == null || companyDto.getName().isEmpty()) {
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

        } else {
            return StatusCode.DUPLICATE;
        }
    }

    public List<CompanyDto> getCompanyList() {
        return companyRepository.findAll().stream().map(companyEntity -> {
            return new CompanyDto(companyEntity.getName(),
                companyEntity.getAddress(),
                companyEntity.getContactName(),
                companyEntity.getContactTitle(),
                companyEntity.getContactPhoneNumber());
        }).collect(Collectors.toList());
    }


    public StatusCode updateCompany(String companyName, CompanyDto companyDto) {
        CompanyEntity companyEntity = companyRepository.findByName(companyName);
        StatusCode statusCode = StatusCode.OTHER;
        boolean nameChanged = false;

        if (companyEntity != null) {
            if (companyDto.getName() != null) {
                CompanyEntity companyEntity1 = companyRepository.findByName(companyDto.getName());
                if (companyEntity1 == null) {
                    companyEntity.setName(companyDto.getName());
                    nameChanged = true;
                }
                else{
                    return StatusCode.DUPLICATE;

                }
            }
            Address newAddress = companyDto.getAddress();
            Address address = companyEntity.getAddress();
            if (newAddress != null) {
                if (newAddress.getStreet() != null) address.setStreet(newAddress.getStreet());
                if (newAddress.getCity() != null) address.setCity(newAddress.getCity());
                if (newAddress.getState() != null) address.setState(newAddress.getState());
                if (newAddress.getCountry() != null) address.setCountry(newAddress.getCountry());
                if (newAddress.getZipCode() != null) address.setZipCode(newAddress.getZipCode());

                companyEntity.setAddress(address);
            }

            if (companyDto.getContactName() != null)
                companyEntity.setContactName(companyDto.getContactName());
            if (companyDto.getContactTitle() != null)
                companyEntity.setContactTitle(companyDto.getContactTitle());
            if (companyDto.getContactPhoneNumber() != null)
                companyEntity.setContactPhoneNumber(companyDto.getContactPhoneNumber());
            companyRepository.save(companyEntity);
            if (nameChanged)
                statusCode = StatusCode.FOUND;
            else
                statusCode = StatusCode.SUCCESS;
        } else {
            statusCode = StatusCode.NOTFOUND;
        }

        return statusCode;
    }

    public CompanyDto fetchCompanyByName(String companyName) {
        CompanyEntity companyEntity = companyRepository.findByName(companyName);

        if (companyEntity != null) {

            return new CompanyDto(
                companyEntity.getName(),
                companyEntity.getAddress(),
                companyEntity.getContactName(),
                companyEntity.getContactTitle(),
                companyEntity.getContactPhoneNumber())
                ;
        }
        return null;
    }
}
