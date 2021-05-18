package com.chaos.invoicify;

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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
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
    private CompanyDto companyDto;

    @BeforeEach
    public void setup() throws Exception {
        address1 = new Address("123 ABC Street", "Toronto", "ON", "Canada", "A1B 2D3");
        address2 = new Address("456 ABC Street", "Cary", "NC", "US", "12345");
        companyDto =
                new CompanyDto("Company1", address1, "Samik", "Account Payable", "467-790-0128");

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
                .andExpect(jsonPath("[0].items").isEmpty())
                .andExpect(jsonPath("[0].totalInvoiceValue").value(0.00))
                .andDo(document("GetInvoices", responseFields(
                        fieldWithPath("[0].invoiceNumber").description("Invoice Number")
                        , fieldWithPath("[0].companyName").description("Company Name")
                        , fieldWithPath("[0].createDate").description("Invoice Creation Date")
                        , fieldWithPath("[0].modifiedDate").description("Invoice Last Modified Date")
                        , fieldWithPath("[0].items").description("List of Items")
                        , fieldWithPath("[0].totalInvoiceValue").description("Total Invoice Value")
                )));
    }

    @Test
    public void AddInvoiceWithOneItems() throws Exception {

        ItemDto itemDTo = new ItemDto("Item1", 10, FeeType.RATEBASED, 20.10, null);
        List<ItemDto> items = Arrays.asList(itemDTo);

        InvoiceDto invoiceDto = new InvoiceDto("Company1", items);

        mockMvc.perform(post("/invoices")
                .content(objectMapper.writeValueAsString(invoiceDto))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value(HttpStatus.CREATED.getReasonPhrase()))
                .andExpect(jsonPath("$.status_code").value(HttpStatus.CREATED.value()))
                .andExpect(jsonPath("$.data.invoiceNumber").isNumber())
                .andExpect(jsonPath("$.data.items").isNotEmpty())
                .andDo(document("AddInvoices"));
    }


    @Test
    public void getInvoiceWithOneItem() throws Exception {
        ItemDto itemDTo = new ItemDto("Item1", 10, FeeType.RATEBASED, 20.10, null);
        List<ItemDto> items = Arrays.asList(itemDTo);

        InvoiceDto invoiceDto = new InvoiceDto("Company1", items);

        mockMvc.perform(post("/invoices")
                .content(objectMapper.writeValueAsString(invoiceDto))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value(HttpStatus.CREATED.getReasonPhrase()))
                .andExpect(jsonPath("$.status_code").value(HttpStatus.CREATED.value()))
                .andExpect(jsonPath("$.data.invoiceNumber").isNumber())
                .andExpect(jsonPath("$.data.items").isNotEmpty())
        ;

        mockMvc.perform(get("/invoices"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("length()").value(1))
                .andExpect(jsonPath("[0].invoiceNumber").isNumber())
                .andExpect(jsonPath("[0].companyName").value("Company1"))
                .andExpect(jsonPath("[0].createDate").isNotEmpty())
                .andExpect(jsonPath("[0].modifiedDate").isNotEmpty())
                .andExpect(jsonPath("[0].items.length()").value(1))
                .andExpect(jsonPath("[0].items[0].totalItemValue").value(201.00))
                .andExpect(jsonPath("[0].totalInvoiceValue").value(201.00))
                .andDo(document("GetInvoices", responseFields(
                        fieldWithPath("[0].invoiceNumber").description("Invoice Number")
                        , fieldWithPath("[0].companyName").description("Company Name")
                        , fieldWithPath("[0].createDate").description("Invoice Creation Date")
                        , fieldWithPath("[0].modifiedDate").description("Invoice Last Modified Date")
                        , fieldWithPath("[0].items").description("List of Items")
                        , fieldWithPath("[0].items[0].itemDescription").description("Item Description")
                        , fieldWithPath("[0].items[0].itemCount").description("Items Count")
                        , fieldWithPath("[0].items[0].itemFeeType").description("Fee Type")
                        , fieldWithPath("[0].items[0].itemUnitPrice").description("Unit Price")
                        , fieldWithPath("[0].items[0].totalItemValue").description("Total Item Value")
                        , fieldWithPath("[0].totalInvoiceValue").description("Total Invoice Value")
                )));
    }

    @Test
    public void AddInvoiceWithMultipleItems() throws Exception {

        ItemDto itemDTo1 = new ItemDto("Item1", 10, FeeType.RATEBASED, 20.10, null);
        ItemDto itemDTo2 = new ItemDto("Item2", 1, FeeType.FLATFEES, 250.0, null);
        ItemDto itemDTo3 = new ItemDto("Item3", 10, FeeType.RATEBASED, 30.10, null);

        List<ItemDto> items = Arrays.asList(itemDTo1, itemDTo2, itemDTo3);

        InvoiceDto invoiceDto = new InvoiceDto("Company1", items);

        mockMvc.perform(post("/invoices")
                .content(objectMapper.writeValueAsString(invoiceDto))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value(HttpStatus.CREATED.getReasonPhrase()))
                .andExpect(jsonPath("$.status_code").value(HttpStatus.CREATED.value()))
                .andExpect(jsonPath("$.data.invoiceNumber").isNumber())
                .andExpect(jsonPath("$.data.items").isNotEmpty())
                .andDo(document("AddInvoices"));
    }

    @Test
    public void getInvoiceWithMultipleItems() throws Exception {
        ItemDto itemDTo1 = new ItemDto("Item1", 10, FeeType.RATEBASED, 20.10, null);
        ItemDto itemDTo2 = new ItemDto("Item2", 1, FeeType.FLATFEES, 250.0, null);
        ItemDto itemDTo3 = new ItemDto("Item3", 10, FeeType.RATEBASED, 30.10, null);

        List<ItemDto> items = Arrays.asList(itemDTo1, itemDTo2, itemDTo3);

        InvoiceDto invoiceDto = new InvoiceDto("Company1", items);

        mockMvc.perform(post("/invoices")
                .content(objectMapper.writeValueAsString(invoiceDto))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value(HttpStatus.CREATED.getReasonPhrase()))
                .andExpect(jsonPath("$.status_code").value(HttpStatus.CREATED.value()))
                .andExpect(jsonPath("$.data.invoiceNumber").isNumber())
                .andExpect(jsonPath("$.data.items").isNotEmpty())
        ;

        mockMvc.perform(get("/invoices"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("length()").value(1))
                .andExpect(jsonPath("[0].invoiceNumber").isNumber())
                .andExpect(jsonPath("[0].companyName").value("Company1"))
                .andExpect(jsonPath("[0].createDate").isNotEmpty())
                .andExpect(jsonPath("[0].modifiedDate").isNotEmpty())
                .andExpect(jsonPath("[0].items.length()").value(3))
                .andExpect(jsonPath("[0].items[0].itemDescription").value("Item1"))
                .andExpect(jsonPath("[0].items[0].itemCount").value(10))
                .andExpect(jsonPath("[0].items[0].itemFeeType").value(FeeType.RATEBASED.name()))
                .andExpect(jsonPath("[0].items[0].itemUnitPrice").value(20.10))
                .andExpect(jsonPath("[0].items[0].totalItemValue").value(201.00))
                .andExpect(jsonPath("[0].totalInvoiceValue").value(752.00))
                .andDo(document("GetInvoices", responseFields(
                        fieldWithPath("[0].invoiceNumber").description("Invoice Number")
                        , fieldWithPath("[0].companyName").description("Company Name")
                        , fieldWithPath("[0].createDate").description("Invoice Creation Date")
                        , fieldWithPath("[0].modifiedDate").description("Invoice Last Modified Date")
                        , fieldWithPath("[0].items").description("List of Items")
                        , fieldWithPath("[0].items[0].itemDescription").description("Item Description")
                        , fieldWithPath("[0].items[0].itemCount").description("Items Count")
                        , fieldWithPath("[0].items[0].itemFeeType").description("Fee Type")
                        , fieldWithPath("[0].items[0].itemUnitPrice").description("Unit Price")
                        , fieldWithPath("[0].items[0].totalItemValue").description("Total Item Value")
                        , fieldWithPath("[0].totalInvoiceValue").description("Total Invoice Value")
                )));
    }

    @Test
    public void searchInvoiceById() throws Exception {
        ItemDto itemDTo = new ItemDto("Item1", 10, FeeType.RATEBASED, 20.10, null);
        List<ItemDto> items = Arrays.asList(itemDTo);

        InvoiceDto invoiceDto = new InvoiceDto("Company1", items);

        MvcResult mvcResult = mockMvc.perform(post("/invoices")
                .content(objectMapper.writeValueAsString(invoiceDto))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value(HttpStatus.CREATED.getReasonPhrase()))
                .andExpect(jsonPath("$.status_code").value(HttpStatus.CREATED.value()))
                .andExpect(jsonPath("$.data.invoiceNumber").isNumber())
                .andExpect(jsonPath("$.data.items").isNotEmpty())
                .andReturn();

        Integer id = JsonPath.read(mvcResult.getResponse().getContentAsString(), "$.data.invoiceNumber");

        mockMvc.perform(get("/invoices/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("length()").value(1))
                .andExpect(jsonPath("[0].invoiceNumber").isNumber())
                .andExpect(jsonPath("[0].companyName").value("Company1"))
                .andExpect(jsonPath("[0].createDate").isNotEmpty())
                .andExpect(jsonPath("[0].modifiedDate").isNotEmpty())
                .andExpect(jsonPath("[0].items.length()").value(1))
                .andExpect(jsonPath("[0].totalInvoiceValue").value(201.00))
                .andDo(document("SearchInvoiceById", responseFields(
                        fieldWithPath("[0].invoiceNumber").description("Invoice Number")
                        , fieldWithPath("[0].companyName").description("Company Name")
                        , fieldWithPath("[0].createDate").description("Invoice Creation Date")
                        , fieldWithPath("[0].modifiedDate").description("Invoice Last Modified Date")
                        , fieldWithPath("[0].items").description("List of Items")
                        , fieldWithPath("[0].items[0].itemDescription").description("Item Description")
                        , fieldWithPath("[0].items[0].itemCount").description("Items Count")
                        , fieldWithPath("[0].items[0].itemFeeType").description("Fee Type")
                        , fieldWithPath("[0].items[0].itemUnitPrice").description("Unit Price")
                        , fieldWithPath("[0].items[0].totalItemValue").description("Total Item Value")
                        , fieldWithPath("[0].totalInvoiceValue").description("Total Invoice Value")
                )));

    }

    @Test
    public void addItemsToInvoice() throws Exception {
        ItemDto itemDTo = new ItemDto("Item", 10, FeeType.RATEBASED, 50.10, null);
        List<ItemDto> items = Arrays.asList(itemDTo);

        InvoiceDto invoiceDto = new InvoiceDto("Company1", items);

        MvcResult mvcResult = mockMvc.perform(post("/invoices")
                .content(objectMapper.writeValueAsString(invoiceDto))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value(HttpStatus.CREATED.getReasonPhrase()))
                .andExpect(jsonPath("$.status_code").value(HttpStatus.CREATED.value()))
                .andExpect(jsonPath("$.data.invoiceNumber").isNumber())
                .andExpect(jsonPath("$.data.items").isNotEmpty())
                .andReturn();

        Integer id = JsonPath.read(mvcResult.getResponse().getContentAsString(), "$.data.invoiceNumber");

        ItemDto itemDTo1 = new ItemDto("Item1", 10, FeeType.RATEBASED, 20.10, null);
        ItemDto itemDTo2 = new ItemDto("Item2", 1, FeeType.FLATFEES, 250.0, null);
        ItemDto itemDTo3 = new ItemDto("Item3", 10, FeeType.RATEBASED, 30.10, null);

        List<ItemDto> itemsNew = Arrays.asList(itemDTo1, itemDTo2, itemDTo3);

        mockMvc.perform(post("/invoices/{id}/items", id)
                .content(objectMapper.writeValueAsString(itemsNew))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(HttpStatus.CREATED.getReasonPhrase()))
                .andExpect(jsonPath("$.status_code").value(HttpStatus.CREATED.value()))
                .andExpect(jsonPath("$.data.invoiceNumber").isNumber())
                .andExpect(jsonPath("$.data.items").isNotEmpty())
                .andDo(document("AddItems"));
    }

    @Test
    public void deleteInvoiceById() throws Exception {
        ItemDto itemDTo = new ItemDto("Item1", 10, FeeType.RATEBASED, 20.10, null);
        List<ItemDto> items = Arrays.asList(itemDTo);

        InvoiceDto invoiceDto = new InvoiceDto("Company1", items);

        MvcResult mvcResult = mockMvc.perform(post("/invoices")
            .content(objectMapper.writeValueAsString(invoiceDto))
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.status").value(HttpStatus.CREATED.getReasonPhrase()))
            .andExpect(jsonPath("$.status_code").value(HttpStatus.CREATED.value()))
            .andExpect(jsonPath("$.data.invoiceNumber").isNumber())
            .andExpect(jsonPath("$.data.items").isNotEmpty())
            .andReturn();

        Integer id = JsonPath.read(mvcResult.getResponse().getContentAsString(), "$.data.invoiceNumber");

        mockMvc.perform(delete("/invoices/{id}", id))
            .andExpect(status().isOk())
            .andDo(document("DeleteInvoice"));


        mockMvc.perform(get("/invoices/{id}", id))
            .andExpect(status().isOk())
            .andExpect(jsonPath("length()").value(0));

    }

}
