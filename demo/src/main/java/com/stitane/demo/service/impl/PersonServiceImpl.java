package com.stitane.demo.service.impl;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.stitane.demo.dto.FilterDTO;
import com.stitane.demo.dto.PersonDto;
import com.stitane.demo.entity.Person;
import com.stitane.demo.excpetions.BusinessException;
import com.stitane.demo.repository.PersonRepository;
import com.stitane.demo.service.PersonService;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;

@Service
@Transactional
public class PersonServiceImpl implements PersonService {
    private static final String NOT_FOUND = "the given id [%d] not found in database";

    @Autowired
    private PersonRepository personRepository;
    @Autowired
    private ModelMapper modelMapper;

    /**
     * find a specific person
     * @param id identifier of a person
     * @return person with the specific id
     */
    public PersonDto retrievePerson(Long id) {
        Optional<Person> person = personRepository.findById(id);

        if (!person.isPresent()) {
            throw new BusinessException(String.format(NOT_FOUND, id));
        }

        return modelMapper.map(person.get(), PersonDto.class);
    }

    /**
     * delete a specific person
     * @param id identifier of a person
     */
    public void deletePerson(Long id) {
        personRepository.deleteById(id);
    }

    /**
     * create a new person
     * @param person a new Person details
     * @return person create with a new id
     */
    @PostMapping("/persons")
    public PersonDto createPerson(PersonDto person) {

        Person p = modelMapper.map(person, Person.class);

        return modelMapper.map(personRepository.save(p), PersonDto.class);
    }

    /**
     * update a specific person
     * @param personDto a updated person's information
     * @param id identifier of a person
     * @return person updated
     */
    public PersonDto updatePerson(PersonDto personDto, Long id) {
        personDto.setId(id);

        Person saved = personRepository.save(modelMapper.map(personDto, Person.class));

        return modelMapper.map(saved, PersonDto.class);
    }

    @Override
    public Page<PersonDto> retrieveAllPersons(FilterDTO filter, Pageable pageable) {
        int pageSize = pageable.getPageSize();
        int currentPage = pageable.getPageNumber();
        int start = currentPage * pageSize;

        Person person = new Person();
        person.setName(filter.getName());
        person.setDateAdded(filter.getDate());

        ExampleMatcher matcher = ExampleMatcher.matching()
                .withIgnoreCase("name")
                .withMatcher("name", ExampleMatcher.GenericPropertyMatchers.contains())
                .withMatcher("added", ExampleMatcher.GenericPropertyMatchers.startsWith());

        List<PersonDto> list = personRepository.findAll(Example.of(person, matcher))
                .stream().map(p -> modelMapper.map(p, PersonDto.class)).collect(Collectors.toList());

        int offset = list.size();

        if (list.size() < start) {
            list = Collections.emptyList();
        } else {
            int toIndex = Math.min(start + pageSize, list.size());
            list = list.subList(start, toIndex);
        }

        return new PageImpl<>(list, PageRequest.of(currentPage, pageSize), offset);
    }
}
