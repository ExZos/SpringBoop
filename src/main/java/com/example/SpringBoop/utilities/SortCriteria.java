package com.example.SpringBoop.utilities;

import com.example.SpringBoop.exception.BoopException;
import org.springframework.data.domain.Sort;

import java.util.Map;

public class SortCriteria {
    private String key;
    private Sort.Direction direction;

    public SortCriteria(Map<String, Object> sortMap) throws BoopException {
        try {
            this.key = sortMap.get("key").toString();
            this.direction = Sort.Direction.fromString(sortMap.get("direction").toString());
        } catch(NullPointerException e) {
            throw new BoopException("Missing required sort name/value pair");
        } catch(IllegalArgumentException e) {
            throw new BoopException("Invalid sort value in name/value pair");
        }
    }

    public String getKey() { return key; }
    public Sort.Direction getDirection() { return direction; }
}
