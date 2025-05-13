package pl.kkozera.recruitment_task.service;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import pl.kkozera.recruitment_task.configuration.ComplaintMapper;
import pl.kkozera.recruitment_task.dto.ComplaintPatchDTO;
import pl.kkozera.recruitment_task.dto.ComplaintRequestDTO;
import pl.kkozera.recruitment_task.dto.ComplaintResponseDTO;
import pl.kkozera.recruitment_task.external.GeoLocationService;
import pl.kkozera.recruitment_task.model.Complaint;
import pl.kkozera.recruitment_task.persistence.ComplaintPersistenceService;
import pl.kkozera.recruitment_task.validation.ComplaintValidator;

import java.util.Optional;

@Service
public class ComplaintService {

    private final ComplaintPersistenceService persistenceService;
    private final ComplaintMapper complaintMapper;
    private final GeoLocationService geoLocationService;

    public ComplaintService(ComplaintPersistenceService persistenceService, GeoLocationService geoLocationService) {
        this.persistenceService = persistenceService;
        this.complaintMapper = ComplaintMapper.INSTANCE;
        this.geoLocationService = geoLocationService;
    }

    public ComplaintResponseDTO addComplaint(ComplaintRequestDTO dto, HttpServletRequest request) {

        Optional<Complaint> existingComplaintOptional = persistenceService.findByProductIdAndSubmittedBy(dto.getProductId(), dto.getSubmittedBy());
        Complaint complaint;

        if (existingComplaintOptional.isPresent()) {
            complaint = existingComplaintOptional.get();
            complaint.incrementSubmissionCount();
        } else {
            String clientIp = getClientIp(request);
            String country = geoLocationService.getCountryByIp(clientIp);

            complaint = complaintMapper.toEntity(dto);
            complaint.setCountry(country);
        }

        complaint = persistenceService.save(complaint);
        return complaintMapper.toDTO(complaint);
    }

    private String getClientIp(HttpServletRequest request) {
        String xfHeader = request.getHeader("X-Forwarded-For");
        return xfHeader != null ? xfHeader.split(",")[0] : request.getRemoteAddr();
    }

    public ComplaintResponseDTO patchComplaintContent(Long id, ComplaintPatchDTO dto) {
        Complaint complaint = persistenceService.findById(id);
        complaint.setContent(dto.getContent());
        Complaint updated = persistenceService.save(complaint);
        return complaintMapper.toDTO(updated);
    }

    public Page<ComplaintResponseDTO> getAllComplaints(int page, int size, String sortBy, String sortOrder) {
        ComplaintValidator.validateSortField(sortBy);

        return persistenceService.findAll(page, size, sortBy, sortOrder)
                .map(complaintMapper::toDTO);
    }
}