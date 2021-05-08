package com.chaos.invoicify.controller;

import com.chaos.invoicify.dto.CompanyDto;
import com.chaos.invoicify.dto.Response;
import com.chaos.invoicify.services.CompanyService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class CompanyController {
    private CompanyService companyService;

    public CompanyController(CompanyService companyService) {
        this.companyService = companyService;
    }

    @PostMapping("/company")
    @ResponseStatus(HttpStatus.CREATED)
    public Object addCompany(@RequestBody CompanyDto companyDto){
        companyService.createCompany(companyDto);
        return new Response(HttpStatus.CREATED.getReasonPhrase(),HttpStatus.CREATED.value(),
                "Company created successfully!");
    }

    @GetMapping("/company")
    @ResponseStatus(HttpStatus.OK)
    public Object getCompanyList(){
        List<CompanyDto> companyDtoList=companyService.getCompanyList();
        return new Response(HttpStatus.OK.getReasonPhrase(), HttpStatus.OK.value(), companyDtoList);
    }
}
