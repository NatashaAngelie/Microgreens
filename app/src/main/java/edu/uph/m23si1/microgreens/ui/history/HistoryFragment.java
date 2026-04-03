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
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import edu.uph.m23si1.microgreens.Adapter.PlantCardAdapter;
import edu.uph.m23si1.microgreens.Model.PlantCardModel;
import edu.uph.m23si1.microgreens.Model.PlantListItem;
import edu.uph.m23si1.microgreens.R;
import edu.uph.m23si1.microgreens.data.AppFirebaseDatabase;
import edu.uph.m23si1.microgreens.data.PlantsQuery;

public class HistoryFragment extends Fragment {

    RecyclerView currentRecycler;
    RecyclerView previousRecycler;
    PlantCardAdapter currentAdapter;
    PlantCardAdapter previousAdapter;
    List<PlantCardModel> currentList;
    List<PlantCardModel> previousList;

    DatabaseReference databaseRootRef;
    private ValueEventListener plantsListener;

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
            if (plant == null || plant.getPlantId() == null) return;
            HistoryLogFragment logFragment = HistoryLogFragment.newInstance(
                    plant.getPlantId(),
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

        databaseRootRef = AppFirebaseDatabase.get().getReference();
        loadData();

        return view;
    }

    private void loadData() {
        if (plantsListener != null) {
            databaseRootRef.removeEventListener(plantsListener);
        }
        plantsListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<PlantListItem> all = PlantsQuery.fromDatabaseRoot(snapshot);
                PlantListItem current = PlantsQuery.currentActive(all);
                List<PlantListItem> prevRows = PlantsQuery.previousPlantsForHistory(all, current);

                currentList.clear();
                if (current != null) {
                    currentList.add(PlantCardModel.fromPlantListItem(current));
                }

                previousList.clear();
                for (PlantListItem row : prevRows) {
                    previousList.add(PlantCardModel.fromPlantListItem(row));
                }

                currentAdapter.notifyDataSetChanged();
                previousAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        };
        databaseRootRef.addValueEventListener(plantsListener);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (plantsListener != null && databaseRootRef != null) {
            databaseRootRef.removeEventListener(plantsListener);
            plantsListener = null;
        }
    }
}
