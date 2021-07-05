package com.mediscreen.ui.model;


import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;


import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@Data
@NoArgsConstructor
public class Note {

    @EqualsAndHashCode.Exclude
    private String id;

    @NotNull(message="{Note.patientId.mandatory}")
    private Integer patientId;

    @NotBlank(message = "{Note.note.mandatory}")
    private String note;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate recordDate;

    public Note(@NotNull(message = "{Note.patientId.mandatory}") Integer patientId,
                @NotBlank(message = "{Note.note.mandatory}") String note,
                LocalDate recordDate) {
        this.patientId = patientId;
        this.note = note;
        this.recordDate = recordDate;
    }
}
