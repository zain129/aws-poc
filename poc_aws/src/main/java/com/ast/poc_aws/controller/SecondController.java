package com.ast.poc_aws.controller;

import com.ast.poc_aws.model.PersonData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.websocket.server.PathParam;
import java.text.SimpleDateFormat;

@RestController
@RequestMapping("/api/2")
@Slf4j
public class SecondController {

    @GetMapping("/sayError")
    public String getError(@PathParam("name") String name) {
        log.info(name + "'s document received a validation error");
        return (name + "'s document received a validation error");
    }

    @PostMapping("/printData")
    public String printData(@RequestBody PersonData person) {
        return (person.getName() + " was born on " + new SimpleDateFormat("ddMMMyyyy").format(person.getDateOfBirth()) +
                " and weighs " + person.getWeight() + person.getWeightUnit());
    }
}
