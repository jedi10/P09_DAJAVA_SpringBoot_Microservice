package com.mediscreen.risk.model;

import com.mediscreen.risk.utils.DatesUtils;
import lombok.Getter;
import lombok.Setter;

import java.util.StringJoiner;

@Getter
@Setter
public class Risk {
    private Patient patient;
    private int age;
    private RiskLevelEnum riskLevelEnum;

    public Risk(Patient patient) {
        this.patient = patient;
        setAge(patient);
    }

    private void setAge(Patient patient){
        this.age = DatesUtils.getAge(patient.getBirthDate());
    }

    @Override
    public String toString() {
        StringJoiner strJoiner = new StringJoiner(" ");
        strJoiner.add("Patient:").add(patient.getFirstName()).add(patient.getLastName())
                .add("(age").add(this.age+")")
                .add("diabetes assessment is:").add(riskLevelEnum.getLabel());
        return strJoiner.toString();
    }
}
