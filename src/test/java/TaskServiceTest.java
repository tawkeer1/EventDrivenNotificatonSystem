import com.notify.dispatcher.EventBus;
import com.notify.event.Event;
import com.notify.event.Priority;
import com.notify.event.TaskAddedEvent;
import com.notify.publisher.UserPublisher;
import com.notify.services.TaskService;
import com.notify.subscriber.Subscriber;
import org.junit.jupiter.api.*;

import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;

class TaskServiceTest {

    private EventBus eventBus;
    private TaskService taskService;

    @BeforeEach
    void setup() {
        eventBus = new EventBus();
        taskService = new TaskService(eventBus);
    }

    @AfterEach
    void tearDown() {
        eventBus.shutdown();
    }

    @Test
    @DisplayName("TaskService should publish TaskAddedEvent to subscribed listeners")
    void testTaskServicePublishesEvent() throws InterruptedException {
        AtomicReference<TaskAddedEvent> receivedEvent = new AtomicReference<>();

        Subscriber testSubscriber = new Subscriber() {
            @Override
            public void notify(Event event) {
                if (event instanceof TaskAddedEvent taskEvent) {
                    receivedEvent.set(taskEvent);
                }
            }

            @Override
            public boolean shouldNotify(Event event) {
                return event instanceof TaskAddedEvent;
            }

            @Override
            public String getName() { return "Admin"; }

            @Override
            public String getId() { return "admin-id"; }
        };

        eventBus.subscribe("TASKADDED", testSubscriber);

        // Act
        taskService.createTask("Fix login bug", Priority.HIGH,new UserPublisher("Testpublisher"));

        // Wait for async dispatch
        Thread.sleep(200);

        TaskAddedEvent event = receivedEvent.get();
        assertNotNull(event, "Subscriber should receive a TaskAddedEvent.");
        assertEquals("Fix login bug", event.getTaskDetails(), "Task details should match.");
        assertEquals(Priority.HIGH, event.getPriority(), "Priority should match.");
    }
}
