package pl.kkozera.recruitment_task.configuration;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Component;
import pl.kkozera.recruitment_task.dto.ComplaintRequestDTO;
import pl.kkozera.recruitment_task.dto.ComplaintResponseDTO;
import pl.kkozera.recruitment_task.model.Complaint;

@Mapper(componentModel = "spring")
public interface ComplaintMapper {
    ComplaintMapper INSTANCE = Mappers.getMapper(ComplaintMapper.class);

    Complaint toEntity(ComplaintRequestDTO dto);

    ComplaintResponseDTO toDTO(Complaint complaint);
}