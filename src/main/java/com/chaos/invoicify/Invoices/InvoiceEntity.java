package com.chaos.invoicify.Invoices;

import com.chaos.invoicify.Item.ItemDto;
import com.chaos.invoicify.Item.ItemEntity;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.List;

@Entity
@Getter
@Setter
@EqualsAndHashCode
@NoArgsConstructor
public class InvoiceEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    Long id;

    @OneToMany(mappedBy = "invoice")
    List<ItemEntity> items;

    String invoiceName;
    String companyName;
    String invoiceDate;

    public InvoiceEntity(String invoiceName, String companyName, String invoiceDate) {
        this.invoiceName = invoiceName;
        this.companyName = companyName;
        this.invoiceDate = invoiceDate;

    }
}
