# Notification System

A Java-based **event-driven notification system** using the **Publisher-Subscriber pattern**. 
This system simulates how users can subscribe to different types of events, and publishers can 
generate events that notify relevant subscribers.

---
### System Design

### Architecture

- **EventBus**: Central message dispatcher that maintains:
    - A queue of events to be dispatched asynchronously
    - Mappings of subscribers by event type
    - History of all events published
- **Event**: Interface implemented by various concrete event types:
    - `TaskAddedEvent`
    - `ReminderEvent`
    - `SystemAlertEvent`
    - `ScheduledEvent`
- **Publisher**: Generates events. Includes `UserPublisher` with a name and type.
- **Subscriber**: Handles received events. Can be:
    - Basic subscriber: listens to all events of a type
    - Filtered subscriber: only responds if certain conditions are met (e.g., high priority, work hours)
- **Services**:
    - `TaskService`: Creates and publishes `TaskAddedEvent`
    - `ScheduledEventService`: Uses `ScheduledExecutorService` to periodically publish events

###  Features

- Multiple event types
- Event filtering (by priority, time, etc.)
- Asynchronous dispatch
- Scheduled publishing
- Subscriber-event mapping view
- Admin & user menus in CLI
- Full event history with filtering & grouping

---

## How to Run the App

### Prerequisites

- Java 17+
- Maven 3.6+

### Steps

1. **Clone the Repository**

```bash
git clone https://github.com/tawkeer1/notificationsystem.git
cd notificationsystem

2. **Build the project**
    use this command to build the project:
       mvn clean install

3. Running the project
    mvn exec:java -Dexec.mainClass="com.notify.Main"

Then after running main class we can see menu showing various options like:
 - Publisher menu to add various types of events
 - Subscriber menu to subscribe to events

## How to run tests
   mvn test
Test Coverage Includes:
EventBusTest: verifies event dispatch and subscriber delivery

FilteredSubscriberTest: ensures filter logic works for matching/non-matching events

TaskServiceTest: validates event publishing through service

ScheduledEventServiceTest: tests scheduled task event emission

EdgeCaseTest: handles null events, duplicate subscribers, etc.

PublisherTest: validates publisher attributes

After tests run, results can be found in
    target/surefire-reports/
