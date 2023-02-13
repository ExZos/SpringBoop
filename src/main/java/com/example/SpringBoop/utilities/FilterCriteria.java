package com.example.SpringBoop.utilities;

import com.example.SpringBoop.exception.BoopException;

import java.util.Map;

public class FilterCriteria {
    private String key;
    private String operator;
    private String value;
    private Boolean orPredicate;

    public FilterCriteria(Map<String, Object> filterMap) throws BoopException {
        try {
            key = filterMap.get("key").toString();
            operator = filterMap.get("operator").toString();
            value = filterMap.get("value").toString();
            orPredicate = filterMap.containsKey("orPredicate") && (Boolean) filterMap.get("orPredicate");
        } catch(NullPointerException e) {
            throw new BoopException("Missing required filter name/value pair");
        } catch(ClassCastException e) {
            throw new BoopException("Invalid filter value in name/value pair");
        }
    }

    public String getKey() { return key; }
    public String getOperator() { return operator; }
    public String getValue() { return value; }
    public Boolean isOrPredicate() { return orPredicate; }
}
