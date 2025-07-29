package com.notify.subscriber;

import com.notify.event.Event;

@FunctionalInterface
public interface Subscriber {
    void notify(Event event);

    //by default subscribers are notified for all events
    default boolean shouldNotify(Event event){
        return true;
    }
}
git init
//git add README.md
//git commit -m "first commit"
//git branch -M main
//git remote add origin https://github.com/tawkeer1/EventDrivenNotificatonSystem.git
//git push -u origin main