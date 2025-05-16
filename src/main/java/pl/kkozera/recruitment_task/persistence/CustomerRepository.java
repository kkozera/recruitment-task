package pl.kkozera.recruitment_task.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.kkozera.recruitment_task.model.Customer;

public interface CustomerRepository extends JpaRepository<Customer, Long> {
    boolean existsByEmail(String email);
}
