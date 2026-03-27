package edu.uph.m23si1.microgreens.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import edu.uph.m23si1.microgreens.Model.SensorModel;
import edu.uph.m23si1.microgreens.R;

public class SensorAdapter extends RecyclerView.Adapter<SensorAdapter.ViewHolder> {

    Context context;
    ArrayList<SensorModel> list;

    public SensorAdapter(Context context, ArrayList<SensorModel> list) {
        this.context = context;
        this.list = list;
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        TextView name, value;
        Switch sw;

        public ViewHolder(View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.txtName);
            value = itemView.findViewById(R.id.txtValue);
            sw = itemView.findViewById(R.id.switchStatus);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context)
                .inflate(R.layout.item_sensor, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        SensorModel sensor = list.get(position);

        holder.name.setText(sensor.getName());
        holder.value.setText(sensor.getValue());
        holder.sw.setChecked(sensor.isStatus());

        holder.sw.setOnCheckedChangeListener((buttonView, isChecked) -> {
            sensor.setStatus(isChecked);

            Toast.makeText(context,
                    sensor.getName() + (isChecked ? " ON" : " OFF"),
                    Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}
