package edu.uph.m23si1.microgreens;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import edu.uph.m23si1.microgreens.Adapter.HistoryAdapter;
import edu.uph.m23si1.microgreens.Model.HistoryModel;

public class HistoryLogActivity extends AppCompatActivity {

    public static final String EXTRA_PLANT_NAME = "extra_plant_name";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history_log);

        String plantName = getIntent() != null ? getIntent().getStringExtra(EXTRA_PLANT_NAME) : null;
        if (plantName == null || plantName.trim().isEmpty()) plantName = "Radish";

        TextView title = findViewById(R.id.historyTitle);
        title.setText("History Log");
        ImageView btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());

        RecyclerView recycler = findViewById(R.id.historyRecycler);
        recycler.setLayoutManager(new LinearLayoutManager(this));

        // Dummy timeline (biar sama seperti low fidelity)
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
    }
}

