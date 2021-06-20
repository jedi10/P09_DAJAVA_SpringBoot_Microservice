package com.mediscreen.ui.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Past;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import com.fasterxml.jackson.annotation.JsonFormat;

@Data
@NoArgsConstructor
public class Patient {

    private Integer id;

    @NotBlank(message = "{Patient.sex.mandatory}")
    @Size(min = 1, max = 1, message = "{Patient.sex.size}")
    private String sex;

    @Size(max=35, message="{Patient.firstName.size}")
    @NotBlank(message = "{Patient.firstName.mandatory}")
    private String firstName;

    @NotBlank(message = "{Patient.lastName.mandatory}")
    @Size(max=60, message = "{Patient.lastName.size}")
    private String lastName;

    @JsonFormat(pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @NotNull(message = "{Patient.birthDay.mandatory}")
    @Past(message = "{Patient.birthDay.past}")
    private LocalDate birthDate;

    @Size(max=200, message="{Patient.address.size}")
    @NotBlank(message = "{Patient.address.mandatory}")
    private String address;

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
