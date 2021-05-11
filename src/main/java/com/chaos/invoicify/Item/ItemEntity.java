package com.chaos.invoicify.Item;

import com.chaos.invoicify.Invoices.FeeType;
import com.chaos.invoicify.Invoices.InvoiceEntity;
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


    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "invoice_id", nullable = true)
    private InvoiceEntity invoice;

    String itemDescription;
    int itemCount;
    FeeType itemFeeType;
    Double itemUnitPrice;

    public ItemEntity(String itemDescription, int itemCount, FeeType itemFeeType, Double itemUnitPrice,InvoiceEntity invoiceEntity) {
        this.itemDescription = itemDescription;
        this.itemCount = itemCount;
        this.itemFeeType = itemFeeType;
        this.itemUnitPrice = itemUnitPrice;
        this.invoice = invoiceEntity;
    }

    @Override
    public String toString() {
        return "ItemEntity{" +
            "id=" + id +
            ", invoice=" + invoice +
            ", itemDescription='" + itemDescription + '\'' +
            ", itemCount=" + itemCount +
            ", itemFeeType=" + itemFeeType +
            ", itemUnitPrice=" + itemUnitPrice +
            '}';
    }
}
