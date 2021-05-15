package com.chaos.invoicify.dto;

import com.chaos.invoicify.helper.FeeType;
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
    Double totalItemValue;


}
