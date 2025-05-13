package pl.kkozera.recruitment_task.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Setter
@Getter
public class ComplaintResponseDTO {

    private Long id;
    private Long productId;
    private String content;
    private LocalDateTime createdAt;
    private String submittedBy;
    private String country;
    private Integer submissionCount;
}
