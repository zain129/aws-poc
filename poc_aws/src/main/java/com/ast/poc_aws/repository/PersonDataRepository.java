package com.ast.poc_aws.repository;

import com.ast.poc_aws.model.PersonData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.List;

public interface PersonDataRepository extends JpaRepository<PersonData, Long> {
    List<PersonData> findByName(String name);

    @Query(value = "CALL STORE_PERSON_DATA(:name, :dob, :weight, :weightUnit, :source, :row_id);", nativeQuery = true)
    int savePersonDataUsingProcedure(@Param("name") String name, @Param("dob") Date dob,
                                      @Param("weight") int weight, @Param("weightUnit") String weightUnit,
                                      @Param("source") String source, @Param("row_id") int row_id);
}