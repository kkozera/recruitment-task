package pl.kkozera.recruitment_task.controller;

import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.kkozera.recruitment_task.configuration.annotation.AllowedSortFields;
import pl.kkozera.recruitment_task.dto.PagedResponse;
import pl.kkozera.recruitment_task.dto.customer.CustomerRequestDTO;
import pl.kkozera.recruitment_task.dto.customer.CustomerResponseDTO;
import pl.kkozera.recruitment_task.service.CustomerService;

import java.net.URI;

@RestController
@RequestMapping("/api/customers")
public class CustomerController {

    private final CustomerService customerService;

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @PostMapping
    public ResponseEntity<CustomerResponseDTO> createCustomer(@Valid @RequestBody CustomerRequestDTO dto) {
        CustomerResponseDTO response = customerService.createCustomer(dto);
        return ResponseEntity.created(URI.create("/api/customers/" + response.id()))
                .body(response);
    }

    @GetMapping
    @AllowedSortFields({"id", "name", "email", "createdAt"})
    public ResponseEntity<PagedResponse<CustomerResponseDTO>> getAllComplaints(Pageable pageable) {
        Page<CustomerResponseDTO> response = customerService.getAllCustomers(pageable);
        return ResponseEntity.ok(PagedResponse.fromPage(response));
    }
}
