package edu.uph.m23si1.microgreens;

import android.os.Bundle;
import android.text.InputType;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.uph.m23si1.microgreens.Adapter.PlantSpecAdapter;
import edu.uph.m23si1.microgreens.Model.PlantSpec;
import edu.uph.m23si1.microgreens.Model.PlantType;
import edu.uph.m23si1.microgreens.data.AppFirebaseDatabase;
import edu.uph.m23si1.microgreens.data.LocalPlantTypeStore;

public class PlantSpecsActivity extends AppCompatActivity {

    private DatabaseReference specsRef;
    private final Map<String, PlantSpec> cache = new HashMap<>();
    private final List<PlantType> allTypes = new ArrayList<>();

    private PlantSpecAdapter adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plant_specs);

        MaterialToolbar tb = findViewById(R.id.toolbarPlantSpecs);
        tb.setNavigationOnClickListener(v -> getOnBackPressedDispatcher().onBackPressed());

        specsRef = AppFirebaseDatabase.get().getReference("plantSpecs");

        RecyclerView rv = findViewById(R.id.recyclerPlantSpecs);
        rv.setLayoutManager(new LinearLayoutManager(this));

        adapter = new PlantSpecAdapter(
                plantTypeId -> cache.get(plantTypeId),
                this::openEditDialog
        );
        rv.setAdapter(adapter);

        loadTypes();
        observeSpecs();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Jenis tanaman custom disimpan lokal di PlantForm; refresh daftar saat kembali ke layar ini.
        loadTypes();
        ensureFirebaseSpecRowsForLoadedTypes();
        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }
    }

    private void loadTypes() {
        allTypes.clear();

        String[] builtins = getResources().getStringArray(R.array.plant_name_suggestions);
        for (String s : builtins) {
            allTypes.add(new PlantType(LocalPlantTypeStore.slugId(s), s, null, true));
        }
        allTypes.addAll(LocalPlantTypeStore.loadUserTypes(this));

        adapter.submit(allTypes);
    }

    /**
     * Buat node {@code plantSpecs/{id}} untuk jenis baru yang belum ada di Firebase/cache,
     * supaya muncul di daftar dan bisa langsung diedit.
     */
    private void ensureFirebaseSpecRowsForLoadedTypes() {
        Map<String, Object> patch = new HashMap<>();
        for (PlantType t : allTypes) {
            if (!cache.containsKey(t.getId())) {
                PlantSpec blank = new PlantSpec(null, null, null);
                patch.put(t.getId(), blank);
                cache.put(t.getId(), blank);
            }
        }
        if (!patch.isEmpty()) {
            specsRef.updateChildren(patch);
        }
    }

    private void observeSpecs() {
        specsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                cache.clear();
                for (DataSnapshot child : snapshot.getChildren()) {
                    String id = child.getKey();
                    if (id == null) continue;
                    PlantSpec s = child.getValue(PlantSpec.class);
                    if (s == null) {
                        s = new PlantSpec(null, null, null);
                    }
                    cache.put(id, s);
                }

                seedDefaultsIfMissing();
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(PlantSpecsActivity.this, error.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void seedDefaultsIfMissing() {
        // Built-in defaults so the shipped types already have specs (still editable).
        Map<String, PlantSpec> defaults = defaultSpecs();

        Map<String, Object> patch = new HashMap<>();
        for (Map.Entry<String, PlantSpec> e : defaults.entrySet()) {
            if (!cache.containsKey(e.getKey())) {
                patch.put(e.getKey(), e.getValue());
                cache.put(e.getKey(), e.getValue());
            }
        }

        // Also ensure user-added types exist (blank defaults) so they can edit immediately.
        for (PlantType t : allTypes) {
            if (!cache.containsKey(t.getId())) {
                PlantSpec blank = new PlantSpec(null, null, null);
                patch.put(t.getId(), blank);
                cache.put(t.getId(), blank);
            }
        }

        if (!patch.isEmpty()) {
            specsRef.updateChildren(patch);
        }
    }

    private Map<String, PlantSpec> defaultSpecs() {
        Map<String, PlantSpec> m = new HashMap<>();
        // Reasonable starting defaults (user can edit).
        m.put(LocalPlantTypeStore.slugId("Sunflower microgreens"), new PlantSpec(12.0, 22.0, 55.0));
        m.put(LocalPlantTypeStore.slugId("Pea shoots"), new PlantSpec(10.0, 20.0, 50.0));
        m.put(LocalPlantTypeStore.slugId("Radish microgreens"), new PlantSpec(12.0, 21.0, 55.0));
        m.put(LocalPlantTypeStore.slugId("Broccoli microgreens"), new PlantSpec(12.0, 21.0, 55.0));
        m.put(LocalPlantTypeStore.slugId("Mustard microgreens"), new PlantSpec(12.0, 21.0, 55.0));
        m.put(LocalPlantTypeStore.slugId("Arugula microgreens"), new PlantSpec(12.0, 20.0, 55.0));
        m.put(LocalPlantTypeStore.slugId("Basil microgreens"), new PlantSpec(14.0, 24.0, 50.0));
        m.put(LocalPlantTypeStore.slugId("Beet microgreens"), new PlantSpec(12.0, 21.0, 55.0));
        m.put(LocalPlantTypeStore.slugId("Amaranth microgreens"), new PlantSpec(14.0, 24.0, 50.0));
        return m;
    }

    private void openEditDialog(@NonNull PlantType type) {
        PlantSpec existing = cache.get(type.getId());
        if (existing == null) existing = new PlantSpec(null, null, null);

        int pad = (int) (16 * getResources().getDisplayMetrics().density);
        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(pad, pad, pad, pad);

        EditText light = numberField("Light hours/day", existing.lightHoursPerDay);
        EditText temp = numberField("Temperature (°C)", existing.temperatureC);
        EditText soil = numberField("Soil moisture (%)", existing.soilMoisturePercent);

        root.addView(light);
        root.addView(temp);
        root.addView(soil);

        new MaterialAlertDialogBuilder(this)
                .setTitle(type.getDisplayName())
                .setView(root)
                .setPositiveButton("Save", (d, which) -> {
                    PlantSpec next = new PlantSpec(
                            parseDoubleOrNull(light.getText() == null ? "" : light.getText().toString()),
                            parseDoubleOrNull(temp.getText() == null ? "" : temp.getText().toString()),
                            parseDoubleOrNull(soil.getText() == null ? "" : soil.getText().toString())
                    );
                    specsRef.child(type.getId()).setValue(next);
                })
                .setNegativeButton(android.R.string.cancel, null)
                .show();
    }

    private EditText numberField(@NonNull String hint, @Nullable Double value) {
        EditText e = new EditText(this);
        e.setHint(hint);
        e.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        if (value != null) {
            e.setText(String.valueOf(value));
        }
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        lp.bottomMargin = (int) (10 * getResources().getDisplayMetrics().density);
        e.setLayoutParams(lp);
        return e;
    }

    @Nullable
    private static Double parseDoubleOrNull(@NonNull String raw) {
        String s = raw.trim();
        if (s.isEmpty()) return null;
        try {
            return Double.parseDouble(s);
        } catch (Exception ignored) {
            return null;
        }
    }
}

