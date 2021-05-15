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

    @Column(columnDefinition = "DECIMAL(15,2)")
    double itemUnitPrice;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "invoice_id", nullable = false)
    private InvoiceEntity invoice;

    public double getTotalItemValue() {
        return this.itemCount * this.itemUnitPrice;
    }

    public ItemEntity(String itemDescription, int itemCount, FeeType itemFeeType, double itemUnitPrice,  InvoiceEntity invoice) {
        this.itemDescription = itemDescription;
        this.itemCount = itemCount;
        this.itemFeeType = itemFeeType;
        this.itemUnitPrice = itemUnitPrice;
        this.invoice = invoice;
    }
}
