package com.example.tutorialrestjavaspring.api.controller.auth;


import com.example.tutorialrestjavaspring.api.model.RegistrationBody;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.icegreen.greenmail.configuration.GreenMailConfiguration;
import com.icegreen.greenmail.junit5.GreenMailExtension;
import com.icegreen.greenmail.util.ServerSetupTest;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class AuthenticationControllerTest {

    @RegisterExtension
    private static GreenMailExtension greenMailExtension = new GreenMailExtension(ServerSetupTest.SMTP)
            .withConfiguration(GreenMailConfiguration.aConfig().withUser("springboot","secret"))
            .withPerMethodLifecycle(true);

    @Autowired
    private MockMvc mockMvc;

    @Test
    @Transactional
    public void testRegister() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        RegistrationBody registrationBody = new RegistrationBody();
        registrationBody.setEmail("UserServiceTest$testRegisterUser@junit.com");
        registrationBody.setFirstName("FirstName");
        registrationBody.setLastName("LastName");
        registrationBody.setPassword("Password123");

        //Test null validation
        registrationBody.setUsername(null);
        mockMvc.perform(post("/auth/register")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(mapper.writeValueAsString(registrationBody)))
                .andExpect(status().is(HttpStatus.BAD_REQUEST.value()));
        registrationBody.setUsername("UserServiceTest$testRegisterUser");
        registrationBody.setEmail(null);
        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(registrationBody)))
                .andExpect(status().is(HttpStatus.BAD_REQUEST.value()));
        registrationBody.setEmail("UserServiceTest$testRegisterUser@junit.com");
        registrationBody.setPassword(null);
        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(registrationBody)))
                .andExpect(status().is(HttpStatus.BAD_REQUEST.value()));
        registrationBody.setPassword("Password123");
        registrationBody.setFirstName(null);
        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(registrationBody)))
                .andExpect(status().is(HttpStatus.BAD_REQUEST.value()));
        registrationBody.setFirstName("FirstName");
        registrationBody.setLastName(null);
        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(registrationBody)))
                .andExpect(status().is(HttpStatus.BAD_REQUEST.value()));
        registrationBody.setLastName("LastName");

        //Test blank validation
        registrationBody.setUsername("");
        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(registrationBody)))
                .andExpect(status().is(HttpStatus.BAD_REQUEST.value()));
        registrationBody.setUsername("UserServiceTest$testRegisterUser");
        registrationBody.setEmail("");
        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(registrationBody)))
                .andExpect(status().is(HttpStatus.BAD_REQUEST.value()));
        registrationBody.setEmail("UserServiceTest$testRegisterUser@junit.com");
        registrationBody.setPassword("");
        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(registrationBody)))
                .andExpect(status().is(HttpStatus.BAD_REQUEST.value()));
        registrationBody.setPassword("Password123");
        registrationBody.setFirstName("");
        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(registrationBody)))
                .andExpect(status().is(HttpStatus.BAD_REQUEST.value()));
        registrationBody.setFirstName("FirstName");
        registrationBody.setLastName("");
        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(registrationBody)))
                .andExpect(status().is(HttpStatus.BAD_REQUEST.value()));
        registrationBody.setLastName("LastName");

        //Test success validation
        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(registrationBody)))
                .andExpect(status().is(HttpStatus.OK.value()));
    }
}
