package edu.uph.m23si1.microgreens.Model;

public class PlantCardModel {
    private final String name;
    private final String lastActivityDateLabel;
    private final String lastActivityTime;

    public PlantCardModel(String name, String lastActivityDateLabel, String lastActivityTime) {
        this.name = name;
        this.lastActivityDateLabel = lastActivityDateLabel;
        this.lastActivityTime = lastActivityTime;
    }

    public String getName() {
        return name;
    }

    public String getLastActivityDateLabel() {
        return lastActivityDateLabel;
    }

    public String getLastActivityTime() {
        return lastActivityTime;
    }
}
