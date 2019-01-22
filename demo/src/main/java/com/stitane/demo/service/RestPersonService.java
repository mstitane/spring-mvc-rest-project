package com.stitane.demo.service;

import java.net.URI;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.stitane.demo.dto.PersonDto;
import com.stitane.demo.entity.Person;
import com.stitane.demo.excpetions.BusinessException;
import com.stitane.demo.repository.PersonRepository;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

//@RestController
public class RestPersonService {
    private static final String NOT_FOUND = "the given id [%d] not found in database";

    @Autowired
    private PersonRepository personRepository;
    @Autowired
    private ModelMapper modelMapper;

    @GetMapping("/persons")
    public List<PersonDto> retrieveAllPersons() {
        return personRepository.findAll().stream()
                .map(p -> modelMapper.map(p, PersonDto.class))
                .collect(Collectors.toList());
    }

    /**
     * find a specific person
     * @param id identifier of a person
     * @return person with the specific id
     */
    @GetMapping("/persons/{id}")
    public PersonDto retrievePerson(@PathVariable long id) {
        Optional<Person> person = personRepository.findById(id);

        if (!person.isPresent())
            throw new BusinessException(String.format(NOT_FOUND, id));

        return modelMapper.map(person.get(), PersonDto.class);
    }

    /**
     * delete a specific person
     * @param id identifier of a person
     */
    @DeleteMapping("/persons/{id}")
    public void deletePerson(@PathVariable long id) {
        personRepository.deleteById(id);
    }

    /**
     * create a new person
     * @param person a new Person details
     * @return
     */
    @PostMapping("/persons")
    public ResponseEntity<Object> createPerson(@RequestBody PersonDto person) {

        Person p = modelMapper.map(person, Person.class);

        Person savedPerson = personRepository.save(p);

        URI request = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
                .buildAndExpand(savedPerson.getId()).toUri();

        return ResponseEntity.created(request).build();
    }

    /**
     * update a specific person
     * @param personDto a updated person's information
     * @param id identifier of a person
     * @return nothing
     */
    @PutMapping("/persons/{id}")
    public ResponseEntity<Object> updatePersonDto(@RequestBody PersonDto personDto, @PathVariable long id) {

        Optional<Person> person = personRepository.findById(id);

        if (!person.isPresent())
            throw new BusinessException(String.format(NOT_FOUND, id));

        personDto.setId(id);

        personRepository.save(modelMapper.map(personDto, Person.class));

        return ResponseEntity.noContent().build();
    }
}
