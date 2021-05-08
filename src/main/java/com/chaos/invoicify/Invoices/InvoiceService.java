package com.chaos.invoicify.Invoices;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class InvoiceService {

    @Autowired
    InvoicesRepository invoicesRepository;


    public void addInvoices(InvoiceDto invoiceDto) {
        invoicesRepository.save(new InvoiceEntity(invoiceDto.getInvoiceName(),
            invoiceDto.getCompanyName(),
            invoiceDto.getInvoiceDate(),
            invoiceDto.getItemsList()));
    }

    public List<InvoiceDto> fetchAllInvoices() {
        return invoicesRepository.findAll()
            .stream()
            .map(invoiceEntity -> {
                return new InvoiceDto(invoiceEntity.getInvoiceName(),
                    invoiceEntity.getCompanyName(),
                    invoiceEntity.getInvoiceDate(),
                    invoiceEntity.getItemsList());
            }).collect(Collectors.toList());
    }
}
