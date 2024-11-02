package com.example.tutorialrestjavaspring.model.dao;

import com.example.tutorialrestjavaspring.model.LocalUser;
import com.example.tutorialrestjavaspring.model.WebOrder;
import org.springframework.data.repository.ListCrudRepository;

import java.util.List;

public interface WebOrderDAO extends ListCrudRepository<WebOrder, Long> {

    List<WebOrder> findByUser(LocalUser user);
}
