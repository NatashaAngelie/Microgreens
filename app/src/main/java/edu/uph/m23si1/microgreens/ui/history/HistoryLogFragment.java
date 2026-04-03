package edu.uph.m23si1.microgreens.ui.history;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import edu.uph.m23si1.microgreens.Adapter.HistoryAdapter;
import edu.uph.m23si1.microgreens.Model.HistoryEvent;
import edu.uph.m23si1.microgreens.Model.HistoryModel;
import edu.uph.m23si1.microgreens.R;
import edu.uph.m23si1.microgreens.data.HistoryEventFormatter;
import edu.uph.m23si1.microgreens.data.MicrogreensHistoryWriter;

public class HistoryLogFragment extends Fragment {

    public static final String ARG_PLANT_ID = "arg_plant_id";
    public static final String ARG_PLANT_NAME = "arg_plant_name";
    public static final String ARG_LAST_ACTIVITY_LABEL = "arg_last_activity_label";
    public static final String ARG_LAST_ACTIVITY_TIME = "arg_last_activity_time";

    public static HistoryLogFragment newInstance(
            @NonNull String plantId,
            String plantName,
            String lastActivityLabel,
            String lastActivityTime
    ) {
        HistoryLogFragment f = new HistoryLogFragment();
        Bundle b = new Bundle();
        b.putString(ARG_PLANT_ID, plantId);
        b.putString(ARG_PLANT_NAME, plantName);
        b.putString(ARG_LAST_ACTIVITY_LABEL, lastActivityLabel);
        b.putString(ARG_LAST_ACTIVITY_TIME, lastActivityTime);
        f.setArguments(b);
        return f;
    }

    private ValueEventListener eventsListener;
    private DatabaseReference eventsRef;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_history_log, container, false);

        Bundle args = getArguments();
        String plantId = args != null ? args.getString(ARG_PLANT_ID) : null;
        String plantName = args != null ? args.getString(ARG_PLANT_NAME) : null;
        String lastActivityLabel = args != null ? args.getString(ARG_LAST_ACTIVITY_LABEL) : null;
        String lastActivityTime = args != null ? args.getString(ARG_LAST_ACTIVITY_TIME) : null;

        if (plantName == null || plantName.trim().isEmpty()) plantName = "Plant";
        if (lastActivityLabel == null || lastActivityLabel.trim().isEmpty()) {
            lastActivityLabel = "Last Activity: -";
        }
        if (lastActivityTime == null || lastActivityTime.trim().isEmpty()) {
            lastActivityTime = "-";
        }

        final String headerPlantName = plantName;
        final String headerActivityLabel = lastActivityLabel;
        final String headerActivityTime = lastActivityTime;

        RecyclerView recycler = view.findViewById(R.id.historyRecycler);
        recycler.setLayoutManager(new LinearLayoutManager(getContext()));

        if (plantId == null || plantId.isEmpty()) {
            List<HistoryModel> rows = new ArrayList<>();
            rows.add(HistoryModel.header(headerPlantName, headerActivityLabel, headerActivityTime));
            recycler.setAdapter(new HistoryAdapter(rows));
            return view;
        }

        eventsRef = MicrogreensHistoryWriter.historyEventsRef(plantId);
        eventsListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<HistoryEvent> events = new ArrayList<>();
                for (DataSnapshot child : snapshot.getChildren()) {
                    HistoryEvent ev = child.getValue(HistoryEvent.class);
                    if (ev != null && ev.getMessage() != null) {
                        events.add(ev);
                    }
                }
                Collections.sort(events, Comparator.comparingLong((HistoryEvent e) -> {
                    Long t = e.getCreatedAt();
                    return t != null ? t : 0L;
                }).reversed());

                List<HistoryModel> rows = new ArrayList<>();
                rows.add(HistoryModel.header(headerPlantName, headerActivityLabel, headerActivityTime));
                for (HistoryEvent ev : events) {
                    long ts = ev.getCreatedAt() != null ? ev.getCreatedAt() : 0L;
                    String timeLine = ts > 0 ? HistoryEventFormatter.formatLine(ts) : "-";
                    rows.add(HistoryModel.event(ev.getMessage(), timeLine));
                }
                recycler.setAdapter(new HistoryAdapter(rows));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        };
        eventsRef.addValueEventListener(eventsListener);

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (eventsListener != null && eventsRef != null) {
            eventsRef.removeEventListener(eventsListener);
            eventsListener = null;
            eventsRef = null;
        }
    }
}
