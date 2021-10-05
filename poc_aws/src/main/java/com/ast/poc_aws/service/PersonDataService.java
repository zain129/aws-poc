package com.ast.poc_aws.service;

import com.ast.poc_aws.model.PersonData;
import com.fasterxml.jackson.core.JsonProcessingException;

import java.text.ParseException;

public interface PersonDataService {
    String getJsonDoc();

    boolean validateData(String personData);

    PersonData getPersonDataFromJSON(String json) throws JsonProcessingException, ParseException;

    PersonData insertDirectIntoPG(PersonData personData);

    int insertUsingSpIntoPG(PersonData personData);

    String getNameOnly(String jsonPersonData) throws JsonProcessingException;
}
