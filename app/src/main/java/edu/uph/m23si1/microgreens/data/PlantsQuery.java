package edu.uph.m23si1.microgreens.data;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.google.firebase.database.DataSnapshot;

import edu.uph.m23si1.microgreens.Model.Plant;
import edu.uph.m23si1.microgreens.Model.PlantListItem;

/**
 * Logika tanaman aktif (belum panen) dan daftar lain — sama dipakai Home & History.
 */
public final class PlantsQuery {

    private PlantsQuery() {}

    public static boolean isUnharvested(@Nullable Plant p) {
        if (p == null) return false;
        String h = p.getHarvestDate();
        return h == null || h.trim().isEmpty();
    }

    /** Tanaman aktif: paling baru ditanam di antara yang belum dipanen. */
    @Nullable
    public static PlantListItem currentActive(@NonNull List<PlantListItem> all) {
        List<PlantListItem> active = new ArrayList<>();
        for (PlantListItem row : all) {
            if (isUnharvested(row.getPlant())) {
                active.add(row);
            }
        }
        if (active.isEmpty()) {
            return null;
        }
        Collections.sort(active, BY_DATE_PLANTED_ASC);
        return active.get(active.size() - 1);
    }

    /**
     * Tanaman sebelumnya untuk kartu Home (kedua yang belum panen, atau terakhir dipanen).
     */
    @Nullable
    public static PlantListItem previousForHomeCard(
            @NonNull List<PlantListItem> all,
            @Nullable PlantListItem current
    ) {
        List<PlantListItem> active = new ArrayList<>();
        for (PlantListItem row : all) {
            if (isUnharvested(row.getPlant())) {
                active.add(row);
            }
        }
        Collections.sort(active, BY_DATE_PLANTED_ASC);
        if (active.size() >= 2) {
            return active.get(active.size() - 2);
        }
        List<PlantListItem> harvested = new ArrayList<>();
        for (PlantListItem row : all) {
            if (!isUnharvested(row.getPlant())) {
                harvested.add(row);
            }
        }
        if (harvested.isEmpty()) {
            return null;
        }
        Collections.sort(harvested, BY_DATE_PLANTED_ASC);
        return harvested.get(harvested.size() - 1);
    }

    /**
     * Daftar untuk layar History (Previous): semua tanaman kecuali current aktif,
     * urut dari penanaman terbaru.
     */
    @NonNull
    public static List<PlantListItem> previousPlantsForHistory(
            @NonNull List<PlantListItem> all,
            @Nullable PlantListItem current
    ) {
        String currentId = current != null ? current.getId() : null;
        List<PlantListItem> rest = new ArrayList<>();
        for (PlantListItem row : all) {
            if (currentId != null && currentId.equals(row.getId())) {
                continue;
            }
            rest.add(row);
        }
        Collections.sort(rest, BY_DATE_PLANTED_DESC);
        return rest;
    }

    /** Key di root DB yang bukan data tanaman (scan legacy). */
    private static final Set<String> RESERVED_ROOT_KEYS = new HashSet<>(Arrays.asList(
            MicrogreensSnapshot.REF_ROOT_PLANTS,
            MicrogreensSnapshot.REF_MICROGREENS,
            MicrogreensSnapshot.REF_USERS,
            "Sensor"
    ));

    /**
     * Baca seluruh DB: utamakan node {@code plants} jika berisi data; jika tidak, baca push-id di root
     * (kompatibel dengan data lama yang tidak punya parent {@code plants}).
     */
    @NonNull
    public static List<PlantListItem> fromDatabaseRoot(@NonNull DataSnapshot root) {
        DataSnapshot plantsNode = root.child(MicrogreensSnapshot.REF_ROOT_PLANTS);
        if (plantsNode.exists() && plantsNode.getChildrenCount() > 0) {
            return fromSnapshot(plantsNode);
        }
        return fromLegacyRootPlants(root);
    }

    @NonNull
    public static List<PlantListItem> fromSnapshot(@NonNull DataSnapshot plantsRoot) {
        List<PlantListItem> out = new ArrayList<>();
        for (DataSnapshot child : plantsRoot.getChildren()) {
            Plant p = child.getValue(Plant.class);
            if (p != null && child.getKey() != null) {
                out.add(new PlantListItem(child.getKey(), p, MicrogreensSnapshot.REF_ROOT_PLANTS));
            }
        }
        return out;
    }

    /** Tanaman yang disimpan langsung di root (sibling {@code users}, dll.). */
    @NonNull
    private static List<PlantListItem> fromLegacyRootPlants(@NonNull DataSnapshot root) {
        List<PlantListItem> out = new ArrayList<>();
        for (DataSnapshot child : root.getChildren()) {
            String key = child.getKey();
            if (key == null || RESERVED_ROOT_KEYS.contains(key)) {
                continue;
            }
            Plant p = child.getValue(Plant.class);
            if (p != null && p.getPlantName() != null && !p.getPlantName().trim().isEmpty()) {
                out.add(new PlantListItem(key, p, ""));
            }
        }
        return out;
    }

    private static final Comparator<PlantListItem> BY_DATE_PLANTED_ASC = (a, b) -> {
        String da = a.getPlant().getDatePlanted() != null ? a.getPlant().getDatePlanted() : "";
        String db = b.getPlant().getDatePlanted() != null ? b.getPlant().getDatePlanted() : "";
        return da.compareTo(db);
    };

    private static final Comparator<PlantListItem> BY_DATE_PLANTED_DESC =
            (a, b) -> BY_DATE_PLANTED_ASC.compare(b, a);
}
