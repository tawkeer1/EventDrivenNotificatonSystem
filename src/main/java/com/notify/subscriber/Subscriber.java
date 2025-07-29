package com.notify.subscriber;

import com.notify.event.Event;


public interface Subscriber {
    String getId();

    default String getName(){
       return "Anonymous";
   }

    void notify(Event event);

    //by default subscribers are notified for all events
    default boolean shouldNotify(Event event){
        return true;
    }
}
