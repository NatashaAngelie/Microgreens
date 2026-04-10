package edu.uph.m23si1.microgreens.Model;

import androidx.annotation.Nullable;

/**
 * Local-only plant type (built-in or user-added).
 * For Firebase we use {@code id} as stable key.
 */
public class PlantType {
    private final String id;
    private final String displayName;
    @Nullable
    private final String localImagePath;
    private final boolean builtin;

    public PlantType(String id, String displayName, @Nullable String localImagePath, boolean builtin) {
        this.id = id;
        this.displayName = displayName;
        this.localImagePath = localImagePath;
        this.builtin = builtin;
    }

    public String getId() {
        return id;
    }

    public String getDisplayName() {
        return displayName;
    }

    @Nullable
    public String getLocalImagePath() {
        return localImagePath;
    }

    public boolean isBuiltin() {
        return builtin;
    }
}

