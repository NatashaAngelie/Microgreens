package edu.uph.m23si1.microgreens;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.ArrayList;
import java.util.List;

import edu.uph.m23si1.microgreens.Adapter.PlantTypeDropdownAdapter;
import edu.uph.m23si1.microgreens.Model.Plant;
import edu.uph.m23si1.microgreens.Model.PlantType;
import edu.uph.m23si1.microgreens.data.AppFirebaseDatabase;
import edu.uph.m23si1.microgreens.data.LocalPlantTypeStore;
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
    private ImageView photo;

    private final List<PlantType> plantTypes = new ArrayList<>();
    @Nullable
    private String pendingNewTypeName;

    private final ActivityResultLauncher<String> pickImage =
            registerForActivityResult(new ActivityResultContracts.GetContent(), this::onPickedImage);

    private static final SimpleDateFormat DATE_TIME_FMT =
            new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.US);

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
        inputName.setThreshold(0);
        inputName.setOnClickListener(v -> {
            hideKeyboard();
            inputName.showDropDown();
        });
        inputName.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                hideKeyboard();
                inputName.showDropDown();
            }
        });
        setupPlantTypesDropdown();

        // Date fields: tap to pick (no manual typing). Long-press clears optional fields.
        setupDateTimeField(inputPlanted, /*required*/ true);
        setupDateTimeField(inputSprouted, /*required*/ false);
        setupDateTimeField(inputHarvest, /*required*/ false);

        if (plantId == null) {
            // default start from today
            if (text(inputPlanted).isEmpty()) {
                inputPlanted.setText(DATE_TIME_FMT.format(Calendar.getInstance().getTime()));
            }
        }

        MaterialButton save = findViewById(R.id.btnSavePlant);
        save.setText(plantId == null ? R.string.btn_add_plant : R.string.btn_save_edit);
        save.setOnClickListener(v -> savePlant());

        photo = findViewById(R.id.ivPlantPhoto);
        photo.setOnClickListener(v -> {
            // Photo is tied to plant type selection for now.
            inputName.showDropDown();
        });

        if (plantId != null) {
            loadExisting(plantId);
        }
    }

    private void setupPlantTypesDropdown() {
        plantTypes.clear();

        // Built-ins from resources
        String[] suggestions = getResources().getStringArray(R.array.plant_name_suggestions);
        for (String s : suggestions) {
            String id = LocalPlantTypeStore.slugId(s);
            plantTypes.add(new PlantType(id, s, null, true));
        }

        // User-added (local)
        plantTypes.addAll(LocalPlantTypeStore.loadUserTypes(this));

        // Special row to add new type
        plantTypes.add(new PlantType("__add__", "+ Add plant type...", null, true));

        PlantTypeDropdownAdapter adapter = new PlantTypeDropdownAdapter(this, plantTypes);
        inputName.setAdapter(adapter);

        inputName.setOnItemClickListener((parent, view, position, id) -> {
            PlantType selected = (PlantType) parent.getItemAtPosition(position);
            if (selected == null) return;

            if ("__add__".equals(selected.getId())) {
                inputName.setText("");
                showAddPlantTypeDialog();
                return;
            }

            inputName.setText(selected.getDisplayName(), false);
            applyTypeImage(selected);
        });
    }

    private void applyTypeImage(@NonNull PlantType t) {
        if (t.getLocalImagePath() != null && !t.getLocalImagePath().trim().isEmpty()) {
            try {
                photo.setImageURI(Uri.fromFile(new File(t.getLocalImagePath())));
                return;
            } catch (Exception ignored) {
            }
        }
        photo.setImageResource(R.drawable.ic_plant_placeholder_small);
    }

    private void showAddPlantTypeDialog() {
        final EditText nameInput = new EditText(this);
        nameInput.setHint(getString(R.string.label_plant_name));
        int pad = (int) (16 * getResources().getDisplayMetrics().density);
        nameInput.setPadding(pad, pad, pad, pad);

        new MaterialAlertDialogBuilder(this)
                .setTitle("Add plant type")
                .setMessage("Enter plant type name, then choose an image (optional).")
                .setView(nameInput)
                .setNeutralButton("Pick image", (d, which) -> {
                    pendingNewTypeName = nameInput.getText() != null ? nameInput.getText().toString().trim() : "";
                    if (pendingNewTypeName == null || pendingNewTypeName.isEmpty()) {
                        Toast.makeText(this, "Plant type name required first.", Toast.LENGTH_LONG).show();
                        pendingNewTypeName = null;
                        return;
                    }
                    pickImage.launch("image/*");
                })
                .setPositiveButton("Save", (d, which) -> {
                    String n = nameInput.getText() != null ? nameInput.getText().toString().trim() : "";
                    if (n.isEmpty()) {
                        Toast.makeText(this, "Plant type name is required.", Toast.LENGTH_LONG).show();
                        return;
                    }
                    // Save with no image for now (user can re-add with same name to replace)
                    LocalPlantTypeStore.saveUserType(this, n, null);
                    setupPlantTypesDropdown();
                    inputName.setText(n, false);
                    photo.setImageResource(R.drawable.ic_plant_placeholder_small);
                })
                .setNegativeButton(android.R.string.cancel, null)
                .show();
    }

    private void onPickedImage(@Nullable Uri uri) {
        if (uri == null) return;
        if (pendingNewTypeName == null || pendingNewTypeName.trim().isEmpty()) {
            return;
        }
        String name = pendingNewTypeName.trim();
        pendingNewTypeName = null;

        try {
            File dir = LocalPlantTypeStore.imagesDir(this);
            String base = LocalPlantTypeStore.slugId(name);
            File out = new File(dir, base + ".jpg");

            try (InputStream in = getContentResolver().openInputStream(uri);
                 FileOutputStream fos = new FileOutputStream(out)) {
                if (in != null) {
                    byte[] buf = new byte[8192];
                    int r;
                    while ((r = in.read(buf)) > 0) {
                        fos.write(buf, 0, r);
                    }
                }
            }

            LocalPlantTypeStore.saveUserType(this, name, out.getAbsolutePath());
            setupPlantTypesDropdown();
            inputName.setText(name, false);
            photo.setImageURI(Uri.fromFile(out));

        } catch (Exception e) {
            Toast.makeText(this, "Could not save image.", Toast.LENGTH_LONG).show();
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

        // best-effort: apply image if plantName matches a known type
        for (PlantType t : plantTypes) {
            if (t.getDisplayName().equalsIgnoreCase(p.getPlantName())) {
                applyTypeImage(t);
                break;
            }
        }
    }

    private void setupDateTimeField(@NonNull EditText field, boolean required) {
        field.setInputType(InputType.TYPE_NULL);
        field.setKeyListener(null);
        field.setOnClickListener(v -> openDateTimePicker(field, required));
        field.setOnLongClickListener(v -> {
            if (!required) {
                field.setText("");
                Toast.makeText(this, R.string.date_picker_clear_hint, Toast.LENGTH_SHORT).show();
                return true;
            }
            return false;
        });
    }

    private void openDateTimePicker(@NonNull EditText target, boolean required) {
        // For now: we keep UX simple—use current time and clamp date >= today.
        // The stored format keeps lexical sorting consistent with existing string compare logic.
        final Calendar now = Calendar.getInstance();
        final Calendar picked = Calendar.getInstance();

        // If existing value parses, start from it
        String existing = text(target);
        if (!existing.isEmpty()) {
            try {
                picked.setTime(DATE_TIME_FMT.parse(existing));
            } catch (Exception ignored) {
                picked.setTime(now.getTime());
            }
        }

        android.app.DatePickerDialog dp = new android.app.DatePickerDialog(
                this,
                (view, year, month, dayOfMonth) -> {
                    picked.set(Calendar.YEAR, year);
                    picked.set(Calendar.MONTH, month);
                    picked.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                    // Validate: must be today or later
                    Calendar today = Calendar.getInstance();
                    clearTime(today);
                    Calendar pickedDay = (Calendar) picked.clone();
                    clearTime(pickedDay);
                    if (pickedDay.before(today)) {
                        Toast.makeText(this, R.string.invalid_date_before_today, Toast.LENGTH_LONG).show();
                        if (!required && existing.isEmpty()) {
                            target.setText("");
                        }
                        return;
                    }

                    android.app.TimePickerDialog tp = new android.app.TimePickerDialog(
                            this,
                            (timeView, hourOfDay, minute) -> {
                                picked.set(Calendar.HOUR_OF_DAY, hourOfDay);
                                picked.set(Calendar.MINUTE, minute);
                                picked.set(Calendar.SECOND, 0);
                                picked.set(Calendar.MILLISECOND, 0);
                                target.setText(DATE_TIME_FMT.format(picked.getTime()));
                            },
                            picked.get(Calendar.HOUR_OF_DAY),
                            picked.get(Calendar.MINUTE),
                            true
                    );
                    tp.setTitle(getString(R.string.pick_date_time));
                    tp.show();
                },
                picked.get(Calendar.YEAR),
                picked.get(Calendar.MONTH),
                picked.get(Calendar.DAY_OF_MONTH)
        );
        dp.setTitle(getString(R.string.pick_date_time));

        // minDate = today (00:00)
        Calendar min = Calendar.getInstance();
        clearTime(min);
        dp.getDatePicker().setMinDate(min.getTimeInMillis());
        dp.show();
    }

    private static void clearTime(@NonNull Calendar c) {
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
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