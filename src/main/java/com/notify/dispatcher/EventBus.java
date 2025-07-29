package com.notify.dispatcher;

import com.notify.event.Event;
import com.notify.subscriber.Subscriber;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CopyOnWriteArrayList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EventBus {
    private static final Logger logger = LoggerFactory.getLogger(EventBus.class.getName());
    //CopyOnWriteArrayList is ideal for situtions like pub/sub
    private final List<Subscriber> subscribers = new CopyOnWriteArrayList<>();
    private final List<Event> eventHistory = new CopyOnWriteArrayList<>();

    public void subscribe(Subscriber subscriber) {
        boolean alreadyExists = subscribers.stream().anyMatch(s -> s.getId().equals(subscriber.getId()));
        if(alreadyExists){
            logger.warn("Subscriber with Id: " + subscriber.getId() + " already exists");
            return;
        }
        subscribers.add(subscriber);
        logger.info("Registered new subscriber: " + subscriber.getName() + " with Id: " + subscriber.getId() );
    }

    public void unsubscribeById(String id){
        boolean removed = subscribers.removeIf(s -> s.getId().equals(id));
        if(removed){
            logger.info("Removed subscriber from list: " + id);
        } else {
            logger.warn("No subscriber found with such ID: " + id);
        }
    }

    //publish an event to matching subscribers
    public void publish(Event event){
        // add event to history
        eventHistory.add(event);
        logger.info("Publishing an event: " + event.getType() + " at " + event.getTimeStamp());

        for(Subscriber subscriber : subscribers){
            //check if subscriber should be notified for this event
            if(subscriber.shouldNotify(event)){
                logger.info("notifying the subscriber: " + subscriber.getName() + " id: " + subscriber.getId());
                try{
                    // completablefuture is used for
                    // non-blocking publishing and parallel delivery
                    CompletableFuture.runAsync(() -> subscriber.notify(event));
                }catch(Exception e){
                    logger.error("Failed to deliver event to: " + subscriber.getName() ,e);
                }
            }
        }
    }
}
