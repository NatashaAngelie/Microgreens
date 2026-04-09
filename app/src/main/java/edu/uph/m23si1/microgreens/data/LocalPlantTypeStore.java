package edu.uph.m23si1.microgreens.data;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import edu.uph.m23si1.microgreens.Model.PlantType;

/**
 * Stores user-added plant types locally (no Firebase) as requested.
 */
public final class LocalPlantTypeStore {

    private static final String PREF = "local_plant_types";
    private static final String KEY_TYPES = "types_json";

    private LocalPlantTypeStore() {}

    @NonNull
    public static List<PlantType> loadUserTypes(@NonNull Context ctx) {
        SharedPreferences sp = ctx.getSharedPreferences(PREF, Context.MODE_PRIVATE);
        String raw = sp.getString(KEY_TYPES, "[]");
        List<PlantType> out = new ArrayList<>();
        try {
            JSONArray arr = new JSONArray(raw);
            for (int i = 0; i < arr.length(); i++) {
                JSONObject o = arr.getJSONObject(i);
                String id = o.optString("id", "");
                String name = o.optString("displayName", "");
                String path = o.optString("localImagePath", "");
                if (id.isEmpty() || name.isEmpty()) continue;
                out.add(new PlantType(id, name, path.isEmpty() ? null : path, false));
            }
        } catch (Exception ignored) {
        }
        return out;
    }

    public static void saveUserType(
            @NonNull Context ctx,
            @NonNull String displayName,
            @Nullable String localImagePath
    ) {
        String id = slugId(displayName);
        List<PlantType> current = loadUserTypes(ctx);

        // replace if exists
        List<PlantType> next = new ArrayList<>();
        boolean replaced = false;
        for (PlantType t : current) {
            if (t.getId().equals(id)) {
                next.add(new PlantType(id, displayName, localImagePath, false));
                replaced = true;
            } else {
                next.add(t);
            }
        }
        if (!replaced) {
            next.add(new PlantType(id, displayName, localImagePath, false));
        }

        persist(ctx, next);
    }

    private static void persist(@NonNull Context ctx, @NonNull List<PlantType> types) {
        JSONArray arr = new JSONArray();
        for (PlantType t : types) {
            try {
                JSONObject o = new JSONObject();
                o.put("id", t.getId());
                o.put("displayName", t.getDisplayName());
                o.put("localImagePath", t.getLocalImagePath() == null ? "" : t.getLocalImagePath());
                arr.put(o);
            } catch (Exception ignored) {
            }
        }
        ctx.getSharedPreferences(PREF, Context.MODE_PRIVATE)
                .edit()
                .putString(KEY_TYPES, arr.toString())
                .apply();
    }

    @NonNull
    public static File imagesDir(@NonNull Context ctx) {
        File dir = new File(ctx.getFilesDir(), "plant_type_images");
        if (!dir.exists()) {
            //noinspection ResultOfMethodCallIgnored
            dir.mkdirs();
        }
        return dir;
    }

    @NonNull
    public static String slugId(@NonNull String displayName) {
        String s = displayName.trim().toLowerCase(Locale.US);
        s = s.replaceAll("[^a-z0-9]+", "_");
        s = s.replaceAll("^_+|_+$", "");
        if (s.isEmpty()) s = "plant_type";
        return s;
    }
}

