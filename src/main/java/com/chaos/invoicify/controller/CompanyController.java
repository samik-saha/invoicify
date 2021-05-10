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

//        if (statusCode == StatusCode.SUCCESS) {
//            return new Response(HttpStatus.CREATED.getReasonPhrase(), HttpStatus.CREATED.value(),
//                    "Company created successfully!");
//        }else if (statusCode == StatusCode.DUPLICATE){
//            return new Response(HttpStatus.BAD_REQUEST.getReasonPhrase(), HttpStatus.BAD_REQUEST.value(),
//                    "Company already exist");
//        }else if (statusCode == StatusCode.NONAME){
//            return new Response(HttpStatus.BAD_REQUEST.getReasonPhrase(), HttpStatus.BAD_REQUEST.value(),
//                    "Company name cannot be null!");
//        }else{
//            return new Response(HttpStatus.BAD_REQUEST.getReasonPhrase(), HttpStatus.BAD_REQUEST.value(),
//                    "Error occurred while creating company!");
//        }
    }

    @GetMapping("/company")
    @ResponseStatus(HttpStatus.OK)
    public Object getCompanyList(){
        List<CompanyDto> companyDtoList=companyService.getCompanyList();
        return new Response(HttpStatus.OK.getReasonPhrase(), HttpStatus.OK.value(), companyDtoList);
    }
}
