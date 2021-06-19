package com.mediscreen.ui.model;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Past;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import com.fasterxml.jackson.annotation.JsonFormat;

@Data
public class Patient {

    private Integer id;

    @NotBlank(message = "{Patient.sex.mandatory}")
    @Size(min = 1, max = 1, message = "{Patient.sex.size}")
    private String sex;

    @Size(max=35, message="{Patient.firstName.size}")
    @NotBlank(message = "{Patient.firstName.mandatory}")
    private String firstname;

    @NotBlank(message = "{Patient.lastName.mandatory}")
    @Size(max=60, message = "{Patient.lastName.size}")
    private String lastname;

    @JsonFormat(pattern = "yyyy-MM-dd")
    @NotNull(message = "{Patient.birthDay.mandatory}")
    @Past(message = "{Patient.birthDay.past}")
    private LocalDate birthDate;

    @Size(max=200, message="{Patient.address.size}")
    @NotBlank(message = "{Patient.address.mandatory}")
    private String address;

    @Size(max=20, message="{Patient.phone.size}")
    @NotBlank(message = "{Patient.phone.mandatory}")
    private String phone;

}
