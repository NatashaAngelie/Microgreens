package edu.uph.m23si1.microgreens.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import edu.uph.m23si1.microgreens.Model.Plant;
import edu.uph.m23si1.microgreens.Model.PlantListItem;
import edu.uph.m23si1.microgreens.R;

public class ManagePlantAdapter extends RecyclerView.Adapter<ManagePlantAdapter.Holder> {

    public interface Listener {
        void onEdit(@NonNull PlantListItem item);

        void onDelete(@NonNull PlantListItem item);
    }

    private final List<PlantListItem> items;
    private final Listener listener;

    public ManagePlantAdapter(List<PlantListItem> items, Listener listener) {
        this.items = items;
        this.listener = listener;
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_manage_plant, parent, false);
        return new Holder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {
        PlantListItem row = items.get(position);
        Plant p = row.getPlant();
        holder.name.setText(p.getPlantName() != null ? p.getPlantName() : "");

        android.content.Context ctx = holder.itemView.getContext();
        holder.planted.setText(ctx.getString(R.string.planted_prefix) + " " + emptyDash(p.getDatePlanted()));
        holder.sprouted.setText(ctx.getString(R.string.sprouted_prefix) + " " + emptyDash(p.getDateSprouted()));
        holder.harvested.setText(ctx.getString(R.string.harvested_label) + " " + emptyDash(p.getHarvestDate()));

        holder.edit.setOnClickListener(v -> listener.onEdit(row));
        holder.delete.setOnClickListener(v -> listener.onDelete(row));
    }

    private static String emptyDash(String s) {
        if (s == null || s.trim().isEmpty()) {
            return "—";
        }
        return s.trim();
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class Holder extends RecyclerView.ViewHolder {
        final TextView name;
        final TextView planted;
        final TextView sprouted;
        final TextView harvested;
        final ImageButton edit;
        final ImageButton delete;

        Holder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.tvPlantName);
            planted = itemView.findViewById(R.id.tvPlanted);
            sprouted = itemView.findViewById(R.id.tvSprouted);
            harvested = itemView.findViewById(R.id.tvHarvested);
            edit = itemView.findViewById(R.id.btnEdit);
            delete = itemView.findViewById(R.id.btnDelete);
        }
    }
}
