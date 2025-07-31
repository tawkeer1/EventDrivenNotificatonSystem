import com.notify.dispatcher.EventBus;
import com.notify.event.Event;
import com.notify.publisher.UserPublisher;
import com.notify.subscriber.Subscriber;
import org.junit.jupiter.api.*;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.*;

class EdgeCaseTest {

    private EventBus eventBus;

    @BeforeEach
    void init() {
        eventBus = new EventBus();
    }

    @AfterEach
    void cleanup() {
        eventBus.shutdown();
    }

    @Test
    @DisplayName("Publishing a null event should not throw any exception")
    void testNullEventIsIgnored() {
        assertDoesNotThrow(() -> eventBus.publish(null,new UserPublisher("TestPublisher")), "Publishing null event should not crash.");
    }

    @Test
    @DisplayName("Duplicate subscriber IDs should not result in multiple registrations")
    void testDuplicateSubscriberIsIgnored() {
        Subscriber s1 = new DummySubscriber("id-1", "D1");
        Subscriber s2 = new DummySubscriber("id-1", "D1");

        eventBus.subscribe("TASKADDED", s1);
        eventBus.subscribe("TASKADDED", s2); // same ID

        // Flatten all subscribers across event types
        List<Subscriber> allSubscribers = eventBus.listSubscribersByEventType().values().stream()
                .flatMap(Collection::stream)
                .toList();

        Set<String> uniqueIds = new HashSet<>();
        for (Subscriber s : allSubscribers) {
            uniqueIds.add(s.getId());
        }

        assertEquals(1, uniqueIds.size(), "Should only allow one subscriber per unique ID.");
    }

    // Dummy subscriber used in tests
    static class DummySubscriber implements Subscriber {
        private final String id;
        private final String name;

        public DummySubscriber(String id, String name) {
            this.id = id;
            this.name = name;
        }

        @Override public void notify(Event event) {}
        @Override public boolean shouldNotify(Event event) { return true; }
        @Override public String getName() { return name; }
        @Override public String getId() { return id; }
    }
}
