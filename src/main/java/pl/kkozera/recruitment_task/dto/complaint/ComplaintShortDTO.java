package pl.kkozera.recruitment_task.dto.complaint;

import java.time.LocalDateTime;

public record ComplaintShortDTO(
        Long id,
        Long productId,
        String content,
        LocalDateTime createdAt,
        String country,
        Integer submissionCount
) {}
