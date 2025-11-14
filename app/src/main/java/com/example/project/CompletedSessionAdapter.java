package com.example.project;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;

import java.util.ArrayList;
import java.util.List;

/**
 * RecyclerView adapter for displaying completed study sessions
 */
public class CompletedSessionAdapter extends RecyclerView.Adapter<CompletedSessionAdapter.ViewHolder> {
    
    private List<CompletedSession> sessions;
    private OnSessionClickListener listener;

    /**
     * Interface for handling session item clicks
     */
    public interface OnSessionClickListener {
        void onSessionClick(CompletedSession session);
    }

    public CompletedSessionAdapter(List<CompletedSession> sessions) {
        this.sessions = sessions != null ? new ArrayList<>(sessions) : new ArrayList<>();
    }

    /**
     * Sets the click listener for session items
     */
    public void setOnSessionClickListener(OnSessionClickListener listener) {
        this.listener = listener;
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
        
        // Reset border to 0 (in case this view was reused)
        holder.cardView.setStrokeWidth(0);
        
        // Set subject
        holder.tvSubject.setText(session.getSubject());
        
        // Set task
        holder.tvTask.setText(session.getTask());
        
        // Set time spent with "Time Spent: " prefix
        holder.tvTimeSpent.setText("Time Spent: " + session.getFormattedTimeSpent());
        
        // Set completion time (relative)
        holder.tvCompletionTime.setText(session.getRelativeTime());
        
        // Set click listener on item view
        holder.itemView.setOnClickListener(v -> {
            // Show border when clicked (similar to technique cards)
            float density = v.getContext().getResources().getDisplayMetrics().density;
            int strokeWidthPx = (int) (3 * density);
            holder.cardView.setStrokeWidth(strokeWidthPx);
            
            if (listener != null) {
                listener.onSessionClick(session);
            }
        });
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
        MaterialCardView cardView;
        TextView tvSubject;
        TextView tvTask;
        TextView tvTimeSpent;
        TextView tvCompletionTime;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            // Get reference to the MaterialCardView (the root item view)
            cardView = (MaterialCardView) itemView;
            tvSubject = itemView.findViewById(R.id.tvSubject);
            tvTask = itemView.findViewById(R.id.tvTask);
            tvTimeSpent = itemView.findViewById(R.id.tvTimeSpent);
            tvCompletionTime = itemView.findViewById(R.id.tvCompletionTime);
        }
    }
}

