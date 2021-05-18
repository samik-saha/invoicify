package com.chaos.invoicify.entity;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
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


    @OneToMany(fetch = FetchType.LAZY, mappedBy = "invoice", cascade = CascadeType.ALL)
    List<ItemEntity> items;


    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "company_id", nullable = false)
    private CompanyEntity company;

    LocalDate createDate;
    LocalDate modifiedDate;

    public InvoiceEntity(CompanyEntity company) {
        this.company = company;
        this.createDate = LocalDate.now();
        this.modifiedDate = LocalDate.now();
        this.items = new ArrayList<>();
    }

    public double getTotalValue() {

        return this.items.stream()
            .map(x -> x.getTotalItemValue())
            .reduce(0.0, Double::sum);
    }


}
