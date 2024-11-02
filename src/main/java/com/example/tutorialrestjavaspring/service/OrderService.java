package com.example.tutorialrestjavaspring.service;

import com.example.tutorialrestjavaspring.model.LocalUser;
import com.example.tutorialrestjavaspring.model.WebOrder;
import com.example.tutorialrestjavaspring.model.dao.WebOrderDAO;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrderService {
    private WebOrderDAO webOrderDAO;

    public OrderService(WebOrderDAO webOrderDAO) {
        this.webOrderDAO = webOrderDAO;
    }

    public List<WebOrder> getAllOrders(LocalUser user) {
        return webOrderDAO.findByUser(user);
    }
}
