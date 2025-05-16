package pl.kkozera.recruitment_task.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import pl.kkozera.recruitment_task.dto.customer.CustomerRequestDTO;
import pl.kkozera.recruitment_task.dto.customer.CustomerResponseDTO;
import pl.kkozera.recruitment_task.model.Customer;
import pl.kkozera.recruitment_task.persistence.CustomerPersistenceService;
import pl.kkozera.recruitment_task.configuration.mapper.CustomerMapper;

@Service
public class CustomerService {

    private final CustomerPersistenceService customerPersistenceService;
    private final CustomerMapper customerMapper;

    public CustomerService(CustomerPersistenceService customerPersistenceService, CustomerMapper customerMapper) {
        this.customerPersistenceService = customerPersistenceService;
        this.customerMapper = customerMapper;
    }

    public CustomerResponseDTO createCustomer(CustomerRequestDTO dto) {
        Customer customer = customerMapper.toEntity(dto);
        return customerMapper.toDto(customerPersistenceService.save(customer));
    }

    public Page<CustomerResponseDTO> getAllCustomers(Pageable pageable) {
        return customerPersistenceService.findAll(pageable)
                .map(customerMapper::toDto);
    }
}