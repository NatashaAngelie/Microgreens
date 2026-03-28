package edu.uph.m23si1.microgreens.Model;

public class HistoryModel {
    public enum RowType { HEADER, EVENT }

    private RowType type;

    // Header fields (top card)
    private String plantName;
    private String lastActivityLabel;
    private String lastActivityTime;

    // Event fields (timeline list)
    private String eventTitle;
    private String eventTime;

    public HistoryModel() {}

    public static HistoryModel header(String plantName, String lastActivityLabel, String lastActivityTime) {
        HistoryModel row = new HistoryModel();
        row.type = RowType.HEADER;
        row.plantName = plantName;
        row.lastActivityLabel = lastActivityLabel;
        row.lastActivityTime = lastActivityTime;
        return row;
    }

    public static HistoryModel event(String eventTitle, String eventTime) {
        HistoryModel row = new HistoryModel();
        row.type = RowType.EVENT;
        row.eventTitle = eventTitle;
        row.eventTime = eventTime;
        return row;
    }

    public RowType getType() { return type; }

    public String getPlantName() { return plantName; }
    public String getLastActivityLabel() { return lastActivityLabel; }
    public String getLastActivityTime() { return lastActivityTime; }

    public String getEventTitle() { return eventTitle; }
    public String getEventTime() { return eventTime; }
}
