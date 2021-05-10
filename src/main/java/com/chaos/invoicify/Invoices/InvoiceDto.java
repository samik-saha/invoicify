package com.chaos.invoicify.Invoices;

import com.chaos.invoicify.Item.ItemDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InvoiceDto {
    String invoiceName;
    String companyName;
    String invoiceDate;
    List<ItemDto> items;

    public InvoiceDto(String invoiceName, String companyName, String invoiceDate) {
        this.invoiceName = invoiceName;
        this.companyName = companyName;
        this.invoiceDate = invoiceDate;
        this.items = new ArrayList<>();
    }

}
