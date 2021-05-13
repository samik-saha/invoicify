package com.chaos.invoicify.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
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


    @OneToMany(fetch = FetchType.LAZY,mappedBy = "invoice")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    List<ItemEntity> items;

    String invoiceName;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "company_id", nullable = false)
    private CompanyEntity company;

    String invoiceDate;

    public InvoiceEntity(String invoiceName, CompanyEntity company,  String invoiceDate) {
        this.invoiceName = invoiceName;
        this.company = company;
        this.invoiceDate = invoiceDate;
    }

    @Override
    public String toString() {
        return "InvoiceEntity{" +
                "id=" + id +
                ", items=" + items +
                ", invoiceName='" + invoiceName + '\'' +
                ", company=" + company +
                ", invoiceDate='" + invoiceDate + '\'' +
                '}';
    }
}
