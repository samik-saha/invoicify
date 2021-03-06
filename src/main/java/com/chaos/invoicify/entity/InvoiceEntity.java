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


    @ManyToOne()
    @JoinColumn(name = "company_id", nullable = false)
    private CompanyEntity company;

    LocalDate createDate;
    LocalDate modifiedDate;
    boolean isPaid;

    public InvoiceEntity(CompanyEntity company) {
        this.company = company;
        this.createDate = LocalDate.now();
        this.modifiedDate = LocalDate.now();
        this.items = new ArrayList<>();
        this.isPaid = false;
    }

    public double getTotalValue() {

        return this.items.stream()
            .map(ItemEntity::getTotalItemValue)
            .reduce(0.0, Double::sum);
    }


}
