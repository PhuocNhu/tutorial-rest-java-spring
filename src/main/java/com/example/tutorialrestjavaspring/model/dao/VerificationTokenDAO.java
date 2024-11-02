package com.example.tutorialrestjavaspring.model.dao;

import com.example.tutorialrestjavaspring.model.LocalUser;
import com.example.tutorialrestjavaspring.model.VerificationToken;
import org.springframework.data.repository.ListCrudRepository;

import java.util.List;
import java.util.Optional;

public interface VerificationTokenDAO extends ListCrudRepository<VerificationToken, Long> {

    Optional<VerificationToken> findByToken(String token);

    void deleteByUser(LocalUser user);

    List<VerificationToken> findByUser_IdOrderByIdDesc(Long id);
}
