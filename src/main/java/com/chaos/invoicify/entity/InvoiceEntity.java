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


    @OneToMany(fetch = FetchType.LAZY, mappedBy = "invoice",cascade = CascadeType.ALL)
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
    //public double totalInvoiceValue() {
        //this.items.forEach(
        //item =
        //Integer sum = items.stream()
        //  .map(x -> x.getPrice())
        //  .reduce(0, Integer::sum);

            /*Integer sum = items.stream()
                    .map(reqValue -> reqValue.getTotalItemValue() )
                    .reduce (0,ArithmeticUtils::add);*/

        //double total = numbers.stream().mapToInt(i -> i.intValue()).sum();
    //}
    public double getTotalValue () {

        Double sum = this.items.stream()
                .map(x -> x.getTotalItemValue())
                .reduce(0.0, Double::sum);
        double totalValue = sum;
        return totalValue;
    }



}
