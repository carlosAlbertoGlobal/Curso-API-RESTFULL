package com.planotatico.demo.services;

import java.util.List;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.planotatico.demo.controller.PersonController;
import com.planotatico.demo.data.PersonVO;
import com.planotatico.demo.exceptions.RequiredObjectisNullExeception;
import com.planotatico.demo.exceptions.ResourcesNotFoundException;
import com.planotatico.demo.mapper.DozerMapper;
import com.planotatico.demo.model.Person;
import com.planotatico.demo.repositories.PersonRepository;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Service
public class PersonServices {

    private Logger loger = Logger.getLogger(PersonServices.class.getName());

    @Autowired
    PersonRepository repository;

    public List<PersonVO> findAll() {
        loger.info("Find all persons: ");

        var persons = DozerMapper.parseListObject(repository.findAll(), PersonVO.class);
        persons
                .stream()
                .forEach(p -> p.add(linkTo(methodOn(PersonController.class).findById(p.getKey())).withSelfRel()));
        return persons;
    }

    public PersonVO findById(Long id) {
        loger.info("Find one person: ");

        var entity = repository.findById(id)
                .orElseThrow(() -> new ResourcesNotFoundException("No records found by ID!"));
        PersonVO vo = DozerMapper.parseObject(entity, PersonVO.class);
        vo.add(linkTo(methodOn(PersonController.class).findById(id)).withSelfRel());
        return vo;
    }

    public PersonVO create(PersonVO person) {

        if (person == null)
            throw new RequiredObjectisNullExeception();
        loger.info("Creating one person: ");
        var entity = DozerMapper.parseObject(person, Person.class);
        var vo = DozerMapper.parseObject(repository.save(entity), PersonVO.class);
        vo.add(linkTo(methodOn(PersonController.class).findById(vo.getKey())).withSelfRel());
        return vo;
    }

    public PersonVO update(PersonVO person) {
        if (person == null)
            throw new RequiredObjectisNullExeception();

        loger.info("Update one person: ");

        var entity = repository.findById(person.getKey())
                .orElseThrow(() -> new ResourcesNotFoundException("No records found by ID!"));

        entity.setFirstName(person.getFirstName());
        entity.setLastName(person.getLastName());
        entity.setAdress(person.getAdress());
        entity.setGender(person.getGender());

        var vo = DozerMapper.parseObject(repository.save(entity), PersonVO.class);

        vo.add(linkTo(methodOn(PersonController.class).findById(vo.getKey())).withSelfRel());
        return vo;
    }

    public void delete(Long id) {
        loger.info("Deleting one person: ");

        var entity = repository.findById(id)
                .orElseThrow(() -> new ResourcesNotFoundException("No records found by ID!"));

        repository.delete(entity);

    }

}
