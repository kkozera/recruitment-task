package pl.kkozera.recruitment_task.persistence;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.kkozera.recruitment_task.model.Complaint;

import java.util.Optional;

@Repository
public interface ComplaintRepository extends JpaRepository<Complaint, Long> {

    Page<Complaint> findAll(Pageable pageable);

    Optional<Complaint> findByProductIdAndSubmittedBy(Long productId, String submittedBy);
}