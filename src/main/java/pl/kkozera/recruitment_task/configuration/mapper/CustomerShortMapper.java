package pl.kkozera.recruitment_task.configuration.mapper;

import org.mapstruct.Mapper;
import pl.kkozera.recruitment_task.dto.customer.CustomerResponseDTO;
import pl.kkozera.recruitment_task.dto.customer.CustomerShortResponseDTO;
import pl.kkozera.recruitment_task.model.Customer;

@Mapper(componentModel = "spring")
public interface CustomerShortMapper {
    Customer toEntity(CustomerShortResponseDTO dto);
    CustomerResponseDTO toDto(Customer customer);
}
