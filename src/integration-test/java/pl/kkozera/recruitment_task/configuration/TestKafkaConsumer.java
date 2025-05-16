package pl.kkozera.recruitment_task.configuration;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

@Component
public class TestKafkaConsumer {

    private final List<String> receivedMessages = Collections.synchronizedList(new ArrayList<>());
    private CountDownLatch latch = new CountDownLatch(1);

    public void resetLatch(int expectedCount) {
        this.latch = new CountDownLatch(expectedCount);
        this.receivedMessages.clear();
    }

    public List<String> getReceivedMessages() {
        return receivedMessages;
    }

    public boolean awaitMessages(long timeout, TimeUnit unit) throws InterruptedException {
        return latch.await(timeout, unit);
    }

    @KafkaListener(topics = "${app.kafka.topic.complaints-events}", groupId = "complaint-consumer-group")
    public void listen(String message) {
        receivedMessages.add(message);
        latch.countDown();
    }
}