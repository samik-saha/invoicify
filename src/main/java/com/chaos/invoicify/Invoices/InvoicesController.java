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
        System.out.println("Invoice in the controller: " + invoiceService.fetchAllInvoices());
        return invoiceService.fetchAllInvoices();
    }

    @PostMapping("{invoiceName}/item")
    @ResponseStatus(HttpStatus.CREATED)
    public void addItem(@PathVariable String invoiceName, @RequestBody ItemDto itemDto) {
        System.out.println("debug values:"+invoiceName);
        System.out.println("second value" +itemDto);
        this.invoiceService.addItem(itemDto);

    }

    @GetMapping("{invoiceName}/item")
    public List<ItemDto> getItems(@PathVariable String invoiceName){
        System.out.println("Item in the Controller: " + invoiceService.fetchAllItems(invoiceName));
        return invoiceService.fetchAllItems(invoiceName);
    }

}
