package com.notify.event;

import java.time.LocalDateTime;
import java.util.UUID;

public interface Event {
    UUID getId();
    String getType();
    LocalDateTime getTimeStamp();
}
