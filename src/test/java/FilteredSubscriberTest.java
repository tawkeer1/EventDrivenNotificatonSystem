import com.notify.dispatcher.EventBus;
import com.notify.event.Event;
import com.notify.event.Priority;
import com.notify.event.TaskAddedEvent;
import com.notify.publisher.UserPublisher;
import com.notify.subscriber.FilteredSubscriber;
import org.junit.jupiter.api.*;

import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.*;

class FilteredSubscriberTest {

    private EventBus eventBus;

    @BeforeEach
    void setup() {
        eventBus = new EventBus();
    }

    @AfterEach
    void cleanup() {
        eventBus.shutdown();
    }

    @Test
    @DisplayName("FilteredSubscriber should receive matching high-priority TaskAddedEvent")
    void testFilteredSubscriberReceivesMatchingEvent() throws InterruptedException {
        AtomicBoolean received = new AtomicBoolean(false);

        FilteredSubscriber subscriber = new FilteredSubscriber(
                "sub-1",
                "Alice",
                event -> event instanceof TaskAddedEvent task && task.getPriority() == Priority.HIGH,
                event -> received.set(true)
        );

        eventBus.subscribe("TASKADDED", subscriber); // match actual event type
        eventBus.publish(new TaskAddedEvent("Urgent", Priority.HIGH), new UserPublisher("TestPublisher"));

        Thread.sleep(300); // allow for async delivery

        assertTrue(received.get(), "FilteredSubscriber should receive matching event.");
    }

    @Test
    @DisplayName("FilteredSubscriber should ignore non-matching TaskAddedEvent")
    void testFilteredSubscriberIgnoresNonMatchingEvent() throws InterruptedException {
        AtomicBoolean received = new AtomicBoolean(false);

        FilteredSubscriber subscriber = new FilteredSubscriber(
                "sub-2",
                "Bob",
                event -> false,  // never match
                event -> received.set(true)
        );

        eventBus.subscribe("TASKADDED", subscriber);
        eventBus.publish(new TaskAddedEvent("Not important", Priority.LOW), new UserPublisher("TestPublisher"));

        Thread.sleep(300);

        assertFalse(received.get(), "FilteredSubscriber should ignore non-matching event.");
    }
}
