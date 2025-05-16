package pl.kkozera.recruitment_task.persistence;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import pl.kkozera.recruitment_task.exception.DuplicateResourceException;
import pl.kkozera.recruitment_task.model.Customer;

@Service
public class CustomerPersistenceService {

    private final CustomerRepository customerRepository;

    public CustomerPersistenceService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    public Page<Customer> findAll(Pageable pageable) {
        return customerRepository.findAll(pageable);
    }

    public Customer save(Customer customer) {
        if (customerRepository.existsByEmail(customer.getEmail())) {
            throw new DuplicateResourceException("Customer with email already exists: " + customer.getEmail());
        }

        return customerRepository.save(customer);
    }

    public Customer findById(Long customerId) {
        return customerRepository.findById(customerId)
                .orElseThrow(() -> new IllegalArgumentException("Customer with id " + customerId + " not found"));
    }
}
