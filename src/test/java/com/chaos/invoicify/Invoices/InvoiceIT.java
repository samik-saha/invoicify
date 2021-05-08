package com.chaos.invoicify.Invoices;

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
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Test
    public void addInvoice() throws Exception {

        InvoiceDto invoiceDto = new InvoiceDto("Invoice1", "Company1", "2021-05-08", "Item1");

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
            .andExpect(jsonPath("[0].itemsList").value("Item1"))
            .andDo(document("GetInvoices", responseFields(
                fieldWithPath("[0].invoiceName").description("Invoice1")
                ,fieldWithPath("[0].companyName").description("Company1")
                ,fieldWithPath("[0].invoiceDate").description("2021-05-08")
                ,fieldWithPath("[0].itemsList").description("Item1")
            )));
    }
}
