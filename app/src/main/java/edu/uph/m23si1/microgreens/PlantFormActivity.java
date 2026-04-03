package edu.uph.m23si1.microgreens;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

import edu.uph.m23si1.microgreens.Model.Plant;
import edu.uph.m23si1.microgreens.data.AppFirebaseDatabase;
import edu.uph.m23si1.microgreens.data.MicrogreensHistoryWriter;
import edu.uph.m23si1.microgreens.data.MicrogreensSnapshot;
import edu.uph.m23si1.microgreens.data.PlantFirebasePaths;

public class PlantFormActivity extends AppCompatActivity {

    public static final String EXTRA_PLANT_ID = "plant_id";
    public static final String EXTRA_PLANT_PARENT_PATH = "plant_parent_path";

    private DatabaseReference plantsRef;
    private DatabaseReference rootRef;

    @Nullable
    private String plantId;

    @Nullable
    private String plantRecordParentPath;

    private AutoCompleteTextView inputName;
    private EditText inputPlanted;
    private EditText inputSprouted;
    private EditText inputHarvest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plant_form);

        View buttonBar = findViewById(R.id.plantFormButtonBar);
        ViewCompat.setOnApplyWindowInsetsListener(buttonBar, (v, insets) -> {
            Insets bars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            int extra = (int) (8 * getResources().getDisplayMetrics().density);
            v.setPadding(v.getPaddingLeft(), v.getPaddingTop(), v.getPaddingRight(), bars.bottom + extra);
            return insets;
        });

        plantsRef = AppFirebaseDatabase.get()
                .getReference(MicrogreensSnapshot.REF_ROOT_PLANTS);

        rootRef = AppFirebaseDatabase.get().getReference();

        plantId = getIntent().getStringExtra(EXTRA_PLANT_ID);

        MaterialToolbar toolbar = findViewById(R.id.toolbarPlantForm);
        toolbar.setNavigationOnClickListener(v -> getOnBackPressedDispatcher().onBackPressed());
        toolbar.setTitle(plantId == null
                ? getString(R.string.add_plants_screen_title)
                : getString(R.string.edit_plants_screen_title));

        inputName = findViewById(R.id.inputPlantName);
        inputPlanted = findViewById(R.id.inputDatePlanted);
        inputSprouted = findViewById(R.id.inputDateSprouted);
        inputHarvest = findViewById(R.id.inputHarvestDate);

        String[] suggestions = getResources().getStringArray(R.array.plant_name_suggestions);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_dropdown_item_1line, suggestions);

        inputName.setAdapter(adapter);
        inputName.setThreshold(1);

        MaterialButton save = findViewById(R.id.btnSavePlant);
        save.setText(plantId == null ? R.string.btn_add_plant : R.string.btn_save_edit);
        save.setOnClickListener(v -> savePlant());

        ImageView photo = findViewById(R.id.ivPlantPhoto);
        photo.setOnClickListener(v ->
                Toast.makeText(this, R.string.photo_upload_hint, Toast.LENGTH_SHORT).show());

        if (plantId != null) {
            loadExisting(plantId);
        }
    }

    private void loadExisting(@NonNull String id) {
        Bundle extras = getIntent().getExtras();

        if (extras != null && extras.containsKey(EXTRA_PLANT_PARENT_PATH)) {

            // ✅ FIX FINAL VARIABLE
            String temp = extras.getString(EXTRA_PLANT_PARENT_PATH);
            if (temp == null) {
                temp = MicrogreensSnapshot.REF_ROOT_PLANTS;
            }
            final String pp = temp;

            DatabaseReference one = PlantFirebasePaths.plantRecord(pp, id);

            one.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    Plant p = snapshot.getValue(Plant.class);
                    if (p == null) {
                        Toast.makeText(PlantFormActivity.this, R.string.plant_not_found, Toast.LENGTH_SHORT).show();
                        finish();
                        return;
                    }

                    plantRecordParentPath = pp;
                    applyPlantToForm(p);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(PlantFormActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
            return;
        }

        plantsRef.child(id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Plant p = snapshot.getValue(Plant.class);

                if (p != null) {
                    plantRecordParentPath = MicrogreensSnapshot.REF_ROOT_PLANTS;
                    applyPlantToForm(p);
                    return;
                }

                rootRef.child(id).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot2) {
                        Plant p2 = snapshot2.getValue(Plant.class);

                        if (p2 != null) {
                            plantRecordParentPath = "";
                            applyPlantToForm(p2);
                            return;
                        }

                        Toast.makeText(PlantFormActivity.this, R.string.plant_not_found, Toast.LENGTH_SHORT).show();
                        finish();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(PlantFormActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(PlantFormActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void applyPlantToForm(@NonNull Plant p) {
        inputName.setText(p.getPlantName());
        inputPlanted.setText(p.getDatePlanted());
        inputSprouted.setText(nonNullStr(p.getDateSprouted()));
        inputHarvest.setText(nonNullStr(p.getHarvestDate()));
    }

    private void savePlant() {
        hideKeyboard();

        String name = text(inputName);
        String planted = text(inputPlanted);

        if (name.isEmpty() || planted.isEmpty()) {
            Toast.makeText(this, R.string.fill_required_fields, Toast.LENGTH_LONG).show();
            return;
        }

        String sprouted = text(inputSprouted);
        String harvest = text(inputHarvest);

        Map<String, Object> payload = new HashMap<>();
        payload.put("plantName", name);
        payload.put("datePlanted", planted);
        payload.put("dateSprouted", sprouted);
        payload.put("harvestDate", harvest);

        if (plantId == null) {
            DatabaseReference newRef = plantsRef.push();
            String newId = newRef.getKey();

            newRef.setValue(payload).addOnCompleteListener(this, t -> {
                if (t.isSuccessful()) {

                    if (newId != null) {
                        MicrogreensHistoryWriter.logPlantCreated(newId, name);
                    }

                    Intent back = new Intent(this, MainActivity.class);
                    back.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    back.putExtra(MainActivity.EXTRA_OPEN_MANAGE_PLANTS, true);
                    startActivity(back);
                    finish();

                } else {
                    Toast.makeText(this, "Save failed", Toast.LENGTH_LONG).show();
                }
            });

        } else {
            String parent = plantRecordParentPath != null
                    ? plantRecordParentPath
                    : MicrogreensSnapshot.REF_ROOT_PLANTS;

            PlantFirebasePaths.plantRecord(parent, plantId)
                    .setValue(payload)
                    .addOnCompleteListener(this, t -> {

                        if (t.isSuccessful()) {

                            Intent back = new Intent(this, MainActivity.class);
                            back.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                            back.putExtra(MainActivity.EXTRA_OPEN_MANAGE_PLANTS, true);
                            startActivity(back);
                            finish();

                        } else {
                            Toast.makeText(this, "Update failed", Toast.LENGTH_LONG).show();
                        }
                    });
        }
    }

    private void hideKeyboard() {
        View v = getCurrentFocus();
        if (v == null) v = findViewById(android.R.id.content);

        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
        }
    }

    private static String text(EditText e) {
        if (e.getText() == null) return "";
        return e.getText().toString().trim();
    }

    private static String nonNullStr(@Nullable String s) {
        return s == null ? "" : s;
    }
}