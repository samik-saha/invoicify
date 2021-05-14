package com.chaos.invoicify.Invoices;

import com.chaos.invoicify.dto.CompanyDto;
import com.chaos.invoicify.dto.ItemDto;
import com.chaos.invoicify.dto.InvoiceDto;
import com.chaos.invoicify.helper.FeeType;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.tomcat.jni.Local;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.test.web.servlet.MockMvc;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
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

    private CompanyDto companyDto;

    @BeforeEach
    public void setup() throws Exception {
        companyDto =
            new CompanyDto("Company1", "Address 123", "Samik", "Account Payable", "467-790-0128");

        mockMvc
            .perform(
                RestDocumentationRequestBuilders.post("/company")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(companyDto)))
            .andExpect(jsonPath("$.status").value("Created"))
            .andExpect(jsonPath("$.status_code").value(201))
            .andExpect(jsonPath("$.data").value("Company created successfully!"));
    }

    @Test
    public void AddInvoicesWithNoItems() throws Exception {
        InvoiceDto invoiceDto = new InvoiceDto("Company1");

        mockMvc.perform(post("/invoices")
            .content(objectMapper.writeValueAsString(invoiceDto))
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.status").value(HttpStatus.CREATED.getReasonPhrase()))
            .andExpect(jsonPath("$.status_code").value(HttpStatus.CREATED.value()))
            .andExpect(jsonPath("$.data.invoiceNumber").isNumber())
            .andDo(document("AddInvoices"));
    }

    @Test
    public void GetInvoiceWithNoItems() throws Exception {

        InvoiceDto invoiceDto = new InvoiceDto("Company1");

        mockMvc.perform(post("/invoices")
            .content(objectMapper.writeValueAsString(invoiceDto))
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.status").value(HttpStatus.CREATED.getReasonPhrase()))
            .andExpect(jsonPath("$.status_code").value(HttpStatus.CREATED.value()))
            .andExpect(jsonPath("$.data.invoiceNumber").isNumber())
        ;

        mockMvc.perform(get("/invoices"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("length()").value(1))
            .andExpect(jsonPath("[0].invoiceNumber").isNumber())
            .andExpect(jsonPath("[0].companyName").value("Company1"))
            .andExpect(jsonPath("[0].createDate").isNotEmpty())
            .andExpect(jsonPath("[0].modifiedDate").isNotEmpty())
            .andExpect(jsonPath("[0].itemDtoList").isEmpty())
            .andDo(document("GetInvoices", responseFields(
                fieldWithPath("[0].invoiceNumber").description("Invoice Number")
                , fieldWithPath("[0].companyName").description("Company Name")
                , fieldWithPath("[0].createDate").description("Invoice Creation Date")
                , fieldWithPath("[0].modifiedDate").description("Invoice Last Modified Date")
                , fieldWithPath("[0].itemDtoList").description("List of Items")
            )));
    }

    @Test
    public void AddInvoicesWithOneItems() throws Exception {

        ItemDto itemDTo = new ItemDto("Item1", 10, FeeType.RATEBASED, 20.10);
        List<ItemDto> itemDtoList = Arrays.asList(itemDTo);

        InvoiceDto invoiceDto = new InvoiceDto("Company1",itemDtoList);

        mockMvc.perform(post("/invoices")
            .content(objectMapper.writeValueAsString(invoiceDto))
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.status").value(HttpStatus.CREATED.getReasonPhrase()))
            .andExpect(jsonPath("$.status_code").value(HttpStatus.CREATED.value()))
            .andExpect(jsonPath("$.data.invoiceNumber").isNumber())
            .andExpect(jsonPath("$.data.itemDtoList").isNotEmpty())
            .andDo(document("AddInvoices"));
    }



    @Test
    public void getInvoiceWithOneItem() throws Exception {
        ItemDto itemDTo = new ItemDto("Item1", 10, FeeType.RATEBASED, 20.10);
        List<ItemDto> itemDtoList = Arrays.asList(itemDTo);

        InvoiceDto invoiceDto = new InvoiceDto("Company1",itemDtoList);

        mockMvc.perform(post("/invoices")
            .content(objectMapper.writeValueAsString(invoiceDto))
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.status").value(HttpStatus.CREATED.getReasonPhrase()))
            .andExpect(jsonPath("$.status_code").value(HttpStatus.CREATED.value()))
            .andExpect(jsonPath("$.data.invoiceNumber").isNumber())
            .andExpect(jsonPath("$.data.itemDtoList").isNotEmpty())
            ;

        mockMvc.perform(get("/invoices"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("length()").value(1))
            .andExpect(jsonPath("[0].invoiceNumber").isNumber())
            .andExpect(jsonPath("[0].companyName").value("Company1"))
            .andExpect(jsonPath("[0].createDate").isNotEmpty())
            .andExpect(jsonPath("[0].modifiedDate").isNotEmpty())
            .andExpect(jsonPath("[0].itemDtoList.length()").value(1))
            .andDo(document("GetInvoices", responseFields(
                fieldWithPath("[0].invoiceNumber").description("Invoice Number")
                , fieldWithPath("[0].companyName").description("Company Name")
                , fieldWithPath("[0].createDate").description("Invoice Creation Date")
                , fieldWithPath("[0].modifiedDate").description("Invoice Last Modified Date")
                , fieldWithPath("[0].itemDtoList").description("List of Items")
                , fieldWithPath("[0].itemDtoList[0].itemDescription").description("Item Description")
                , fieldWithPath("[0].itemDtoList[0].itemCount").description("Items Count")
                , fieldWithPath("[0].itemDtoList[0].itemFeeType").description("Fee Type")
                , fieldWithPath("[0].itemDtoList[0].itemUnitPrice").description("Unit Price")
            )));
    }

    @Test
    public void getAllInvoiceWithMultipleItems() throws Exception {
        CompanyDto companyDto =
            new CompanyDto("Company1", "Address 123", "Samik", "Account Payable", "467-790-0128");

        InvoiceDto invoiceDto = new InvoiceDto("Company1");

        mockMvc.perform(post("/invoices")
            .content(objectMapper.writeValueAsString(invoiceDto))
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.invoiceName").value("Invoice1"))
            .andExpect(jsonPath("$.companyDto.name").value("Company1"))
            .andExpect(jsonPath("$.companyDto.address").value("Address 123"))
            .andExpect(jsonPath("$.companyDto.contactName").value("Samik"))
            .andExpect(jsonPath("$.companyDto.contactTitle").value("Account Payable"))
            .andExpect(jsonPath("$.companyDto.contactPhoneNumber").value("467-790-0128"))
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
            .andExpect(jsonPath("[0].companyDto.name").value("Company1"))
            .andExpect(jsonPath("[0].companyDto.address").value("Address 123"))
            .andExpect(jsonPath("[0].companyDto.contactName").value("Samik"))
            .andExpect(jsonPath("[0].companyDto.contactTitle").value("Account Payable"))
            .andExpect(jsonPath("[0].companyDto.contactPhoneNumber").value("467-790-0128"))
            .andExpect(jsonPath("[0].invoiceDate").value("2021-05-08"))
            .andExpect(jsonPath("[0].itemDtoList[0].itemDescription").value("Item1"))
            .andExpect(jsonPath("[0].itemDtoList[0].itemCount").value(10))
            .andExpect(jsonPath("[0].itemDtoList[0].itemFeeType").value(FeeType.RATEBASED.name()))
            .andExpect(jsonPath("[0].itemDtoList[0].itemUnitPrice").value(20.10))
            .andDo(document("GetInvoices", responseFields(
                fieldWithPath("[0].invoiceName").description("Invoice1")
                , fieldWithPath("[0].companyDto.name").description("Company1")
                , fieldWithPath("[0].companyDto.address").description("Address 123")
                , fieldWithPath("[0].companyDto.contactName").description("Samik")
                , fieldWithPath("[0].companyDto.contactTitle").description("Account Payable")
                , fieldWithPath("[0].companyDto.contactPhoneNumber").description("467-790-0128")
                , fieldWithPath("[0].invoiceDate").description("2021-05-08")
                , fieldWithPath("[0].itemDtoList[0].itemDescription").description("Item1")
                , fieldWithPath("[0].itemDtoList[0].itemCount").description(10)
                , fieldWithPath("[0].itemDtoList[0].itemFeeType").description(FeeType.RATEBASED.name())
                , fieldWithPath("[0].itemDtoList[0].itemUnitPrice").description(20.10)
            )));
    }

}
