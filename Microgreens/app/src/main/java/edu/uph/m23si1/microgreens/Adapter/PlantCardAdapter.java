package edu.uph.m23si1.microgreens.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import edu.uph.m23si1.microgreens.Model.PlantCardModel;
import edu.uph.m23si1.microgreens.R;

public class PlantCardAdapter extends RecyclerView.Adapter<PlantCardAdapter.ViewHolder> {

    public interface OnPlantClickListener {
        void onClick(PlantCardModel plant);
    }

    private final List<PlantCardModel> list;
    private final OnPlantClickListener listener;

    public PlantCardAdapter(List<PlantCardModel> list, OnPlantClickListener listener) {
        this.list = list;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_plant_card, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        PlantCardModel model = list.get(position);
        holder.plantName.setText(model.getName());
        holder.lastActivityDate.setText(model.getLastActivityDateLabel());
        holder.lastActivityTime.setText(model.getLastActivityTime());

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onClick(model);
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView plantName;
        TextView lastActivityDate;
        TextView lastActivityTime;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            plantName = itemView.findViewById(R.id.plantName);
            lastActivityDate = itemView.findViewById(R.id.lastActivityDate);
            lastActivityTime = itemView.findViewById(R.id.lastActivityTime);
        }
    }
}

