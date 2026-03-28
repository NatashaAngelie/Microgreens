package edu.uph.m23si1.microgreens.ui.history;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.*;

import java.util.ArrayList;
import java.util.List;

import edu.uph.m23si1.microgreens.Adapter.HistoryAdapter;
import edu.uph.m23si1.microgreens.Model.HistoryModel;
import edu.uph.m23si1.microgreens.R;

public class HistoryFragment extends Fragment {

    RecyclerView recyclerView;
    HistoryAdapter adapter;
    List<HistoryModel> list;

    DatabaseReference db;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_history, container, false);

        recyclerView = view.findViewById(R.id.historyRecycler);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        list = new ArrayList<>();
        adapter = new HistoryAdapter(list);

        recyclerView.setAdapter(adapter);

        db = FirebaseDatabase.getInstance().getReference("microgreens");

        loadData();

        return view;
    }

    private void loadData(){

        db.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {

                list.clear();

                String plantName = snapshot.child("plant").child("name").getValue(String.class);
                String planted = snapshot.child("plant").child("datePlanted").getValue(String.class);
                String sprouted = snapshot.child("plant").child("dateSprouted").getValue(String.class);
                String harvested = snapshot.child("plant").child("dateHarvested").getValue(String.class);

                String lastWatered = snapshot.child("history").child("lastWatered").getValue(String.class);

                Boolean lamp = snapshot.child("control").child("lamp").getValue(Boolean.class);
                String lampStatus = lamp != null && lamp ? "ON" : "OFF";

                String lampChanged = snapshot.child("history").child("lastLampChange").getValue(String.class);

                Boolean fan = snapshot.child("control").child("fan").getValue(Boolean.class);
                String fanStatus = fan != null && fan ? "ON" : "OFF";

                String fanChanged = snapshot.child("history").child("lastFanChange").getValue(String.class);

                list.add(new HistoryModel(
                        plantName,
                        planted,
                        sprouted,
                        harvested,
                        lastWatered,
                        lampStatus,
                        lampChanged,
                        fanStatus,
                        fanChanged
                ));

                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError error) {

            }
        });
    }
}