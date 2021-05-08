package com.chaos.invoicify.Invoices;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
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


    String invoiceName;
    String companyName;
    String invoiceDate;
    String itemsList;

    public InvoiceEntity(String invoiceName, String companyName, String invoiceDate, String itemsList) {
        this.invoiceName = invoiceName;
        this.companyName = companyName;
        this.invoiceDate = invoiceDate;
        this.itemsList = itemsList;
    }
}
