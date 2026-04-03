package edu.uph.m23si1.microgreens.Model;

import androidx.annotation.NonNull;

import edu.uph.m23si1.microgreens.data.MicrogreensSnapshot;

/**
 * Satu baris tanaman: key Firebase + data + lokasi parent di DB.
 * {@code parentPath} kosong = record langsung di root; {@link MicrogreensSnapshot#REF_ROOT_PLANTS} = di {@code plants/}.
 */
public class PlantListItem {

    private final String id;
    private final Plant plant;
    private final String parentPath;

    public PlantListItem(@NonNull String id, @NonNull Plant plant) {
        this(id, plant, MicrogreensSnapshot.REF_ROOT_PLANTS);
    }

    public PlantListItem(@NonNull String id, @NonNull Plant plant, @NonNull String parentPath) {
        this.id = id;
        this.plant = plant;
        this.parentPath = parentPath;
    }

    @NonNull
    public String getId() {
        return id;
    }

    @NonNull
    public Plant getPlant() {
        return plant;
    }

    /**
     * String kosong = tanaman disimpan di root DB; selain itu segment path (mis. {@code plants}).
     */
    @NonNull
    public String getParentPath() {
        return parentPath;
    }
}
