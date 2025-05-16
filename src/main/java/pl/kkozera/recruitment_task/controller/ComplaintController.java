package pl.kkozera.recruitment_task.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.kkozera.recruitment_task.configuration.annotation.AllowedSortFields;
import pl.kkozera.recruitment_task.dto.PagedResponse;
import pl.kkozera.recruitment_task.dto.complaint.ComplaintPatchDTO;
import pl.kkozera.recruitment_task.dto.complaint.ComplaintRequestDTO;
import pl.kkozera.recruitment_task.dto.complaint.ComplaintResponseDTO;
import pl.kkozera.recruitment_task.service.ComplaintService;

import java.net.URI;

@RestController
@RequestMapping("/api/complaints")
public class ComplaintController {

    private final ComplaintService complaintService;

    public ComplaintController(ComplaintService complaintService) {
        this.complaintService = complaintService;
    }

    @PostMapping
    public ResponseEntity<ComplaintResponseDTO> createComplaint(@RequestBody @Valid ComplaintRequestDTO dto,
                                                                HttpServletRequest request) {

        ComplaintResponseDTO response = complaintService.addComplaint(dto, request);
        return ResponseEntity.created(URI.create("/api/complaints/" + response.id()))
                .body(response);
    }

    @PatchMapping("/{id}/content")
    public ResponseEntity<ComplaintResponseDTO> patchComplaintContent(@PathVariable Long id, @RequestBody @Valid ComplaintPatchDTO dto) {
        return ResponseEntity.ok(complaintService.patchComplaintContent(id, dto));
    }

    @GetMapping
    @AllowedSortFields({"id", "productId", "country", "createdAt"})
    public ResponseEntity<PagedResponse<ComplaintResponseDTO>> getAllComplaints(Pageable pageable) {

        Page<ComplaintResponseDTO> response = complaintService.getAllComplaints(pageable);
        return ResponseEntity.ok(PagedResponse.fromPage(response));
    }
}
