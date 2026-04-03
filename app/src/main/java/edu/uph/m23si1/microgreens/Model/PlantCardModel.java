package edu.uph.m23si1.microgreens.Model;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import edu.uph.m23si1.microgreens.data.HistoryEventFormatter;

public class PlantCardModel {
    private final String plantId;
    private final String name;
    private final String lastActivityDateLabel;
    private final String lastActivityTime;

    public PlantCardModel(
            @Nullable String plantId,
            @NonNull String name,
            @NonNull String lastActivityDateLabel,
            @NonNull String lastActivityTime
    ) {
        this.plantId = plantId;
        this.name = name;
        this.lastActivityDateLabel = lastActivityDateLabel;
        this.lastActivityTime = lastActivityTime;
    }

    @Nullable
    public String getPlantId() {
        return plantId;
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

    @NonNull
    public static PlantCardModel fromPlantListItem(@NonNull PlantListItem p) {
        String id = p.getId();
        Plant plant = p.getPlant();
        String name = plant.getPlantName() != null ? plant.getPlantName() : "";
        Long at = plant.getLastHistoryAt();
        String msg = plant.getLastHistoryMessage();
        String activity;
        if (at != null && at > 0) {
            activity = "Last Activity: " + HistoryEventFormatter.formatCardActivity(at);
        } else if (msg != null && !msg.trim().isEmpty()) {
            activity = "Last Activity: " + msg.trim();
        } else {
            activity = "Last Activity: -";
        }
        return new PlantCardModel(id, name, activity, "");
    }
}
