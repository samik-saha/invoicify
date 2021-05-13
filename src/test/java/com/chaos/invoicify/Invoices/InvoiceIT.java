package com.chaos.invoicify.Invoices;

import com.chaos.invoicify.dto.CompanyDto;
import com.chaos.invoicify.dto.ItemDto;
import com.chaos.invoicify.dto.InvoiceDto;
import com.chaos.invoicify.helper.FeeType;
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
    public void getAllInvoicesWithNoItems() throws Exception {
        CompanyDto companyDto =
                new CompanyDto("Company5877877", "Address 123", "Samik", "Account Payable", "467-790-0128");

        InvoiceDto invoiceDto = new InvoiceDto("Invoice1", companyDto, "2021-05-08", null);

        mockMvc.perform(post("/invoices")
                .content(objectMapper.writeValueAsString(invoiceDto))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.invoiceName").value("Invoice1"))
                .andExpect(jsonPath("$.companyDto.name").value("Company2"))
                .andExpect(jsonPath("$.companyDto.address").value("Address 123"))
                .andExpect(jsonPath("$.companyDto.contactName").value("Samik"))
                .andExpect(jsonPath("$.companyDto.contactTitle").value("Account Payable"))
                .andExpect(jsonPath("$.companyDto.contactPhoneNumber").value("467-790-0128"))
                .andExpect(jsonPath("$.invoiceDate").value("2021-05-08"))
                .andDo(document("AddInvoices"));

        mockMvc.perform(get("/invoices"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("length()").value(1))
                .andExpect(jsonPath("[0].invoiceName").value("Invoice1"))
                .andExpect(jsonPath("[0].companyDto.name").value("Company2"))
                .andExpect(jsonPath("[0].companyDto.address").value("Address 123"))
                .andExpect(jsonPath("[0].companyDto.contactName").value("Samik"))
                .andExpect(jsonPath("[0].companyDto.contactTitle").value("Account Payable"))
                .andExpect(jsonPath("[0].companyDto.contactPhoneNumber").value("467-790-0128"))
                .andExpect(jsonPath("[0].invoiceDate").value("2021-05-08"))
                .andExpect(jsonPath("[0].itemDtoList").isEmpty())
                .andDo(document("GetInvoices", responseFields(
                        fieldWithPath("[0].invoiceName").description("Invoice1")
                        , fieldWithPath("[0].companyDto.name").description("Company2")
                        , fieldWithPath("[0].companyDto.address").description("Address 123")
                        , fieldWithPath("[0].companyDto.contactName").description("Samik")
                        , fieldWithPath("[0].companyDto.contactTitle").description("Account Payable")
                        , fieldWithPath("[0].companyDto.contactPhoneNumber").description("467-790-0128")
                        , fieldWithPath("[0].invoiceDate").description("2021-05-08")
                        , fieldWithPath("[0].itemDtoList").description("[]")
                )));
    }

    @Test
    public void getAllInvoiceWithOneItem() throws Exception {
        CompanyDto companyDto =
                new CompanyDto("Company1", "Address 123", "Samik", "Account Payable", "467-790-0128");

        InvoiceDto invoiceDto = new InvoiceDto("Invoice1", companyDto, "2021-05-08", null);

        mockMvc.perform(post("/invoices")
                .content(objectMapper.writeValueAsString(invoiceDto))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.invoiceName").value("Invoice1"))
                .andExpect(jsonPath("$.companyName").value("Company1"))
                .andExpect(jsonPath("$.invoiceDate").value("2021-05-08"))
                .andDo(document("AddInvoices"));

        ItemDto itemDTo = new ItemDto("Item1", 10, FeeType.RATEBASED, 20.10);
        List<ItemDto> itemDtoList = Arrays.asList(itemDTo);

        mockMvc.perform(post("/invoices/Invoice1/items")
                .content(objectMapper.writeValueAsString(itemDtoList))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("[0].itemDescription").value("Item1"))
                .andExpect(jsonPath("[0].itemCount").value(10))
                .andExpect(jsonPath("[0].itemFeeType").value(FeeType.RATEBASED.name()))
                .andExpect(jsonPath("[0].itemUnitPrice").value(20.10))
                .andDo(document("AddItems"));

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
                        , fieldWithPath("[0].itemDtoList[0].itemDescription").description("Item1")
                        , fieldWithPath("[0].itemDtoList[0].itemCount").description(10)
                        , fieldWithPath("[0].itemDtoList[0].itemFeeType").description(FeeType.RATEBASED.name())
                        , fieldWithPath("[0].itemDtoList[0].itemUnitPrice").description(20.10)
                )));
    }

    @Test
    public void getAllInvoiceWithMultipleItems() throws Exception {
        CompanyDto companyDto =
                new CompanyDto("Company1", "Address 123", "Samik", "Account Payable", "467-790-0128");

        InvoiceDto invoiceDto = new InvoiceDto("Invoice1", companyDto, "2021-05-08", null);

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

        List<ItemDto> itemDtoList = Arrays.asList(itemDTo1, itemDTo2, itemDTo3);

        mockMvc.perform(post("/invoices/Invoice1/items")
                .content(objectMapper.writeValueAsString(itemDtoList))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("[0].itemDescription").value("Item1"))
                .andExpect(jsonPath("[0].itemCount").value(10))
                .andExpect(jsonPath("[0].itemFeeType").value(FeeType.RATEBASED.name()))
                .andExpect(jsonPath("[0].itemUnitPrice").value(20.10))
                .andExpect(jsonPath("[1].itemDescription").value("Item2"))
                .andDo(document("AddItems"));

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
                        , fieldWithPath("[0].itemDtoList[0].itemDescription").description("Item1")
                        , fieldWithPath("[0].itemDtoList[0].itemCount").description(10)
                        , fieldWithPath("[0].itemDtoList[0].itemFeeType").description(FeeType.RATEBASED.name())
                        , fieldWithPath("[0].itemDtoList[0].itemUnitPrice").description(20.10)
                )));
    }

}
