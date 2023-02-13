package com.example.SpringBoop.controller;

import com.example.SpringBoop.exception.BoopException;
import com.example.SpringBoop.model.Boop;
import com.example.SpringBoop.repository.BoopRepository;
import com.example.SpringBoop.specification.BoopSpecificationBuilder;
import com.example.SpringBoop.utilities.FilterCriteria;
import com.example.SpringBoop.utilities.SortBuilder;
import com.example.SpringBoop.utilities.SortCriteria;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.mapping.PropertyReferenceException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.*;

@RestController
@RequestMapping("/boop")
public class BoopController {
    @Autowired
    private BoopRepository boopRepository;

    @GetMapping
    public Iterable<Boop> list() {
        return boopRepository.findAll();
    }


    @GetMapping("/{id}")
    public Boop get(@PathVariable int id) {
        Optional<Boop> optionalBoop = boopRepository.findById(id);
        if(!optionalBoop.isPresent()) throw new ResponseStatusException(HttpStatus.NOT_FOUND);

        return optionalBoop.get();
    }

    @PostMapping
    public Boop add(@RequestParam String description, @RequestParam(name="lastModified", required=false) String lastModifiedStr) {
        try {
            if(description == null || description.trim().isEmpty())
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST);

            LocalDateTime lastModified;
            if(lastModifiedStr == null || lastModifiedStr.trim().isEmpty())
                lastModified = LocalDateTime.now();
            else
                lastModified = LocalDateTime.parse(lastModifiedStr);

            Boop boop = new Boop(description, lastModified);
            boopRepository.save(boop);
            return boop;
        } catch(DateTimeParseException e) {
            e.printStackTrace();
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/{id}")
    public Boop update(@PathVariable int id, @RequestParam(required=false) String description,
                       @RequestParam(name="lastModified", required=false) String lastModifiedStr) {
        try {
            Optional<Boop> optionalBoop = boopRepository.findById(id);
            if (!optionalBoop.isPresent()) throw new ResponseStatusException(HttpStatus.NOT_FOUND);

            LocalDateTime lastModified;
            if(lastModifiedStr == null || lastModifiedStr.trim().isEmpty())
                lastModified = LocalDateTime.now();
            else
                lastModified = LocalDateTime.parse(lastModifiedStr);

            Boop boop = optionalBoop.get();
            if(description != null && !description.trim().isEmpty()) boop.setDescription(description.trim());
            boop.setLastModified(lastModified);
            boopRepository.save(boop);

            return boop;
        } catch(DateTimeParseException e) {
            e.printStackTrace();
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("/{id}")
    public Boop delete(@PathVariable int id) {
        Optional<Boop> optionalBoop = boopRepository.findById(id);
        if(!optionalBoop.isPresent()) throw new ResponseStatusException(HttpStatus.NOT_FOUND);

        Boop boop = optionalBoop.get();
        boopRepository.delete(boop);
        return boop;
    }

    // TODO: More specific error handling
    // TODO: Handle when key value is empty
    @PostMapping(path="/filter",
            consumes=MediaType.APPLICATION_JSON_VALUE)
    public Iterable<Boop> filter(@RequestBody Object data) {
        try {
            // Validate JSON data type
            if(!(data instanceof Map))
                throw new BoopException("Invalid JSON data type: must provide an object with pageNum, pageSize, filter (optional) and sort (optional) keys");
            Map<String, Object> search = (Map<String, Object>) data;

            // Check required keys
            if(search.isEmpty() || (!search.containsKey("pageNum") && !search.containsKey("pageSize")))
                throw new BoopException("Incomplete JSON data: must include pageNum and pageSize keys");
            else if(!search.containsKey("pageNum"))
                throw new BoopException("Incomplete JSON data: must include pageNum key");
            else if(!search.containsKey("pageSize"))
                throw new BoopException("Incomplete JSON data: must include pageSize key");

            // Check filter value
            if(search.get("filter") != null && !(search.get("filter") instanceof List))
                throw new BoopException("Invalid filter value: must provide an array of objects each with key, operator, value and isPredicate (optional) keys");
            List<Map<String, Object>> filters = (List<Map<String, Object>>) search.get("filter");

            // Handle filter values
            List<FilterCriteria> filterCriterias = new ArrayList<>();
            if(filters != null) {
                for(Map<String, Object> f : filters)
                    filterCriterias.add(new FilterCriteria(f));
            }

            // Check sort value
            if(search.get("sort") != null && !(search.get("sort") instanceof List))
                throw new BoopException("Invalid sort value: must provide an array of objects each with key and direction keys");
            List<Map<String, Object>> sorts = (List<Map<String, Object>>) search.get("sort");

            // Handle sort values
            List<SortCriteria> sortCriterias = new ArrayList<>();
            if(sorts != null) {
                for(Map<String, Object> s : sorts)
                    sortCriterias.add(new SortCriteria(s));
            }

            // Check pageNum value
            if(!(search.get("pageNum") instanceof Integer))
                throw new BoopException("Invalid pageNum value: must be an integer");
            Integer pageNum = (Integer) search.get("pageNum");
            if(pageNum < 0) throw new BoopException("Invalid pageNum value: must be greater or equal to 0");

            // Check pageSize value
            if(!(search.get("pageSize") instanceof Integer))
                throw new BoopException("Invalid pageSize value: must be an integer");
            Integer pageSize = (Integer) search.get("pageSize");
            if(pageSize < 1) throw new BoopException("Invalid pageSize value: must be greater or equal to 1");

            BoopSpecificationBuilder boopSpecificationBuilder = new BoopSpecificationBuilder(filterCriterias);
            SortBuilder sortBuilder = new SortBuilder(sortCriterias);
            return boopRepository.findAll(boopSpecificationBuilder.build(), PageRequest.of(pageNum, pageSize, sortBuilder.build()));
        } catch(BoopException | InvalidDataAccessApiUsageException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        } catch(PropertyReferenceException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid sort key [" + e.getPropertyName() + "]");
        }
    }
}
