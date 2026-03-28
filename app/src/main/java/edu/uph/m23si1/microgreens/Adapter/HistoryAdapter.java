package edu.uph.m23si1.microgreens.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import edu.uph.m23si1.microgreens.Model.HistoryModel;
import edu.uph.m23si1.microgreens.R;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.ViewHolder> {

    List<HistoryModel> list;

    public HistoryAdapter(List<HistoryModel> list){
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_history, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        HistoryModel model = list.get(position);

        holder.plantName.setText(model.getPlantName());
        holder.datePlanted.setText(model.getDatePlanted());
        holder.dateSprouted.setText(model.getDateSprouted());
        holder.dateHarvested.setText(model.getDateHarvested());

        holder.lastWatered.setText(model.getLastWatered());

        holder.lampStatus.setText(model.getLampStatus());
        holder.lampChanged.setText(model.getLampChanged());

        holder.fanStatus.setText(model.getFanStatus());
        holder.fanChanged.setText(model.getFanChanged());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView plantName,datePlanted,dateSprouted,dateHarvested;
        TextView lastWatered;
        TextView lampStatus,lampChanged;
        TextView fanStatus,fanChanged;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            plantName = itemView.findViewById(R.id.plantName);
            datePlanted = itemView.findViewById(R.id.datePlanted);
            dateSprouted = itemView.findViewById(R.id.dateSprouted);
            dateHarvested = itemView.findViewById(R.id.dateHarvested);

            lastWatered = itemView.findViewById(R.id.lastWatered);

            lampStatus = itemView.findViewById(R.id.lampStatus);
            lampChanged = itemView.findViewById(R.id.lampChanged);

            fanStatus = itemView.findViewById(R.id.fanStatus);
            fanChanged = itemView.findViewById(R.id.fanChanged);
        }
    }
}