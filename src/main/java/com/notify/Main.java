package com.notify;

import com.notify.dispatcher.EventBus;
import com.notify.event.Event;
import com.notify.event.ScheduledEvent;
import com.notify.utilities.BaseLogger;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) {
        EventBus eventBus = new EventBus();
        ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
        scheduler.scheduleAtFixedRate(() -> {
            Event scheduledEvent = new ScheduledEvent("SCHEDULED_EVENT");
            eventBus.publish(scheduledEvent);
        },0,10, TimeUnit.SECONDS);
    }
}