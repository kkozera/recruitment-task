package pl.kkozera.recruitment_task.persistence;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import pl.kkozera.recruitment_task.exception.ComplaintNotFoundException;
import pl.kkozera.recruitment_task.model.Complaint;

import java.util.Optional;

@Component
public class ComplaintPersistenceService {

    private final ComplaintRepository complaintRepository;

    public ComplaintPersistenceService(ComplaintRepository complaintRepository) {
        this.complaintRepository = complaintRepository;
    }

    public Page<Complaint> findAll(Pageable pageable) {
        return complaintRepository.findAll(pageable);
    }

    public Complaint findById(Long id) {
        return complaintRepository.findById(id)
                .orElseThrow(() -> new ComplaintNotFoundException("Complaint with ID " + id + " not found"));
    }

    public Complaint save(Complaint complaint) {
        return complaintRepository.save(complaint);
    }

    public Optional<Complaint> findByProductIdAndCustomerId(Long productId, Long customerId) {
        return complaintRepository.findByProductIdAndCustomerId(productId, customerId);
    }
}