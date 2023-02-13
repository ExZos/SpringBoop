package com.example.SpringBoop.utilities;

import org.springframework.data.domain.Sort;

import java.util.List;

public class SortBuilder {
    private final List<SortCriteria> sortCriterias;

    public SortBuilder(List<SortCriteria> sortCriterias) {
        this.sortCriterias = sortCriterias;
    }

    public Sort build() {
        if(sortCriterias.isEmpty())
            return Sort.unsorted();

        Sort result = Sort.by(sortCriterias.get(0).getDirection(), sortCriterias.get(0).getKey());
        for(int i = 1; i < sortCriterias.size(); i++)
            result = result.and(Sort.by(sortCriterias.get(i).getDirection(), sortCriterias.get(i).getKey()));

        return result;
    }
}
