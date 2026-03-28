package edu.uph.m23si1.microgreens.ui.history;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import edu.uph.m23si1.microgreens.Adapter.PlantCardAdapter;
import edu.uph.m23si1.microgreens.Model.PlantCardModel;
import edu.uph.m23si1.microgreens.R;
import edu.uph.m23si1.microgreens.data.MicrogreensSnapshot;

public class HistoryFragment extends Fragment {

    RecyclerView currentRecycler;
    RecyclerView previousRecycler;
    PlantCardAdapter currentAdapter;
    PlantCardAdapter previousAdapter;
    List<PlantCardModel> currentList;
    List<PlantCardModel> previousList;

    DatabaseReference db;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_history_list, container, false);

        currentRecycler = view.findViewById(R.id.currentPlantRecycler);
        previousRecycler = view.findViewById(R.id.previousPlantRecycler);

        currentRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
        previousRecycler.setLayoutManager(new LinearLayoutManager(getContext()));

        currentList = new ArrayList<>();
        previousList = new ArrayList<>();

        PlantCardAdapter.OnPlantClickListener click = plant -> {
            if (plant == null) return;
            HistoryLogFragment logFragment = HistoryLogFragment.newInstance(
                    plant.getName(),
                    plant.getLastActivityDateLabel(),
                    plant.getLastActivityTime()
            );
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

        db = FirebaseDatabase.getInstance().getReference(MicrogreensSnapshot.REF_MICROGREENS);
        loadData();

        return view;
    }

    private void loadData() {
        db.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                currentList.clear();

                PlantCardModel current = MicrogreensSnapshot.buildCurrentPlantCard(snapshot);
                if (current != null) {
                    currentList.add(current);
                }

                if (previousList.isEmpty()) {
                    previousList.add(new PlantCardModel("Bean Sprouts", "Last Activity: 11 September", "2025 11.03.05"));
                    previousList.add(new PlantCardModel("Spinach", "Last Activity: 11 September", "2025 11.03.05"));
                    previousList.add(new PlantCardModel("Carrot", "Last Activity: 11 September", "2025 11.03.05"));
                }

                currentAdapter.notifyDataSetChanged();
                previousAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }
}
