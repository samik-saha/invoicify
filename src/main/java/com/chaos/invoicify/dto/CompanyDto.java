package com.chaos.invoicify.dto;

import com.chaos.invoicify.helper.Address;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Embedded;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CompanyDto {
    private String name;
    @Embedded
    private Address address;
    private String contactName;
    private String contactTitle;
    private String contactPhoneNumber;

}
