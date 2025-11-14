package com.example.project;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

/**
 * RecyclerView adapter for displaying completed study sessions
 */
public class CompletedSessionAdapter extends RecyclerView.Adapter<CompletedSessionAdapter.ViewHolder> {
    
    private List<CompletedSession> sessions;

    public CompletedSessionAdapter(List<CompletedSession> sessions) {
        this.sessions = sessions != null ? new ArrayList<>(sessions) : new ArrayList<>();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_completed_session, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CompletedSession session = sessions.get(position);
        
        // Set technique name (TextView, not Chip)
        holder.chipTechnique.setText(session.getTechnique());
        
        // Set subject
        holder.tvSubject.setText(session.getSubject());
        
        // Set task
        holder.tvTask.setText(session.getTask());
        
        // Set time spent with "Time Spent: " prefix
        holder.tvTimeSpent.setText("Time Spent: " + session.getFormattedTimeSpent());
        
        // Set completion time (relative)
        holder.tvCompletionTime.setText(session.getRelativeTime());
    }

    @Override
    public int getItemCount() {
        return sessions.size();
    }

    /**
     * Updates the adapter with a new list of sessions
     */
    public void updateList(List<CompletedSession> newList) {
        this.sessions = newList != null ? new ArrayList<>(newList) : new ArrayList<>();
        notifyDataSetChanged();
    }

    /**
     * ViewHolder class for RecyclerView items
     */
    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView chipTechnique; // Changed from Chip to TextView
        TextView tvSubject;
        TextView tvTask;
        TextView tvTimeSpent;
        TextView tvCompletionTime;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            chipTechnique = itemView.findViewById(R.id.chipTechnique);
            tvSubject = itemView.findViewById(R.id.tvSubject);
            tvTask = itemView.findViewById(R.id.tvTask);
            tvTimeSpent = itemView.findViewById(R.id.tvTimeSpent);
            tvCompletionTime = itemView.findViewById(R.id.tvCompletionTime);
        }
    }
}

