package pl.kkozera.recruitment_task.service;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import pl.kkozera.recruitment_task.configuration.mapper.ComplaintMapper;
import pl.kkozera.recruitment_task.dto.complaint.ComplaintPatchDTO;
import pl.kkozera.recruitment_task.dto.complaint.ComplaintRequestDTO;
import pl.kkozera.recruitment_task.dto.complaint.ComplaintResponseDTO;
import pl.kkozera.recruitment_task.external.GeoLocationService;
import pl.kkozera.recruitment_task.kafka.KafkaProducerService;
import pl.kkozera.recruitment_task.model.Complaint;
import pl.kkozera.recruitment_task.model.Customer;
import pl.kkozera.recruitment_task.persistence.ComplaintPersistenceService;
import pl.kkozera.recruitment_task.persistence.CustomerPersistenceService;

import java.util.Optional;

@Service
public class ComplaintService {

    private final ComplaintPersistenceService complaintPersistenceService;
    private final CustomerPersistenceService customerPersistenceService;
    private final ComplaintMapper complaintMapper;
    private final GeoLocationService geoLocationService;
    private final KafkaProducerService kafkaProducerService;

    public ComplaintService(ComplaintPersistenceService complaintPersistenceService,
                            CustomerPersistenceService customerPersistenceService,
                            ComplaintMapper complaintMapper,
                            GeoLocationService geoLocationService, KafkaProducerService kafkaProducerService) {
        this.complaintPersistenceService = complaintPersistenceService;
        this.customerPersistenceService = customerPersistenceService;
        this.complaintMapper = complaintMapper;
        this.geoLocationService = geoLocationService;
        this.kafkaProducerService = kafkaProducerService;
    }

    public ComplaintResponseDTO addComplaint(ComplaintRequestDTO dto, HttpServletRequest request) {
        Optional<Complaint> existingComplaintOptional = complaintPersistenceService.findByProductIdAndCustomerId(dto.getProductId(), dto.getCustomerId());
        Complaint complaint;

        if (existingComplaintOptional.isPresent()) {
            complaint = existingComplaintOptional.get();
            complaint.incrementSubmissionCount();
        } else {
            complaint = createNewComplaint(dto, request);
        }

        complaint = complaintPersistenceService.save(complaint);
        kafkaProducerService.sendComplaintEvent(complaint);
        return complaintMapper.toDTO(complaint);
    }

    private Complaint createNewComplaint(ComplaintRequestDTO dto, HttpServletRequest request) {
        Customer customer = customerPersistenceService.findById(dto.getCustomerId());
        String country = geoLocationService.fetchClientCountry(request);
        Complaint complaint = complaintMapper.toEntity(dto);
        complaint.setCountry(country);
        complaint.setCustomer(customer);
        return complaint;
    }

    public ComplaintResponseDTO patchComplaintContent(Long id, ComplaintPatchDTO dto) {
        Complaint complaint = complaintPersistenceService.findById(id);
        complaint.setContent(dto.getContent());
        Complaint updated = complaintPersistenceService.save(complaint);
        return complaintMapper.toDTO(updated);
    }

    public Page<ComplaintResponseDTO> getAllComplaints(Pageable pageable) {
        return complaintPersistenceService.findAll(pageable)
                .map(complaintMapper::toDTO);
    }
}