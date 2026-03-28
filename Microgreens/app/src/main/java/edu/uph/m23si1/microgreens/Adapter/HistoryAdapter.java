package edu.uph.m23si1.microgreens.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import edu.uph.m23si1.microgreens.Model.HistoryModel;
import edu.uph.m23si1.microgreens.R;

public class HistoryAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final List<HistoryModel> list;

    public HistoryAdapter(List<HistoryModel> list) {
        this.list = list;
    }

    @Override
    public int getItemViewType(int position) {
        HistoryModel model = list.get(position);
        return model.getType() == HistoryModel.RowType.HEADER ? 0 : 1;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == 0) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_history, parent, false);
            return new HeaderViewHolder(view);
        }

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_history_event, parent, false);
        return new EventViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        HistoryModel model = list.get(position);

        if (holder instanceof HeaderViewHolder) {
            HeaderViewHolder h = (HeaderViewHolder) holder;
            h.plantName.setText(model.getPlantName() != null ? model.getPlantName() : "-");
            h.lastActivityLabel.setText(model.getLastActivityLabel() != null ? model.getLastActivityLabel() : "Last Activity: -");
            h.lastActivityTime.setText(model.getLastActivityTime() != null ? model.getLastActivityTime() : "-");
            return;
        }

        EventViewHolder e = (EventViewHolder) holder;
        e.eventTitle.setText(model.getEventTitle() != null ? model.getEventTitle() : "-");
        e.eventTime.setText(model.getEventTime() != null ? model.getEventTime() : "-");
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class HeaderViewHolder extends RecyclerView.ViewHolder {
        ImageView plantImage;
        TextView plantName;
        TextView lastActivityLabel;
        TextView lastActivityTime;

        public HeaderViewHolder(@NonNull View itemView) {
            super(itemView);
            plantImage = itemView.findViewById(R.id.plantImage);
            plantName = itemView.findViewById(R.id.plantName);
            lastActivityLabel = itemView.findViewById(R.id.lastActivityLabel);
            lastActivityTime = itemView.findViewById(R.id.lastActivityTime);
        }
    }

    public static class EventViewHolder extends RecyclerView.ViewHolder {
        TextView eventTitle;
        TextView eventTime;

        public EventViewHolder(@NonNull View itemView) {
            super(itemView);
            eventTitle = itemView.findViewById(R.id.eventTitle);
            eventTime = itemView.findViewById(R.id.eventTime);
        }
    }
}
