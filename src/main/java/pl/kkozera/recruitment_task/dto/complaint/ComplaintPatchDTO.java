package pl.kkozera.recruitment_task.dto.complaint;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ComplaintPatchDTO {

    @NotBlank(message = "Content is required")
    @Size(max = 10000)
    private String content;
}