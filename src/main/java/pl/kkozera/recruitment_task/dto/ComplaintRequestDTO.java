package pl.kkozera.recruitment_task.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ComplaintRequestDTO {

    public ComplaintRequestDTO(Long productId, String content, String submittedBy) {
        this.productId = productId;
        this.content = content;
        this.submittedBy = submittedBy;
    }

    @NotNull
    private Long productId;

    @NotBlank
    @Size(max = 10000)
    private String content;

    @NotBlank
    @Size(max = 255)
    private String submittedBy;
}