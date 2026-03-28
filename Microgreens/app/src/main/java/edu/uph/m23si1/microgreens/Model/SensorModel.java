package edu.uph.m23si1.microgreens.Model;

public class SensorModel {

    String name;
    String value;
    boolean status;

    public SensorModel(String name, String value, boolean status) {
        this.name = name;
        this.value = value;
        this.status = status;
    }

    public String getName() {
        return name;
    }

    public String getValue() {
        return value;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }
}
