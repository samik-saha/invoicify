package com.chaos.invoicify.Invoices;

import com.chaos.invoicify.Item.ItemDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import javax.transaction.Transactional;
import java.util.Arrays;
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
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Test
    public void getAllInvoicesWithNoItems() throws Exception{
        InvoiceDto invoiceDto = new InvoiceDto("Invoice1", "Company1", "2021-05-08",null);

        mockMvc.perform(post("/invoices")
            .content(objectMapper.writeValueAsString(invoiceDto))
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.invoiceName").value("Invoice1"))
            .andExpect(jsonPath("$.companyName").value("Company1"))
            .andExpect(jsonPath("$.invoiceDate").value("2021-05-08"))
            .andDo(document("AddInvoices"));

        mockMvc.perform(get("/invoices"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("length()").value(1))
            .andExpect(jsonPath("[0].invoiceName").value("Invoice1"))
            .andExpect(jsonPath("[0].companyName").value("Company1"))
            .andExpect(jsonPath("[0].invoiceDate").value("2021-05-08"))
            .andExpect(jsonPath("[0].itemDtoList").isEmpty())
            .andDo(document("GetInvoices", responseFields(
                fieldWithPath("[0].invoiceName").description("Invoice1")
                , fieldWithPath("[0].companyName").description("Company1")
                , fieldWithPath("[0].invoiceDate").description("2021-05-08")
                ,fieldWithPath("[0].itemDtoList").description("[]")
            )));
    }
    @Test
    public void getAllInvoiceWithOneItem() throws Exception{
        InvoiceDto invoiceDto = new InvoiceDto("Invoice1", "Company1", "2021-05-08",null);

        mockMvc.perform(post("/invoices")
            .content(objectMapper.writeValueAsString(invoiceDto))
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.invoiceName").value("Invoice1"))
            .andExpect(jsonPath("$.companyName").value("Company1"))
            .andExpect(jsonPath("$.invoiceDate").value("2021-05-08"))
            .andDo(document("AddInvoices"));

        ItemDto itemDTo = new ItemDto("Item1", 10, FeeType.RATEBASED, 20.10);

        mockMvc.perform(post("/invoices/Invoice1/item")
            .content(objectMapper.writeValueAsString(itemDTo))
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.itemDescription").value("Item1"))
            .andExpect(jsonPath("$.itemCount").value(10))
            .andExpect(jsonPath("$.itemFeeType").value(FeeType.RATEBASED.name()))
            .andExpect(jsonPath("$.itemUnitPrice").value(20.10))
            .andDo(document("AddItem"));

        mockMvc.perform(get("/invoices"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("length()").value(1))
            .andExpect(jsonPath("[0].invoiceName").value("Invoice1"))
            .andExpect(jsonPath("[0].companyName").value("Company1"))
            .andExpect(jsonPath("[0].invoiceDate").value("2021-05-08"))
            .andExpect(jsonPath("[0].itemDtoList[0].itemDescription").value("Item1"))
            .andExpect(jsonPath("[0].itemDtoList[0].itemCount").value(10))
            .andExpect(jsonPath("[0].itemDtoList[0].itemFeeType").value(FeeType.RATEBASED.name()))
            .andExpect(jsonPath("[0].itemDtoList[0].itemUnitPrice").value(20.10))
            .andDo(document("GetInvoices", responseFields(
                fieldWithPath("[0].invoiceName").description("Invoice1")
                , fieldWithPath("[0].companyName").description("Company1")
                , fieldWithPath("[0].invoiceDate").description("2021-05-08")
                ,fieldWithPath("[0].itemDtoList[0].itemDescription").description("Item1")
                ,fieldWithPath("[0].itemDtoList[0].itemCount").description(10)
                ,fieldWithPath("[0].itemDtoList[0].itemFeeType").description(FeeType.RATEBASED.name())
                ,fieldWithPath("[0].itemDtoList[0].itemUnitPrice").description(20.10)
            )));
    }

    @Test
    public void getAllInvoiceWithMultipleItems() throws Exception{
        InvoiceDto invoiceDto = new InvoiceDto("Invoice1", "Company1", "2021-05-08",null);

        mockMvc.perform(post("/invoices")
            .content(objectMapper.writeValueAsString(invoiceDto))
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.invoiceName").value("Invoice1"))
            .andExpect(jsonPath("$.companyName").value("Company1"))
            .andExpect(jsonPath("$.invoiceDate").value("2021-05-08"))
            .andDo(document("AddInvoices"));

        ItemDto itemDTo1 = new ItemDto("Item1", 10, FeeType.RATEBASED, 20.10);
        ItemDto itemDTo2 = new ItemDto("Item2", 1, FeeType.FLATFEES, 250.0);
        ItemDto itemDTo3 = new ItemDto("Item3", 10, FeeType.RATEBASED, 30.10);

        List<ItemDto> itemDtoList = Arrays.asList(itemDTo1,itemDTo2,itemDTo3);

        mockMvc.perform(post("/invoices/Invoice1/items")
            .content(objectMapper.writeValueAsString(itemDtoList))
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("[0].itemDescription").value("Item1"))
            .andExpect(jsonPath("[0].itemCount").value(10))
            .andExpect(jsonPath("[0].itemFeeType").value(FeeType.RATEBASED.name()))
            .andExpect(jsonPath("[0].itemUnitPrice").value(20.10))
            .andExpect(jsonPath("[1].itemDescription").value("Item2"))
            .andDo(document("AddItem"));

        mockMvc.perform(get("/invoices"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("length()").value(1))
            .andExpect(jsonPath("[0].invoiceName").value("Invoice1"))
            .andExpect(jsonPath("[0].companyName").value("Company1"))
            .andExpect(jsonPath("[0].invoiceDate").value("2021-05-08"))
            .andExpect(jsonPath("[0].itemDtoList[0].itemDescription").value("Item1"))
            .andExpect(jsonPath("[0].itemDtoList[0].itemCount").value(10))
            .andExpect(jsonPath("[0].itemDtoList[0].itemFeeType").value(FeeType.RATEBASED.name()))
            .andExpect(jsonPath("[0].itemDtoList[0].itemUnitPrice").value(20.10))
            .andDo(document("GetInvoices", responseFields(
                fieldWithPath("[0].invoiceName").description("Invoice1")
                , fieldWithPath("[0].companyName").description("Company1")
                , fieldWithPath("[0].invoiceDate").description("2021-05-08")
                ,fieldWithPath("[0].itemDtoList[0].itemDescription").description("Item1")
                ,fieldWithPath("[0].itemDtoList[0].itemCount").description(10)
                ,fieldWithPath("[0].itemDtoList[0].itemFeeType").description(FeeType.RATEBASED.name())
                ,fieldWithPath("[0].itemDtoList[0].itemUnitPrice").description(20.10)
            )));
    }

}
