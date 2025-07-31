
import com.notify.dispatcher.EventBus;
import com.notify.event.TaskAddedEvent;
import com.notify.event.Priority;
import com.notify.publisher.UserPublisher;
import com.notify.subscriber.FilteredSubscriber;
import com.notify.subscriber.Subscriber;
import org.junit.jupiter.api.*;

import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.*;

class EventBusTest {

    private EventBus eventBus;

    @BeforeEach
    void setup() {
        eventBus = new EventBus();
    }

    @AfterEach
    void tearDown() {
        eventBus.shutdown();
    }

    @Test
    void testSubscribeAndPublishTaskEvent() {
        AtomicBoolean notified = new AtomicBoolean(false);

        Subscriber subscriber = new FilteredSubscriber(
                "sub1", "Alice",
                event -> event.getType().equals("TASKADDED"),
                event -> notified.set(true)
        );

        eventBus.subscribe("TASKADDED", subscriber);

        UserPublisher publisher = new UserPublisher("Tester");
        TaskAddedEvent event = new TaskAddedEvent("Fix bug", Priority.HIGH);
        eventBus.publish(event, publisher);

        // Wait for async dispatch
        try { Thread.sleep(100); } catch (InterruptedException ignored) {}

        assertTrue(notified.get(), "Subscriber should be notified");
    }
}
