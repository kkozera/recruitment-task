package pl.kkozera.recruitment_task.persistence;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import pl.kkozera.recruitment_task.exception.DuplicateResourceException;
import pl.kkozera.recruitment_task.model.Customer;

@Service
@Slf4j
public class CustomerPersistenceService {

    public static final String CUSTOMER_WITH_EMAIL_ALREADY_EXISTS = "Customer with email already exists: ";
    private final CustomerRepository customerRepository;

    public CustomerPersistenceService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    public Page<Customer> findAll(Pageable pageable) {
        return customerRepository.findAll(pageable);
    }

    public Customer save(Customer customer) {
        if (customerRepository.existsByEmail(customer.getEmail())) {
            log.info(CUSTOMER_WITH_EMAIL_ALREADY_EXISTS + customer.getEmail());
            throw new DuplicateResourceException(CUSTOMER_WITH_EMAIL_ALREADY_EXISTS + customer.getEmail());
        }

        log.info("Saving customer: {}", customer);
        return customerRepository.save(customer);
    }

    public Customer findById(Long customerId) {
        return customerRepository.findById(customerId)
                .orElseThrow(() -> new IllegalArgumentException("Customer with id " + customerId + " not found"));
    }
}
