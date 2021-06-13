package com.mediscreen.patient.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Past;
import javax.validation.constraints.Size;
import java.time.LocalDate;

@Entity
@Data
public class Patient {

        @Id
        @GeneratedValue(strategy = GenerationType.AUTO)
        private Integer id;

        @Column(length=1, nullable = false)
        @NotBlank(message = "{Patient.sex.mandatory}")
        @Size(min = 1, max = 1, message = "{Patient.sex.size}")
        private String sex;

        @Column(name="first_name", length=35, nullable = false)
        @Size(max=35, message="{Patient.firstName.size}")
        @NotBlank(message = "{Patient.firstName.mandatory}")
        private String firstname;

        @Column(name = "last_name", length=60, nullable = false)
        @NotBlank(message = "{Patient.lastName.mandatory}")
        @Size(max=60, message = "{Patient.lastName.size}")
        private String lastname;

        @Column(name = "birth_date", columnDefinition = "TIMESTAMP")
        @JsonFormat(pattern = "yyyy-MM-dd")
        @NotNull(message = "{Patient.birthDay.mandatory}")
        @Past(message = "{Patient.birthDay.past}")
        private LocalDate birthDate;

        @Column(length=200, nullable = false)
        @Size(max=200, message="{Patient.address.size}")
        @NotBlank(message = "{Patient.address.mandatory}")
        private String address;

        @Column(length=20, nullable = false)
        @Size(max=20, message="{Patient.phone.size}")
        @NotBlank(message = "{Patient.phone.mandatory}")
        private String phone;

}
