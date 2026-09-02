package com.castle.property.controller;

import com.castle.property.dto.PagedResponse;
import com.castle.property.dto.PersonRequest;
import com.castle.property.dto.PersonResponse;
import com.castle.property.entity.Person;
import com.castle.property.mapper.PersonMapper;
import com.castle.property.service.PersonService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/persons")
@RequiredArgsConstructor
public class PersonController {

    private final PersonService personService;

    @GetMapping
    public PagedResponse<PersonResponse> list(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "25") int size,
            @RequestParam(required = false) String search
    ) {
        Page<Person> result = personService.getPersons(search, PageRequest.of(page, size));
        return PagedResponse.of(result.map(PersonMapper::toResponse));
    }

    @GetMapping("/list")
    public List<PersonResponse> listAll(
            @RequestParam(required = false) String search
    ) {
        return personService.listPersons(search).stream()
                .map(PersonMapper::toResponse)
                .toList();
    }

    @GetMapping("/{id}")
    public PersonResponse get(@PathVariable UUID id) {
        return PersonMapper.toResponse(personService.getPerson(id));
    }

    @PostMapping
    public ResponseEntity<PersonResponse> create(@Valid @RequestBody PersonRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(PersonMapper.toResponse(personService.createPerson(request)));
    }

    @PutMapping("/{id}")
    public PersonResponse update(@PathVariable UUID id, @Valid @RequestBody PersonRequest request) {
        return PersonMapper.toResponse(personService.updatePerson(id, request));
    }
}