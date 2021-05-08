package com.chaos.invoicify.controller;

import com.chaos.invoicify.dto.CompanyDto;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CompanyController {

    @PostMapping("/company")
    @ResponseStatus(HttpStatus.CREATED)
    public Object addCompany(@RequestBody CompanyDto companyDto){
        return "{\"status\":\"Created\",\"status_code\":201,\"data\":\"Company created successfully!\"}";
    }
}
