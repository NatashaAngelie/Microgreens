package edu.uph.m23si1.microgreens.Model;

public class HistoryModel {

    String plantName;
    String datePlanted;
    String dateSprouted;
    String dateHarvested;

    String lastWatered;

    String lampStatus;
    String lampChanged;

    String fanStatus;
    String fanChanged;

    public HistoryModel() {}

    public HistoryModel(String plantName, String datePlanted, String dateSprouted,
                        String dateHarvested, String lastWatered,
                        String lampStatus, String lampChanged,
                        String fanStatus, String fanChanged) {

        this.plantName = plantName;
        this.datePlanted = datePlanted;
        this.dateSprouted = dateSprouted;
        this.dateHarvested = dateHarvested;
        this.lastWatered = lastWatered;
        this.lampStatus = lampStatus;
        this.lampChanged = lampChanged;
        this.fanStatus = fanStatus;
        this.fanChanged = fanChanged;
    }

    public String getPlantName() { return plantName; }
    public String getDatePlanted() { return datePlanted; }
    public String getDateSprouted() { return dateSprouted; }
    public String getDateHarvested() { return dateHarvested; }
    public String getLastWatered() { return lastWatered; }
    public String getLampStatus() { return lampStatus; }
    public String getLampChanged() { return lampChanged; }
    public String getFanStatus() { return fanStatus; }
    public String getFanChanged() { return fanChanged; }
}