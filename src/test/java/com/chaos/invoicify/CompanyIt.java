package com.chaos.invoicify;

import com.chaos.invoicify.dto.CompanyDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;


@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureRestDocs
@Transactional
public class CompanyIt {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Test
    public void createCompanyTest()throws Exception{
        CompanyDto companyDto=new CompanyDto("Comapany1","Adress 123","Samik","Account Payable","467-790-0128");
        mockMvc.perform(post("/company")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(companyDto)))
                .andExpect(jsonPath("$.status").value("Created"))
                .andExpect(jsonPath("$.status_code").value(201))
                .andExpect(jsonPath("$.data").value("Company created successfully!"));

    }

    @Test
    public void getZeroCompanyTest()throws Exception{
        mockMvc.perform(get("/company"))
                .andExpect(jsonPath("$.status").value("Ok"))
                .andExpect(jsonPath("$.status_code").value(200))
                .andExpect(jsonPath("$.data.length()").value(0));
    }
    @Test
    public void getOneCompanyTest()throws Exception{
        CompanyDto companyDto=new CompanyDto("Comapany1","Adress 123","Samik","Account Payable","467-790-0128");
        mockMvc.perform(post("/company")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(companyDto)))
                .andExpect(jsonPath("$.status").value("Created"))
                .andExpect(jsonPath("$.status_code").value(201))
                .andExpect(jsonPath("$.data").value("Company created successfully!"));
        mockMvc.perform(get("/company"))
                .andExpect(jsonPath("$.status").value("Ok"))
                .andExpect(jsonPath("$.status_code").value(200))
                .andExpect(jsonPath("$.data.length()").value(1))
                .andExpect(jsonPath("$.data[0].name").value("Comapany1"))
                .andExpect(jsonPath("$.data[0].address").value("Address 123"))
                .andExpect(jsonPath("$.data[0].contactName").value("Samik"))
                .andExpect(jsonPath("$.data[0].contactTitle").value("Account Payable"))
                .andExpect(jsonPath("$.data[0].contactPhoneNumber").value("467-790-0128"));

    }
}
