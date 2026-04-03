package edu.uph.m23si1.microgreens.data;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.uph.m23si1.microgreens.Model.HistoryEvent;
import edu.uph.m23si1.microgreens.Model.PlantListItem;

/**
 * Menulis log ke {@code microgreens/history/byPlant/{plantId}/...} dan memperbarui ringkasan di record tanaman.
 */
public final class MicrogreensHistoryWriter {

    private MicrogreensHistoryWriter() {}

    @NonNull
    public static DatabaseReference historyEventsRef(@NonNull String plantId) {
        return AppFirebaseDatabase.get()
                .getReference(MicrogreensSnapshot.REF_MICROGREENS)
                .child("history")
                .child("byPlant")
                .child(plantId);
    }

    public static void logPlantCreated(@NonNull String plantId, @NonNull String plantName) {
        String safe = plantName.trim().isEmpty() ? "Plant" : plantName.trim();
        String msg = safe + " planting started";
        appendEvent(plantId, msg, MicrogreensSnapshot.REF_ROOT_PLANTS);
    }

    public static void logFan(@NonNull String plantId, @NonNull String plantName, boolean on, @NonNull String plantParentPath) {
        String msg = on ? "Fan was turned on" : "Fan was turned off";
        appendEvent(plantId, msg, plantParentPath);
    }

    public static void logPump(@NonNull String plantId, @NonNull String plantName, boolean on, @NonNull String plantParentPath) {
        String safe = plantName.trim().isEmpty() ? "Plant" : plantName.trim();
        String msg = on ? (safe + " was watered") : "Water pump was turned off";
        appendEvent(plantId, msg, plantParentPath);
    }

    public static void logLed(@NonNull String plantId, @NonNull String plantName, boolean on, @NonNull String plantParentPath) {
        String msg = on ? "Grow light was turned on" : "Grow light was turned off";
        appendEvent(plantId, msg, plantParentPath);
    }

    private static void appendEvent(@NonNull String plantId, @NonNull String message, @NonNull String plantParentPath) {
        long now = System.currentTimeMillis();
        DatabaseReference ev = historyEventsRef(plantId).push();
        HistoryEvent row = new HistoryEvent(message, now);
        ev.setValue(row);
        Map<String, Object> summary = new HashMap<>();
        summary.put("lastHistoryMessage", message);
        summary.put("lastHistoryAt", now);
        PlantFirebasePaths.plantRecord(plantParentPath, plantId).updateChildren(summary);
    }

    public static void logFanForActivePlant(boolean on) {
        runWithActivePlant((id, name, parent) -> logFan(id, name, on, parent));
    }

    public static void logPumpForActivePlant(boolean on) {
        runWithActivePlant((id, name, parent) -> logPump(id, name, on, parent));
    }

    public static void logLedForActivePlant(boolean on) {
        runWithActivePlant((id, name, parent) -> logLed(id, name, on, parent));
    }

    private interface ActivePlantCallback {
        void run(@NonNull String plantId, @NonNull String plantName, @NonNull String plantParentPath);
    }

    private static void runWithActivePlant(@NonNull ActivePlantCallback cb) {
        AppFirebaseDatabase.get().getReference().addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<PlantListItem> all = PlantsQuery.fromDatabaseRoot(snapshot);
                PlantListItem current = PlantsQuery.currentActive(all);
                if (current == null) {
                    return;
                }
                String id = current.getId();
                String name = current.getPlant().getPlantName();
                if (name == null) name = "";
                cb.run(id, name, current.getParentPath());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }
}
