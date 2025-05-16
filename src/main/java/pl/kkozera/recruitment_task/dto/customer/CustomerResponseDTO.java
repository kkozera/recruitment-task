package pl.kkozera.recruitment_task.dto.customer;

import pl.kkozera.recruitment_task.dto.complaint.ComplaintShortDTO;

import java.time.LocalDateTime;
import java.util.List;

public record CustomerResponseDTO(
        Long id,
        String name,
        String email,
        List<ComplaintShortDTO> complaints,
        LocalDateTime createdAt) {
}
