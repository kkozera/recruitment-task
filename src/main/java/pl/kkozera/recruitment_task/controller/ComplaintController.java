package pl.kkozera.recruitment_task.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.kkozera.recruitment_task.dto.ComplaintPatchDTO;
import pl.kkozera.recruitment_task.dto.ComplaintRequestDTO;
import pl.kkozera.recruitment_task.dto.ComplaintResponseDTO;
import pl.kkozera.recruitment_task.service.ComplaintService;

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
        return ResponseEntity.ok(complaintService.addComplaint(dto, request));
    }

    @PutMapping("/{id}/content")
    public ResponseEntity<ComplaintResponseDTO> patchComplaintContent(@PathVariable Long id, @RequestBody @Valid ComplaintPatchDTO dto) {
        return ResponseEntity.ok(complaintService.patchComplaintContent(id, dto));
    }

    @GetMapping
    public ResponseEntity<Page<ComplaintResponseDTO>> getAllComplaints(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy) {

        Page<ComplaintResponseDTO> complaints = complaintService.getAllComplaints(page, size, sortBy);
        return ResponseEntity.ok(complaints);
    }
}
