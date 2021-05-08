package com.chaos.invoicify.controller;

import com.chaos.invoicify.dto.CompanyDto;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
public class CompanyController {

    @PostMapping("/company")
    @ResponseStatus(HttpStatus.CREATED)
    public Object addCompany(@RequestBody CompanyDto companyDto){
        return "{\"status\":\"Created\",\"status_code\":201,\"data\":\"Company created successfully!\"}";
    }

    @GetMapping("/company")
    @ResponseStatus(HttpStatus.OK)
    public Object getCompanyList(){
        return "{\"status\":\"Ok\",\"status_code\":200,\"data\":[]}";
    }
}
