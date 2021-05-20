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
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.requestParameters;
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
    private CompanyDto companyDto, companyDto2, companyDto3;

    @BeforeEach
    public void setup() throws Exception {
        address1 = new Address("123 ABC Street", "Toronto", "ON", "Canada", "A1B 2D3");
        address2 = new Address("456 ABC Street", "Cary", "NC", "US", "12345");
        companyDto =
            new CompanyDto("Company1", address1, "Samik", "Account Payable", "467-790-0128");
        companyDto2 =
            new CompanyDto("Company2", address1, "Samik", "Account Payable", "467-790-0128");
        companyDto3 =
            new CompanyDto("Company3", address1, "Samik", "Account Payable", "467-790-0128");

        mockMvc
            .perform(
                RestDocumentationRequestBuilders.post("/company")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(companyDto)))
            .andExpect(jsonPath("$.status").value("Created"))
            .andExpect(jsonPath("$.status_code").value(201))
            .andExpect(jsonPath("$.data").value("Company created successfully!"));

        mockMvc
            .perform(
                RestDocumentationRequestBuilders.post("/company")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(companyDto3)))
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
                , fieldWithPath("[0].paid").description("Is Invoice Paid?")
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
            .andDo(document("AddInvoices",
                requestFields(
                    fieldWithPath("invoiceNumber").description("Invoice Number")
                    , fieldWithPath("companyName").description("Company Name")
                    , fieldWithPath("createDate").description("Invoice Creation Date")
                    , fieldWithPath("modifiedDate").description("Invoice Last Modified Date")
                    , fieldWithPath("items").description("List of Items")
                    , fieldWithPath("items[0].itemDescription").description("Item Description")
                    , fieldWithPath("items[0].itemCount").description("Item Count")
                    , fieldWithPath("items[0].itemFeeType").description("Item FeeType")
                    , fieldWithPath("items[0].itemUnitPrice").description("Item Unit Price")
                    , fieldWithPath("items[0].totalItemValue").description("Total Item Value")
                    , fieldWithPath("totalInvoiceValue").description("Total Invoice Value")
                    , fieldWithPath("paid").description("Is Invoice Paid?")
                ),
                responseFields(
                    fieldWithPath("status").description("Status")
                    , fieldWithPath("status_code").description("Status Code")
                    , fieldWithPath("data.invoiceNumber").description("Invoice Number")
                    , fieldWithPath("data.companyName").description("Company Name")
                    , fieldWithPath("data.createDate").description("Invoice Creation Date")
                    , fieldWithPath("data.modifiedDate").description("Invoice Last Modified Date")
                    , fieldWithPath("data.items").description("List of Items")
                    , fieldWithPath("data.items[0].itemDescription").description("Item Description")
                    , fieldWithPath("data.items[0].itemCount").description("Item Count")
                    , fieldWithPath("data.items[0].itemFeeType").description("Item FeeType")
                    , fieldWithPath("data.items[0].itemUnitPrice").description("Item Unit Price")
                    , fieldWithPath("data.items[0].totalItemValue").description("Total Item Value")
                    , fieldWithPath("data.totalInvoiceValue").description("Total Invoice Value")
                    , fieldWithPath("data.paid").description("Is Invoice Paid?")
                )));
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
            .andExpect(jsonPath("[0].paid").value("false"))
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
                , fieldWithPath("[0].paid").description("Is Invoice Paid?")
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
                , fieldWithPath("[0].paid").description("Is Invoice Paid?")
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
                , fieldWithPath("[0].paid").description("Is Invoice Paid?")
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
    public void deleteInvoiceFailedDateCheckAndPaidCheck() throws Exception {
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
            .andExpect(jsonPath("$.status").value(HttpStatus.BAD_REQUEST.getReasonPhrase()))
            .andExpect(jsonPath("$.data").value("Invoice is NOT an year later or Unpaid so cannot be Deleted"))
            .andDo(document("DeleteInvoice"));

        mockMvc.perform(get("/invoices/{id}", id))
            .andExpect(status().isOk())
            .andExpect(jsonPath("length()").value(1));
    }

    @Test
    public void deleteInvoiceFailedPaidCheck() throws Exception {
        ItemDto itemDTo = new ItemDto("Item1", 10, FeeType.RATEBASED, 20.10, null);
        List<ItemDto> items = Arrays.asList(itemDTo);

        InvoiceDto invoiceDto = new InvoiceDto("Company1", LocalDate.of(2020, 01, 01), items);

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
            .andExpect(jsonPath("$.status").value(HttpStatus.BAD_REQUEST.getReasonPhrase()))
            .andExpect(jsonPath("$.data").value("Invoice is NOT an year later or Unpaid so cannot be Deleted"))
            .andDo(document("DeleteInvoice"));

        mockMvc.perform(get("/invoices/{id}", id))
            .andExpect(status().isOk())
            .andExpect(jsonPath("length()").value(1));
    }

    @Test
    public void deleteInvoiceFailedDateCheck() throws Exception {
        ItemDto itemDTo = new ItemDto("Item", 10, FeeType.RATEBASED, 20.10, null);
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
        invoiceDto.setPaid(true);

        mockMvc.perform(post("/invoices/{id}", id)
            .content(objectMapper.writeValueAsString(invoiceDto))
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.status").value(HttpStatus.OK.getReasonPhrase()))
            .andExpect(jsonPath("$.status_code").value(HttpStatus.OK.value()))
            .andExpect(jsonPath("$.data.invoiceNumber").isNumber())
            .andExpect(jsonPath("$.data.items").isNotEmpty());

        mockMvc.perform(delete("/invoices/{id}", id))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.status").value(HttpStatus.BAD_REQUEST.getReasonPhrase()))
            .andExpect(jsonPath("$.data").value("Invoice is NOT an year later or Unpaid so cannot be Deleted"))
            .andDo(document("DeleteInvoice"));

        mockMvc.perform(get("/invoices/{id}", id))
            .andExpect(status().isOk())
            .andExpect(jsonPath("length()").value(1));
    }

    @Test
    public void deleteInvoicePassed() throws Exception {
        ItemDto itemDTo = new ItemDto("Item", 10, FeeType.RATEBASED, 20.10, null);
        List<ItemDto> items = Arrays.asList(itemDTo);

        InvoiceDto invoiceDto = new InvoiceDto("Company1", LocalDate.of(2020, 01, 01), items);

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
        invoiceDto.setPaid(true);

        mockMvc.perform(post("/invoices/{id}", id)
            .content(objectMapper.writeValueAsString(invoiceDto))
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.status").value(HttpStatus.OK.getReasonPhrase()))
            .andExpect(jsonPath("$.status_code").value(HttpStatus.OK.value()))
            .andExpect(jsonPath("$.data.invoiceNumber").isNumber())
            .andExpect(jsonPath("$.data.items").isNotEmpty());

        mockMvc.perform(delete("/invoices/{id}", id))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.status").value(HttpStatus.OK.getReasonPhrase()))
            .andExpect(jsonPath("$.status_code").value(HttpStatus.OK.value()))
            .andExpect(jsonPath("$.data").value("Invoice is Deleted"))
            .andDo(document("DeleteInvoice"));

        mockMvc.perform(get("/invoices/{id}", id))
            .andExpect(status().isOk())
            .andExpect(jsonPath("length()").value(0));
    }

    @Test
    public void getAllInvoicesWithPaginationTest() throws Exception {
        ItemDto itemDTo = new ItemDto("Item1", 10, FeeType.RATEBASED, 20.10, null);
        List<ItemDto> items = Arrays.asList(itemDTo);
        InvoiceDto invoiceDto = new InvoiceDto("Company1", items);
        List<Integer> invoiceIds = new ArrayList<>();

        for (int i = 0; i < 15; i++) {
            MvcResult mvcResult = mockMvc.perform(post("/invoices")
                .content(objectMapper.writeValueAsString(invoiceDto))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andReturn();

            invoiceIds.add(JsonPath.read(mvcResult.getResponse().getContentAsString(), "$.data.invoiceNumber"));
        }

        mockMvc.perform(get("/invoices"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("length()").value(10))
            .andExpect(jsonPath("$[0].invoiceNumber").value(invoiceIds.get(0)))
            .andExpect(jsonPath("$[9].invoiceNumber").value(invoiceIds.get(9)));

        mockMvc.perform(get("/invoices").queryParam("page", "1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("length()").value(10))
            .andExpect(jsonPath("$[0].invoiceNumber").value(invoiceIds.get(0)))
            .andExpect(jsonPath("$[9].invoiceNumber").value(invoiceIds.get(9)));

        mockMvc.perform(get("/invoices").queryParam("page", "2"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("length()").value(5))
            .andExpect(jsonPath("$[0].invoiceNumber").value(invoiceIds.get(10)))
            .andExpect(jsonPath("$[4].invoiceNumber").value(invoiceIds.get(14)));

        mockMvc.perform(get("/invoices")
            .queryParam("page", "1")
            .queryParam("pageSize", "8")
            .queryParam("sortBy", "id"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("length()").value(8))
            .andExpect(jsonPath("$[0].invoiceNumber").value(invoiceIds.get(0)))
            .andExpect(jsonPath("$[7].invoiceNumber").value(invoiceIds.get(7)))
            .andDo(document("Paginated-Invoices",
                requestParameters(
                    parameterWithName("page").description("Page Number"),
                    parameterWithName("pageSize").description("Page Size"),
                    parameterWithName("sortBy").description("Sort By Column"))));

    }

    @Test
    public void markInvoicePaid() throws Exception {
        ItemDto itemDTo = new ItemDto("Item", 10, FeeType.RATEBASED, 20.10, null);
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
        invoiceDto.setPaid(true);

        mockMvc.perform(post("/invoices/{id}", id)
            .content(objectMapper.writeValueAsString(invoiceDto))
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.status").value(HttpStatus.OK.getReasonPhrase()))
            .andExpect(jsonPath("$.status_code").value(HttpStatus.OK.value()))
            .andExpect(jsonPath("$.data.invoiceNumber").isNumber())
            .andExpect(jsonPath("$.data.items").isNotEmpty());

        mockMvc.perform(get("/invoices/{id}", id))
            .andExpect(status().isOk())
            .andExpect(jsonPath("length()").value(1))
            .andExpect(jsonPath("[0].invoiceNumber").isNumber())
            .andExpect(jsonPath("[0].companyName").value("Company1"))
            .andExpect(jsonPath("[0].createDate").isNotEmpty())
            .andExpect(jsonPath("[0].modifiedDate").isNotEmpty())
            .andExpect(jsonPath("[0].items.length()").value(1))
            .andExpect(jsonPath("[0].totalInvoiceValue").value(201.00))
            .andExpect(jsonPath("[0].paid").value(true));

    }

    @Test
    public void updateInvalidCompanyOnInvoice() throws Exception {
        ItemDto itemDTo = new ItemDto("Item", 10, FeeType.RATEBASED, 20.10, null);
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
        invoiceDto.setCompanyName("Company2");

        mockMvc.perform(post("/invoices/{id}", id)
            .content(objectMapper.writeValueAsString(invoiceDto))
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.status").value(HttpStatus.BAD_REQUEST.getReasonPhrase()))
            .andExpect(jsonPath("$.status_code").value(HttpStatus.BAD_REQUEST.value()))
            .andExpect(jsonPath("$.data").value("Invalid Company Name"))
        ;

        mockMvc.perform(get("/invoices/{id}", id))
            .andExpect(status().isOk())
            .andExpect(jsonPath("length()").value(1))
            .andExpect(jsonPath("[0].invoiceNumber").isNumber())
            .andExpect(jsonPath("[0].companyName").value("Company1"))
            .andExpect(jsonPath("[0].createDate").isNotEmpty())
            .andExpect(jsonPath("[0].modifiedDate").isNotEmpty())
            .andExpect(jsonPath("[0].items.length()").value(1))
            .andExpect(jsonPath("[0].totalInvoiceValue").value(201.00))
        ;

    }

    @Test
    public void updateValidCompanyOnInvoice() throws Exception {
        ItemDto itemDTo = new ItemDto("Item", 10, FeeType.RATEBASED, 20.10, null);
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
        invoiceDto.setCompanyName("Company3");

        mockMvc.perform(post("/invoices/{id}", id)
            .content(objectMapper.writeValueAsString(invoiceDto))
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.status").value(HttpStatus.OK.getReasonPhrase()))
            .andExpect(jsonPath("$.status_code").value(HttpStatus.OK.value()))
            .andExpect(jsonPath("$.data.invoiceNumber").isNumber())
            .andExpect(jsonPath("$.data.items").isNotEmpty())
        ;

        mockMvc.perform(get("/invoices/{id}", id))
            .andExpect(status().isOk())
            .andExpect(jsonPath("length()").value(1))
            .andExpect(jsonPath("[0].invoiceNumber").isNumber())
            .andExpect(jsonPath("[0].companyName").value("Company3"))
            .andExpect(jsonPath("[0].createDate").isNotEmpty())
            .andExpect(jsonPath("[0].modifiedDate").isNotEmpty())
            .andExpect(jsonPath("[0].items.length()").value(1))
            .andExpect(jsonPath("[0].totalInvoiceValue").value(201.00))
        ;

    }

    @Test
    public void updateItemsOnInvoice() throws Exception {
        ItemDto itemDTo = new ItemDto("Item", 10, FeeType.RATEBASED, 20.10, null);
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

        List<ItemDto> items2 = Arrays.asList(itemDTo1, itemDTo2, itemDTo3);

        InvoiceDto invoiceDto1 = new InvoiceDto("Company1", items2);

        mockMvc.perform(post("/invoices/{id}", id)
            .content(objectMapper.writeValueAsString(invoiceDto1))
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.status").value(HttpStatus.OK.getReasonPhrase()))
            .andExpect(jsonPath("$.status_code").value(HttpStatus.OK.value()))
            .andExpect(jsonPath("$.data.invoiceNumber").isNumber())
            .andExpect(jsonPath("$.data.items").isNotEmpty());

        mockMvc.perform(get("/invoices/{id}", id))
            .andExpect(status().isOk())
            .andExpect(jsonPath("length()").value(1))
            .andExpect(jsonPath("[0].invoiceNumber").isNumber())
            .andExpect(jsonPath("[0].companyName").value("Company1"))
            .andExpect(jsonPath("[0].createDate").isNotEmpty())
            .andExpect(jsonPath("[0].modifiedDate").isNotEmpty())
            .andExpect(jsonPath("[0].items.length()").value(3))
            .andExpect(jsonPath("[0].totalInvoiceValue").value(752.00))
        ;

    }

}
