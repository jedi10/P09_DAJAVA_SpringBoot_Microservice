package com.mediscreen.patient.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Past;
import javax.validation.constraints.Size;
import java.time.LocalDate;

@Entity
@Data
@NoArgsConstructor
public class Patient {

        @EqualsAndHashCode.Exclude
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
        private String firstName;

        @Column(name = "last_name", length=60, nullable = false)
        @NotBlank(message = "{Patient.lastName.mandatory}")
        @Size(max=60, message = "{Patient.lastName.size}")
        private String lastName;

        @Column(name = "birth_date", columnDefinition = "TIMESTAMP NULL DEFAULT NULL")
        @JsonFormat(pattern = "yyyy-MM-dd")
        //@NotBlank(message = "{Patient.birthDay.mandatory}")
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

        /**
         * Constructor with params
         * @param sex sex
         * @param firstname firstname
         * @param lastname lastname
         * @param birthdate birthdate
         * @param address address
         * @param phone phone
         */
        public Patient(String sex, String firstname, String lastname, LocalDate birthdate, String address, String phone)
        {
                this.sex = sex;
                this.firstName =  firstname;
                this.lastName = lastname;
                this.address= address;
                this.phone = phone;
                this.birthDate = birthdate;
        }
}
