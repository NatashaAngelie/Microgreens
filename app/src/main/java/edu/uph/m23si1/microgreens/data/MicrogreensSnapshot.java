package edu.uph.m23si1.microgreens.data;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.firebase.database.DataSnapshot;

import edu.uph.m23si1.microgreens.Model.PlantCardModel;

/**
 * Satu sumber kebenaran untuk membaca node {@code microgreens} di Realtime Database,
 * dipakai bersama oleh Home (header tanaman) dan History (kartu Current Plant).
 */
public final class MicrogreensSnapshot {

    public static final String REF_MICROGREENS = "microgreens";

    private MicrogreensSnapshot() {}

    @Nullable
    public static String plantName(@NonNull DataSnapshot root) {
        return root.child("plant").child("name").getValue(String.class);
    }

    @Nullable
    public static String lastWatered(@NonNull DataSnapshot root) {
        return root.child("history").child("lastWatered").getValue(String.class);
    }

    @Nullable
    public static String datePlanted(@NonNull DataSnapshot root) {
        return root.child("plant").child("datePlanted").getValue(String.class);
    }

    @Nullable
    public static String dateSprouted(@NonNull DataSnapshot root) {
        return root.child("plant").child("dateSprouted").getValue(String.class);
    }

    @Nullable
    public static String dateHarvested(@NonNull DataSnapshot root) {
        return root.child("plant").child("dateHarvested").getValue(String.class);
    }

    /**
     * Kartu "tanaman aktif" untuk layar History — konsisten dengan nama di header Home.
     */
    @Nullable
    public static PlantCardModel buildCurrentPlantCard(@NonNull DataSnapshot root) {
        String name = plantName(root);
        if (name == null || name.trim().isEmpty()) {
            return null;
        }
        String watered = lastWatered(root);
        String activityLabel = watered != null && !watered.isEmpty()
                ? "Last Activity: " + watered
                : "Last Activity: -";
        String timeLine = watered != null ? watered : "-";
        return new PlantCardModel(name, activityLabel, timeLine);
    }
}
