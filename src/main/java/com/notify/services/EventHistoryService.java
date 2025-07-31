package com.notify.services;

import com.notify.event.Event;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class EventHistoryService {
    public static List<Event> recentEvents(List<Event> all, int limit){
        return all.stream().sorted(Comparator.comparing(Event::getTimeStamp).reversed())
                .limit(limit).collect(Collectors.toList());
    }

    public static List<Event> eventsFromLastHours(List<Event> all, int hours) {
        LocalDateTime cutoff = LocalDateTime.now().minusHours(hours);
        return all.stream()
                .filter(e -> e.getTimeStamp().isAfter(cutoff))
                .sorted(Comparator.comparing(Event::getTimeStamp).reversed())
                .collect(Collectors.toList());
    }


    public static Map<String, List<Event>> groupByType(List<Event> events){
        return events.stream().collect(Collectors.groupingBy(Event::getType));
    }

    public static Map<String, List<Event>> groupByHour(List<Event> events) {
        return events.stream()
                .collect(Collectors.groupingBy(e ->
                        e.getTimeStamp().withMinute(0).withSecond(0).withNano(0).toString()
                ));
    }

}
