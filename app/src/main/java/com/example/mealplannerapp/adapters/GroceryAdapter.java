package com.example.mealplannerapp.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.mealplannerapp.R;
import com.example.mealplannerapp.models.GroceryItem;

import java.util.List;

public class GroceryAdapter extends RecyclerView.Adapter<GroceryAdapter.ViewHolder> {

    List<GroceryItem> list;
    boolean isPurchasedList;
    OnItemActionListener listener;

    public interface OnItemActionListener {
        void onDelete(int position);
        void onIncrease(int position);
        void onDecrease(int position);
        void onMoveToPurchased(int position);
    }

    public GroceryAdapter(List<GroceryItem> list, boolean isPurchasedList, OnItemActionListener listener) {
        this.list = list;
        this.isPurchasedList = isPurchasedList;
        this.listener = listener;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView name, qty, plus, minus;
        ImageView delete;

        public ViewHolder(View view) {
            super(view);
            name = view.findViewById(R.id.itemName);
            qty = view.findViewById(R.id.itemQty);
            delete = view.findViewById(R.id.deleteBtn);
            plus = view.findViewById(R.id.btnPlus);
            minus = view.findViewById(R.id.btnMinus);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_grocery, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        GroceryItem item = list.get(position);

        holder.name.setText(item.name);
        holder.qty.setText(String.valueOf(item.quantity));

        holder.plus.setOnClickListener(v -> {
            int pos = holder.getAdapterPosition();
            if (pos != RecyclerView.NO_POSITION) {
                listener.onIncrease(pos);
            }
        });

        holder.minus.setOnClickListener(v -> {
            int pos = holder.getAdapterPosition();
            if (pos != RecyclerView.NO_POSITION) {
                listener.onDecrease(pos);
            }
        });

        holder.delete.setOnClickListener(v -> {
            int pos = holder.getAdapterPosition();
            if (pos != RecyclerView.NO_POSITION) {
                listener.onDelete(pos);
            }
        });

        if (!isPurchasedList) {
            holder.itemView.setOnLongClickListener(v -> {
                int pos = holder.getAdapterPosition();
                if (pos != RecyclerView.NO_POSITION) {
                    listener.onMoveToPurchased(pos);
                }
                return true;
            });
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}