package pl.kkozera.recruitment_task.dto.complaint;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ComplaintRequestDTO {

    @NotNull(message = "Product identifier is required")
    private Long productId;

    @NotBlank(message = "Content is required")
    @Size(max = 10000)
    private String content;

    @NotNull(message = "Customer identifier is required")
    private Long customerId;
}