package pl.kkozera.recruitment_task.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
public class ComplaintPatchDTO {

    @NotBlank
    @Size(max = 10000)
    private String content;
}