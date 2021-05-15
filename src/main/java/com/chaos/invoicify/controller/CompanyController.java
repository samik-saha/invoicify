package com.chaos.invoicify.controller;

import com.chaos.invoicify.dto.CompanyDto;
import com.chaos.invoicify.dto.Response;
import com.chaos.invoicify.helper.CompanyView;
import com.chaos.invoicify.helper.StatusCode;
import com.chaos.invoicify.service.CompanyService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
public class CompanyController {
    private CompanyService companyService;

    public CompanyController(CompanyService companyService) {
        this.companyService = companyService;
    }

    @PostMapping("/company")
    @ResponseStatus(HttpStatus.CREATED)
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

    @PostMapping("/company/{companyName}")
    @ResponseStatus(HttpStatus.OK)
    public Response updateCompany(@PathVariable String companyName, @RequestBody CompanyDto companyDto){
        Response response = null;
        StatusCode statusCode=null;

        if(companyName != null){
            statusCode = companyService.updateCompany(companyName, companyDto);
        }
        else{
            response = new Response(HttpStatus.BAD_REQUEST.getReasonPhrase(), HttpStatus.BAD_REQUEST.value(),
                "Company name is mandatory for updating company details!");
        }

        if (statusCode == StatusCode.SUCCESS){
            response = new Response(HttpStatus.OK.getReasonPhrase(), HttpStatus.OK.value(),
                "Company updated successfully!");
        }
        return response;
    }


    @GetMapping("company-list")
    @ResponseStatus(HttpStatus.OK)
    public Object getSimpleCompanyList(){
        List<CompanyDto> companyDtoList=companyService.getCompanyList();
        List<CompanyView> companyViewList = companyDtoList.stream().map(companyDto ->
            new CompanyView(companyDto.getName(),
                companyDto.getAddress().getCity(),
                companyDto.getAddress().getState())).collect(Collectors.toList());

        return new Response(HttpStatus.OK.getReasonPhrase(), HttpStatus.OK.value(), companyViewList);
    }

}
