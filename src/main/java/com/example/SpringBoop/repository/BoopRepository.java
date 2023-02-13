package com.example.SpringBoop.repository;

import com.example.SpringBoop.model.Boop;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;

public interface BoopRepository extends CrudRepository<Boop, Integer>, JpaSpecificationExecutor<Boop> {
}
