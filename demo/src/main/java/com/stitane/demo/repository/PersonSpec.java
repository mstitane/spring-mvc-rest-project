package com.stitane.demo.repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

import javax.persistence.criteria.Predicate;

import com.stitane.demo.entity.Person;

import org.springframework.data.convert.Jsr310Converters;
import org.springframework.data.jpa.domain.Specification;

public class PersonSpec {
    public static Specification<Person> nameLike(String name) {
        return (root, query, cb) -> name == null ? cb.conjunction() :  cb.like(root.get("name"), "%".concat(name).concat("%") );
    }

    public static Specification<Person> dateLike(LocalDate date) {
        return (root, query, cb) -> {
            if (date == null)
                return cb.conjunction();
            else {
                Date d1 = Date.from(date.atStartOfDay(ZoneId.systemDefault()).toInstant());
                Date d2 = Date.from(date.plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant());
                return cb.between(root.get("dateAdded"), d1, d2);
            }
        };
    }
}
