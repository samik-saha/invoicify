package com.chaos.invoicify.Invoices;

import com.chaos.invoicify.dto.CompanyDto;
import com.chaos.invoicify.dto.ItemDto;
import com.chaos.invoicify.dto.InvoiceDto;
import com.chaos.invoicify.helper.Address;
import com.chaos.invoicify.helper.FeeType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
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
import org.springframework.test.web.servlet.MvcResult;

import javax.transaction.Transactional;
import java.util.Arrays;
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

    Address address1, address2;
    private CompanyDto companyDto, companyDto2;

    @BeforeEach
    public void setup() throws Exception {
        address1 = new Address("123 ABC Street", "Toronto", "ON", "Canada", "A1B 2D3");
        address2 = new Address("456 ABC Street", "Cary", "NC", "US", "12345");
        companyDto =
            new CompanyDto("Company1", address1, "Samik", "Account Payable", "467-790-0128");
        companyDto2 =
            new CompanyDto("Company2", address2, "Rajendra", "Account Payable", "123-456-7890");

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
    public void AddInvoiceWithOneItems() throws Exception {

        ItemDto itemDTo = new ItemDto("Item1", 10, FeeType.RATEBASED, 20.10);
        List<ItemDto> itemDtoList = Arrays.asList(itemDTo);

        InvoiceDto invoiceDto = new InvoiceDto("Company1", itemDtoList);

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

        InvoiceDto invoiceDto = new InvoiceDto("Company1", itemDtoList);

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
    public void AddInvoiceWithMultipleItems() throws Exception {

        ItemDto itemDTo1 = new ItemDto("Item1", 10, FeeType.RATEBASED, 20.10);
        ItemDto itemDTo2 = new ItemDto("Item2", 1, FeeType.FLATFEES, 250.0);
        ItemDto itemDTo3 = new ItemDto("Item3", 10, FeeType.RATEBASED, 30.10);

        List<ItemDto> itemDtoList = Arrays.asList(itemDTo1, itemDTo2, itemDTo3);

        InvoiceDto invoiceDto = new InvoiceDto("Company1", itemDtoList);

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
    public void getInvoiceWithMultipleItems() throws Exception {
        ItemDto itemDTo1 = new ItemDto("Item1", 10, FeeType.RATEBASED, 20.10);
        ItemDto itemDTo2 = new ItemDto("Item2", 1, FeeType.FLATFEES, 250.0);
        ItemDto itemDTo3 = new ItemDto("Item3", 10, FeeType.RATEBASED, 30.10);

        List<ItemDto> itemDtoList = Arrays.asList(itemDTo1, itemDTo2, itemDTo3);

        InvoiceDto invoiceDto = new InvoiceDto("Company1", itemDtoList);

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
            .andExpect(jsonPath("[0].itemDtoList.length()").value(3))
            .andExpect(jsonPath("[0].itemDtoList[0].itemDescription").value("Item1"))
            .andExpect(jsonPath("[0].itemDtoList[0].itemCount").value(10))
            .andExpect(jsonPath("[0].itemDtoList[0].itemFeeType").value(FeeType.RATEBASED.name()))
            .andExpect(jsonPath("[0].itemDtoList[0].itemUnitPrice").value(20.10))
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
    public void searchInvoiceById() throws Exception {
        ItemDto itemDTo = new ItemDto("Item1", 10, FeeType.RATEBASED, 20.10);
        List<ItemDto> itemDtoList = Arrays.asList(itemDTo);

        InvoiceDto invoiceDto = new InvoiceDto("Company1", itemDtoList);

        MvcResult mvcResult = mockMvc.perform(post("/invoices")
            .content(objectMapper.writeValueAsString(invoiceDto))
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.status").value(HttpStatus.CREATED.getReasonPhrase()))
            .andExpect(jsonPath("$.status_code").value(HttpStatus.CREATED.value()))
            .andExpect(jsonPath("$.data.invoiceNumber").isNumber())
            .andExpect(jsonPath("$.data.itemDtoList").isNotEmpty())
            .andReturn();

        Integer id = JsonPath.read(mvcResult.getResponse().getContentAsString(), "$.data.invoiceNumber");

        mockMvc.perform(get("/invoices/{id}", id))
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
    public void addItemsToInvoice() throws Exception {
        ItemDto itemDTo = new ItemDto("Item", 10, FeeType.RATEBASED, 50.10);
        List<ItemDto> itemDtoList = Arrays.asList(itemDTo);

        InvoiceDto invoiceDto = new InvoiceDto("Company1", itemDtoList);

        MvcResult mvcResult = mockMvc.perform(post("/invoices")
            .content(objectMapper.writeValueAsString(invoiceDto))
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.status").value(HttpStatus.CREATED.getReasonPhrase()))
            .andExpect(jsonPath("$.status_code").value(HttpStatus.CREATED.value()))
            .andExpect(jsonPath("$.data.invoiceNumber").isNumber())
            .andExpect(jsonPath("$.data.itemDtoList").isNotEmpty())
            .andReturn();

        Integer id = JsonPath.read(mvcResult.getResponse().getContentAsString(), "$.data.invoiceNumber");

        ItemDto itemDTo1 = new ItemDto("Item1", 10, FeeType.RATEBASED, 20.10);
        ItemDto itemDTo2 = new ItemDto("Item2", 1, FeeType.FLATFEES, 250.0);
        ItemDto itemDTo3 = new ItemDto("Item3", 10, FeeType.RATEBASED, 30.10);

        List<ItemDto> itemDtoListNew = Arrays.asList(itemDTo1, itemDTo2, itemDTo3);

        mockMvc.perform(post("/invoices/{id}/items", id)
            .content(objectMapper.writeValueAsString(itemDtoListNew))
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.status").value(HttpStatus.CREATED.getReasonPhrase()))
            .andExpect(jsonPath("$.status_code").value(HttpStatus.CREATED.value()))
            .andExpect(jsonPath("$.data.invoiceNumber").isNumber())
            .andExpect(jsonPath("$.data.itemDtoList").isNotEmpty())
//            .andExpect(jsonPath("$.data.itemDtoList.length()").value(3))
            .andDo(document("AddItems"));
    }

}
