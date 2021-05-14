package com.chaos.invoicify.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InvoiceDto {
    Long invoiceNumber;
    String companyName;
    LocalDate createDate;
    LocalDate modifiedDate;

    List<ItemDto> itemDtoList;

    public InvoiceDto(String companyName) {
        this.companyName = companyName;
    }

    public InvoiceDto(String companyName, List<ItemDto> itemDtoList) {
        this.companyName = companyName;
        this.itemDtoList = itemDtoList;
    }
}
