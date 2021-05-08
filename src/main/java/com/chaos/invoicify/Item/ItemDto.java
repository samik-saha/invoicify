package com.chaos.invoicify.Item;

import com.chaos.invoicify.Invoices.FeeType;
import com.chaos.invoicify.Invoices.InvoiceDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ItemDto {

    String itemDescription;
    int itemCount;
    FeeType itemFeeType;
    Double itemUnitPrice;
    InvoiceDto invoiceDto;

    @Override
    public String toString() {
        return "ItemDto{" +
                "itemDescription='" + itemDescription + '\'' +
                ", itemCount=" + itemCount +
                ", itemFeeType=" + itemFeeType +
                ", itemUnitPrice=" + itemUnitPrice +
                ", invoiceDto=" + invoiceDto +
                '}';
    }
}

