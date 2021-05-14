package com.chaos.invoicify.entity;

import com.chaos.invoicify.helper.FeeType;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter
@Setter
@EqualsAndHashCode
@NoArgsConstructor
public class ItemEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    Long id;

    String itemDescription;
    int itemCount;
    FeeType itemFeeType;
    Double itemUnitPrice;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "invoice_id", nullable = false)
    private InvoiceEntity invoice;

    public ItemEntity(String itemDescription, int itemCount, FeeType itemFeeType, Double itemUnitPrice, InvoiceEntity invoice) {
        this.itemDescription = itemDescription;
        this.itemCount = itemCount;
        this.itemFeeType = itemFeeType;
        this.itemUnitPrice = itemUnitPrice;
        this.invoice = invoice;
    }
}
