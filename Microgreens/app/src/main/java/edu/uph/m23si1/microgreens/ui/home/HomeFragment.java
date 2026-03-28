package edu.uph.m23si1.microgreens.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import edu.uph.m23si1.microgreens.R;
import edu.uph.m23si1.microgreens.databinding.FragmentHomeBinding;

public class HomeFragment extends Fragment {

    TextView tvTemp, tvHum, tvSoil, tvLed, tvFan, tvPump;
    TextView tvPrevPlant, tvPlanted, tvSprout, tvHarvest;

    public HomeFragment(){}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_home, container, false);

        // ===== INISIALISASI SENSOR =====
        tvTemp = view.findViewById(R.id.tvTemp);
        tvHum = view.findViewById(R.id.tvHum);
        tvSoil = view.findViewById(R.id.tvSoil);
        tvLed = view.findViewById(R.id.tvLed);
        tvFan = view.findViewById(R.id.tvFan);
        tvPump = view.findViewById(R.id.tvPump);

        // ===== INISIALISASI PREVIOUS PLANT =====
        tvPrevPlant = view.findViewById(R.id.tvPrevPlant);
        tvPlanted = view.findViewById(R.id.tvPlanted);
        tvSprout = view.findViewById(R.id.tvSprout);
        tvHarvest = view.findViewById(R.id.tvHarvest);

        // ===== DUMMY DATA (INITIAL STATE UI) =====
        tvTemp.setText("--°C");
        tvHum.setText("--%");
        tvSoil.setText("--%");
        tvLed.setText("OFF");
        tvFan.setText("OFF");
        tvPump.setText("OFF");

        tvPrevPlant.setText("No data");
        tvPlanted.setText("Planted: -");
        tvSprout.setText("Sprouted: -");
        tvHarvest.setText("Harvested: -");

        return view;
    }
}