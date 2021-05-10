package com.chaos.invoicify.Invoices;

import com.chaos.invoicify.Item.ItemDto;
import com.chaos.invoicify.Item.ItemEntity;
import com.chaos.invoicify.Item.ItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class InvoiceService {

    @Autowired
    InvoicesRepository invoicesRepository;
    @Autowired
    ItemRepository itemRepository;


    public void addInvoices(InvoiceDto invoiceDto) {


        invoicesRepository.save(new InvoiceEntity(invoiceDto.getInvoiceName(),
            invoiceDto.getCompanyName(),
            invoiceDto.getInvoiceDate()));

        if (!invoiceDto.getItems().isEmpty()) {
            ItemDto itemDto = new ItemDto(invoiceDto.getItems().get(0).getItemDescription(),
                    invoiceDto.getItems().get(0).getItemCount(),
                    invoiceDto.getItems().get(0).getItemFeeType(),
                    invoiceDto.getItems().get(0).getItemUnitPrice());

            itemRepository.save(new ItemEntity(itemDto.getItemDescription(),
                    itemDto.getItemCount(),
                    itemDto.getItemFeeType(),
                    itemDto.getItemUnitPrice(),
                    new InvoiceEntity(itemDto.getInvoiceDto().getInvoiceName(),
                            itemDto.getInvoiceDto().getCompanyName(),
                            itemDto.getInvoiceDto().getInvoiceDate())));
        }
    }

    public List<InvoiceDto> fetchAllInvoices() {
        return invoicesRepository.findAll()
                .stream()
                .map(invoiceEntity -> {
                    return new InvoiceDto(invoiceEntity.getInvoiceName(),
                            invoiceEntity.getCompanyName(),
                            invoiceEntity.getInvoiceDate(),
                            (invoiceEntity.getItems().stream().map(itemEntity -> {
                                return new ItemDto(itemEntity.getItemDescription(),
                                        itemEntity.getItemCount(),
                                        itemEntity.getItemFeeType(),
                                        itemEntity.getItemUnitPrice(),
                                        new InvoiceDto(itemEntity.getInvoice().getInvoiceName(),
                                                itemEntity.getInvoice().getCompanyName(),
                                                itemEntity.getInvoice().getInvoiceDate()));
                            }).collect(Collectors.toList()))
                    );
                }).collect(Collectors.toList());
    }

    public void addItem(ItemDto itemDto) {
        Optional<InvoiceEntity> invoiceExists = invoicesRepository.findAll().stream()
                .filter(invoiceEntity -> invoiceEntity.getInvoiceName().equals(itemDto.getInvoiceDto().getInvoiceName())).findAny();
        if (invoiceExists.isEmpty()) {
            invoicesRepository.save(new InvoiceEntity(itemDto.getInvoiceDto().getInvoiceName(),
                    itemDto.getInvoiceDto().getCompanyName(),
                    itemDto.getInvoiceDto().getInvoiceDate()
                    ));
        }

        itemRepository.save(new ItemEntity(itemDto.getItemDescription(),
                itemDto.getItemCount(),
                itemDto.getItemFeeType(),
                itemDto.getItemUnitPrice(),
                new InvoiceEntity(itemDto.getInvoiceDto().getInvoiceName(),
                        itemDto.getInvoiceDto().getCompanyName(),
                        itemDto.getInvoiceDto().getInvoiceDate())));
    }
    public List<ItemDto> fetchAllItems(String invoiceName) {
        return itemRepository.findAll()
                .stream()
                .filter(itemEntity -> itemEntity.getInvoice().getInvoiceName().equals(invoiceName))
                .map(itemEntity -> {
                    return new ItemDto(itemEntity.getItemDescription(),
                            itemEntity.getItemCount(),
                            itemEntity.getItemFeeType(),
                            itemEntity.getItemUnitPrice(),
                            new InvoiceDto(itemEntity.getInvoice().getInvoiceName(),
                                    itemEntity.getInvoice().getCompanyName(),
                                    itemEntity.getInvoice().getInvoiceDate())
                    );
                }).collect(Collectors.toList());
    }
}
