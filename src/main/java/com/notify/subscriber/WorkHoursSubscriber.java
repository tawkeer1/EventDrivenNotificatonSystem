package com.notify.subscriber;

import com.notify.event.Event;
import com.notify.utilities.BaseLogger;

import java.time.LocalDateTime;

public class WorkHoursSubscriber extends BaseLogger implements Subscriber {
    @Override
    public boolean shouldNotify(Event event){
        int hour = LocalDateTime.now().getHour();
        return hour >= 9 && hour < 17;
    }

    @Override
    public void notify(Event event){
        logger.info("Time specific subscriber notified at: " + LocalDateTime.now());
    }
}
