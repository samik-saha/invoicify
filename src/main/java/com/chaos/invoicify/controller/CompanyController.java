package com.chaos.invoicify.controller;

import com.chaos.invoicify.dto.CompanyDto;
import com.chaos.invoicify.dto.Response;
import com.chaos.invoicify.helper.StatusCode;
import com.chaos.invoicify.service.CompanyService;
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
    public Object addCompany(@RequestBody CompanyDto companyDto){

        StatusCode statusCode =companyService.createCompany(companyDto);
        Response response = null;
        switch (statusCode){
            case SUCCESS:
                response = new Response(HttpStatus.CREATED.getReasonPhrase(), HttpStatus.CREATED.value(),
                        "Company created successfully!");
                break;
            case DUPLICATE:
                response = new Response(HttpStatus.BAD_REQUEST.getReasonPhrase(), HttpStatus.BAD_REQUEST.value(),
                        "Company already exist");
                break;
            case NONAME:
                response = new Response(HttpStatus.BAD_REQUEST.getReasonPhrase(), HttpStatus.BAD_REQUEST.value(),
                        "Company name cannot be empty!");
                break;
        }

        return response;

    }

    @GetMapping("/company")
    @ResponseStatus(HttpStatus.OK)
    public Object getCompanyList(){
        List<CompanyDto> companyDtoList=companyService.getCompanyList();
        return new Response(HttpStatus.OK.getReasonPhrase(), HttpStatus.OK.value(), companyDtoList);
    }

    @PutMapping("/company")
    @ResponseStatus(HttpStatus.OK)
    public Object updateCompany(@RequestParam(required = false) String companyName, @RequestBody CompanyDto companyDto){
        Response response = null;
        StatusCode statusCode;

        if (companyName == null) {
            statusCode = companyService.updateCompany(companyDto.getName(),companyDto);
        }
        else{
            statusCode = companyService.updateCompany(companyName, companyDto);
        }

        if (statusCode == StatusCode.SUCCESS){
            response = new Response(HttpStatus.OK.getReasonPhrase(), HttpStatus.OK.value(),
                    "Company updated successfully!");
        }
        return response;
    }

}
