package com.stitane.demo.service;

import com.stitane.demo.dto.FilterDTO;
import com.stitane.demo.dto.PersonDto;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PersonService {
    PersonDto createPerson(PersonDto person);
    PersonDto retrievePerson(Long id);
    PersonDto updatePerson(PersonDto person, Long id);
    void deletePerson(Long id);
    Page<PersonDto> retrieveAllPersons(FilterDTO filter, Pageable page);
}
