package pl.kkozera.recruitment_task.kafka;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class Event {
    private EventType eventType;
    private String customerName;
    private String message;
}
