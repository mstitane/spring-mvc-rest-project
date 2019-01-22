package com.stitane.demo.controller;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import com.stitane.demo.dto.FilterDTO;
import com.stitane.demo.dto.PersonDto;
import com.stitane.demo.service.PersonService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class PersonController {
    @Autowired
    PersonService personService;

    @RequestMapping("/")
    public String home() {
        return "default";
    }

    @RequestMapping("/persons")
    public String list(Model model,@RequestParam Optional<String> name,
            @RequestParam @DateTimeFormat(pattern="yyyy-MM-dd") Optional<Date> date,
            @RequestParam("page") Optional<Integer> page,
            @RequestParam("size") Optional<Integer> size) {
        FilterDTO filter = new FilterDTO();
        filter.setName(name.orElse(null));
        filter.setDate(date.orElse(null));
        model.addAttribute("filter", filter);

        int currentPage = page.orElse(1);
        int pageSize = size.orElse(5);
        Page<PersonDto> list = personService.retrieveAllPersons(filter, PageRequest.of(currentPage - 1, pageSize));

        model.addAttribute("persons", list);

        int totalPages = list.getTotalPages();
        if (totalPages > 0) {
            List<Integer> pageNumbers = IntStream.rangeClosed(1, totalPages)
                    .boxed()
                    .collect(Collectors.toList());
            model.addAttribute("pageNumbers", pageNumbers);
        }

        return "persons";
    }

    @RequestMapping("/create")
    public String create() {
        return "create";
    }

    @RequestMapping("/save")
    public String save(@RequestParam String name,
            @RequestParam @DateTimeFormat(pattern="yyyy-MM-dd") Date added) {
        PersonDto personDto = new PersonDto();
        personDto.setName(name);
        personDto.setDateAdded(added);
        personService.createPerson(personDto);
        return "redirect:/persons";
    }

    @RequestMapping("/delete/{id}")
    public String delete(@PathVariable long id) {
        personService.deletePerson(id);
        return "redirect:/persons";
    }

    @RequestMapping("/edit/{id}")
    public String edit(@PathVariable long id, Model model) {
        model.addAttribute("person", personService.retrievePerson(id));
        return "edit";
    }

    @RequestMapping("/update")
    public String update(@RequestParam long id, @RequestParam String name,
            @RequestParam @DateTimeFormat(pattern="yyyy-MM-dd") Date added) {
        PersonDto personDto = personService.retrievePerson(id);
        personDto.setName(name);
        personDto.setDateAdded(added);
        personService.updatePerson(personDto, id);
        return "redirect:/persons";
    }
}
