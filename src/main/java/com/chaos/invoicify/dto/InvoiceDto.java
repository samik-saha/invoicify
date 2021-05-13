package com.chaos.invoicify.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InvoiceDto {
    String invoiceName;
    CompanyDto companyDto;
    String invoiceDate;

    List<ItemDto> itemDtoList;

}
