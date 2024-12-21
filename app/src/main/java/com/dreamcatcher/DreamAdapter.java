package com.dreamcatcher;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class DreamAdapter extends RecyclerView.Adapter<DreamAdapter.DreamViewHolder> {

    private List<Dream> dreams;

    public DreamAdapter(List<Dream> dreams) {
        this.dreams = dreams;
    }

    @NonNull
    @Override
    public DreamViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_dream, parent, false);
        return new DreamViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DreamViewHolder holder, int position) {
        Dream dream = dreams.get(position);
        holder.contentTextView.setText(dream.getContent());
        holder.authorTextView.setText(dream.getAuthor());
        holder.dateTextView.setText(dream.getDate());
    }

    @Override
    public int getItemCount() {
        return dreams.size();
    }

    public static class DreamViewHolder extends RecyclerView.ViewHolder {

        TextView contentTextView;
        TextView authorTextView;
        TextView dateTextView;

        public DreamViewHolder(@NonNull View itemView) {
            super(itemView);
            contentTextView = itemView.findViewById(R.id.dream_content);
            authorTextView = itemView.findViewById(R.id.dream_author);
            dateTextView = itemView.findViewById(R.id.dream_date);
        }
    }
}
