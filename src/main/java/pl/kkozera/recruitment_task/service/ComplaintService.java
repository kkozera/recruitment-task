package pl.kkozera.recruitment_task.service;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import pl.kkozera.recruitment_task.configuration.ComplaintMapper;
import pl.kkozera.recruitment_task.dto.ComplaintPatchDTO;
import pl.kkozera.recruitment_task.dto.ComplaintRequestDTO;
import pl.kkozera.recruitment_task.dto.ComplaintResponseDTO;
import pl.kkozera.recruitment_task.external.GeoLocationService;
import pl.kkozera.recruitment_task.model.Complaint;
import pl.kkozera.recruitment_task.persistence.ComplaintPersistenceService;

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
        String clientIp = getClientIp(request);
        String country = geoLocationService.getCountryByIp(clientIp);

        Complaint complaint = complaintMapper.toEntity(dto);
        complaint.setCountry(country);

        Complaint saved = persistenceService.save(complaint);
        return complaintMapper.toDTO(saved);
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

    public Page<ComplaintResponseDTO> getAllComplaints(int page, int size, String sortDirection) {
        Page<Complaint> complaints = persistenceService.findAll(page, size, Sort.Direction.fromString(sortDirection));
        return complaints.map(complaintMapper::toDTO);
    }
}