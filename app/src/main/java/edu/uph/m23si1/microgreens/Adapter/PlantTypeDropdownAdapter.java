package edu.uph.m23si1.microgreens.Adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.File;
import java.util.List;

import edu.uph.m23si1.microgreens.Model.PlantType;
import edu.uph.m23si1.microgreens.R;

public class PlantTypeDropdownAdapter extends ArrayAdapter<PlantType> {

    public PlantTypeDropdownAdapter(@NonNull Context context, @NonNull List<PlantType> objects) {
        super(context, 0, objects);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return row(position, convertView, parent);
    }

    @NonNull
    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return row(position, convertView, parent);
    }

    @NonNull
    private View row(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View v = convertView;
        if (v == null) {
            v = LayoutInflater.from(getContext()).inflate(R.layout.item_plant_type_dropdown, parent, false);
        }

        PlantType t = getItem(position);
        TextView tv = v.findViewById(R.id.tvTypeName);
        ImageView iv = v.findViewById(R.id.ivTypeIcon);

        if (t == null) {
            tv.setText("");
            iv.setImageResource(R.drawable.ic_plant_placeholder_small);
            return v;
        }

        tv.setText(t.getDisplayName());

        Bitmap b = loadBitmap(t.getLocalImagePath());
        if (b != null) {
            iv.setImageBitmap(b);
        } else {
            iv.setImageResource(R.drawable.ic_plant_placeholder_small);
        }

        return v;
    }

    @Nullable
    private Bitmap loadBitmap(@Nullable String path) {
        if (path == null || path.trim().isEmpty()) return null;
        try {
            File f = new File(path);
            if (!f.exists()) return null;
            return BitmapFactory.decodeFile(f.getAbsolutePath());
        } catch (Exception ignored) {
            return null;
        }
    }
}

