package edu.uph.m23si1.microgreens.data;

import com.google.firebase.database.DatabaseReference;

import androidx.annotation.NonNull;

import edu.uph.m23si1.microgreens.Model.PlantListItem;

/**
 * Lokasi record tanaman di Realtime DB: {@code plants/{id}} atau langsung {@code {id}} di root.
 */
public final class PlantFirebasePaths {

    private PlantFirebasePaths() {}

    @NonNull
    public static DatabaseReference plantRecord(@NonNull PlantListItem item) {
        return plantRecord(item.getParentPath(), item.getId());
    }

    /**
     * @param parentPath kosong = record di root; {@link MicrogreensSnapshot#REF_ROOT_PLANTS} = di bawah {@code plants/}
     */
    @NonNull
    public static DatabaseReference plantRecord(@NonNull String parentPath, @NonNull String plantId) {
        DatabaseReference r = AppFirebaseDatabase.get().getReference();
        if (parentPath.isEmpty()) {
            return r.child(plantId);
        }
        return r.child(parentPath).child(plantId);
    }
}
