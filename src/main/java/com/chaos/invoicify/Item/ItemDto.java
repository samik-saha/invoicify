package com.chaos.invoicify.Item;

import com.chaos.invoicify.Invoices.FeeType;
import com.chaos.invoicify.Invoices.InvoiceDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ItemDto {

    String itemDescription;
    int itemCount;
    FeeType itemFeeType;
    Double itemUnitPrice;
    InvoiceDto invoiceDto;

    public ItemDto(String itemDescription, int itemCount, FeeType itemFeeType, Double itemUnitPrice) {
        this.itemDescription = itemDescription;
        this.itemCount = itemCount;
        this.itemFeeType = itemFeeType;
        this.itemUnitPrice = itemUnitPrice;
    }

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

