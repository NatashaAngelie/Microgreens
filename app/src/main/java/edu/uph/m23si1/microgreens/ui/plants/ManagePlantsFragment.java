package edu.uph.m23si1.microgreens.ui.plants;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import edu.uph.m23si1.microgreens.Adapter.ManagePlantAdapter;
import edu.uph.m23si1.microgreens.MainActivity;
import edu.uph.m23si1.microgreens.Model.Plant;
import edu.uph.m23si1.microgreens.Model.PlantListItem;
import edu.uph.m23si1.microgreens.PlantFormActivity;
import edu.uph.m23si1.microgreens.R;
import edu.uph.m23si1.microgreens.data.AppFirebaseDatabase;
import edu.uph.m23si1.microgreens.data.PlantFirebasePaths;
import edu.uph.m23si1.microgreens.data.PlantsQuery;

public class ManagePlantsFragment extends Fragment {

    private DatabaseReference databaseRootRef;
    private final List<PlantListItem> plantRows = new ArrayList<>();
    private ManagePlantAdapter adapter;
    private ValueEventListener plantsListener;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_manage_plants, container, false);

        databaseRootRef = AppFirebaseDatabase.get().getReference();

        MaterialToolbar tb = view.findViewById(R.id.toolbarManagePlants);
        tb.setNavigationOnClickListener(v -> {
            if (getActivity() instanceof MainActivity) {
                ((MainActivity) getActivity()).navigateToHomeFromManagePlants();
            }
        });

        MaterialButton add = view.findViewById(R.id.btnAddPlant);
        add.setOnClickListener(v ->
                startActivity(new Intent(requireContext(), PlantFormActivity.class)));

        RecyclerView rv = view.findViewById(R.id.recyclerPlants);
        rv.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new ManagePlantAdapter(plantRows, new ManagePlantAdapter.Listener() {
            @Override
            public void onEdit(@NonNull PlantListItem item) {
                Intent i = new Intent(requireContext(), PlantFormActivity.class);
                i.putExtra(PlantFormActivity.EXTRA_PLANT_ID, item.getId());
                i.putExtra(PlantFormActivity.EXTRA_PLANT_PARENT_PATH, item.getParentPath());
                startActivity(i);
            }

            @Override
            public void onDelete(@NonNull PlantListItem item) {
                Plant p = item.getPlant();
                String label = p.getPlantName() != null ? p.getPlantName() : item.getId();
                new AlertDialog.Builder(requireContext())
                        .setTitle(R.string.delete_plant_title)
                        .setMessage(getString(R.string.delete_plant_message, label))
                        .setNegativeButton(android.R.string.cancel, null)
                        .setPositiveButton(R.string.delete, (d, w) ->
                                PlantFirebasePaths.plantRecord(item).removeValue())
                        .show();
            }
        });
        rv.setAdapter(adapter);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (getActivity() instanceof MainActivity) {
            ((MainActivity) getActivity()).setMainToolbarVisible(false);
        }
    }

    @Override
    public void onDestroyView() {
        if (getActivity() instanceof MainActivity) {
            ((MainActivity) getActivity()).setMainToolbarVisible(true);
        }
        super.onDestroyView();
    }

    @Override
    public void onStart() {
        super.onStart();
        if (plantsListener == null) {
            plantsListener = new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    plantRows.clear();
                    plantRows.addAll(PlantsQuery.fromDatabaseRoot(snapshot));
                    Collections.sort(plantRows, (a, b) -> {
                        String da = a.getPlant().getDatePlanted();
                        String db = b.getPlant().getDatePlanted();
                        if (da == null) da = "";
                        if (db == null) db = "";
                        return db.compareTo(da);
                    });
                    adapter.notifyDataSetChanged();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    if (getContext() != null) {
                        Toast.makeText(getContext(), error.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }
            };
        }
        databaseRootRef.addValueEventListener(plantsListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (plantsListener != null) {
            databaseRootRef.removeEventListener(plantsListener);
        }
    }
}
