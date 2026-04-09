package edu.uph.m23si1.microgreens.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import edu.uph.m23si1.microgreens.Model.PlantSpec;
import edu.uph.m23si1.microgreens.Model.PlantType;
import edu.uph.m23si1.microgreens.R;

public class PlantSpecAdapter extends RecyclerView.Adapter<PlantSpecAdapter.VH> {

    public interface OnEditClick {
        void onEdit(@NonNull PlantType type);
    }

    private final List<PlantType> types = new ArrayList<>();
    private final SpecProvider provider;
    private final OnEditClick onEditClick;

    public interface SpecProvider {
        PlantSpec getSpec(@NonNull String plantTypeId);
    }

    public PlantSpecAdapter(@NonNull SpecProvider provider, @NonNull OnEditClick onEditClick) {
        this.provider = provider;
        this.onEditClick = onEditClick;
    }

    public void submit(@NonNull List<PlantType> items) {
        types.clear();
        types.addAll(items);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_plant_spec, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH h, int position) {
        PlantType t = types.get(position);
        h.name.setText(t.getDisplayName());

        PlantSpec s = provider.getSpec(t.getId());
        h.summary.setText(summary(s));

        h.edit.setOnClickListener(v -> onEditClick.onEdit(t));
    }

    @Override
    public int getItemCount() {
        return types.size();
    }

    private static String summary(PlantSpec s) {
        if (s == null) return "No specs yet (tap Edit).";
        String lh = s.lightHoursPerDay == null ? "—" : String.format(Locale.US, "%.1f h/day", s.lightHoursPerDay);
        String tc = s.temperatureC == null ? "—" : String.format(Locale.US, "%.1f °C", s.temperatureC);
        String sm = s.soilMoisturePercent == null ? "—" : String.format(Locale.US, "%.0f%%", s.soilMoisturePercent);
        return "Light: " + lh + " • Temp: " + tc + " • Soil: " + sm;
    }

    static class VH extends RecyclerView.ViewHolder {
        final TextView name;
        final TextView summary;
        final View edit;

        VH(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.tvSpecPlantName);
            summary = itemView.findViewById(R.id.tvSpecSummary);
            edit = itemView.findViewById(R.id.btnEditSpec);
        }
    }
}

