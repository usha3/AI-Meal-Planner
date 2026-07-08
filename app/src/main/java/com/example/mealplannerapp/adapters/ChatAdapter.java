package com.example.mealplannerapp.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mealplannerapp.R;
import com.example.mealplannerapp.models.ChatMessage;

import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ViewHolder> {

    List<ChatMessage> list;

    public ChatAdapter(List<ChatMessage> list) {
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_chat, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        ChatMessage msg = list.get(position);

        if (msg.isUser()) {

            holder.txtUser.setVisibility(View.VISIBLE);
            holder.txtAi.setVisibility(View.GONE);

            holder.txtUser.setText(msg.getMessage());

        } else {

            holder.txtAi.setVisibility(View.VISIBLE);
            holder.txtUser.setVisibility(View.GONE);

            holder.txtAi.setText(msg.getMessage());
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView txtUser, txtAi;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            txtUser = itemView.findViewById(R.id.txtUser);
            txtAi = itemView.findViewById(R.id.txtAi);
        }
    }
}