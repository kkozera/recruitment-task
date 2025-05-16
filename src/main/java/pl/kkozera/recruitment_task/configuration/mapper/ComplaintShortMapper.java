package pl.kkozera.recruitment_task.configuration.mapper;

import org.mapstruct.Mapper;
import pl.kkozera.recruitment_task.dto.complaint.ComplaintShortDTO;
import pl.kkozera.recruitment_task.model.Complaint;

@Mapper(componentModel = "spring")
public interface ComplaintShortMapper {
    ComplaintShortDTO toDto(Complaint complaint);
}