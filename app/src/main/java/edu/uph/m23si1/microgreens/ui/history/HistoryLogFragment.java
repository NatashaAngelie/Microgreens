package edu.uph.m23si1.microgreens.ui.history;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import edu.uph.m23si1.microgreens.Adapter.HistoryAdapter;
import edu.uph.m23si1.microgreens.Model.HistoryModel;
import edu.uph.m23si1.microgreens.R;

public class HistoryLogFragment extends Fragment {

    public static final String ARG_PLANT_NAME = "arg_plant_name";
    public static final String ARG_LAST_ACTIVITY_LABEL = "arg_last_activity_label";
    public static final String ARG_LAST_ACTIVITY_TIME = "arg_last_activity_time";

    public static HistoryLogFragment newInstance(String plantName, String lastActivityLabel, String lastActivityTime) {
        HistoryLogFragment f = new HistoryLogFragment();
        Bundle b = new Bundle();
        b.putString(ARG_PLANT_NAME, plantName);
        b.putString(ARG_LAST_ACTIVITY_LABEL, lastActivityLabel);
        b.putString(ARG_LAST_ACTIVITY_TIME, lastActivityTime);
        f.setArguments(b);
        return f;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_history_log, container, false);

        ImageView btnBack = view.findViewById(R.id.btnBack);
        TextView title = view.findViewById(R.id.historyTitle);

        Bundle args = getArguments();
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

        title.setText("History Log");

        btnBack.setOnClickListener(v -> requireActivity()
                .getSupportFragmentManager()
                .popBackStack());

        RecyclerView recycler = view.findViewById(R.id.historyRecycler);
        recycler.setLayoutManager(new LinearLayoutManager(getContext()));

        List<HistoryModel> rows = new ArrayList<>();
        rows.add(HistoryModel.header(plantName, lastActivityLabel, lastActivityTime));

        rows.add(HistoryModel.event(plantName + " was watered", "11 September 2025 | 11.03.05 WIB"));
        rows.add(HistoryModel.event("Fan was turned on", "10 September 2025 | 23.09.15 WIB"));
        rows.add(HistoryModel.event(plantName + " was watered", "11 September 2025 | 11.03.05 WIB"));
        rows.add(HistoryModel.event(plantName + " was watered", "11 September 2025 | 11.03.05 WIB"));
        rows.add(HistoryModel.event(plantName + " was watered", "11 September 2025 | 11.03.05 WIB"));
        rows.add(HistoryModel.event(plantName + " was watered", "11 September 2025 | 11.03.05 WIB"));

        recycler.setAdapter(new HistoryAdapter(rows));

        return view;
    }
}
