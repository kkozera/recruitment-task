package pl.kkozera.recruitment_task.persistence;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;
import pl.kkozera.recruitment_task.exception.ComplaintNotFoundException;
import pl.kkozera.recruitment_task.model.Complaint;

import java.util.List;

@Component
public class ComplaintPersistenceService {

    private final ComplaintRepository complaintRepository;

    public ComplaintPersistenceService(ComplaintRepository complaintRepository) {
        this.complaintRepository = complaintRepository;
    }

    public Page<Complaint> findAll(int page, int size, Sort.Direction sortDirection) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, "createdAt"));
        return complaintRepository.findAll(pageable);
    }

    public Complaint findById(Long id) {
        return complaintRepository.findById(id)
                .orElseThrow(() -> new ComplaintNotFoundException("Complaint with ID " + id + " not found"));
    }

    public Complaint save(Complaint complaint) {
        return complaintRepository.save(complaint);
    }
}