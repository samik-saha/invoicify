package com.chaos.invoicify.controller;

import com.chaos.invoicify.dto.InvoiceDto;
import com.chaos.invoicify.dto.ItemDto;
import com.chaos.invoicify.dto.Response;
import com.chaos.invoicify.service.InvoiceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("invoices")
public class InvoicesController {

  @Autowired InvoiceService invoiceService;

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public Object addInvoices(@RequestBody InvoiceDto invoiceDto) {

    InvoiceDto responseInvoiceDto = this.invoiceService.addInvoices(invoiceDto);

    Response response = null;
    if (responseInvoiceDto == null) {
      response =
          new Response(
              HttpStatus.BAD_REQUEST.getReasonPhrase(),
              HttpStatus.BAD_REQUEST.value(),
              "Company does not exist!");
    } else {
      response =
          new Response(
              HttpStatus.CREATED.getReasonPhrase(), HttpStatus.CREATED.value(), responseInvoiceDto);
    }

    return response;
  }

  @GetMapping
  public List<InvoiceDto> getAllInvoices() {
    return invoiceService.fetchAllInvoices();
  }

  @PostMapping("{invoiceNumber}/items")
  @ResponseStatus(HttpStatus.CREATED)
  public List<ItemDto> addItems(
      @PathVariable Long invoiceNumber, @RequestBody List<ItemDto> itemDtoList) {
    return this.invoiceService.addItems(invoiceNumber, itemDtoList);
  }
}
