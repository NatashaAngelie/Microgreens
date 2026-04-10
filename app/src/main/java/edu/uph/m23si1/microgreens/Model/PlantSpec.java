package edu.uph.m23si1.microgreens.Model;

/**
 * Firebase model for {@code plantSpecs/{plantTypeId}}.
 */
public class PlantSpec {
    public Double lightHoursPerDay;
    public Double temperatureC;
    public Double soilMoisturePercent;

    public PlantSpec() {}

    public PlantSpec(Double lightHoursPerDay, Double temperatureC, Double soilMoisturePercent) {
        this.lightHoursPerDay = lightHoursPerDay;
        this.temperatureC = temperatureC;
        this.soilMoisturePercent = soilMoisturePercent;
    }
}

