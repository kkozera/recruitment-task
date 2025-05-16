package pl.kkozera.recruitment_task.dto.complaint;

import pl.kkozera.recruitment_task.dto.customer.CustomerShortResponseDTO;

import java.time.LocalDateTime;

public record ComplaintResponseDTO(
        Long id,
        Long productId,
        String content,
        LocalDateTime createdAt,
        CustomerShortResponseDTO customer,
        String country,
        Integer submissionCount
) {}
