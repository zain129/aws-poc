package com.ast.poc_aws.service;

import com.ast.poc_aws.model.PersonData;
import com.fasterxml.jackson.core.JsonProcessingException;

import java.text.ParseException;
import java.util.Date;

public interface PersonDataService {
    String getJsonDoc();

    PersonData getPersonDataFromJSON(String json) throws JsonProcessingException, ParseException;

    PersonData insertDirectIntoPG(PersonData personData);

    int insertUsingSpIntoPG(PersonData personData);

    String getNameOnly(String jsonPersonData) throws JsonProcessingException;

    boolean validateData(Date dateOfBirth, int weight);
}
