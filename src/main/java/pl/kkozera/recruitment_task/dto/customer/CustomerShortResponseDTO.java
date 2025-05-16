package pl.kkozera.recruitment_task.dto.customer;

import java.time.LocalDateTime;

public record CustomerShortResponseDTO(
        Long id,
        String name,
        String email,
        LocalDateTime createdAt) {
}
