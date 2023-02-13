package com.example.SpringBoop.specification;

import com.example.SpringBoop.model.Boop;
import com.example.SpringBoop.utilities.FilterCriteria;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

public class BoopSpecification implements Specification<Boop>  {
    private String key;
    private String operation;
    private Object value;

    public BoopSpecification(String key, String operation, Object value) {
        this.key = key;
        this.operation = operation;
        this.value = value;
    }

    public BoopSpecification(FilterCriteria searchCriteria) {
        this.key = searchCriteria.getKey();
        this.operation = searchCriteria.getOperator();
        this.value = searchCriteria.getValue();
    }

    @Override
    public Predicate toPredicate(Root<Boop> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) throws InvalidDataAccessApiUsageException {
        try {
            if(root.get(key).getJavaType() == String.class) {
                if(operation.equalsIgnoreCase("LIKE"))
                    return criteriaBuilder.like(root.get(key), "%" + value + "%");
            } else if(root.get(key).getJavaType() == Integer.class) {
                if(operation.equals("="))
                    return criteriaBuilder.equal(root.get(key), value.toString());
                else if(operation.equals("<"))
                    return criteriaBuilder.lessThan(root.get(key), value.toString());
                else if(operation.equals("<="))
                    return criteriaBuilder.lessThanOrEqualTo(root.get(key), value.toString());
                else if(operation.equals(">"))
                    return criteriaBuilder.greaterThan(root.get(key), value.toString());
                else if(operation.equals(">="))
                    return criteriaBuilder.greaterThanOrEqualTo(root.get(key), value.toString());
                else if(operation.equals("!="))
                    return criteriaBuilder.notEqual(root.get(key), value.toString());
            }

            throw new InvalidDataAccessApiUsageException("Invalid filter operation [" + operation + "] for key [" + key + "]");
        } catch(NumberFormatException e) {
            throw new InvalidDataAccessApiUsageException("Invalid filter value [" + value + "] for key [" + key + "]");
        } catch(IllegalArgumentException e) {
            throw new InvalidDataAccessApiUsageException("Invalid filter key [" + key + "]");
        }
    }
}
