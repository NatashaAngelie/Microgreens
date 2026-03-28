package edu.uph.m23si1.microgreens.ui.history;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import edu.uph.m23si1.microgreens.Adapter.PlantCardAdapter;
import edu.uph.m23si1.microgreens.Model.PlantCardModel;
import edu.uph.m23si1.microgreens.R;

public class HistoryFragment extends Fragment {

    RecyclerView currentRecycler;
    RecyclerView previousRecycler;
    PlantCardAdapter currentAdapter;
    PlantCardAdapter previousAdapter;
    List<PlantCardModel> currentList;
    List<PlantCardModel> previousList;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_history_list, container, false);

        currentRecycler = view.findViewById(R.id.currentPlantRecycler);
        previousRecycler = view.findViewById(R.id.previousPlantRecycler);

        currentRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
        previousRecycler.setLayoutManager(new LinearLayoutManager(getContext()));

        currentList = new ArrayList<>();
        previousList = new ArrayList<>();

        // Dummy data (biar langsung kelihatan seperti low fidelity)
        currentList.add(new PlantCardModel(
                "Radish",
                "Last Activity: 11 September",
                "2025 11.03.05"
        ));

        previousList.add(new PlantCardModel("Bean Sprouts", "Last Activity: 11 September", "2025 11.03.05"));
        previousList.add(new PlantCardModel("Spinach", "Last Activity: 11 September", "2025 11.03.05"));
        previousList.add(new PlantCardModel("Carrot", "Last Activity: 11 September", "2025 11.03.05"));

        PlantCardAdapter.OnPlantClickListener click = plant -> {
            if (plant == null) return;
            HistoryLogFragment logFragment = HistoryLogFragment.newInstance(plant.getName());
            requireActivity()
                    .getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragmentContainer, logFragment)
                    .addToBackStack(null)
                    .commit();
        };

        currentAdapter = new PlantCardAdapter(currentList, click);
        previousAdapter = new PlantCardAdapter(previousList, click);
        currentRecycler.setAdapter(currentAdapter);
        previousRecycler.setAdapter(previousAdapter);

        return view;
    }
}
