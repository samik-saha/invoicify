package com.chaos.invoicify.service;

import com.chaos.invoicify.dto.ItemDto;
import com.chaos.invoicify.entity.CompanyEntity;
import com.chaos.invoicify.entity.ItemEntity;
import com.chaos.invoicify.repository.CompanyRepository;
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
    @Autowired
    CompanyRepository companyRepository;

    public InvoiceDto addInvoices(InvoiceDto invoiceDto) {
        InvoiceDto newInvoiceDto = null;
        CompanyEntity companyEntity = companyRepository.findByName(invoiceDto.getCompanyName());

        if (companyEntity != null) {
            InvoiceEntity invoiceEntity = new InvoiceEntity(companyEntity);

            if (invoiceDto.getItemDtoList() != null) {

                invoiceEntity.setItems(
                        invoiceDto.getItemDtoList().stream()
                                .map(
                                        itemDto -> new ItemEntity(
                                                itemDto.getItemDescription(),
                                                itemDto.getItemCount(),
                                                itemDto.getItemFeeType(),
                                                itemDto.getItemUnitPrice(),
                                                invoiceEntity)).collect(Collectors.toList()));
            }

            invoicesRepository.save(invoiceEntity);
            newInvoiceDto = new InvoiceDto(invoiceEntity.getId(),
                    invoiceEntity.getCompany().getName(),
                    invoiceEntity.getCreateDate(),
                    invoiceEntity.getModifiedDate(),
                    invoiceEntity.getTotalValue(),
                    invoiceEntity.getItems().stream()
                            .map(
                                    itemEntity -> new ItemDto(
                                            itemEntity.getItemDescription(),
                                            itemEntity.getItemCount(),
                                            itemEntity.getItemFeeType(),
                                            itemEntity.getItemUnitPrice(),
                                            itemEntity.getTotalItemValue()
                                            )).collect(Collectors.toList()));
        }

        return newInvoiceDto;
    }

    public List<InvoiceDto> fetchAllInvoices() {
        return invoicesRepository.findAll()
                .stream()
                .map(
                        invoiceEntity -> new InvoiceDto(
                                invoiceEntity.getId(),
                                invoiceEntity.getCompany().getName(),
                                invoiceEntity.getCreateDate(),
                                invoiceEntity.getModifiedDate(),
                                invoiceEntity.getTotalValue(),
                                invoiceEntity.getItems().stream()
                                        .map(itemEntity -> new ItemDto(
                                                itemEntity.getItemDescription(),
                                                itemEntity.getItemCount(),
                                                itemEntity.getItemFeeType(),
                                                itemEntity.getItemUnitPrice(),
                                                itemEntity.getTotalItemValue())).collect(Collectors.toList())
                        )).collect(Collectors.toList());
    }

    public InvoiceDto addItems(Long invoiceNumber, List<ItemDto> itemDtoList) {
        InvoiceDto newInvoiceDto = null;
        InvoiceEntity invoiceEntity = this.invoicesRepository.findById(invoiceNumber).orElse(null);

        if (invoiceEntity != null) {
            itemDtoList
                    .forEach(
                            itemDto -> {
                                ItemEntity itemEntity =
                                        new ItemEntity(
                                                itemDto.getItemDescription(),
                                                itemDto.getItemCount(),
                                                itemDto.getItemFeeType(),
                                                itemDto.getItemUnitPrice(),
                                                invoiceEntity);
                                this.itemRepository.save(itemEntity);
                            });
            newInvoiceDto = new InvoiceDto(
                    invoiceEntity.getId(),
                    invoiceEntity.getCompany().getName(),
                    invoiceEntity.getCreateDate(),
                    invoiceEntity.getModifiedDate(),
                    invoiceEntity.getTotalValue(),
                    invoiceEntity.getItems().stream()
                            .map(itemEntity -> new ItemDto(
                                    itemEntity.getItemDescription(),
                                    itemEntity.getItemCount(),
                                    itemEntity.getItemFeeType(),
                                    itemEntity.getItemUnitPrice(),
                                    itemEntity.getTotalItemValue())).collect(Collectors.toList()));
        }
        return newInvoiceDto;
    }

    public List<InvoiceDto> fetchInvoiceById(Long invoiceNumber) {
        return invoicesRepository.findById(invoiceNumber)
                .stream()
                .map(invoiceEntity -> new InvoiceDto(
                        invoiceEntity.getId(),
                        invoiceEntity.getCompany().getName(),
                        invoiceEntity.getCreateDate(),
                        invoiceEntity.getModifiedDate(),
                        invoiceEntity.getTotalValue(),
                        invoiceEntity.getItems().stream()
                                .map(itemEntity -> new ItemDto(
                                        itemEntity.getItemDescription(),
                                        itemEntity.getItemCount(),
                                        itemEntity.getItemFeeType(),
                                        itemEntity.getItemUnitPrice(),
                                        itemEntity.getTotalItemValue())).collect(Collectors.toList())
                )).collect(Collectors.toList());
    }


}
