package com.mediscreen.note.model;


import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@Data
@NoArgsConstructor
public class Note {

    @EqualsAndHashCode.Exclude
    @Id
    private String id;

    @NotNull(message="{Note.patientId.mandatory}")
    private Integer patientId;

    @NotBlank(message = "{Note.note.mandatory}")
    private String note;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate createdDate;
}
