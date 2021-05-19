package com.chaos.invoicify.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InvoiceDto {
    Long invoiceNumber;
    String companyName;
    LocalDate createDate;
    LocalDate modifiedDate;
    double totalInvoiceValue;
    boolean isPaid;
    List<ItemDto> items;

    public InvoiceDto(String companyName) {
        this.companyName = companyName;
    }

    public InvoiceDto(String companyName, List<ItemDto> items) {
        this.companyName = companyName;
        this.items = items;
    }

    public InvoiceDto(String companyName, LocalDate createDate, List<ItemDto> items) {
        this.companyName = companyName;
        this.createDate = createDate;
        this.items = items;
    }
}
