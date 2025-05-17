package pl.kkozera.recruitment_task.kafka;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import pl.kkozera.recruitment_task.model.Complaint;

@Slf4j
@Service
public class KafkaProducerService {
    private final KafkaTemplate<String, Event> kafkaTemplate;
    private final String complaintsEventsTopic;

    public KafkaProducerService(KafkaTemplate<String, Event> kafkaTemplate,
                                @Value("${app.kafka.topic.complaints-events}") String complaintsEventsTopic) {
        this.kafkaTemplate = kafkaTemplate;
        this.complaintsEventsTopic = complaintsEventsTopic;
    }

    public void sendComplaintEvent(Complaint complaint) {
        Event event = new Event(EventType.SEND_EMAIL, complaint.getCustomer().getName(), complaint.getCustomer().getEmail());
        log.info("Sending complaint event: {}", event);
        kafkaTemplate.send(complaintsEventsTopic, event);
    }
}
