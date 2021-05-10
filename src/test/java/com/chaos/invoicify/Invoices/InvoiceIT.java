package com.chaos.invoicify.Invoices;

import com.chaos.invoicify.Item.ItemDto;
import com.chaos.invoicify.Item.ItemRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.core.IsNull;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import javax.transaction.Transactional;
import java.util.List;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureRestDocs
@Transactional
public class InvoiceIT {
    @Autowired
    InvoicesRepository invoicesRepository;

    @Autowired
    ItemRepository itemRepository;

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Test
    public void addInvoiceWithNoItems() throws Exception {
        InvoiceDto invoiceDto = new InvoiceDto("Invoice1", "Company1", "2021-05-08");

        mockMvc.perform(post("/invoices")
                .content(objectMapper.writeValueAsString(invoiceDto))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andDo(document("AddInvoices"));

        mockMvc.perform(get("/invoices"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("length()").value(1))
                .andExpect(jsonPath("[0].invoiceName").value("Invoice1"))
                .andExpect(jsonPath("[0].companyName").value("Company1"))
                .andExpect(jsonPath("[0].invoiceDate").value("2021-05-08"))
                .andExpect(jsonPath("[0].items").isEmpty())
                .andDo(document("GetInvoices", responseFields(
                        fieldWithPath("[0].invoiceName").description("Invoice1")
                        , fieldWithPath("[0].companyName").description("Company1")
                        , fieldWithPath("[0].invoiceDate").description("2021-05-08")
                        , fieldWithPath("[0].items").description("[]")
                )));
    }
//      ADD INVOICE
//          addInvoiceWithOneItem
    @Test
    public void addInvoiceWithOneItem() throws Exception{
        ItemDto itemListDTo = new ItemDto("Description1", 10, FeeType.RATEBASED, 20.10);
        InvoiceDto invoiceDto = new InvoiceDto("Invoice1", "Company1", "2021-05-08", List.of(itemListDTo));

        mockMvc.perform(post("/invoices")
                .content(objectMapper.writeValueAsString(invoiceDto))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andDo(document("AddInvoices"));
        mockMvc.perform(get("/invoices"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("length()").value(1))
                .andExpect(jsonPath("[0].invoiceName").value("Invoice1"))
                .andExpect(jsonPath("[0].companyName").value("Company1"))
                .andExpect(jsonPath("[0].invoiceDate").value("2021-05-08"))
                .andExpect(jsonPath("[0].items").value("Description1"))
                .andDo(document("GetInvoices", responseFields(
                        fieldWithPath("[0].invoiceName").description("Invoice1")
                        , fieldWithPath("[0].companyName").description("Company1")
                        , fieldWithPath("[0].invoiceDate").description("2021-05-08")
                        , fieldWithPath("[0].items.itemDescription").description("[]")
                )));



    }
//          addInvoiceWithMultipleItems
//          addInvoiceWithNonUniqueValue
//      GET INVOICE
//          getInvoiceEmptyList
//          getInvoiceWithOneInvoice
//          getInvoiceWithMultipleInvoices
//          getInvoiceWithSpecificValue
//      UPDATE INVOICE
//          Update Invoice Information
//          Update List of Items
//          Track Update Date (Should this be a list)
    @Test
    public void addItem() throws Exception {
        System.out.println(("first step"));
        InvoiceDto invoiceDto = new InvoiceDto("Invoice1", "Company1", "2021-05-08");
        System.out.println(("second step" +invoiceDto.invoiceName));
        ItemDto itemListDTo = new ItemDto("Description1", 10, FeeType.RATEBASED, 20.10, invoiceDto);

        System.out.println("Item Dto is : " + itemListDTo);
        mockMvc.perform(post("/invoices/Invoice1/item")
                .content(objectMapper.writeValueAsString(itemListDTo))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andDo(document("AddItem"));
        mockMvc.perform(get("/invoices/Invoice1/item"))
            .andExpect(jsonPath("[0].itemDescription").value("Description1"))
            .andExpect(jsonPath("[0].itemCount").value(10))
            .andExpect(jsonPath("[0].itemFeeType").value(FeeType.RATEBASED.name()))
            .andExpect(jsonPath("[0].itemUnitPrice").value(20.10));

    }


}
