package edu.uph.m23si1.microgreens.Model;

import androidx.annotation.NonNull;

/** One row in Manage Plants: Firebase push key + stored {@link Plant}. */
public class PlantListItem {

    private final String id;
    private final Plant plant;

    public PlantListItem(@NonNull String id, @NonNull Plant plant) {
        this.id = id;
        this.plant = plant;
    }

    @NonNull
    public String getId() {
        return id;
    }

    @NonNull
    public Plant getPlant() {
        return plant;
    }
}
