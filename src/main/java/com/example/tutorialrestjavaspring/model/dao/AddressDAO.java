package com.example.tutorialrestjavaspring.model.dao;

import com.example.tutorialrestjavaspring.model.Address;
import org.springframework.data.repository.ListCrudRepository;

import java.util.List;

public interface AddressDAO extends ListCrudRepository<Address, Long> {

    List<Address> findByUser_Id(Long id);
}
