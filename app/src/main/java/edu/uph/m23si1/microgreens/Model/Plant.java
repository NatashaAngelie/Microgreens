package edu.uph.m23si1.microgreens.Model;

import androidx.annotation.Nullable;

/**
 * POJO for {@code microgreens/plants/{id}} in Firebase Realtime Database.
 */
public class Plant {

    private String plantName;
    private String datePlanted;
    private String dateSprouted;
    private String harvestDate;

    public Plant() {}

    public Plant(
            String plantName,
            String datePlanted,
            @Nullable String dateSprouted,
            @Nullable String harvestDate
    ) {
        this.plantName = plantName;
        this.datePlanted = datePlanted;
        this.dateSprouted = dateSprouted != null ? dateSprouted : "";
        this.harvestDate = harvestDate != null ? harvestDate : "";
    }

    public String getPlantName() {
        return plantName;
    }

    public void setPlantName(String plantName) {
        this.plantName = plantName;
    }

    public String getDatePlanted() {
        return datePlanted;
    }

    public void setDatePlanted(String datePlanted) {
        this.datePlanted = datePlanted;
    }

    public String getDateSprouted() {
        return dateSprouted;
    }

    public void setDateSprouted(String dateSprouted) {
        this.dateSprouted = dateSprouted;
    }

    public String getHarvestDate() {
        return harvestDate;
    }

    public void setHarvestDate(String harvestDate) {
        this.harvestDate = harvestDate;
    }
}
