package edu.uph.m23si1.microgreens.ui.home;

import android.os.Bundle;
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

import edu.uph.m23si1.microgreens.R;
import edu.uph.m23si1.microgreens.data.AppFirebaseDatabase;
import edu.uph.m23si1.microgreens.data.MicrogreensSnapshot;

public class HomeFragment extends Fragment {

    TextView tvTemp, tvHum, tvSoil, tvLed, tvFan, tvPump;
    TextView tvCurrentPlantName;
    TextView tvPrevPlant, tvPlanted, tvSprout, tvHarvest;

    DatabaseReference microgreensRef;

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
        microgreensRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String name = MicrogreensSnapshot.plantName(snapshot);
                if (name != null && !name.trim().isEmpty()) {
                    tvCurrentPlantName.setText(name);
                    tvPrevPlant.setText(name);
                } else {
                    tvCurrentPlantName.setText(R.string.plant_name_placeholder);
                    tvPrevPlant.setText(R.string.plant_name_placeholder);
                }

                String planted = MicrogreensSnapshot.datePlanted(snapshot);
                String sprouted = MicrogreensSnapshot.dateSprouted(snapshot);
                String harvested = MicrogreensSnapshot.dateHarvested(snapshot);
                tvPlanted.setText("Planted: " + (planted != null && !planted.isEmpty() ? planted : "-"));
                tvSprout.setText("Sprouted: " + (sprouted != null && !sprouted.isEmpty() ? sprouted : "-"));
                tvHarvest.setText("Harvested: " + (harvested != null && !harvested.isEmpty() ? harvested : "-"));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

        return view;
    }
}
