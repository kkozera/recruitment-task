package pl.kkozera.recruitment_task.persistence;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.kkozera.recruitment_task.model.Complaint;

import java.util.Optional;

@Repository
public interface ComplaintRepository extends JpaRepository<Complaint, Long> {

    @EntityGraph(attributePaths = "customer")
    Page<Complaint> findAll(Pageable pageable);

    Optional<Complaint> findByProductIdAndCustomerId(Long productId, Long customerId);
}