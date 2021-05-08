package com.chaos.invoicify.Invoices;

import com.chaos.invoicify.Item.ItemDto;
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

    @PostMapping("{invoiceName}/item")
    @ResponseStatus(HttpStatus.CREATED)
    public void addItem(@PathVariable String invoiceName, @RequestBody ItemDto itemDto) {
        System.out.println("debug valuees:"+invoiceName);
        System.out.println("second value" +itemDto);
        this.invoiceService.addItem(itemDto);

    }

}
