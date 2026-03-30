package edu.uph.m23si1.microgreens.ui.control;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.fragment.app.Fragment;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import edu.uph.m23si1.microgreens.R;
import edu.uph.m23si1.microgreens.data.AppFirebaseDatabase;

public class ControlFragment extends Fragment {

    Button ledOn, ledOff;
    Button pumpOn, pumpOff;
    Button fanOn, fanOff;

    DatabaseReference controlRef;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_control, container, false);

        ledOn = view.findViewById(R.id.ledOn);
        ledOff = view.findViewById(R.id.ledOff);

        pumpOn = view.findViewById(R.id.pumpOn);
        pumpOff = view.findViewById(R.id.pumpOff);

        fanOn = view.findViewById(R.id.fanOn);
        fanOff = view.findViewById(R.id.fanOff);

        controlRef = AppFirebaseDatabase.get().getReference("microgreens/control");

        setupButtons();
        readFirebase();

        return view;
    }

    private void setupButtons() {

        ledOn.setOnClickListener(v -> controlRef.child("led").setValue(true));
        ledOff.setOnClickListener(v -> controlRef.child("led").setValue(false));

        pumpOn.setOnClickListener(v -> controlRef.child("pump").setValue(true));
        pumpOff.setOnClickListener(v -> controlRef.child("pump").setValue(false));

        fanOn.setOnClickListener(v -> controlRef.child("fan").setValue(true));
        fanOff.setOnClickListener(v -> controlRef.child("fan").setValue(false));
    }

    private void readFirebase() {

        controlRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {

                Boolean led = snapshot.child("led").getValue(Boolean.class);
                Boolean pump = snapshot.child("pump").getValue(Boolean.class);
                Boolean fan = snapshot.child("fan").getValue(Boolean.class);

                // nanti kalau mau tampilkan status bisa disini
            }

            @Override
            public void onCancelled(DatabaseError error) {

            }
        });
    }
}
