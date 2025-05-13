package pl.kkozera.recruitment_task.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import pl.kkozera.recruitment_task.dto.ComplaintPatchDTO;
import pl.kkozera.recruitment_task.dto.ComplaintRequestDTO;
import pl.kkozera.recruitment_task.dto.ComplaintResponseDTO;
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

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(response.getId())
                .toUri();

        return ResponseEntity
                .created(location)
                .body(response);
    }

    @PutMapping("/{id}/content")
    public ResponseEntity<ComplaintResponseDTO> patchComplaintContent(@PathVariable Long id, @RequestBody @Valid ComplaintPatchDTO dto) {
        return ResponseEntity.ok(complaintService.patchComplaintContent(id, dto));
    }

    @GetMapping
    public ResponseEntity<Page<ComplaintResponseDTO>> getAllComplaints(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortOrder) {

        Page<ComplaintResponseDTO> response = complaintService.getAllComplaints(page, size, sortBy, sortOrder);
        return ResponseEntity.ok(response);
    }
}
