package com.example.tutorialrestjavaspring.api.security;


import com.auth0.jwt.exceptions.JWTDecodeException;
import com.example.tutorialrestjavaspring.model.LocalUser;
import com.example.tutorialrestjavaspring.model.dao.LocalUserDAO;
import com.example.tutorialrestjavaspring.service.JWTService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.SimpMessageType;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Component
public class JWTRequestFilter extends OncePerRequestFilter implements ChannelInterceptor {
    private JWTService jwtService;
    private LocalUserDAO localUserDAO;

    public JWTRequestFilter(JWTService jwtService, LocalUserDAO localUserDAO) {
        this.jwtService = jwtService;
        this.localUserDAO = localUserDAO;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String tokenHeader = request.getHeader("Authorization");
        UsernamePasswordAuthenticationToken token = checkToken(tokenHeader);
        if (token != null) {
            token.setDetails(new WebAuthenticationDetails(request));
        }
        filterChain.doFilter(request, response);
    }

    private UsernamePasswordAuthenticationToken checkToken(String token) {
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
            try {
                String username = jwtService.getUserNameFromAuthenticationToken(token);
                Optional<LocalUser> opUser = localUserDAO.findByUsernameIgnoreCase(username);
                if (opUser.isPresent()) {
                    LocalUser user = opUser.get();
                    if(user.getIsVerified()) {
                        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(user, null, new ArrayList<>());
                        SecurityContextHolder.getContext().setAuthentication(authentication);
                        return authentication;
                    }
                }
            } catch (JWTDecodeException e) {

            }
        }
        SecurityContextHolder.getContext().setAuthentication(null);
        return null;
    }

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        SimpMessageType messageType = (SimpMessageType) message.getHeaders().get("simpMessageType");
        if(messageType.equals(SimpMessageType.SUBSCRIBE) || messageType.equals(SimpMessageType.MESSAGE)) {
            Map nativeHeaders = (Map) message.getHeaders().get("nativeHeaders");
            if (nativeHeaders != null) {
                List authTokens = (List) nativeHeaders.get("Authorization");
                if (authTokens != null) {
                    String tokenHeader = authTokens.get(0).toString();
                    checkToken(tokenHeader);
                }
            }
        }
        return message;
    }
}
