package pl.kkozera.recruitment_task.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Setter
@Getter
@Entity
@Table(name = "complaint")
public class Complaint {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "product_id", nullable = false)
    private Long productId;

    @Column(name = "content", nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "submitted_by", nullable = false, length = 255)
    private String submittedBy;

    @Column(name = "country", nullable = false, length = 100)
    private String country;

    @Column(name = "submission_count", nullable = false)
    private Integer submissionCount = 1;

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }

    public void incrementSubmissionCount() {
        submissionCount++;
    }
}