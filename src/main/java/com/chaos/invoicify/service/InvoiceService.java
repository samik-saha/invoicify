package com.chaos.invoicify.service;

import com.chaos.invoicify.dto.ItemDto;
import com.chaos.invoicify.entity.ItemEntity;
import com.chaos.invoicify.repository.InvoicesRepository;
import com.chaos.invoicify.repository.ItemRepository;
import com.chaos.invoicify.dto.InvoiceDto;
import com.chaos.invoicify.entity.InvoiceEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class InvoiceService {

    @Autowired
    InvoicesRepository invoicesRepository;
    @Autowired
    ItemRepository itemRepository;


    public InvoiceDto addInvoices(InvoiceDto invoiceDto) {
        invoicesRepository.save(new InvoiceEntity(invoiceDto.getInvoiceName(),
            invoiceDto.getCompanyName(),
            invoiceDto.getInvoiceDate()));

        return invoiceDto;
    }

    public List<InvoiceDto> fetchAllInvoices() {
        return invoicesRepository.findAll()
            .stream()
            .map(invoiceEntity -> {
                List<ItemDto> itemDtoList = this.fetchAllItems(invoiceEntity);
                return new InvoiceDto(invoiceEntity.getInvoiceName(),
                    invoiceEntity.getCompanyName(),
                    invoiceEntity.getInvoiceDate(),
                    this.fetchAllItems(invoiceEntity)
                );
            }).collect(Collectors.toList());
    }

    public InvoiceEntity fetchInvoiceByName(String invoiceName) {
        InvoiceEntity invoiceEntity = invoicesRepository.findByInvoiceName(invoiceName);

        List<ItemEntity> itemEntities = itemRepository.findAll()
            .stream()
            .filter(itemEntity -> itemEntity.getInvoice().getInvoiceName().equals(invoiceName))
            .collect(Collectors.toList());

        invoiceEntity.setItems(itemEntities);
        System.out.println(invoiceEntity.getItems().get(0).getItemDescription());

        return invoiceEntity;
    }

    public ItemDto addItem(String invoiceName, ItemDto itemDto) {
        InvoiceEntity invoiceEntity = this.invoicesRepository.findByInvoiceName(invoiceName);
        if (invoiceEntity != null) {
            System.out.println("Invoice inside add Item: " + invoiceEntity.getInvoiceName() + " " + invoiceEntity.getCompanyName());
        } else {
            System.out.println("returned a null");
        }
        ItemEntity itemEntity = new ItemEntity(itemDto.getItemDescription(),
            itemDto.getItemCount(),
            itemDto.getItemFeeType(),
            itemDto.getItemUnitPrice(),
            invoiceEntity);

        this.itemRepository.save(itemEntity);
        return itemDto;
    }

    public List<ItemDto> fetchAllItems(InvoiceEntity invoiceEntity) {
        return itemRepository.findAll()
                .stream()
                .filter(itemEntity -> itemEntity.getInvoice().equals(invoiceEntity))
                .map(itemEntity -> {
                    return new ItemDto(itemEntity.getItemDescription(),
                            itemEntity.getItemCount(),
                            itemEntity.getItemFeeType(),
                            itemEntity.getItemUnitPrice()
//                          itemEntity.getInvoice().getInvoiceName()
                    );
                }).collect(Collectors.toList());
    }

    public List<ItemDto> addItems(String invoiceName, List<ItemDto> itemDtoList) {
        InvoiceEntity invoiceEntity = this.invoicesRepository.findByInvoiceName(invoiceName);
        if (invoiceEntity != null) {
            System.out.println("Invoice inside addItems: " + invoiceEntity.getInvoiceName() + " " + invoiceEntity.getCompanyName());
        } else {
            System.out.println("returned a null");
        }

        itemDtoList
            .stream()
            .forEach(itemDto -> {
                System.out.println(itemDto.getItemDescription());
                ItemEntity itemEntity = new ItemEntity(itemDto.getItemDescription(),
                    itemDto.getItemCount(),
                    itemDto.getItemFeeType(),
                    itemDto.getItemUnitPrice(),
                    invoiceEntity);
                this.itemRepository.save(itemEntity);
            });

        return itemDtoList;
    }
}
