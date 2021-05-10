package com.chaos.invoicify.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CompanyEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(nullable = false)
    private String name;
    private String address;
    private String contactName;
    private String contactTitle;
    private String contactPhoneNumber;

    public CompanyEntity(String name, String address, String contactName, String contactTitle, String contactPhoneNumber) {
        this.name = name;
        this.address = address;
        this.contactName = contactName;
        this.contactTitle = contactTitle;
        this.contactPhoneNumber = contactPhoneNumber;
    }
}
