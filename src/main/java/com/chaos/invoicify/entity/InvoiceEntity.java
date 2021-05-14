package com.chaos.invoicify.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
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


    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "company_id", nullable = false)
    private CompanyEntity company;

    LocalDate createDate;
    LocalDate modifiedDate;

    public InvoiceEntity(CompanyEntity company, LocalDate createDate, LocalDate modifiedDate) {
        this.company = company;
        this.createDate = createDate;
        this.modifiedDate = modifiedDate;
    }

    public InvoiceEntity(CompanyEntity company) {
        this.company = company;
        this.createDate = LocalDate.now();
        this.modifiedDate = LocalDate.now();
        this.items = new ArrayList<>();
    }

    @Override
    public String toString() {
        return "InvoiceEntity{" +
            "id=" + id +
            ", items=" + items +
            ", company=" + company +
            ", createDate=" + createDate +
            ", modifiedDate=" + modifiedDate +
            '}';
    }
}
