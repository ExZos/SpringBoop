package com.example.SpringBoop.specification;

import com.example.SpringBoop.utilities.FilterCriteria;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;

public class BoopSpecificationBuilder {
    private final List<FilterCriteria> filterCriterias;

    public BoopSpecificationBuilder(List<FilterCriteria> filterCriterias) {
        this.filterCriterias = filterCriterias;
    }

    public Specification build() {
        if(filterCriterias.isEmpty())
            return null;

        Specification result = new BoopSpecification(filterCriterias.get(0));
        for(int i = 1; i < filterCriterias.size(); i++) {
            if(filterCriterias.get(i).isOrPredicate())
                result = Specification.where(result).or(new BoopSpecification(filterCriterias.get(i)));
            else
                result = Specification.where(result).and(new BoopSpecification(filterCriterias.get(i)));
        }

        return result;
    }
}
