package pl.kkozera.recruitment_task.configuration.mapper;

import org.mapstruct.Mapper;
import pl.kkozera.recruitment_task.dto.customer.CustomerRequestDTO;
import pl.kkozera.recruitment_task.dto.customer.CustomerResponseDTO;
import pl.kkozera.recruitment_task.model.Customer;

@Mapper(componentModel = "spring", uses = { ComplaintShortMapper.class })
public interface CustomerMapper {
    Customer toEntity(CustomerRequestDTO dto);
    CustomerResponseDTO toDto(Customer customer);
}
