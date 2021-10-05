package com.ast.poc_aws.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "person_data")
@NamedStoredProcedureQuery(name = "PersonData.savePersonDataUsingProcedure",
        procedureName = "STORE_PERSON_DATA", parameters = {
        @StoredProcedureParameter(mode = ParameterMode.IN, name = "name_in", type = String.class),
        @StoredProcedureParameter(mode = ParameterMode.IN, name = "dob_in", type = Date.class),
        @StoredProcedureParameter(mode = ParameterMode.IN, name = "weight_in", type = Integer.class),
        @StoredProcedureParameter(mode = ParameterMode.IN, name = "weight_unit_in", type = String.class),
        @StoredProcedureParameter(mode = ParameterMode.IN, name = "source_in", type = String.class)
        , @StoredProcedureParameter(mode = ParameterMode.INOUT, name = "id_out", type = Integer.class)
})
@Getter
@Setter
@EqualsAndHashCode
public class PersonData {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long personId;
    @Basic
    @Column(name = "name")
    private String name;
    @Basic
    @Column(name = "dob")
    private Date dateOfBirth;
    @Basic
    @Column(name = "weight")
    private int weight;
    @Basic
    @Column(name = "weight_unit")
    private String weightUnit;
    @Basic
    @Column(name = "source")
    private String source;
}
