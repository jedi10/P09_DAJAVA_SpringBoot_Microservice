package com.mediscreen.risk.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.StringJoiner;

@Getter
@Setter
@AllArgsConstructor
public class Risk {
    private Patient patient;
    private int age;
    private RiskEnum riskEnum;

    @Override
    public String toString() {
        StringJoiner strJoiner = new StringJoiner(" ");
        strJoiner.add("Patient:").add(patient.getFirstName()).add(patient.getLastName())
                .add("(age").add(this.age+")")
                .add("diabetes assessment is:").add(riskEnum.getLabel());
        return strJoiner.toString();
    }
}
