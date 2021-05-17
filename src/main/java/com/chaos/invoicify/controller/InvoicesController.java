package com.chaos.invoicify.controller;

import com.chaos.invoicify.dto.ItemDto;
import com.chaos.invoicify.dto.InvoiceDto;
import com.chaos.invoicify.dto.Response;
import com.chaos.invoicify.service.InvoiceService;
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
    public Object addInvoices(@RequestBody InvoiceDto invoiceDto) {

        InvoiceDto responseInvoiceDto = this.invoiceService.addInvoices(invoiceDto);
        Response response;
        if (responseInvoiceDto == null) {
            response =
                new Response(
                    HttpStatus.BAD_REQUEST.getReasonPhrase(),
                    HttpStatus.BAD_REQUEST.value(),
                    "Invoice Creation Not Successful!");
        } else {
            response =
                new Response(
                    HttpStatus.CREATED.getReasonPhrase(),
                    HttpStatus.CREATED.value(),
                    responseInvoiceDto);
        }

        return response;
    }

    @GetMapping
    public List<InvoiceDto> getAllInvoices() {
        return invoiceService.fetchAllInvoices();
    }

    @GetMapping("{invoiceNumber}")
    public List<InvoiceDto> searchInvoiceById(@PathVariable Long invoiceNumber) {
        return invoiceService.fetchInvoiceById(invoiceNumber);
    }

    @DeleteMapping("{invoiceNumber}")
    public void deleteInvoiceById(@PathVariable Long invoiceNumber) {
        invoiceService.deleteInvoiceById(invoiceNumber);
    }

    @PostMapping("{invoiceNumber}/items")
    @ResponseStatus(HttpStatus.CREATED)
    public Object addItems(
        @PathVariable Long invoiceNumber, @RequestBody List<ItemDto> itemDtoList) {
        InvoiceDto responseInvoiceDto = this.invoiceService.addItems(invoiceNumber, itemDtoList);

        Response response;
        if (responseInvoiceDto == null) {
            response =
                new Response(
                    HttpStatus.BAD_REQUEST.getReasonPhrase(),
                    HttpStatus.BAD_REQUEST.value(),
                    "Item addition to Invoice Not Successful!");
        } else {
            response = new Response(
                HttpStatus.CREATED.getReasonPhrase(),
                HttpStatus.CREATED.value(),
                responseInvoiceDto);
        }

        return response;
    }

}
