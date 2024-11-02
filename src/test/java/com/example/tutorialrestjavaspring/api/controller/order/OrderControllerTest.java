package com.example.tutorialrestjavaspring.api.controller.order;

import com.example.tutorialrestjavaspring.model.WebOrder;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@AutoConfigureMockMvc
public class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void testUnauthenticatedOrderList() throws Exception {
        mockMvc.perform(get("/order")).andExpect(status().is(HttpStatus.FORBIDDEN.value()));
    }

    @Test
    @WithUserDetails("UserA")
    public void testUserAAuthenticatedOrderList() throws Exception {
        testAuthenticatedListBelongsToUser("UserA");
    }

    @Test
    @WithUserDetails("UserB")
    public void testUserBAuthenticatedOrderList() throws Exception {
        testAuthenticatedListBelongsToUser("UserB");
    }

    private void testAuthenticatedListBelongsToUser(String username) throws Exception {
        mockMvc.perform(get("/order")).andExpect(status().is(HttpStatus.OK.value()))
                .andExpect(result -> {
                    String json = result.getResponse().getContentAsString();
                    List<WebOrder> orderList = new ObjectMapper().readValue(json, new TypeReference<List<WebOrder>>() {});
                    for(WebOrder webOrder : orderList) {
                        Assertions.assertEquals(username, webOrder.getUser().getUsername());
                    }
                });

    }
}
