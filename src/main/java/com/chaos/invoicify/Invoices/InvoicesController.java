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
    public InvoiceDto addInvoices(@RequestBody InvoiceDto invoiceDto){
        return this.invoiceService.addInvoices(invoiceDto);
    }

    @GetMapping
    public List<InvoiceDto> getAllInvoices() {return invoiceService.fetchAllInvoices();}

    @PostMapping("{invoiceName}/item")
    @ResponseStatus(HttpStatus.CREATED)
    public ItemDto addItem(@PathVariable String invoiceName, @RequestBody ItemDto itemDto) {
        return this.invoiceService.addItem(invoiceName,itemDto);
    }

    @PostMapping("{invoiceName}/items")
    @ResponseStatus(HttpStatus.CREATED)
    public List<ItemDto> addItems(@PathVariable String invoiceName, @RequestBody List<ItemDto> itemDtoList) {
        return this.invoiceService.addItems(invoiceName,itemDtoList);
    }

}
