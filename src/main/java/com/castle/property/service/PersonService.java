package com.castle.property.service;

import com.castle.property.dto.PersonRequest;
import com.castle.property.entity.Person;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface PersonService {
    Person createPerson(@Valid PersonRequest request);

    Person updatePerson(@NotNull UUID personPublicId, @Valid PersonRequest request);

    Person getPerson(@NotNull UUID personPublicId);

    List<Person> listPersons(String searchParam);

    Page<Person> getPersons(String searchParam, Pageable pageable);

    Page<Person> searchPersons(String identificationNumber, String phoneNumber, String searchParam, Pageable pageable);

    Person getPersonById(String rowKey);

    Number getPersonsCount(String searchParam);

    Number searchPersonsCount(String identificationNumber, String phoneNumber, String searchParam);

    String generatePassword(String si, String password, StringBuilder saltString);
}
