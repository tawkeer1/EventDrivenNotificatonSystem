package com.notify;

import com.notify.dispatcher.EventBus;
import com.notify.event.*;
import com.notify.publisher.UserPublisher;
import com.notify.services.EventHistoryService;
import com.notify.services.ScheduledEventService;
import com.notify.services.TaskService;
import com.notify.subscriber.FilteredSubscriber;
import com.notify.subscriber.Subscriber;
import com.notify.utilities.ConsolePrinter;
import com.notify.utilities.InputUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class Main {
    private static final Logger logger = LoggerFactory.getLogger(Main.class);
    private static final EventBus eventBus = new EventBus();
    private static final TaskService taskService = new TaskService(eventBus);
    private static final ScheduledEventService scheduledEventService = new ScheduledEventService(eventBus);
    private static final Map<String, UserPublisher> publishers = new HashMap<>();

    public static void main(String[] args) {
        scheduledEventService.startScheduledTask("SCHEDULEDEVENT", 60, new UserPublisher("PubScheduler"));

        while (true) {
            logger.info("\n========== Notification System ==========");
            logger.info("1. Publisher Login");
            logger.info("2. Subsriber Login");
            logger.info("3. List Subscribers");
            logger.info("4. View Subscribers by Event Type");
            logger.info("5. View Subscriptions by Subscriber");
            logger.info("0. Exit");
            logger.info("-----------------------------------------");

            int option = InputUtils.promptInt("Enter option: ",
                    i -> i >= 0 && i <= 5,
                    "Please enter a valid option (0–5).",
                    logger);


            switch (option) {
                case 1 -> adminMenu();
                case 2 -> userLogin();
                case 3 -> {
                    List<Subscriber> allSubs = eventBus.listSubscribers();
                    if (allSubs.isEmpty()) {
                        logger.info("No subscribers registered.");
                    } else {
                        allSubs.forEach(sub -> logger.info("[{}] {}", sub.getId(), sub.getName()));
                    }
                }
                case 4 -> {
                    Map<String, List<Subscriber>> map = eventBus.listSubscribersByEventType();
                    if (map.isEmpty()) {
                        logger.info("No subscriptions found.");
                    } else {
                        logger.info("Subscribers grouped by Event Type:");
                        map.forEach((eventType, subs) -> {
                            logger.info("Event: {}", eventType);
                            subs.forEach(sub -> logger.info("  - [{}] {}", sub.getId(), sub.getName()));
                        });
                    }
                }

                case 5 -> {
                    Map<String, Set<String>> map = eventBus.getSubscriberEventMap();
                    if (map.isEmpty()) {
                        logger.info("No subscriber event data found.");
                    } else {
                        logger.info("Event Types grouped by Subscriber:");
                        map.forEach((subId, eventTypes) -> {
                            String name = eventBus.listSubscribers().stream()
                                    .filter(s -> s.getId().equals(subId))
                                    .map(s -> s.getName())
                                    .findFirst().orElse("Unknown");

                            logger.info("[{}] {} -> {}", subId, name, eventTypes);
                        });
                    }
                }

                case 0 -> {
                    logger.info("Shutting down...");
                    eventBus.shutdown();
                    scheduledEventService.shutdown();
                    return;
                }
            }
        }

    }

    private static void adminMenu() {
        while (true) {
            ConsolePrinter.printMenu(logger,
                    "\n---------- Admin Menu ----------",
                    "1. Create Task as Publisher",
                    "2. View All Events",
                    "3. Filter Events by Type",
                    "4. Recent Events",
                    "5. Events From Last Hour",
                    "6. Group Events by Type",
                    "7. Group Events by Hour",
                    "8. Back",
                    "9. Trigger UserRegistered Event",
                    "10. Trigger System Alert",
                    "11. Trigger Reminder",
                    "--------------------------------"
            );

            int choice = InputUtils.promptInt("Enter choice: ",
                    i -> (i >= 1 && i <= 11) || i == 8,
                    "Please enter a valid admin menu option (1–11 or 8 to go back).",
                    logger);

            switch (choice) {
                case 1 -> {
                    String name = InputUtils.promptNonEmpty("Enter your publisher name: ", logger);
                    UserPublisher publisher = publishers.computeIfAbsent(name, UserPublisher::new);

                    String details = InputUtils.promptNonEmpty("Enter task details: ", logger);
                    String priorityInput = InputUtils.promptNonEmpty("Enter priority (HIGH/MEDIUM/LOW): ", logger).toUpperCase();
                    try {
                        Priority priority = Priority.valueOf(priorityInput);
                        taskService.createTask(details, priority, publisher);
                    } catch (IllegalArgumentException e) {
                        logger.warn("Invalid priority. Please enter HIGH, MEDIUM, or LOW.");
                    }
                }
                case 2 -> eventBus.getAllEvents()
                        .forEach(e -> logger.info("[{}] {}", e.getType(), e.getTimeStamp()));
                case 3 -> {
                    String type = InputUtils.promptNonEmpty("Enter event type to filter: ", logger);
                    eventBus.getEventHistory(e -> e.getType().equalsIgnoreCase(type))
                            .forEach(e -> logger.info("[{}] {}", e.getType(), e.getTimeStamp()));
                }
                case 4 -> EventHistoryService.recentEvents(eventBus.getAllEvents(), 5)
                        .forEach(e -> logger.info("Recent [{}] {}", e.getType(), e.getTimeStamp()));
                case 5 -> EventHistoryService.eventsFromLastHours(eventBus.getAllEvents(), 1)
                        .forEach(e -> logger.info("Hour [{}] {}", e.getType(), e.getTimeStamp()));
                case 6 -> {
                    Map<String, List<Event>> grouped = EventHistoryService.groupByType(eventBus.getAllEvents());
                    grouped.forEach((type, list) -> {
                        logger.info("\nType: {} -> {} event(s)", type, list.size());
                        list.forEach(e -> logger.info("  {}", e.getTimeStamp()));
                    });
                }
                case 7 -> {
                    Map<String, List<Event>> grouped = EventHistoryService.groupByHour(eventBus.getAllEvents());
                    grouped.forEach((hour, list) -> {
                        logger.info("\nHour: {} -> {} event(s)", hour, list.size());
                        list.forEach(e -> logger.info("  [{}] {}", e.getType(), e.getTimeStamp()));
                    });
                }
                case 8 -> {
                    logger.info("Returning to main menu...");
                    return;
                }
                case 9 -> {
                    String username = InputUtils.promptNonEmpty("Enter username to register: ", logger);
                    eventBus.publish(new UserRegisteredEvent(username), new UserPublisher("Admin"));
                }
                case 10 -> {
                    String msg = InputUtils.promptNonEmpty("Enter alert message: ", logger);
                    String severity = InputUtils.promptNonEmpty("Enter severity (LOW/MEDIUM/HIGH/): ", logger);
                    eventBus.publish(new SystemAlertEvent(msg, severity), new UserPublisher("SystemMonitor"));
                }
                case 11 -> {
                    String msg = InputUtils.promptNonEmpty("Enter reminder message: ", logger);
                    eventBus.publish(new ReminderEvent(msg), new UserPublisher("ReminderBot"));
                }

            }
        }
    }

    private static void userLogin() {
        String name = InputUtils.prompt(
                "Enter your name: ",
                s -> s != null && !s.isBlank() &&  !s.trim().matches("\\d+"),
                "Name should not be empty or digits",
                logger
        );
        String id = UUID.randomUUID().toString();

        while (true) {
            logger.info("\n--- Choose event type to subscribe ---");
            logger.info("1. TASKADDED");
            logger.info("2. SCHEDULEDEVENT");
            logger.info("3. REMINDEREVENT");
            logger.info("4. SYSTEMALERTEVENT");
            logger.info("5. High Priority Task Subscriber");
            logger.info("6. Working Hour Subscriber");
            logger.info("0. Done Subscribing");

            int choice = InputUtils.promptInt("Enter choice: ",
                    i -> i >= 0 && i <= 6,
                    "Enter a valid event type number (0–6).",
                    logger);

            if (choice == 0) break;

            FilteredSubscriber subscriber;

            switch (choice) {
                case 1 -> {
                    subscriber = new FilteredSubscriber(
                            id, name,
                            e -> e.getType().equalsIgnoreCase("TASKADDED"),
                            e -> logger.info("[{}] received task event: {}", name, e)
                    );
                    eventBus.subscribe("TASKADDED", subscriber);
                }
                case 2 -> {
                    subscriber = new FilteredSubscriber(
                            id, name,
                            e -> e.getType().equalsIgnoreCase("SCHEDULEDEVENT"),
                            e -> logger.info("[{}] received scheduled event: {}", name, e)
                    );
                    eventBus.subscribe("SCHEDULEDEVENT", subscriber);
                }
                case 3 -> {
                    subscriber = new FilteredSubscriber(
                            id, name,
                            e -> e instanceof ReminderEvent,
                            e -> logger.info("[{}] received reminder: {}", name, ((ReminderEvent) e).getMessage())
                    );
                    eventBus.subscribe("REMINDEREVENT", subscriber);
                }
                case 4 -> {
                    subscriber = new FilteredSubscriber(
                            id, name,
                            e -> e instanceof SystemAlertEvent,
                            e -> logger.warn("[{}] received alert: {}", name, ((SystemAlertEvent) e).getMessage())
                    );
                    eventBus.subscribe("SYSTEMALERTEVENT", subscriber);
                }
                case 5 -> {
                    subscriber = new FilteredSubscriber(
                            id, name,
                            e -> e instanceof TaskAddedEvent t && t.getPriority() == Priority.HIGH,
                            e -> logger.info("[{}] received HIGH PRIORITY task: {}", name, e)
                    );
                    eventBus.subscribe("TASKADDED", subscriber);
                }
                case 6 -> {
                    String type = InputUtils.promptNonEmpty("Enter event type for working-hour filter (e.g., TASKADDED): ", logger);
                    subscriber = new FilteredSubscriber(
                            id, name,
                            e -> e.getType().equalsIgnoreCase(type) &&
                                    e.getTimeStamp().getHour() >= 9 &&
                                    e.getTimeStamp().getHour() < 18,
                            e -> logger.info("[{}] received work-hour event: {}", name, e)
                    );
                    eventBus.subscribe(type, subscriber);
                }
            }

            logger.info("Subscribed {} to selected event.\n", name);
        }

        logger.info("Subscription complete. Type 'back' to return.");
        Scanner sc = new Scanner(System.in);
        while (true) {
            String input = sc.nextLine().trim();
            if ("back".equalsIgnoreCase(input)) break;
            logger.info("Waiting for 'back' command...");
        }
    }
}
