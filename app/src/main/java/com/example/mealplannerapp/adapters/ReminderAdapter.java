package com.example.mealplannerapp.adapters;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.mealplannerapp.R;

import java.util.ArrayList;

public class ReminderAdapter extends ArrayAdapter<String> {

    private final ArrayList<String> list;
    private final Activity context;

    public ReminderAdapter(Activity context, ArrayList<String> list) {
        super(context, R.layout.item_reminder, list);
        this.context = context;
        this.list = list;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = context.getLayoutInflater();
        View row = inflater.inflate(R.layout.item_reminder, null, true);

        TextView txt = row.findViewById(R.id.txtReminder);
        ImageView delete = row.findViewById(R.id.btnDelete);

        txt.setText(list.get(position));

        delete.setOnClickListener(v -> {
            list.remove(position);
            notifyDataSetChanged();
        });

        return row;
    }
}
