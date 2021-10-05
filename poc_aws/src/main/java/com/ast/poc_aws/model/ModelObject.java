package com.ast.poc_aws.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class ModelObject {
    @JsonProperty("name")
    public String name;
    @JsonProperty("birthdate")
    public String dob;
    @JsonProperty("weight")
    public String weight;
    @JsonProperty("weight-unit")
    public String weightUnit;
}
