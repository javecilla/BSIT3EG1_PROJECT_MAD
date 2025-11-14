package com.example.project;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

/**
 * Fragment for the My Activity tab - displays completed study sessions
 * Shows empty state when no sessions, or RecyclerView with completed sessions
 */
public class MyActivityFragment extends Fragment {
    
    private RecyclerView recyclerViewCompletedSessions;
    private View emptyStateView;
    private CompletedSessionAdapter adapter;
    
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_my_activity, container, false);
        
        // Get references to views
        recyclerViewCompletedSessions = view.findViewById(R.id.recyclerViewCompletedSessions);
        emptyStateView = view.findViewById(R.id.emptyStateView);
        
        // Set up RecyclerView
        setupRecyclerView();
        
        return view;
    }
    
    /**
     * Sets up the RecyclerView with completed sessions
     */
    private void setupRecyclerView() {
        // Get MainActivity instance
        MainActivity activity = (MainActivity) getActivity();
        if (activity == null) {
            return; // Safety check
        }
        
        // Get completed sessions list from MainActivity
        List<CompletedSession> sessions = activity.getCompletedSessions();
        
        // Create adapter
        adapter = new CompletedSessionAdapter(sessions);
        
        // Set adapter to RecyclerView
        recyclerViewCompletedSessions.setAdapter(adapter);
        
        // Set layout manager (vertical linear layout)
        recyclerViewCompletedSessions.setLayoutManager(new LinearLayoutManager(getContext()));
        
        // Show/hide empty state and RecyclerView based on list size
        updateUI(sessions);
    }
    
    /**
     * Updates UI to show/hide empty state and RecyclerView
     */
    private void updateUI(List<CompletedSession> sessions) {
        if (sessions == null || sessions.isEmpty()) {
            // Show empty state, hide RecyclerView
            if (emptyStateView != null) {
                emptyStateView.setVisibility(View.VISIBLE);
            }
            if (recyclerViewCompletedSessions != null) {
                recyclerViewCompletedSessions.setVisibility(View.GONE);
            }
        } else {
            // Hide empty state, show RecyclerView
            if (emptyStateView != null) {
                emptyStateView.setVisibility(View.GONE);
            }
            if (recyclerViewCompletedSessions != null) {
                recyclerViewCompletedSessions.setVisibility(View.VISIBLE);
            }
        }
    }
    
    /**
     * Refreshes the RecyclerView with updated data
     * Called when fragment becomes visible (onResume)
     */
    private void refreshList() {
        MainActivity activity = (MainActivity) getActivity();
        if (activity == null) {
            return; // Safety check
        }
        
        // Get updated list from MainActivity
        List<CompletedSession> sessions = activity.getCompletedSessions();
        
        // Update adapter with new list
        if (adapter != null) {
            adapter.updateList(sessions);
        } else {
            // Adapter not created yet, set it up
            setupRecyclerView();
            return;
        }
        
        // Update UI visibility
        updateUI(sessions);
    }
    
    @Override
    public void onResume() {
        super.onResume();
        // Refresh list when fragment becomes visible
        refreshList();
    }
}

