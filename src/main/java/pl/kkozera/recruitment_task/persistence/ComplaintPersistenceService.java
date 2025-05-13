package pl.kkozera.recruitment_task.persistence;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;
import pl.kkozera.recruitment_task.exception.ComplaintNotFoundException;
import pl.kkozera.recruitment_task.model.Complaint;

import java.util.List;
import java.util.Optional;

@Component
public class ComplaintPersistenceService {

    private final ComplaintRepository complaintRepository;

    public ComplaintPersistenceService(ComplaintRepository complaintRepository) {
        this.complaintRepository = complaintRepository;
    }

    public Page<Complaint> findAll(int page, int size, String sortBy, String sortOrder) {
        Sort.Direction direction = sortOrder.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));
        return complaintRepository.findAll(pageable);
    }

    public Complaint findById(Long id) {
        return complaintRepository.findById(id)
                .orElseThrow(() -> new ComplaintNotFoundException("Complaint with ID " + id + " not found"));
    }

    public Complaint save(Complaint complaint) {
        return complaintRepository.save(complaint);
    }

    public Optional<Complaint> findByProductIdAndSubmittedBy(Long productId, String submittedBy) {
        return complaintRepository.findByProductIdAndSubmittedBy(productId, submittedBy);
    }
}