package edu.uph.m23si1.microgreens.Model;

/**
 * Satu baris log di {@code microgreens/history/byPlant/{plantId}/{pushId}}.
 */
public class HistoryEvent {

    private String message;
    private Long createdAt;

    public HistoryEvent() {}

    public HistoryEvent(String message, long createdAt) {
        this.message = message;
        this.createdAt = createdAt;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Long createdAt) {
        this.createdAt = createdAt;
    }
}
