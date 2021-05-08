package com.chaos.invoicify.Invoices;

import com.chaos.invoicify.Item.ItemDto;
import com.chaos.invoicify.Item.ItemRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

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
    public void addInvoice() throws Exception {


        InvoiceDto invoiceDto = new InvoiceDto("Invoice1", "Company1", "2021-05-08");
        ItemDto itemListDTo = new ItemDto("Description1", 10, FeeType.RATEBASED, 20.10,invoiceDto);
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
                .andExpect(jsonPath("[0].itemList.itemDescription").value("Description1"))
                .andDo(document("GetInvoices", responseFields(
                        fieldWithPath("[0].invoiceName").description("Invoice1")
                        , fieldWithPath("[0].companyName").description("Company1")
                        , fieldWithPath("[0].invoiceDate").description("2021-05-08")
                        , fieldWithPath("[0].itemList.itemDescription").description("Description1")
                )));
    }

    @Test
    public void addItem() throws Exception {
        InvoiceDto invoiceDto = new InvoiceDto("Invoice1", "Company1", "2021-05-08");
        ItemDto itemListDTo = new ItemDto("Description1", 10, FeeType.RATEBASED, 20.10, invoiceDto);
        mockMvc.perform(post("/invoices/Invoice1/item")
                .content(objectMapper.writeValueAsString(itemListDTo))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andDo(document("AddItem"));

    }
}
