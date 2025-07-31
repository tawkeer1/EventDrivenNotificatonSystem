package com.notify.event;

public class SystemAlertEvent extends BaseEvent {
    private final String message;
    private final String severity;

    public SystemAlertEvent(String message, String severity) {
        this.message = message;
        this.severity = severity;
    }

    public String getMessage() {
        return message;
    }

    public String getSeverity() {
        return severity;
    }

    @Override
    public String toString() {
        return "SystemAlertEvent{severity='" + severity + "', message='" + message + "'}";
    }
}
