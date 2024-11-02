package com.example.tutorialrestjavaspring.api.security;

import com.example.tutorialrestjavaspring.model.LocalUser;
import com.example.tutorialrestjavaspring.model.dao.LocalUserDAO;
import com.example.tutorialrestjavaspring.service.JWTService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@AutoConfigureMockMvc
public class JWTRequestFilterTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JWTService jwtService;

    @Autowired
    private LocalUserDAO localUserDAO;

    private static final String AUTHENTICATED_PATH = "/auth/me";

    @Test
    public void testUnauthenticatedRequest() throws Exception {
        mockMvc.perform(get(AUTHENTICATED_PATH)).andExpect(status().is(HttpStatus.FORBIDDEN.value()));
    }

    @Test
    public void testBadTokenRequest() throws Exception {
        mockMvc.perform(get(AUTHENTICATED_PATH).header("Authorization","ThisIsBadToken"))
                .andExpect(status().is(HttpStatus.FORBIDDEN.value()));
        mockMvc.perform(get(AUTHENTICATED_PATH).header("Authorization","Bearer ThisIsBadToken"))
                .andExpect(status().is(HttpStatus.FORBIDDEN.value()));
    }

    @Test
    public void testUnverifiedUserRequest() throws Exception {
        LocalUser localUser = localUserDAO.findByUsernameIgnoreCase("UserB").get();
        String token = jwtService.createToken(localUser);
        mockMvc.perform(get(AUTHENTICATED_PATH).header("Authorization","Bearer " + token))
                .andExpect(status().is(HttpStatus.FORBIDDEN.value()));
    }

    @Test
    public void testVerifiedUserRequest() throws Exception {
        LocalUser localUser = localUserDAO.findByUsernameIgnoreCase("UserA").get();
        String token = jwtService.createToken(localUser);
        mockMvc.perform(get(AUTHENTICATED_PATH).header("Authorization","Bearer " + token))
                .andExpect(status().is(HttpStatus.OK.value()));
    }
}
