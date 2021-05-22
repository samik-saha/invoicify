package com.chaos.invoicify.controller;

import com.chaos.invoicify.dto.CompanyDto;
import com.chaos.invoicify.dto.InvoiceDto;
import com.chaos.invoicify.dto.Response;
import com.chaos.invoicify.helper.CompanyView;
import com.chaos.invoicify.helper.StatusCode;
import com.chaos.invoicify.service.CompanyService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
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
    public Object addCompany(@RequestBody CompanyDto companyDto) {

        StatusCode statusCode = companyService.createCompany(companyDto);
        Response response = null;
        switch (statusCode) {
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
        }
        return response;

    }

    @GetMapping("/company")
    @ResponseStatus(HttpStatus.OK)
    public Object getCompanyList() {
        List<CompanyDto> companyDtoList = companyService.getCompanyList();
        return new Response(HttpStatus.OK.getReasonPhrase(), HttpStatus.OK.value(), companyDtoList);
    }

    @PostMapping("/company/{companyName}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Response> updateCompany(@PathVariable String companyName, @RequestBody CompanyDto companyDto) {
        Response response;
        ResponseEntity<Response> responseEntity;

        StatusCode statusCode = companyService.updateCompany(companyName, companyDto);
        if (statusCode == StatusCode.NOTFOUND) {
            response = new Response(HttpStatus.BAD_REQUEST.getReasonPhrase(), HttpStatus.BAD_REQUEST.value(),
                "Company does not exist!");
            responseEntity = new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        } else if (statusCode == StatusCode.FOUND) {
            String newCompanyName = companyDto.getName();

            response = new Response(HttpStatus.FOUND.getReasonPhrase(), HttpStatus.FOUND.value(),
                "Company updated successfully!");

            responseEntity = ResponseEntity.status(HttpStatus.FOUND)
                .location(URI.create("/company/" + newCompanyName))
                .build()
            ;

        }
        else if (statusCode==StatusCode.DUPLICATE){
            response = new Response(HttpStatus.BAD_REQUEST.getReasonPhrase(), HttpStatus.BAD_REQUEST.value(),
                    "Company with this name already Exists");
            responseEntity = new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }

        else {
            response = new Response(HttpStatus.OK.getReasonPhrase(), HttpStatus.OK.value(),
                "Company updated successfully!");
            responseEntity = new ResponseEntity<>(response, HttpStatus.OK);
        }

        return responseEntity;
    }


    @GetMapping("company-list")
    @ResponseStatus(HttpStatus.OK)
    public Object getSimpleCompanyList() {
        List<CompanyDto> companyDtoList = companyService.getCompanyList();
        List<CompanyView> companyViewList = companyDtoList.stream().map(companyDto ->
            new CompanyView(companyDto.getName(),
                companyDto.getAddress().getCity(),
                companyDto.getAddress().getState())).collect(Collectors.toList());

        return new Response(HttpStatus.OK.getReasonPhrase(), HttpStatus.OK.value(), companyViewList);
    }

    @GetMapping("/company/{companyName}")
    public Object getCompanyByName(@PathVariable String companyName) {
        CompanyDto companyDto = companyService.fetchCompanyByName(companyName);
        if (companyDto == null) {
            return new Response(HttpStatus.NOT_FOUND.getReasonPhrase(), HttpStatus.NOT_FOUND.value(),
                "Company name not found!");
        } else {
            return new Response(HttpStatus.OK.getReasonPhrase(), HttpStatus.OK.value(),
                companyDto);
        }
    }

}
