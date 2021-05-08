package com.chaos.invoicify.Invoices;

import com.chaos.invoicify.Item.ItemDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InvoiceDto {
    String invoiceName;
    String companyName;
    String invoiceDate;

}
