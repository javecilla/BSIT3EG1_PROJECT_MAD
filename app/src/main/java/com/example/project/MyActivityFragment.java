package com.example.project;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.Chip;

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
        
        // Step 2.9: Set click listener for session items
        adapter.setOnSessionClickListener(session -> {
            showSessionDetailDialog(session);
        });
        
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
            // Reconnect click listener (in case adapter was recreated)
            adapter.setOnSessionClickListener(session -> {
                showSessionDetailDialog(session);
            });
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

    /**
     * Step 2.9: Shows the session detail dialog when a completed session item is clicked
     * @param session The completed session to display details for
     */
    private void showSessionDetailDialog(CompletedSession session) {
        if (session == null || getContext() == null) {
            return; // Safety check
        }

        // Create bottom sheet dialog
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(getContext());
        View dialogView = LayoutInflater.from(getContext())
                .inflate(R.layout.bottom_card_session_detail, null);
        bottomSheetDialog.setContentView(dialogView);

        // Get views from dialog
        Chip chipTechnique = dialogView.findViewById(R.id.chipTechnique);
        TextView tvCycleLabel = dialogView.findViewById(R.id.tvCycleLabel);
        TextView tvCycle = dialogView.findViewById(R.id.tvCycle);
        TextView tvSubject = dialogView.findViewById(R.id.tvSubject);
        TextView tvTask = dialogView.findViewById(R.id.tvTask);
        TextView tvSetDuration = dialogView.findViewById(R.id.tvSetDuration);
        TextView tvTimeSpent = dialogView.findViewById(R.id.tvTimeSpent);
        MaterialButton btnClose = dialogView.findViewById(R.id.btnClose);

        // Populate dialog with session data
        chipTechnique.setText(session.getTechnique());
        tvSubject.setText(session.getSubject());
        tvTask.setText(session.getTask());
        tvSetDuration.setText(session.getFormattedSetDuration());
        tvTimeSpent.setText(session.getFormattedTimeSpent());

        // Step 2.9: Show/hide Cycle TextView based on technique
        // Only show cycle for Pomodoro technique
        if (session.isPomodoro()) {
            tvCycleLabel.setVisibility(View.VISIBLE);
            tvCycle.setVisibility(View.VISIBLE);
            tvCycle.setText(String.valueOf(session.getCycle()));
        } else {
            tvCycleLabel.setVisibility(View.GONE);
            tvCycle.setVisibility(View.GONE);
        }

        // Close button click listener
        btnClose.setOnClickListener(v -> {
            bottomSheetDialog.dismiss();
        });

        // Clear border when dialog is dismissed
        bottomSheetDialog.setOnDismissListener(dialog -> {
            // Clear border from all items by refreshing the adapter
            // This will reset all stroke widths to 0
            if (adapter != null) {
                adapter.notifyDataSetChanged();
            }
        });

        // Show dialog
        bottomSheetDialog.show();
    }
}

