package edu.uph.m23si1.microgreens.ui.home;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

import edu.uph.m23si1.microgreens.R;
import edu.uph.m23si1.microgreens.Model.PlantListItem;
import edu.uph.m23si1.microgreens.data.AppFirebaseDatabase;
import edu.uph.m23si1.microgreens.data.MicrogreensSnapshot;
import edu.uph.m23si1.microgreens.data.PlantsQuery;

public class HomeFragment extends Fragment {

    TextView tvTemp, tvHum, tvSoil, tvLed, tvFan, tvPump;
    TextView tvCurrentPlantName;
    TextView tvPrevPlant, tvPlanted, tvSprout, tvHarvest;

    DatabaseReference microgreensRef;
    DatabaseReference databaseRootRef;
    DatabaseReference sensorRef;

    private ValueEventListener plantsListener;

    public HomeFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_home, container, false);

        tvTemp = view.findViewById(R.id.tvTemp);
        tvHum = view.findViewById(R.id.tvHum);
        tvSoil = view.findViewById(R.id.tvSoil);
        tvLed = view.findViewById(R.id.tvLed);
        tvFan = view.findViewById(R.id.tvFan);
        tvPump = view.findViewById(R.id.tvPump);

        tvCurrentPlantName = view.findViewById(R.id.tvCurrentPlantName);
        tvPrevPlant = view.findViewById(R.id.tvPrevPlant);
        tvPlanted = view.findViewById(R.id.tvPlanted);
        tvSprout = view.findViewById(R.id.tvSprout);
        tvHarvest = view.findViewById(R.id.tvHarvest);

        tvTemp.setText("--°C");
        tvHum.setText("--%");
        tvSoil.setText("--%");
        tvLed.setText("OFF");
        tvFan.setText("OFF");
        tvPump.setText("OFF");

        tvCurrentPlantName.setText(R.string.plant_name_placeholder);
        tvPrevPlant.setText(R.string.plant_name_placeholder);
        tvPlanted.setText("Planted: -");
        tvSprout.setText("Sprouted: -");
        tvHarvest.setText("Harvested: -");

        microgreensRef = AppFirebaseDatabase.get().getReference(MicrogreensSnapshot.REF_MICROGREENS);
        databaseRootRef = AppFirebaseDatabase.get().getReference();
        sensorRef = AppFirebaseDatabase.get().getReference("Sensor");

        plantsListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<PlantListItem> all = PlantsQuery.fromDatabaseRoot(snapshot);
                PlantListItem current = PlantsQuery.currentActive(all);
                if (current == null) {
                    tvCurrentPlantName.setText(R.string.plant_name_placeholder);
                    tvPrevPlant.setText(R.string.plant_name_placeholder);
                    tvPlanted.setText("Planted: -");
                    tvSprout.setText("Sprouted: -");
                    tvHarvest.setText("Harvested: -");
                    return;
                }

                String currentName = current.getPlant().getPlantName();
                if (currentName == null || currentName.trim().isEmpty()) {
                    currentName = getString(R.string.plant_name_placeholder);
                }
                tvCurrentPlantName.setText(currentName);

                PlantListItem previous = PlantsQuery.previousForHomeCard(all, current);
                if (previous == null) {
                    tvPrevPlant.setText(R.string.plant_name_placeholder);
                    tvPlanted.setText("Planted: -");
                    tvSprout.setText("Sprouted: -");
                    tvHarvest.setText("Harvested: -");
                    return;
                }

                String prevName = previous.getPlant().getPlantName();
                if (prevName == null || prevName.trim().isEmpty()) {
                    prevName = getString(R.string.plant_name_placeholder);
                }
                tvPrevPlant.setText(prevName);

                String planted = previous.getPlant().getDatePlanted();
                String sprouted = previous.getPlant().getDateSprouted();
                String harvested = previous.getPlant().getHarvestDate();
                tvPlanted.setText("Planted: " + (planted != null && !planted.isEmpty() ? planted : "-"));
                tvSprout.setText("Sprouted: " + (sprouted != null && !sprouted.isEmpty() ? sprouted : "-"));
                tvHarvest.setText("Harvested: " + (harvested != null && !harvested.isEmpty() ? harvested : "-"));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("HomeFragment", "Gagal baca plants", error.toException());
            }
        };
        databaseRootRef.addValueEventListener(plantsListener);

        sensorRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Log.d("HomeFragment", "root Sensor snapshot: " + snapshot.getValue());
                if (!snapshot.exists()) return;

                Object tempVal = firstNonNull(
                        snapshot.child("temperature").getValue(),
                        snapshot.child("temp").getValue(),
                        snapshot.child("suhu").getValue(),
                        snapshot.child("Suhu").getValue()
                );
                Object humVal = firstNonNull(
                        snapshot.child("humidity").getValue(),
                        snapshot.child("kelembaban").getValue(),
                        snapshot.child("Kelembaban").getValue(),
                        snapshot.child("hum").getValue()
                );
                Object soilVal = firstNonNull(
                        snapshot.child("soilMoisture").getValue(),
                        snapshot.child("soil").getValue(),
                        snapshot.child("tanah").getValue(),
                        snapshot.child("Tanah").getValue()
                );

                if (tempVal != null) tvTemp.setText(tempVal + "°C");
                if (humVal != null) tvHum.setText(humVal + "%");
                if (soilVal != null) tvSoil.setText(soilVal + "%");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("HomeFragment", "Gagal baca root Sensor", error.toException());
            }
        });

        microgreensRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Log.d("HomeFragment", "microgreens snapshot: " + snapshot.getValue());

                DataSnapshot sensorsSnap = snapshot.child("sensors").exists()
                        ? snapshot.child("sensors")
                        : snapshot.child("Sensor");

                if (sensorsSnap.exists()) {
                    Object tempVal = firstNonNull(
                            sensorsSnap.child("temperature").getValue(),
                            sensorsSnap.child("temp").getValue(),
                            sensorsSnap.child("suhu").getValue(),
                            sensorsSnap.child("Suhu").getValue()
                    );
                    Object humVal = firstNonNull(
                            sensorsSnap.child("humidity").getValue(),
                            sensorsSnap.child("kelembaban").getValue(),
                            sensorsSnap.child("Kelembaban").getValue(),
                            sensorsSnap.child("hum").getValue()
                    );
                    Object soilVal = firstNonNull(
                            sensorsSnap.child("soilMoisture").getValue(),
                            sensorsSnap.child("soil").getValue(),
                            sensorsSnap.child("tanah").getValue(),
                            sensorsSnap.child("Tanah").getValue()
                    );

                    if (tempVal != null) tvTemp.setText(tempVal + "°C");
                    if (humVal != null) tvHum.setText(humVal + "%");
                    if (soilVal != null) tvSoil.setText(soilVal + "%");
                }

                DataSnapshot controlSnap = snapshot.child("control");
                if (controlSnap.exists()) {
                    Object ledVal = controlSnap.child("led").getValue();
                    Object fanVal = controlSnap.child("fan").getValue();
                    Object pumpVal = controlSnap.child("pump").getValue();

                    boolean ledOn = ledVal != null && "true".equalsIgnoreCase(String.valueOf(ledVal));
                    boolean fanOn = fanVal != null && "true".equalsIgnoreCase(String.valueOf(fanVal));
                    boolean pumpOn = pumpVal != null && "true".equalsIgnoreCase(String.valueOf(pumpVal));

                    tvLed.setText(ledOn ? "ON" : "OFF");
                    tvFan.setText(fanOn ? "ON" : "OFF");
                    tvPump.setText(pumpOn ? "ON" : "OFF");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("HomeFragment", "Gagal baca microgreens", error.toException());
            }
        });

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (plantsListener != null && databaseRootRef != null) {
            databaseRootRef.removeEventListener(plantsListener);
            plantsListener = null;
        }
    }

    private static Object firstNonNull(Object... values) {
        if (values == null) return null;
        for (Object value : values) {
            if (value != null) return value;
        }
        return null;
    }
}
