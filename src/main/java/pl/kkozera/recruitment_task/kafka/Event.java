package pl.kkozera.recruitment_task.kafka;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@AllArgsConstructor
@ToString
public class Event {
    private EventType eventType;
    private String customerName;
    private String message;
}
