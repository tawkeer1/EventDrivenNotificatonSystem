import com.notify.dispatcher.EventBus;
import com.notify.event.Event;
import com.notify.event.ScheduledEvent;
import com.notify.publisher.UserPublisher;
import com.notify.services.ScheduledEventService;
import com.notify.subscriber.Subscriber;
import org.junit.jupiter.api.*;

import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

class ScheduledEventServiceTest {

    private EventBus eventBus;
    private ScheduledEventService scheduler;

    @BeforeEach
    void setup() {
        eventBus = new EventBus();
        scheduler = new ScheduledEventService(eventBus);
    }

    @AfterEach
    void tearDown() {
        scheduler.shutdown();
        eventBus.shutdown();
    }

    @Test
    @DisplayName("ScheduledEventService should fire events at fixed intervals")
    void testScheduledEventIsFired() throws InterruptedException {
        AtomicInteger count = new AtomicInteger(0);

        Subscriber subscriber = new Subscriber() {
            @Override
            public void notify(Event event) {
                if (event instanceof ScheduledEvent) {
                    count.incrementAndGet();
                }
            }

            @Override
            public boolean shouldNotify(Event event) {
                return event instanceof ScheduledEvent;
            }

            @Override
            public String getName() { return "Cron"; }

            @Override
            public String getId() { return "scheduler-id"; }
        };

        eventBus.subscribe("SCHEDULEDEVENT", subscriber);
        scheduler.startScheduledTask("SCHEDULEDEVENT", 1, new UserPublisher("TestPublisher"));

        Thread.sleep(3500); // wait ~3.5 sec to receive ~3 events

        assertTrue(count.get() >= 2, "Expected at least 2 scheduled events to be received.");
    }
}
