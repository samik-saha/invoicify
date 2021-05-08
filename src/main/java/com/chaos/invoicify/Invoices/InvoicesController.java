package com.chaos.invoicify.Invoices;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("invoices")
public class InvoicesController {

    @Autowired
    InvoiceService invoiceService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void addInvoices(@RequestBody InvoiceDto invoiceDto){
        this.invoiceService.addInvoices(invoiceDto);
    }

    @GetMapping
    public List<InvoiceDto> getAllInvoices() {
        return invoiceService.fetchAllInvoices();
    }
}
