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

    public static HistoryLogFragment newInstance(String plantName) {
        HistoryLogFragment f = new HistoryLogFragment();
        Bundle b = new Bundle();
        b.putString(ARG_PLANT_NAME, plantName);
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

        String plantName = getArguments() != null ? getArguments().getString(ARG_PLANT_NAME) : null;
        if (plantName == null || plantName.trim().isEmpty()) plantName = "Plant";
        title.setText("History Log");

        btnBack.setOnClickListener(v -> requireActivity()
                .getSupportFragmentManager()
                .popBackStack());

        RecyclerView recycler = view.findViewById(R.id.historyRecycler);
        recycler.setLayoutManager(new LinearLayoutManager(getContext()));

        // Dummy timeline (low fidelity) until Firebase history list is implemented
        List<HistoryModel> rows = new ArrayList<>();
        rows.add(HistoryModel.header(
                plantName,
                "Last Activity: 11 September",
                "2025 11.03.05 WIB"
        ));

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

