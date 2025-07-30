package com.notify.services;

import com.notify.event.Event;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class EventHistoryService {
    public static List<Event> recentEvents(List<Event> all, int limit){
        return all.stream().sorted(Comparator.comparing(Event::getTimeStamp).reversed())
                .limit(limit).collect(Collectors.toList());
    }

    public static Map<String, List<Event>> groupByType(List<Event> events){
        return events.stream().collect(Collectors.groupingBy(Event::getType));
    }
}
