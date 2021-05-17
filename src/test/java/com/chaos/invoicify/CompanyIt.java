package com.chaos.invoicify;

import com.chaos.invoicify.dto.CompanyDto;
import com.chaos.invoicify.helper.Address;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureRestDocs
@Transactional
public class CompanyIt {
  @Autowired  private MockMvc mockMvc;
  @Autowired private ObjectMapper objectMapper;
  private CompanyDto companyDto, companyDto2;
  Address address1, address2;

  @BeforeEach
  public void setup() {
    address1 = new Address("123 ABC Street", "Toronto", "ON", "Canada", "A1B 2D3");
    address2 = new Address("456 ABC Street", "Cary", "NC", "US", "12345");
    companyDto =
            new CompanyDto("Company1", address1, "Samik", "Account Payable", "467-790-0128");
    companyDto2 =
            new CompanyDto("Company2", address2, "Rajendra", "Account Payable", "123-456-7890");
  }

  @Test
  public void createCompanyTest() throws Exception {
    mockMvc
        .perform(
            post("/company")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(companyDto)))
        .andExpect(jsonPath("$.status").value("Created"))
        .andExpect(jsonPath("$.status_code").value(201))
        .andExpect(jsonPath("$.data").value("Company created successfully!"))
        .andDo(
            document(
                "Create-Company",
                requestFields(
                    fieldWithPath("name").description("Company name"),
                    fieldWithPath("address.street").description("Address Line 1"),
                    fieldWithPath("address.city").description("City"),
                    fieldWithPath("address.state").description("Street Address"),
                    fieldWithPath("address.country").description("Country"),
                    fieldWithPath("address.zipCode").description("Zip Code"),
                    fieldWithPath("contactName").description("Contact name"),
                    fieldWithPath("contactTitle").description("Contact title"),
                    fieldWithPath("contactPhoneNumber").description("Contact phone number")),
                responseFields(
                    fieldWithPath("status").description("Return the http status description"),
                    fieldWithPath("status_code").description("Return the http status code"),
                    fieldWithPath("data").description("Return company creation status message"))));
  }

  @Test
  public void getZeroCompanyTest() throws Exception {
    mockMvc
        .perform(get("/company"))
        .andExpect(jsonPath("$.status").value("OK"))
        .andExpect(jsonPath("$.status_code").value(200))
        .andExpect(jsonPath("$.data.length()").value(0))
        .andDo(
            document(
                "Get-Zero-Company",
                responseFields(
                    fieldWithPath("status").description("Return the http status description"),
                    fieldWithPath("status_code").description("Return the http status code"),
                    fieldWithPath("data").description("List of companies"))));
  }

  @Test
  public void getOneCompanyTest() throws Exception {
    mockMvc
        .perform(
            post("/company")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(companyDto)))
        .andExpect(jsonPath("$.status").value("Created"))
        .andExpect(jsonPath("$.status_code").value(201))
        .andExpect(jsonPath("$.data").value("Company created successfully!"));

    mockMvc
        .perform(get("/company"))
        .andExpect(jsonPath("$.status").value("OK"))
        .andExpect(jsonPath("$.status_code").value(200))
        .andExpect(jsonPath("$.data.length()").value(1))
        .andExpect(jsonPath("$.data[0].name").value("Company1"))
        .andExpect(jsonPath("$.data[0].address").value(address1))
        .andExpect(jsonPath("$.data[0].contactName").value("Samik"))
        .andExpect(jsonPath("$.data[0].contactTitle").value("Account Payable"))
        .andExpect(jsonPath("$.data[0].contactPhoneNumber").value("467-790-0128"))
        .andDo(
            document(
                "Get-One-Company",
                responseFields(
                    fieldWithPath("status").description("Return the http status description"),
                    fieldWithPath("status_code").description("Return the http status code"),
                    fieldWithPath("data").description("List of companies"),
                    fieldWithPath("data[0].name").description("Company name"),
                    fieldWithPath("data[0].address.street").description("Address Line 1"),
                    fieldWithPath("data[0].address.city").description("City"),
                    fieldWithPath("data[0].address.state").description("Street Address"),
                    fieldWithPath("data[0].address.country").description("Country"),
                    fieldWithPath("data[0].address.zipCode").description("Zip Code"),
                    fieldWithPath("data[0].contactName").description("Contact name"),
                    fieldWithPath("data[0].contactTitle").description("Contact title"),
                    fieldWithPath("data[0].contactPhoneNumber")
                        .description("Contact phone number"))));
  }

  @Test
  public void getManyCompaniesTest() throws Exception {
    mockMvc
            .perform(
                    post("/company")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(companyDto)));
    mockMvc
            .perform(
                    post("/company")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(companyDto2)));

    mockMvc
            .perform(get("/company"))
            .andExpect(jsonPath("$.status").value("OK"))
            .andExpect(jsonPath("$.status_code").value(200))
            .andExpect(jsonPath("$.data.length()").value(2))
            .andExpect(jsonPath("$.data[0].name").value("Company1"))
            .andExpect(jsonPath("$.data[0].address").value(address1))
            .andExpect(jsonPath("$.data[0].contactName").value("Samik"))
            .andExpect(jsonPath("$.data[0].contactTitle").value("Account Payable"))
            .andExpect(jsonPath("$.data[0].contactPhoneNumber").value("467-790-0128"))
            .andExpect(jsonPath("$.data[1].name").value("Company2"))
            .andExpect(jsonPath("$.data[1].address").value(address2))
            .andExpect(jsonPath("$.data[1].contactName").value("Rajendra"))
            .andExpect(jsonPath("$.data[1].contactTitle").value("Account Payable"))
            .andExpect(jsonPath("$.data[1].contactPhoneNumber").value("123-456-7890"))
            .andDo(
                    document(
                            "Get-Many-Companies",
                            responseFields(
                                    fieldWithPath("status").description("Return the http status description"),
                                    fieldWithPath("status_code").description("Return the http status code"),
                                    fieldWithPath("data").description("List of companies"),
                                    fieldWithPath("data[0].name").description("Company name"),
                                    fieldWithPath("data[0].address.street").description("Address Line 1"),
                                    fieldWithPath("data[0].address.city").description("City"),
                                    fieldWithPath("data[0].address.state").description("Street Address"),
                                    fieldWithPath("data[0].address.country").description("Country"),
                                    fieldWithPath("data[0].address.zipCode").description("Zip Code"),
                                    fieldWithPath("data[0].contactName").description("Contact name"),
                                    fieldWithPath("data[0].contactTitle").description("Contact title"),
                                    fieldWithPath("data[0].contactPhoneNumber")
                                            .description("Contact phone number"))));
  }

  @Test
  public void uniqueCompanyTest() throws Exception {
    mockMvc
        .perform(
            post("/company")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(companyDto)))
        .andExpect(jsonPath("$.status").value("Created"))
        .andExpect(jsonPath("$.status_code").value(201))
        .andExpect(jsonPath("$.data").value("Company created successfully!"));

    mockMvc
        .perform(
            post("/company")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(companyDto)))
        .andExpect(jsonPath("$.status").value(HttpStatus.BAD_REQUEST.getReasonPhrase()))
        .andExpect(jsonPath("$.status_code").value(HttpStatus.BAD_REQUEST.value()))
        .andExpect(jsonPath("$.data").value("Company already exist"));
  }

  @Test
  public void addCompanyWithBlankNameTest () throws Exception{
    CompanyDto companyDtoNullName =
            new CompanyDto(null, address1, "Samik", "Account Payable", "467-790-0128");
    mockMvc
            .perform(
                    post("/company")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(companyDtoNullName)))
            .andExpect(jsonPath("$.status").value(HttpStatus.BAD_REQUEST.getReasonPhrase()))
            .andExpect(jsonPath("$.status_code").value(HttpStatus.BAD_REQUEST.value()))
            .andExpect(jsonPath("$.data").value("Company name cannot be empty!"));

    CompanyDto companyDtoBlankName =
            new CompanyDto("", address1, "Samik", "Account Payable", "467-790-0128");
    mockMvc
            .perform(
                    post("/company")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(companyDtoBlankName)))
            .andExpect(jsonPath("$.status").value(HttpStatus.BAD_REQUEST.getReasonPhrase()))
            .andExpect(jsonPath("$.status_code").value(HttpStatus.BAD_REQUEST.value()))
            .andExpect(jsonPath("$.data").value("Company name cannot be empty!"));
  }

  @Test
  public void updateCompanyTest() throws Exception{
    mockMvc
            .perform(
                    post("/company")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(companyDto)))
            .andExpect(jsonPath("$.status").value("Created"))
            .andExpect(jsonPath("$.status_code").value(201))
            .andExpect(jsonPath("$.data").value("Company created successfully!"));

    CompanyDto updatedCompanyDto = new CompanyDto();
    updatedCompanyDto.setName("Company1");
    updatedCompanyDto.setAddress(address2);
    updatedCompanyDto.setContactName("ABCD");
    updatedCompanyDto.setContactTitle("XYZ");
    updatedCompanyDto.setContactPhoneNumber("999-999-9999");

    mockMvc
            .perform(
                    post("/company/{companyName}", "Company1")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(updatedCompanyDto)))
            .andExpect(jsonPath("$.status").value(HttpStatus.OK.getReasonPhrase()))
            .andExpect(jsonPath("$.status_code").value(HttpStatus.OK.value()))
            .andExpect(jsonPath("$.data").value("Company updated successfully!"))
            .andDo(
                    document(
                            "Update-Company-ExceptName",
                            requestFields(
                                    fieldWithPath("name").description("Company name"),
                                    fieldWithPath("address").description("Company address"),
                                    fieldWithPath("address.street").description("Address Line 1"),
                                    fieldWithPath("address.city").description("City"),
                                    fieldWithPath("address.state").description("Street Address"),
                                    fieldWithPath("address.country").description("Country"),
                                    fieldWithPath("address.zipCode").description("Zip Code"),
                                    fieldWithPath("contactName").description("Contact name"),
                                    fieldWithPath("contactTitle").description("Contact title"),
                                    fieldWithPath("contactPhoneNumber").description("Contact phone number")),
                            responseFields(
                                    fieldWithPath("status").description("Return the http status description"),
                                    fieldWithPath("status_code").description("Return the http status code"),
                                    fieldWithPath("data").description("Return company update status"))));

    mockMvc
            .perform(get("/company"))
            .andExpect(jsonPath("$.data[0].address").value(address2))
            .andExpect(jsonPath("$.data[0].contactName").value("ABCD"))
            .andExpect(jsonPath("$.data[0].contactTitle").value("XYZ"))
            .andExpect(jsonPath("$.data[0].contactPhoneNumber").value("999-999-9999"));

  }

  @Test
  public void updateCompanyNameTest() throws Exception{
    mockMvc
            .perform(
                    post("/company")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(companyDto)))
            .andExpect(jsonPath("$.status").value("Created"))
            .andExpect(jsonPath("$.status_code").value(201))
            .andExpect(jsonPath("$.data").value("Company created successfully!"));

    CompanyDto updatedCompanyDto = new CompanyDto();
    updatedCompanyDto.setName("Apple");

    mockMvc
        .perform(
            post("/company/{companyName}", "Company1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedCompanyDto)))
        .andExpect(jsonPath("$.status").value(HttpStatus.OK.getReasonPhrase()))
        .andExpect(jsonPath("$.status_code").value(HttpStatus.OK.value()))
        .andExpect(jsonPath("$.data").value("Company updated successfully!"))
        .andDo(
            document(
                "Update-Company",
                pathParameters(parameterWithName("companyName").description("Company name to be updated")),
                requestFields(
                    fieldWithPath("name").description("Company name"),
                    fieldWithPath("address").description("Company address"),
                    fieldWithPath("contactName").description("Contact name"),
                    fieldWithPath("contactTitle").description("Contact title"),
                    fieldWithPath("contactPhoneNumber").description("Contact phone number")),
                responseFields(
                    fieldWithPath("status").description("Return the http status description"),
                    fieldWithPath("status_code").description("Return the http status code"),
                    fieldWithPath("data").description("Return company update status"))));

    mockMvc
            .perform(get("/company"))
            .andExpect(jsonPath("$.data[0].name").value("Apple"))
            .andExpect(jsonPath("$.data[0].address").value(address1))
            .andExpect(jsonPath("$.data[0].contactName").value("Samik"))
            .andExpect(jsonPath("$.data[0].contactTitle").value("Account Payable"))
            .andExpect(jsonPath("$.data[0].contactPhoneNumber").value("467-790-0128"));

  }

  @Test
  public void updateNonExistentCompanyFailureTest() throws Exception{
    mockMvc
            .perform(
                    post("/company")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(companyDto)))
            .andExpect(jsonPath("$.status").value("Created"))
            .andExpect(jsonPath("$.status_code").value(201))
            .andExpect(jsonPath("$.data").value("Company created successfully!"));

    CompanyDto updatedCompanyDto = new CompanyDto();
    updatedCompanyDto.setContactName("Raj");

    mockMvc
            .perform(
                    post("/company/{companyName}", "NonExistentCompany")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(updatedCompanyDto)))
            .andExpect(jsonPath("$.status").value(HttpStatus.BAD_REQUEST.getReasonPhrase()))
            .andExpect(jsonPath("$.status_code").value(HttpStatus.BAD_REQUEST.value()))
            .andExpect(jsonPath("$.data").value("Company does not exist!"));

  }

  @Test
  public void updateCompanyAddressTest() throws Exception{
    mockMvc
            .perform(
                    post("/company")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(companyDto)))
            .andExpect(jsonPath("$.status").value("Created"))
            .andExpect(jsonPath("$.status_code").value(201))
            .andExpect(jsonPath("$.data").value("Company created successfully!"));

    CompanyDto updatedCompanyDto = new CompanyDto();
    Address updatedAddress = new Address();
    updatedAddress.setState("Ontario");// Changed from "ON" to "Ontario"
    updatedCompanyDto.setAddress(updatedAddress);

    mockMvc
            .perform(
                    post("/company/{companyName}", "Company1")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(updatedCompanyDto)))
            .andExpect(jsonPath("$.status").value(HttpStatus.OK.getReasonPhrase()))
            .andExpect(jsonPath("$.status_code").value(HttpStatus.OK.value()))
            .andExpect(jsonPath("$.data").value("Company updated successfully!"));

    mockMvc
            .perform(get("/company"))
            .andExpect(jsonPath("$.data[0].name").value("Company1"))
            .andExpect(jsonPath("$.data[0].address.state").value("Ontario"))
            .andExpect(jsonPath("$.data[0].address.city").value("Toronto"))
            .andExpect(jsonPath("$.data[0].address.country").value("Canada"))
            .andExpect(jsonPath("$.data[0].address.zipCode").value("A1B 2D3"));

    updatedAddress.setCity("NewCity");
    updatedAddress.setState(null);
    updatedCompanyDto.setAddress(updatedAddress);
    mockMvc
            .perform(
                    post("/company/{companyName}", "Company1")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(updatedCompanyDto)));
    mockMvc
            .perform(get("/company"))
            .andExpect(jsonPath("$.data[0].name").value("Company1"))
            .andExpect(jsonPath("$.data[0].address.state").value("Ontario"))
            .andExpect(jsonPath("$.data[0].address.city").value("NewCity"))
            .andExpect(jsonPath("$.data[0].address.country").value("Canada"))
            .andExpect(jsonPath("$.data[0].address.zipCode").value("A1B 2D3"));

  }

  @Test
  public void getSimpleCompanyListTest() throws Exception{
    mockMvc
            .perform(
                    post("/company")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(companyDto)))
            .andExpect(status().isCreated());

    mockMvc
            .perform(get("/company-list"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.status").value(HttpStatus.OK.getReasonPhrase()))
            .andExpect(jsonPath("$.status_code").value(HttpStatus.OK.value()))
            .andExpect(jsonPath("$.data[0].name").value("Company1"))
            .andExpect(jsonPath("$.data[0].city").value("Toronto"))
            .andExpect(jsonPath("$.data[0].state").value("ON"))
            .andDo(document("Company-List",responseFields(
                    fieldWithPath("status").description("Return the http status description"),
                    fieldWithPath("status_code").description("Return the http status code"),
                    fieldWithPath("data").description("List of companies"),
                    fieldWithPath("data[0].name").description("Company name"),
                    fieldWithPath("data[0].city").description("City"),
                    fieldWithPath("data[0].state").description("State"))
            ));
  }

}
