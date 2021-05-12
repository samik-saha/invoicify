package com.chaos.invoicify.Item;

import com.chaos.invoicify.Invoices.FeeType;
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
}
