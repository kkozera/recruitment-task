package pl.kkozera.recruitment_task.configuration.mapper;

import org.mapstruct.Mapper;
import pl.kkozera.recruitment_task.dto.complaint.ComplaintRequestDTO;
import pl.kkozera.recruitment_task.dto.complaint.ComplaintResponseDTO;
import pl.kkozera.recruitment_task.model.Complaint;

@Mapper(componentModel = "spring", uses = { CustomerShortMapper.class })
public interface ComplaintMapper {
    Complaint toEntity(ComplaintRequestDTO dto);
    ComplaintResponseDTO toDTO(Complaint complaint);
}