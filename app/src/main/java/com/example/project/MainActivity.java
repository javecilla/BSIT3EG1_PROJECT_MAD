package com.example.project;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.chip.Chip;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class MainActivity extends AppCompatActivity {

    // TEST MODE: Set to true for faster testing (reduces all timers to seconds instead of minutes)
    // Set to false for production
    private static final boolean TEST_MODE = true; // TODO: Set to false before release
    
    // Test mode multiplier: 1 = normal, 60 = convert minutes to seconds (60x faster)
    private static final int TEST_MODE_MULTIPLIER = TEST_MODE ? 60 : 1;

    // Technique constants
    private static final String TECHNIQUE_POMODORO = "Pomodoro";
    private static final String TECHNIQUE_5217 = "52/17";
    private static final String TECHNIQUE_90MINUTE = "90-Minute";
    private static final String TECHNIQUE_SPRINT = "Sprint";
    private static final String TECHNIQUE_DEADLINE = "Deadline";
    private static final String TECHNIQUE_CUSTOM = "Custom Goal";

    // Transient variable for selected technique
    private String selectedTechnique = null;

    // Transient variable for current session
    private StudySession currentSession = null;

    // Current session state (for quick access, synced with currentSession.getState())
    private SessionState currentState = SessionState.IDLE;
    
    // Track previous state before pausing (to know if we were in work or break)
    private SessionState previousStateBeforePause = null;

    // Reference to active bottom sheet dialog (for sticky card behavior)
    private BottomSheetDialog activeBottomSheetDialog = null;

    // Reference to completion dialog (to prevent duplicate dialogs)
    private android.app.AlertDialog completionDialog = null;

    // Timer instances
    private CountDownTimer workTimer = null;
    private CountDownTimer breakTimer = null;
    
    // Store current break total duration for extend break functionality
    private long currentBreakTotalDurationMillis = 0;

    // Flag to track if app was paused (to distinguish from being killed)
    private boolean wasPaused = false;

    // References to active session UI views (for timer updates)
    private TextView tvTimerDisplay = null;
    private TextView tvTimeRemaining = null;
    private TextView tvPhaseIndicator = null;
    private TextView tvCycleCounter = null;
    private View progressBar = null;
    private MaterialButton btnPause = null;
    private MaterialButton btnCancelSession = null;
    private MaterialButton btnMarkDone = null;
    private MaterialButton btnSkipBreak = null;
    private MaterialButton btnExtendBreak = null;
    private LinearLayout buttonRow = null;
    private LinearLayout breakButtonRow = null;
    private ImageButton btnMinimize = null;
    private FrameLayout progressContainer = null;
    
    // Minimize/Maximize state
    private boolean isMinimized = false;

    // TabLayout and ViewPager2
    private TabLayout tabLayout;
    private ViewPager2 viewPager;
    private ViewPagerAdapter viewPagerAdapter;
    private PlaygroundFragment playgroundFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Step 1.6: Initialize TabLayout and ViewPager2
        tabLayout = findViewById(R.id.tabLayout);
        viewPager = findViewById(R.id.viewPager);
        
        // Create adapter
        viewPagerAdapter = new ViewPagerAdapter(this);
        viewPager.setAdapter(viewPagerAdapter);
        
        // Connect TabLayout with ViewPager2
        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            switch (position) {
                case 0:
                    tab.setText("PlayGround");
                    break;
                case 1:
                    tab.setText("My Activity");
                    break;
            }
        }).attach();
        
        // Get reference to PlaygroundFragment after ViewPager2 is set up
        // We'll set up the listener after fragments are created
        viewPager.post(() -> {
            // ViewPager2 uses tag format: "f" + itemId
            long itemId = viewPagerAdapter.getItemId(0);
            Fragment fragment = getSupportFragmentManager().findFragmentByTag("f" + itemId);
            if (fragment instanceof PlaygroundFragment) {
                playgroundFragment = (PlaygroundFragment) fragment;
                setupPlaygroundFragment();
            } else {
                // Alternative: search through all fragments
                for (Fragment f : getSupportFragmentManager().getFragments()) {
                    if (f instanceof PlaygroundFragment) {
                        playgroundFragment = (PlaygroundFragment) f;
                        setupPlaygroundFragment();
                        break;
                    }
                }
                // If still not found, try again after a short delay
                if (playgroundFragment == null) {
                    new Handler(Looper.getMainLooper()).postDelayed(() -> {
                        for (Fragment f : getSupportFragmentManager().getFragments()) {
                            if (f instanceof PlaygroundFragment) {
                                playgroundFragment = (PlaygroundFragment) f;
                                setupPlaygroundFragment();
                                break;
                            }
                        }
                    }, 100);
                }
            }
        });
    }
    
    /**
     * Step 1.6: Sets up the PlaygroundFragment listener and initializes card references
     */
    private void setupPlaygroundFragment() {
        if (playgroundFragment == null) {
            return;
        }
        
        // Set up listener for technique selection
        playgroundFragment.setPlaygroundListener(new PlaygroundFragment.PlaygroundListener() {
            @Override
            public void onTechniqueSelected(String technique) {
                handleTechniqueSelection(technique);
            }
            
            @Override
            public void onShowBottomSheet() {
                selectedTechnique = null; // Clear any selected technique
                clearCardHighlights();
                showBottomSheet();
            }
        });
    }

    /**
     * Handles technique selection from technique cards
     */
    private void handleTechniqueSelection(String technique) {
        selectedTechnique = technique;
        highlightSelectedCard(technique);
        showBottomSheet();
    }

    /**
     * Highlights the selected technique card
     */
    private void highlightSelectedCard(String technique) {
        // Clear all highlights first
        clearCardHighlights();

        if (playgroundFragment == null) {
            return;
        }

        // Highlight the selected card
        MaterialCardView selectedCard = null;
        switch (technique) {
            case TECHNIQUE_POMODORO:
                selectedCard = playgroundFragment.getCardTechnique1();
                break;
            case TECHNIQUE_5217:
                selectedCard = playgroundFragment.getCardTechnique2();
                break;
            case TECHNIQUE_90MINUTE:
                selectedCard = playgroundFragment.getCardTechnique3();
                break;
            case TECHNIQUE_SPRINT:
                selectedCard = playgroundFragment.getCardTechnique4();
                break;
            case TECHNIQUE_DEADLINE:
                selectedCard = playgroundFragment.getCardTechnique5();
                break;
        }

        if (selectedCard != null) {
            // Convert 3dp to pixels
            float density = getResources().getDisplayMetrics().density;
            int strokeWidthPx = (int) (3 * density);
            selectedCard.setStrokeWidth(strokeWidthPx);
        }
    }

    /**
     * Clears highlights from all technique cards
     */
    private void clearCardHighlights() {
        if (playgroundFragment == null) {
            return;
        }
        
        MaterialCardView card1 = playgroundFragment.getCardTechnique1();
        MaterialCardView card2 = playgroundFragment.getCardTechnique2();
        MaterialCardView card3 = playgroundFragment.getCardTechnique3();
        MaterialCardView card4 = playgroundFragment.getCardTechnique4();
        MaterialCardView card5 = playgroundFragment.getCardTechnique5();
        
        if (card1 != null) card1.setStrokeWidth(0);
        if (card2 != null) card2.setStrokeWidth(0);
        if (card3 != null) card3.setStrokeWidth(0);
        if (card4 != null) card4.setStrokeWidth(0);
        if (card5 != null) card5.setStrokeWidth(0);
    }

    /**
     * Gets the duration in minutes for a given technique
     */
    private int getTechniqueDuration(String technique) {
        switch (technique) {
            case TECHNIQUE_POMODORO:
                return 25;
            case TECHNIQUE_5217:
                return 52;
            case TECHNIQUE_90MINUTE:
                return 90;
            case TECHNIQUE_SPRINT:
                return 12;
            case TECHNIQUE_DEADLINE:
            case TECHNIQUE_CUSTOM:
            default:
                return 0; // User input required
        }
    }

    private void showBottomSheet() {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
        View sheetView = getLayoutInflater().inflate(R.layout.bottom_card_create, null);
        bottomSheetDialog.setContentView(sheetView);

        // Get views
        TextInputEditText etSubject = sheetView.findViewById(R.id.etSubject);
        TextInputEditText etTask = sheetView.findViewById(R.id.etTask);
        TextInputEditText etDuration = sheetView.findViewById(R.id.etDuration);
        TextInputLayout tilDuration = sheetView.findViewById(R.id.tilDuration);
        Chip chipTechnique = sheetView.findViewById(R.id.chipTechnique);
        TextView tvTechniqueIndicator = sheetView.findViewById(R.id.tvTechniqueIndicator);
        MaterialButton btnClose = sheetView.findViewById(R.id.btnClose);
        MaterialButton btnConfirmGoal = sheetView.findViewById(R.id.btnConfirmGoal);

        // Update technique indicator and chip
        String techniqueName = selectedTechnique != null ? selectedTechnique : TECHNIQUE_CUSTOM;
        chipTechnique.setText(techniqueName);
        tvTechniqueIndicator.setText("Using: " + techniqueName);
        
        // Step 1.3: Add click listener to technique chip to show info dialog
        chipTechnique.setOnClickListener(v -> {
            showTechniqueInfoDialog(techniqueName, false); // false = from create dialog
        });

        // Handle duration field based on technique selection
        if (selectedTechnique != null && !selectedTechnique.equals(TECHNIQUE_DEADLINE)) {
            // Pre-fill duration for pre-made techniques
            int duration = getTechniqueDuration(selectedTechnique);
            etDuration.setText(String.valueOf(duration));
            etDuration.setEnabled(false);
            tilDuration.setHelperText(duration + " min (" + selectedTechnique + ")");
        } else {
            // Enable duration field for custom goal or deadline
            // Explicitly clear all fields for custom goal
            etSubject.setText("");
            etTask.setText("");
            etDuration.setText("");
            etDuration.setEnabled(true);
            tilDuration.setHelperText("Enter duration in minutes");
        }

        btnClose.setOnClickListener(v -> {
            bottomSheetDialog.dismiss();
            selectedTechnique = null; // Clear selection when closed
            clearCardHighlights();
        });

        // Start Session button click handler
        // This implements the complete session start flow
        btnConfirmGoal.setOnClickListener(v -> {
            // Get and trim input values from form
            String subject = etSubject.getText().toString().trim();
            String task = etTask.getText().toString().trim();
            String duration = etDuration.getText().toString().trim();

            // Validate subject field
            if (subject.isEmpty()) {
                Toast.makeText(this, "Please enter a subject", Toast.LENGTH_SHORT).show();
                etSubject.requestFocus();
                return;
            }

            // Validate task field
            if (task.isEmpty()) {
                Toast.makeText(this, "Please enter a task", Toast.LENGTH_SHORT).show();
                etTask.requestFocus();
                return;
            }

            // Validate duration field
            if (duration.isEmpty()) {
                Toast.makeText(this, "Please enter a duration", Toast.LENGTH_SHORT).show();
                etDuration.requestFocus();
                return;
            }

            // Validate duration is a valid positive number
            int durationInt;
            try {
                durationInt = Integer.parseInt(duration);
                if (durationInt <= 0) {
                    Toast.makeText(this, "Duration must be greater than 0", Toast.LENGTH_SHORT).show();
                    etDuration.requestFocus();
                    return;
                }
            } catch (NumberFormatException e) {
                Toast.makeText(this, "Please enter a valid number for duration", Toast.LENGTH_SHORT).show();
                etDuration.requestFocus();
                return;
            }

            // Step 3.6: Start Session Flow
            // Create StudySession object with form data
            String technique = selectedTechnique != null ? selectedTechnique : TECHNIQUE_CUSTOM;
            
            // Set technique-specific break durations:
            // - Pomodoro: 5 min break
            // - 52/17: 17 min break
            // - 90-Minute: 25 min break
            // - Sprint: 2 min break (1.5 rounded up)
            // - Deadline/Custom: 0 min break
            int breakDuration = StudySession.getBreakDurationForTechnique(technique);
            
            // Create session with:
            // - Technique name
            // - Subject and task from form
            // - Work duration from form (pre-filled for techniques, user input for custom/deadline)
            // - Break duration from technique-specific method
            // - Start time is automatically stored in StudySession constructor
            currentSession = new StudySession(technique, subject, task, durationInt, breakDuration);

            // Close create bottom sheet
            bottomSheetDialog.dismiss();

            // Clear selection after starting session
            selectedTechnique = null;
            clearCardHighlights();

            // Show active bottom sheet with session data
            // This will:
            // - Set session state to ACTIVE_WORK
            // - Start countdown timer
            // - Update UI to show active session
            showActiveBottomSheet();
        });

        bottomSheetDialog.show();
    }

    /**
     * Shows the active session bottom sheet with session data
     */
    private void showActiveBottomSheet() {
        if (currentSession == null) {
            return; // Safety check
        }

        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
        View sheetView = getLayoutInflater().inflate(R.layout.bottom_card_active, null);
        bottomSheetDialog.setContentView(sheetView);

        // Get views from active session layout
        Chip chipTechniqueName = sheetView.findViewById(R.id.chipTechniqueName);
        tvPhaseIndicator = sheetView.findViewById(R.id.tvPhaseIndicator);
        tvTimerDisplay = sheetView.findViewById(R.id.tvTimerDisplay);
        tvTimeRemaining = sheetView.findViewById(R.id.tvTimeRemaining);
        tvCycleCounter = sheetView.findViewById(R.id.tvCycleCounter);
        progressBar = sheetView.findViewById(R.id.progressBar);
        btnPause = sheetView.findViewById(R.id.btnPause);
        btnCancelSession = sheetView.findViewById(R.id.btnCancelSession);
        btnMarkDone = sheetView.findViewById(R.id.btnMarkDone);
        btnSkipBreak = sheetView.findViewById(R.id.btnSkipBreak);
        btnExtendBreak = sheetView.findViewById(R.id.btnExtendBreak);
        buttonRow = sheetView.findViewById(R.id.buttonRow);
        breakButtonRow = sheetView.findViewById(R.id.breakButtonRow);
        btnMinimize = sheetView.findViewById(R.id.btnMinimize);
        progressContainer = sheetView.findViewById(R.id.progressContainer);

        // Populate active sheet with session data
        chipTechniqueName.setText(currentSession.getTechnique());
        
        // Step 1.3: Add click listener to technique chip in active session to show info dialog
        chipTechniqueName.setOnClickListener(v -> {
            showTechniqueInfoDialog(currentSession.getTechnique(), true); // true = from active dialog
        });
        
        // Step 4.5: Update cycle counter display
        updateCycleCounterDisplay();
        
        // Format timer display (MM:SS) - use helper method
        String timerText = currentSession.getFormattedTimeRemaining();
        tvTimerDisplay.setText(timerText);
        
        // Set time remaining text - use helper method
        tvTimeRemaining.setText(currentSession.getFormattedTimeRemainingText());

        // Initialize progress bar to full scale (100%)
        if (progressBar != null) {
            progressBar.setScaleX(1.0f);
            progressBar.setScaleY(1.0f);
        }

        // Set session state to ACTIVE_WORK and start timer
        updateSessionState(SessionState.ACTIVE_WORK);
        startWorkTimer(currentSession.getWorkDuration());

        // Set up button click listeners
        btnPause.setOnClickListener(v -> {
            // Step 4.4: Pause/Resume Functionality
            if (currentState == SessionState.ACTIVE_WORK || currentState == SessionState.ACTIVE_BREAK) {
                // Pause the timer
                pauseTimer();
            } else if (currentState == SessionState.PAUSED) {
                // Resume the timer
                resumeTimer();
            }
        });

        btnCancelSession.setOnClickListener(v -> {
            // Show confirmation dialog before canceling
            // Timer continues running until user confirms cancellation
            showCancelConfirmationDialog(bottomSheetDialog);
        });

        btnMarkDone.setOnClickListener(v -> {
            // Show confirmation dialog before marking as done
            showMarkDoneConfirmationDialog(bottomSheetDialog);
        });

        // Step 4.7: Skip Break button - immediately start next work session
        if (btnSkipBreak != null) {
            btnSkipBreak.setOnClickListener(v -> {
                // Cancel break timer
                if (breakTimer != null) {
                    breakTimer.cancel();
                    breakTimer = null;
                }
                // Immediately start next work session (skip the break)
                startNextWorkSession();
            });
        }

        // Step 4.7: Extend Break button - add 5 minutes to break timer
        if (btnExtendBreak != null) {
            btnExtendBreak.setOnClickListener(v -> {
                extendBreakTimer();
            });
        }

        // Step 5.6: Minimize/Maximize button functionality
        if (btnMinimize != null) {
            btnMinimize.setOnClickListener(v -> {
                toggleMinimizeMaximize();
            });
        }

        // Make bottom sheet non-dismissible (sticky)
        bottomSheetDialog.setCancelable(false);
        bottomSheetDialog.setCanceledOnTouchOutside(false);

        // Store reference to active bottom sheet
        activeBottomSheetDialog = bottomSheetDialog;

        bottomSheetDialog.show();
    }

    /**
     * Shows confirmation dialog for canceling session
     */
    private void showCancelConfirmationDialog(BottomSheetDialog activeBottomSheet) {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_confirm_cancel, null);
        builder.setView(dialogView);

        android.app.AlertDialog confirmDialog = builder.create();
        confirmDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        // Get buttons from confirmation dialog
        MaterialButton btnCancel = dialogView.findViewById(R.id.btnCancel);
        MaterialButton btnYesCancel = dialogView.findViewById(R.id.btnYesCancel);

        // Handle "Cancel" button (dismiss confirmation dialog only)
        btnCancel.setOnClickListener(v -> confirmDialog.dismiss());

        // Handle "Yes, Cancel" button (confirm cancellation)
        btnYesCancel.setOnClickListener(v -> {
            confirmDialog.dismiss();
            
            // Cancel timers if running
            if (workTimer != null) {
                workTimer.cancel();
                workTimer = null;
            }
            if (breakTimer != null) {
                breakTimer.cancel();
                breakTimer = null;
            }
            
            // Clear session and close active bottom sheet
            currentSession = null;
            updateSessionState(SessionState.IDLE);
            activeBottomSheetDialog = null;
            tvTimerDisplay = null;
            tvTimeRemaining = null;
            tvPhaseIndicator = null;
            progressBar = null;
            btnPause = null;
            btnCancelSession = null;
            btnMarkDone = null;
            
            if (activeBottomSheet != null && activeBottomSheet.isShowing()) {
                activeBottomSheet.dismiss();
            }
            
            // Return to IDLE state (main activity is already visible)
        });

        confirmDialog.show();
    }

    /**
     * Shows confirmation dialog for marking session as done
     */
    private void showMarkDoneConfirmationDialog(BottomSheetDialog activeBottomSheet) {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_confirm_done, null);
        builder.setView(dialogView);

        android.app.AlertDialog confirmDialog = builder.create();
        confirmDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        // Get buttons from confirmation dialog
        MaterialButton btnCancel = dialogView.findViewById(R.id.btnCancel);
        MaterialButton btnYesMarkDone = dialogView.findViewById(R.id.btnYesMarkDone);

        // Handle "Cancel" button (dismiss confirmation dialog only)
        btnCancel.setOnClickListener(v -> confirmDialog.dismiss());

        // Handle "Yes, Mark as Done" button (confirm and show completion dialog)
        btnYesMarkDone.setOnClickListener(v -> {
            confirmDialog.dismiss();
            
            // Cancel timers if running
            if (workTimer != null) {
                workTimer.cancel();
                workTimer = null;
            }
            if (breakTimer != null) {
                breakTimer.cancel();
                breakTimer = null;
            }
            
            // Close active bottom sheet
            if (activeBottomSheet != null && activeBottomSheet.isShowing()) {
                activeBottomSheet.dismiss();
            }
            activeBottomSheetDialog = null;
            tvTimerDisplay = null;
            tvTimeRemaining = null;
            tvPhaseIndicator = null;
            progressBar = null;
            btnPause = null;
            btnCancelSession = null;
            btnMarkDone = null;
            
            // Update state to completed before showing completion dialog
            if (currentSession != null) {
                updateSessionState(SessionState.COMPLETED);
            }
            
            // Show completion dialog
            showCompletionDialog();
        });

        confirmDialog.show();
    }

    /**
     * Shows completion dialog with session summary
     * @param isBreakCompletion If true, shows "Ok" button instead of "Done" and "Create New Goal"
     */
    private void showCompletionDialog(boolean isBreakCompletion) {
        if (currentSession == null) {
            return; // Safety check
        }

        // Prevent duplicate dialogs - if one is already showing, don't show another
        if (completionDialog != null && completionDialog.isShowing()) {
            return;
        }

        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_session_complete, null);
        builder.setView(dialogView);

        completionDialog = builder.create();
        completionDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        
        // Set dismiss listener to clear reference when dialog is dismissed
        completionDialog.setOnDismissListener(dialog -> {
            completionDialog = null;
        });

        // Get views from completion dialog
        TextView tvSubject = dialogView.findViewById(R.id.tvSubject);
        TextView tvTask = dialogView.findViewById(R.id.tvTask);
        TextView tvTimeSpent = dialogView.findViewById(R.id.tvTimeSpent);
        MaterialButton btnDone = dialogView.findViewById(R.id.btnDone);
        MaterialButton btnCreateNewGoal = dialogView.findViewById(R.id.btnCreateNewGoal);
        MaterialButton btnOk = dialogView.findViewById(R.id.btnOk);
        LinearLayout buttonRow = dialogView.findViewById(R.id.buttonRow);

        // Populate completion dialog with session data
        tvSubject.setText(currentSession.getSubject());
        tvTask.setText(currentSession.getTask());
        
        // Step 5.3: Calculate and display actual time spent
        String timeSpentText = calculateTimeSpent();
        tvTimeSpent.setText(timeSpentText);

        if (isBreakCompletion) {
            // Break completion mode: Hide "Done" and "Create New Goal", show "Ok"
            if (buttonRow != null) {
                buttonRow.setVisibility(View.GONE);
            }
            if (btnOk != null) {
                btnOk.setVisibility(View.VISIBLE);
                btnOk.setOnClickListener(v -> {
                    if (completionDialog != null) {
                        completionDialog.dismiss();
                        completionDialog = null;
                    }
                    // Stay in break complete state, "Start Next Session" button will handle the rest
                });
            }
        } else {
            // Work completion mode: Show "Done" and "Create New Goal", hide "Ok"
            if (buttonRow != null) {
                buttonRow.setVisibility(View.VISIBLE);
            }
            if (btnOk != null) {
                btnOk.setVisibility(View.GONE);
            }

            // Handle "Done" button (close all, return to IDLE)
            btnDone.setOnClickListener(v -> {
                if (completionDialog != null) {
                    completionDialog.dismiss();
                    completionDialog = null;
                }
                // Cancel timers if running
                if (workTimer != null) {
                    workTimer.cancel();
                    workTimer = null;
                }
                if (breakTimer != null) {
                    breakTimer.cancel();
                    breakTimer = null;
                }
                // Close active bottom sheet if showing
                if (activeBottomSheetDialog != null && activeBottomSheetDialog.isShowing()) {
                    activeBottomSheetDialog.dismiss();
                }
                // Clear session and UI references
                currentSession = null;
                updateSessionState(SessionState.IDLE);
                activeBottomSheetDialog = null;
                tvTimerDisplay = null;
                tvTimeRemaining = null;
                tvPhaseIndicator = null;
                progressBar = null;
                btnPause = null;
                btnCancelSession = null;
                btnMarkDone = null;
            });

            // Handle "Create New Goal" button (show create sheet with previous technique)
            btnCreateNewGoal.setOnClickListener(v -> {
                if (completionDialog != null) {
                    completionDialog.dismiss();
                    completionDialog = null;
                }
                // Save the current technique to pre-select it in the form
                String previousTechnique = currentSession.getTechnique();
                if (previousTechnique != null && !previousTechnique.equals(TECHNIQUE_CUSTOM)) {
                    selectedTechnique = previousTechnique;
                    highlightSelectedCard(previousTechnique);
                } else {
                    selectedTechnique = null;
                    clearCardHighlights();
                }
                // Cancel timers if running
                if (workTimer != null) {
                    workTimer.cancel();
                    workTimer = null;
                }
                if (breakTimer != null) {
                    breakTimer.cancel();
                    breakTimer = null;
                }
                // Close active bottom sheet if showing
                if (activeBottomSheetDialog != null && activeBottomSheetDialog.isShowing()) {
                    activeBottomSheetDialog.dismiss();
                }
                // Clear session and UI references
                currentSession = null;
                updateSessionState(SessionState.IDLE);
                activeBottomSheetDialog = null;
                tvTimerDisplay = null;
                tvTimeRemaining = null;
                tvPhaseIndicator = null;
                progressBar = null;
                btnPause = null;
                btnCancelSession = null;
                btnMarkDone = null;
                // Show create goal bottom sheet
                showBottomSheet();
            });
        }

        completionDialog.show();
    }

    /**
     * Overloaded method for backward compatibility (work completion from "Mark as Done")
     */
    private void showCompletionDialog() {
        showCompletionDialog(false);
    }

    /**
     * Handles back button press - prevents dismissing active session card
     */
    @Override
    public void onBackPressed() {
        // If active session card is showing, don't allow back button to dismiss it
        if (activeBottomSheetDialog != null && activeBottomSheetDialog.isShowing()) {
            // Show a message that user must use Cancel or Mark as Done buttons
            Toast.makeText(this, "Please use Cancel Session or Mark as Done to end the session", Toast.LENGTH_SHORT).show();
            return; // Don't call super, preventing dismissal
        }
        // Otherwise, allow normal back button behavior
        super.onBackPressed();
    }

    /**
     * Step 3.7: Activity Lifecycle Handling - onPause
     * Called when the activity is paused (e.g., user switches apps, receives a call)
     */
    @Override
    protected void onPause() {
        super.onPause();
        
        // Save timer state (remaining time) when app is paused
        if (currentSession != null && (workTimer != null || breakTimer != null)) {
            // The remaining time is already being saved in the session on each tick
            // But we should ensure it's saved here as well
            // Note: We can't get remaining time directly from CountDownTimer,
            // but it's already stored in currentSession.getRemainingTimeMillis()
            // from the onTick() callback
            
            // Mark that we were paused (not killed)
            wasPaused = true;
            
            // Cancel work timer if running
            if (workTimer != null) {
                workTimer.cancel();
                workTimer = null;
            }
            
            // Cancel break timer if running
            if (breakTimer != null) {
                breakTimer.cancel();
                breakTimer = null;
            }
            
            // Update session state to PAUSED if it was active
            if (currentState == SessionState.ACTIVE_WORK || currentState == SessionState.ACTIVE_BREAK) {
                updateSessionState(SessionState.PAUSED);
            }
        } else {
            // No active session, reset pause flag
            wasPaused = false;
        }
    }

    /**
     * Step 3.7: Activity Lifecycle Handling - onResume
     * Called when the activity is resumed (e.g., user returns to the app)
     */
    @Override
    protected void onResume() {
        super.onResume();
        
        // Resume timer if session was active and app wasn't killed
        if (currentSession != null && wasPaused) {
            // App was paused (not killed), so we can resume
            SessionState sessionState = currentSession.getState();
            
            // Check if session was in an active state before pause
            if (sessionState == SessionState.PAUSED || 
                sessionState == SessionState.ACTIVE_WORK || 
                sessionState == SessionState.ACTIVE_BREAK) {
                
                // Get remaining time from session
                long remainingTimeMillis = currentSession.getRemainingTimeMillis();
                
                // Only resume if there's time remaining
                if (remainingTimeMillis > 0) {
                    // Determine if it was work or break phase
                    if (sessionState == SessionState.ACTIVE_WORK || 
                        (sessionState == SessionState.PAUSED && currentSession.getBreakDuration() == 0)) {
                        // Resume work timer
                        resumeWorkTimer(remainingTimeMillis);
                    } else if (sessionState == SessionState.ACTIVE_BREAK) {
                        // Resume break timer
                        resumeBreakTimer(remainingTimeMillis);
                    } else if (sessionState == SessionState.PAUSED) {
                        // Was paused, determine which timer to resume based on break duration
                        // If break duration > 0 and we're paused, likely was in break
                        // Otherwise, resume work timer
                        if (currentSession.getBreakDuration() > 0) {
                            // Check if we should resume break or work
                            // For now, assume work if we can't determine
                            resumeWorkTimer(remainingTimeMillis);
                        } else {
                            resumeWorkTimer(remainingTimeMillis);
                        }
                    }
                } else {
                    // Time expired while app was paused
                    // Determine which timer expired
                    if (sessionState == SessionState.ACTIVE_BREAK) {
                        handleBreakTimerComplete();
                    } else {
                        handleWorkTimerComplete();
                    }
                }
            }
            
            // Reset pause flag
            wasPaused = false;
        } else if (currentSession == null) {
            // App was killed (session lost - acceptable)
            // Reset pause flag
            wasPaused = false;
        }
        // If wasPaused is false and currentSession is null, app was killed - that's acceptable
    }

    /**
     * Starts the work timer countdown
     * @param durationMinutes The duration in minutes
     */
    private void startWorkTimer(int durationMinutes) {
        // Cancel any existing timer
        if (workTimer != null) {
            workTimer.cancel();
            workTimer = null;
        }

        // Convert minutes to milliseconds (apply test mode multiplier for faster testing)
        // In test mode: 1 minute = 1 second (60x faster)
        long durationMillis = (durationMinutes * 60 * 1000L) / TEST_MODE_MULTIPLIER;
        
        // Store initial duration in session
        if (currentSession != null) {
            currentSession.setRemainingTimeMillis(durationMillis);
        }

        // Start timer with full duration
        startWorkTimerFromRemaining(durationMillis, durationMillis);
    }

    /**
     * Resumes the work timer from a specific remaining time
     * Used when resuming after app pause
     * @param remainingTimeMillis Remaining time in milliseconds
     */
    private void resumeWorkTimer(long remainingTimeMillis) {
        // Cancel any existing timer
        if (workTimer != null) {
            workTimer.cancel();
            workTimer = null;
        }

        // Get the original work duration to calculate progress
        // Apply test mode multiplier to match the remaining time units
        int workDurationMinutes = currentSession != null ? currentSession.getWorkDuration() : 25;
        long totalDurationMillis = (workDurationMinutes * 60 * 1000L) / TEST_MODE_MULTIPLIER;

        // Update session state back to ACTIVE_WORK
        if (currentSession != null) {
            updateSessionState(SessionState.ACTIVE_WORK);
            // Clear previous state tracking after resuming
            previousStateBeforePause = null;
        }

        // Start timer from remaining time
        startWorkTimerFromRemaining(remainingTimeMillis, totalDurationMillis);
    }

    /**
     * Step 4.1 & 4.6: Resumes the break timer from a specific remaining time
     * Used when resuming after app pause
     * @param remainingTimeMillis Remaining time in milliseconds
     */
    private void resumeBreakTimer(long remainingTimeMillis) {
        // Cancel any existing break timer
        if (breakTimer != null) {
            breakTimer.cancel();
            breakTimer = null;
        }

        // Step 4.6: Get the original break duration to calculate progress
        // Check if this is a long break for Pomodoro (after 4 cycles)
        int breakDurationMinutes = currentSession != null ? currentSession.getBreakDuration() : 5;
        boolean isLongBreak = false;
        
        if (currentSession != null && 
            currentSession.getTechnique().equals(TECHNIQUE_POMODORO) && 
            currentSession.getCurrentCycle() == 4) {
            // This is a long break (25 minutes)
            breakDurationMinutes = 25;
            isLongBreak = true;
        }
        
        // Apply test mode multiplier to match the remaining time units
        long totalDurationMillis = (breakDurationMinutes * 60 * 1000L) / TEST_MODE_MULTIPLIER;
        
        // Store total break duration for extend break functionality
        currentBreakTotalDurationMillis = totalDurationMillis;

        // Update session remaining time
        currentSession.setRemainingTimeMillis(remainingTimeMillis);

        // Update progress bar based on remaining time
        if (progressBar != null) {
            float progress = (float) remainingTimeMillis / (float) totalDurationMillis;
            progressBar.setScaleX(progress);
            progressBar.setScaleY(progress);
        }

        // Update phase indicator
        if (tvPhaseIndicator != null) {
            if (isLongBreak) {
                tvPhaseIndicator.setText("LONG BREAK");
            } else {
                tvPhaseIndicator.setText("BREAK TIME");
            }
            tvPhaseIndicator.setTextColor(getResources().getColor(R.color.phase_break, null));
        }
        
        // Step 4.7: Show break action buttons when resuming break
        if (buttonRow != null) {
            buttonRow.setVisibility(View.GONE); // Hide work session buttons
        }
        if (breakButtonRow != null) {
            breakButtonRow.setVisibility(View.VISIBLE); // Show break action buttons
        }

        // Create and start break countdown timer
        breakTimer = new CountDownTimer(remainingTimeMillis, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                // Update session remaining time
                if (currentSession != null) {
                    currentSession.setRemainingTimeMillis(millisUntilFinished);
                }
                
                // Update timer display
                updateTimerDisplay(millisUntilFinished);
                
                // Update progress indicator (use total duration for progress)
                updateProgressIndicator(millisUntilFinished, currentBreakTotalDurationMillis);
            }

            @Override
            public void onFinish() {
                // Break timer completed
                handleBreakTimerComplete();
            }
        }.start();
        
        // Update session state back to ACTIVE_BREAK
        if (currentSession != null) {
            updateSessionState(SessionState.ACTIVE_BREAK);
            // Clear previous state tracking after resuming
            previousStateBeforePause = null;
        }
    }

    /**
     * Step 4.4: Pauses the current timer (work or break)
     * Stores remaining time and updates state to PAUSED
     */
    private void pauseTimer() {
        if (currentSession == null) {
            return; // Safety check
        }

        // Store the current state before pausing (so we know if it was work or break)
        previousStateBeforePause = currentState;

        // Cancel the appropriate timer and save remaining time
        if (workTimer != null && currentState == SessionState.ACTIVE_WORK) {
            // Pause work timer
            workTimer.cancel();
            workTimer = null;
            // Remaining time is already saved in currentSession.getRemainingTimeMillis() from onTick()
        } else if (breakTimer != null && currentState == SessionState.ACTIVE_BREAK) {
            // Pause break timer
            breakTimer.cancel();
            breakTimer = null;
            // Remaining time is already saved in currentSession.getRemainingTimeMillis() from onTick()
        } else {
            // No active timer to pause
            return;
        }

        // Update session state to PAUSED
        currentSession.setPaused(true);
        updateSessionState(SessionState.PAUSED);

        // UI updates are handled by updateUIForState() which is called by updateSessionState()
        // Button text changes to "Resume" and phase indicator shows "PAUSED"
    }

    /**
     * Step 4.4: Resumes the timer from the saved remaining time
     * Restores the previous state (ACTIVE_WORK or ACTIVE_BREAK) and restarts the timer
     */
    private void resumeTimer() {
        if (currentSession == null) {
            return; // Safety check
        }

        // Check if we're actually paused
        if (currentState != SessionState.PAUSED) {
            return; // Not paused, can't resume
        }

        // Get remaining time from session
        long remainingTimeMillis = currentSession.getRemainingTimeMillis();

        // Check if there's time remaining
        if (remainingTimeMillis <= 0) {
            // Time expired while paused
            // Determine which timer expired based on previous state
            if (previousStateBeforePause == SessionState.ACTIVE_BREAK) {
                handleBreakTimerComplete();
            } else {
                handleWorkTimerComplete();
            }
            return;
        }

        // Resume the appropriate timer based on previous state
        if (previousStateBeforePause == SessionState.ACTIVE_WORK) {
            // Resume work timer
            resumeWorkTimer(remainingTimeMillis);
        } else if (previousStateBeforePause == SessionState.ACTIVE_BREAK) {
            // Resume break timer
            resumeBreakTimer(remainingTimeMillis);
        } else {
            // Unknown previous state, default to work
            resumeWorkTimer(remainingTimeMillis);
        }

        // Clear paused flag
        currentSession.setPaused(false);
        // previousStateBeforePause will be cleared when we update to the new state
    }

    /**
     * Internal method to start work timer from a specific remaining time
     * @param remainingTimeMillis Remaining time in milliseconds
     * @param totalDurationMillis Total duration in milliseconds (for progress calculation)
     */
    private void startWorkTimerFromRemaining(long remainingTimeMillis, long totalDurationMillis) {
        // Create and start countdown timer
        workTimer = new CountDownTimer(remainingTimeMillis, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                // Update session remaining time
                if (currentSession != null) {
                    currentSession.setRemainingTimeMillis(millisUntilFinished);
                }
                
                // Update timer display
                updateTimerDisplay(millisUntilFinished);
                
                // Update progress indicator (use total duration for accurate progress)
                updateProgressIndicator(millisUntilFinished, totalDurationMillis);
            }

            @Override
            public void onFinish() {
                // Timer completed
                handleWorkTimerComplete();
            }
        }.start();
    }

    /**
     * Updates the timer display TextView with formatted time
     * Called every second during countdown to update the display smoothly
     * @param millisUntilFinished Remaining time in milliseconds
     */
    private void updateTimerDisplay(long millisUntilFinished) {
        if (tvTimerDisplay == null) {
            return; // Safety check
        }

        // Format milliseconds to MM:SS format
        long totalSeconds = millisUntilFinished / 1000;
        long minutes = totalSeconds / 60;
        long seconds = totalSeconds % 60;
        String formattedTime = String.format("%02d:%02d", minutes, seconds);
        
        // Update timer TextView with formatted time
        tvTimerDisplay.setText(formattedTime);
        
        // Update "Time remaining" text with smart format
        if (tvTimeRemaining != null) {
            String remainingText = formatTimeRemainingText(millisUntilFinished);
            tvTimeRemaining.setText(remainingText);
        }
    }

    /**
     * Formats time remaining text with smart labels (hours, minutes, seconds)
     * Examples:
     * - "1 hour 30 minutes remaining" (for 90:00)
     * - "25 minutes 49 seconds remaining" (for 25:49)
     * - "56 seconds remaining" (for 0:56)
     * @param millisUntilFinished Remaining time in milliseconds
     * @return Formatted time remaining string
     */
    private String formatTimeRemainingText(long millisUntilFinished) {
        long totalSeconds = millisUntilFinished / 1000;
        long hours = totalSeconds / 3600;
        long minutes = (totalSeconds % 3600) / 60;
        long seconds = totalSeconds % 60;
        
        StringBuilder text = new StringBuilder();
        
        // Format based on time remaining
        if (hours > 0) {
            // Has hours: show "X hour(s) and Y minute(s) remaining"
            // For hours, we typically don't show seconds to keep it clean
            text.append(hours);
            text.append(hours == 1 ? " hour" : " hours");
            
            if (minutes > 0) {
                text.append(" and ");
                text.append(minutes);
                text.append(minutes == 1 ? " minute" : " minutes");
            }
        } else if (minutes > 0) {
            // Only minutes: show "X minute(s) Y second(s) remaining"
            text.append(minutes);
            text.append(minutes == 1 ? " minute" : " minutes");
            
            // Always show seconds when we have minutes (for precision)
            if (seconds > 0) {
                text.append(" ");
                text.append(seconds);
                text.append(seconds == 1 ? " second" : " seconds");
            }
        } else {
            // Only seconds: show "X second(s) remaining"
            text.append(seconds);
            text.append(seconds == 1 ? " second" : " seconds");
        }
        
        text.append(" remaining");
        return text.toString();
    }

    /**
     * Updates the progress indicator based on remaining time
     * Called every second during countdown to update the circular progress smoothly
     * @param millisUntilFinished Remaining time in milliseconds
     * @param totalDurationMillis Total duration in milliseconds
     */
    private void updateProgressIndicator(long millisUntilFinished, long totalDurationMillis) {
        if (progressBar == null) {
            return; // Safety check
        }

        // Calculate progress percentage (0.0 to 1.0)
        // Progress represents remaining time, so 1.0 = full time, 0.0 = no time left
        float progress = (float) millisUntilFinished / (float) totalDurationMillis;
        
        // Clamp progress between 0.0 and 1.0 to prevent invalid values
        if (progress < 0.0f) {
            progress = 0.0f;
        } else if (progress > 1.0f) {
            progress = 1.0f;
        }

        // Update circular progress indicator using scale
        // Shrinks from 1.0 (full) to 0.0 (empty) as time decreases
        progressBar.setScaleX(progress);
        progressBar.setScaleY(progress);
        
        // Optional: Add visual feedback with alpha (fade as time decreases)
        // Uncomment to enable fading effect
        // float alpha = 0.3f + (progress * 0.45f); // Range from 0.3 to 0.75
        // progressBar.setAlpha(alpha);
    }

    /**
     * Step 4.1: Starts the break timer countdown
     * @param durationMinutes The break duration in minutes
     * @param isLongBreak If true, shows "LONG BREAK" indicator (for Pomodoro after 4 cycles)
     */
    private void startBreakTimer(int durationMinutes, boolean isLongBreak) {
        // Cancel any existing break timer
        if (breakTimer != null) {
            breakTimer.cancel();
            breakTimer = null;
        }

        // Convert minutes to milliseconds (apply test mode multiplier for faster testing)
        // In test mode: 1 minute = 1 second (60x faster)
        long durationMillis = (durationMinutes * 60 * 1000L) / TEST_MODE_MULTIPLIER;
        
        // Store initial break duration in session
        if (currentSession != null) {
            currentSession.setRemainingTimeMillis(durationMillis);
        }

        // Update session state to ACTIVE_BREAK
        // This will update phase indicator to "BREAK TIME" and hide Cancel/Pause buttons
        updateSessionState(SessionState.ACTIVE_BREAK);

        // Step 4.6: Show "LONG BREAK" indicator for Pomodoro after 4 cycles
        if (tvPhaseIndicator != null) {
            if (isLongBreak) {
                tvPhaseIndicator.setText("LONG BREAK");
            } else {
                tvPhaseIndicator.setText("BREAK TIME");
            }
            tvPhaseIndicator.setTextColor(getResources().getColor(R.color.phase_break, null));
        }

        // Step 4.7: Show break action buttons (Skip Break, Extend Break) during break session
        if (buttonRow != null) {
            buttonRow.setVisibility(View.GONE); // Hide work session buttons
        }
        if (breakButtonRow != null) {
            breakButtonRow.setVisibility(View.VISIBLE); // Show break action buttons
        }

        // Store total break duration for extend break functionality
        currentBreakTotalDurationMillis = durationMillis;

        // Initialize progress bar to full scale (100%)
        if (progressBar != null) {
            progressBar.setScaleX(1.0f);
            progressBar.setScaleY(1.0f);
        }

        // Create and start break countdown timer
        breakTimer = new CountDownTimer(durationMillis, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                // Update session remaining time
                if (currentSession != null) {
                    currentSession.setRemainingTimeMillis(millisUntilFinished);
                }
                
                // Update timer display
                updateTimerDisplay(millisUntilFinished);
                
                // Update progress indicator (use total break duration for progress)
                updateProgressIndicator(millisUntilFinished, currentBreakTotalDurationMillis);
            }

            @Override
            public void onFinish() {
                // Break timer completed
                handleBreakTimerComplete();
            }
        }.start();
    }
    
    /**
     * Step 4.7: Extends the current break timer by 5 minutes
     */
    private void extendBreakTimer() {
        if (breakTimer == null || currentSession == null) {
            return; // No active break timer
        }
        
        // Get current remaining time
        long currentRemainingTime = currentSession.getRemainingTimeMillis();
        
        // Add 5 minutes (apply test mode multiplier)
        long additionalTime = (5 * 60 * 1000L) / TEST_MODE_MULTIPLIER;
        long newRemainingTime = currentRemainingTime + additionalTime;
        
        // Update total break duration for progress calculation
        currentBreakTotalDurationMillis += additionalTime;
        
        // Cancel current timer
        breakTimer.cancel();
        breakTimer = null;
        
        // Update session remaining time
        currentSession.setRemainingTimeMillis(newRemainingTime);
        
        // Determine if this is a long break (for display purposes)
        boolean isLongBreak = false;
        if (currentSession.getTechnique().equals(TECHNIQUE_POMODORO) && 
            currentSession.getCurrentCycle() == 4) {
            isLongBreak = true;
        }
        
        // Restart timer with extended time
        restartBreakTimerWithNewDuration(newRemainingTime, currentBreakTotalDurationMillis, isLongBreak);
        
        // Show toast notification
        Toast.makeText(this, "Break extended by 5 minutes", Toast.LENGTH_SHORT).show();
    }
    
    /**
     * Step 4.7: Helper method to restart break timer with new duration
     */
    private void restartBreakTimerWithNewDuration(long remainingTimeMillis, long totalDurationMillis, boolean isLongBreak) {
        // Cancel any existing timer
        if (breakTimer != null) {
            breakTimer.cancel();
            breakTimer = null;
        }
        
        // Update session remaining time
        if (currentSession != null) {
            currentSession.setRemainingTimeMillis(remainingTimeMillis);
        }
        
        // Update progress bar to reflect new total duration
        if (progressBar != null) {
            float progress = (float) remainingTimeMillis / (float) totalDurationMillis;
            progressBar.setScaleX(progress);
            progressBar.setScaleY(progress);
        }
        
        // Create and start break countdown timer with extended time
        breakTimer = new CountDownTimer(remainingTimeMillis, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                // Update session remaining time
                if (currentSession != null) {
                    currentSession.setRemainingTimeMillis(millisUntilFinished);
                }
                
                // Update timer display
                updateTimerDisplay(millisUntilFinished);
                
                // Update progress indicator (use total duration for progress)
                updateProgressIndicator(millisUntilFinished, totalDurationMillis);
            }

            @Override
            public void onFinish() {
                // Break timer completed
                handleBreakTimerComplete();
            }
        }.start();
    }

    /**
     * Step 4.1 & 4.6: Handles break timer completion
     */
    private void handleBreakTimerComplete() {
        // Cancel timer reference
        breakTimer = null;

        // Update session state
        if (currentSession != null) {
            currentSession.setRemainingTimeMillis(0);
        }

        // Step 4.6: Check if this was a long break for Pomodoro (after 4 cycles)
        boolean wasLongBreak = false;
        if (currentSession != null && 
            currentSession.getTechnique().equals(TECHNIQUE_POMODORO) && 
            currentSession.getCurrentCycle() == 4) {
            wasLongBreak = true;
            // Note: Cycle will be reset when user starts next work session (in startNextWorkSession)
            // This allows us to distinguish between cycle 1 after reset vs cycle 1 for new session
        }

        // Update UI to show break completion
        if (tvTimerDisplay != null) {
            tvTimerDisplay.setText("00:00");
        }
        
        if (tvTimeRemaining != null) {
            tvTimeRemaining.setText("0 seconds remaining");
        }
        
        if (progressBar != null) {
            progressBar.setScaleX(0.0f);
            progressBar.setScaleY(0.0f);
        }

        // Update session state to COMPLETED so "Start Next Session" button becomes visible
        updateSessionState(SessionState.COMPLETED);

        // Show break completion notification
        String breakMessage = wasLongBreak ? "Long Break Complete! 🎉" : "Break Complete! 🎉";
        Toast.makeText(this, breakMessage, Toast.LENGTH_LONG).show();

        // Update phase indicator to show break complete
        if (tvPhaseIndicator != null) {
            tvPhaseIndicator.setText("Break Complete!");
            tvPhaseIndicator.setTextColor(getResources().getColor(R.color.success_color, null));
        }

        // Step 4.3 & 4.7: Hide all action buttons when break completes
        // Hide work session buttons (Cancel, Pause)
        if (buttonRow != null) {
            buttonRow.setVisibility(View.GONE);
        }
        if (btnCancelSession != null) {
            btnCancelSession.setVisibility(View.GONE);
        }
        if (btnPause != null) {
            btnPause.setVisibility(View.GONE);
        }
        
        // Hide break action buttons (Skip Break, Extend Break)
        if (breakButtonRow != null) {
            breakButtonRow.setVisibility(View.GONE);
        }
        if (btnSkipBreak != null) {
            btnSkipBreak.setVisibility(View.GONE);
        }
        if (btnExtendBreak != null) {
            btnExtendBreak.setVisibility(View.GONE);
        }
        
        // Reset break duration tracking
        currentBreakTotalDurationMillis = 0;

        // Step 4.3: Update "Mark as Done" button to "Start Next Session" when break completes
        // This allows user to start next work cycle
        if (btnMarkDone != null) {
            btnMarkDone.setText("Start Next Session");
            // Update click listener to start next session instead of marking as done
            btnMarkDone.setOnClickListener(v -> {
                startNextWorkSession();
            });
        }
        
        // NO completion dialog when break completes - user can directly click "Start Next Session"
    }

    /**
     * Step 4.3 & 4.5: Starts the next work session after break completion
     * Option A: Keeps the same session, increments cycle counter, allows optional task/subject update
     */
    private void startNextWorkSession() {
        if (currentSession == null) {
            return; // Safety check
        }

        // Step 4.5 & 4.6: Handle cycle counter (keep same session for multi-cycle tracking)
        int currentCycle = currentSession.getCurrentCycle();
        boolean completedAllCycles = false;
        
        // Step 4.6: If Pomodoro and cycle is 4, we just completed a long break
        // Reset cycle to 1 for the next cycle
        if (currentSession.getTechnique().equals(TECHNIQUE_POMODORO) && currentCycle == 4) {
            currentSession.resetCycle();
            completedAllCycles = true; // Mark that all cycles are complete
        } else if (currentSession.getTechnique().equals(TECHNIQUE_SPRINT) && currentCycle == 4) {
            // Sprint also has 4 cycles - reset after completing all 4
            currentSession.resetCycle();
            completedAllCycles = true; // Mark that all cycles are complete
        } else {
            // Otherwise, increment cycle for next work session
            currentSession.incrementCycle();
        }
        
        // Save the current technique to pre-select it in the form
        String previousTechnique = currentSession.getTechnique();
        if (previousTechnique != null && !previousTechnique.equals(TECHNIQUE_CUSTOM)) {
            // Pre-select the same technique for the next cycle
            selectedTechnique = previousTechnique;
            highlightSelectedCard(previousTechnique);
        } else {
            // Clear selection for custom goal
            selectedTechnique = null;
            clearCardHighlights();
        }
        
        // Store flag to know if we should clear subject field (all cycles complete)
        // We'll pass this to showBottomSheetForNextCycle

        // Cancel any running timers
        if (workTimer != null) {
            workTimer.cancel();
            workTimer = null;
        }
        if (breakTimer != null) {
            breakTimer.cancel();
            breakTimer = null;
        }

        // Close the active bottom sheet
        if (activeBottomSheetDialog != null && activeBottomSheetDialog.isShowing()) {
            activeBottomSheetDialog.dismiss();
        }

        // Clear UI references (but keep currentSession for cycle tracking)
        activeBottomSheetDialog = null;
        tvTimerDisplay = null;
        tvTimeRemaining = null;
        tvPhaseIndicator = null;
        tvCycleCounter = null;
        progressBar = null;
        btnPause = null;
        btnCancelSession = null;
        btnMarkDone = null;
        btnSkipBreak = null;
        btnExtendBreak = null;
        buttonRow = null;
        breakButtonRow = null;
        btnMinimize = null;
        progressContainer = null;
        isMinimized = false;
        currentBreakTotalDurationMillis = 0;

        // Show the create goal form so user can optionally update task/subject
        // The form will be pre-filled with current session data
        // If all cycles are complete, clear subject field for fresh start
        showBottomSheetForNextCycle(completedAllCycles);
    }
    
    /**
     * Step 4.5: Shows bottom sheet for next cycle (pre-filled with current session data)
     * @param clearSubject If true, clears subject field (for fresh start after completing all cycles)
     */
    private void showBottomSheetForNextCycle(boolean clearSubject) {
        if (currentSession == null) {
            return; // Safety check
        }
        
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
        View sheetView = getLayoutInflater().inflate(R.layout.bottom_card_create, null);
        bottomSheetDialog.setContentView(sheetView);

        // Get views
        TextInputEditText etSubject = sheetView.findViewById(R.id.etSubject);
        TextInputEditText etTask = sheetView.findViewById(R.id.etTask);
        TextInputEditText etDuration = sheetView.findViewById(R.id.etDuration);
        TextInputLayout tilDuration = sheetView.findViewById(R.id.tilDuration);
        Chip chipTechnique = sheetView.findViewById(R.id.chipTechnique);
        TextView tvTechniqueIndicator = sheetView.findViewById(R.id.tvTechniqueIndicator);
        MaterialButton btnClose = sheetView.findViewById(R.id.btnClose);
        MaterialButton btnConfirmGoal = sheetView.findViewById(R.id.btnConfirmGoal);

        // Pre-fill form with current session data
        String techniqueName = currentSession.getTechnique();
        chipTechnique.setText(techniqueName);
        tvTechniqueIndicator.setText("Using: " + techniqueName);
        
        // Step 1.3: Add click listener to technique chip to show info dialog
        chipTechnique.setOnClickListener(v -> {
            showTechniqueInfoDialog(techniqueName, false); // false = from create dialog
        });
        
        // Step 4.6: If all cycles are complete, clear subject field for fresh start
        // Otherwise, pre-fill subject (user continues with same subject)
        if (clearSubject) {
            etSubject.setText(""); // Clear subject - user starts fresh after completing all cycles
        } else {
            etSubject.setText(currentSession.getSubject()); // Pre-fill subject for next cycle
        }
        // Task field is always empty - user will enter new task for this cycle
        etTask.setText("");
        
        // Handle duration field based on technique
        if (techniqueName != null && !techniqueName.equals(TECHNIQUE_DEADLINE)) {
            int duration = getTechniqueDuration(techniqueName);
            etDuration.setText(String.valueOf(duration));
            etDuration.setEnabled(false);
            tilDuration.setHelperText(duration + " min (" + techniqueName + ")");
        } else {
            // For deadline/custom, allow editing
            etDuration.setText(String.valueOf(currentSession.getWorkDuration()));
            etDuration.setEnabled(true);
            tilDuration.setHelperText("Enter duration in minutes");
        }

        btnClose.setOnClickListener(v -> {
            // Just close the form - user doesn't want to start next cycle
            bottomSheetDialog.dismiss();
            // User returns to main screen (active session was already dismissed in startNextWorkSession)
            // They can start a new session or continue later
        });

        // Start Session button click handler
        btnConfirmGoal.setOnClickListener(v -> {
            // Get and trim input values from form
            String subject = etSubject.getText().toString().trim();
            String task = etTask.getText().toString().trim();
            String duration = etDuration.getText().toString().trim();

            // Validate subject field
            if (subject.isEmpty()) {
                Toast.makeText(this, "Please enter a subject", Toast.LENGTH_SHORT).show();
                etSubject.requestFocus();
                return;
            }

            // Validate task field
            if (task.isEmpty()) {
                Toast.makeText(this, "Please enter a task", Toast.LENGTH_SHORT).show();
                etTask.requestFocus();
                return;
            }

            // Validate duration field
            if (duration.isEmpty()) {
                Toast.makeText(this, "Please enter a duration", Toast.LENGTH_SHORT).show();
                etDuration.requestFocus();
                return;
            }

            // Validate duration is a valid positive number
            int durationInt;
            try {
                durationInt = Integer.parseInt(duration);
                if (durationInt <= 0) {
                    Toast.makeText(this, "Duration must be greater than 0", Toast.LENGTH_SHORT).show();
                    etDuration.requestFocus();
                    return;
                }
            } catch (NumberFormatException e) {
                Toast.makeText(this, "Please enter a valid number for duration", Toast.LENGTH_SHORT).show();
                etDuration.requestFocus();
                return;
            }

            // Update current session with new data (keep same session for cycle tracking)
            currentSession.setSubject(subject);
            currentSession.setTask(task);
            // Note: Work duration might change for deadline/custom, but we'll keep it for now
            // Break duration stays the same based on technique

            // Close create bottom sheet
            bottomSheetDialog.dismiss();

            // Show active bottom sheet with updated session data and incremented cycle
            showActiveBottomSheet();
        });

        bottomSheetDialog.show();
    }
    
    /**
     * Step 4.5: Updates the cycle counter display based on current session
     */
    private void updateCycleCounterDisplay() {
        if (tvCycleCounter == null || currentSession == null) {
            return;
        }
        
        String technique = currentSession.getTechnique();
        int cycle = currentSession.getCurrentCycle();
        
        // Only show cycle counter for Pomodoro (has functional purpose: long break at cycle 4)
        // Sprint doesn't show cycle counter since it has no special behavior
        if (technique != null && technique.equals(TECHNIQUE_POMODORO)) {
            // Show cycle counter for Pomodoro only
            tvCycleCounter.setVisibility(View.VISIBLE);
            tvCycleCounter.setText("Cycle " + cycle + " of 4");
        } else {
            // Hide cycle counter for all other techniques (Sprint, 52/17, 90-Minute, etc.)
            tvCycleCounter.setVisibility(View.GONE);
        }
    }

    /**
     * Step 4.2: Handles work timer completion
     * Implements smooth work-to-break transition
     */
    private void handleWorkTimerComplete() {
        // Cancel timer reference
        workTimer = null;

        // Check if technique has break (breakDuration > 0)
        if (currentSession != null && currentSession.getBreakDuration() > 0) {
            // Show "Work Session Complete!" message
            Toast.makeText(this, "Work Session Complete! 🎉", Toast.LENGTH_SHORT).show();
            
            // Update UI to show work completion
            if (tvTimerDisplay != null) {
                tvTimerDisplay.setText("00:00");
            }
            
            if (tvTimeRemaining != null) {
                tvTimeRemaining.setText("0 seconds remaining");
            }
            
            if (progressBar != null) {
                progressBar.setScaleX(0.0f);
                progressBar.setScaleY(0.0f);
            }
            
            // Update phase indicator to show work complete
            if (tvPhaseIndicator != null) {
                tvPhaseIndicator.setText("Work Session Complete!");
                tvPhaseIndicator.setTextColor(getResources().getColor(R.color.success_color, null));
            }
            
            // Show completion dialog with only "Ok" button (before transitioning to break)
            // Only show if not already showing
            if (completionDialog == null || !completionDialog.isShowing()) {
                showCompletionDialog(true); // true = show only "Ok" button
            }
            
            // Auto-transition to break phase after user dismisses dialog (or after delay)
            // Using Handler to delay the transition for smooth UX
            // In test mode, reduce delay to 0.5 seconds for faster testing
            //long transitionDelay = TEST_MODE ? 500 : 2500; // 0.5s in test mode, 2.5s in production
            long transitionDelay = 2500;
            Handler handler = new Handler(Looper.getMainLooper());
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    // Only start break if session still exists (user didn't cancel)
                    if (currentSession != null && currentSession.getBreakDuration() > 0) {
                        // Step 4.6: Check if this is a long break for Pomodoro (after 4 cycles)
                        int breakDuration = currentSession.getBreakDuration();
                        boolean isLongBreak = false;
                        
                        if (currentSession.getTechnique().equals(TECHNIQUE_POMODORO) && 
                            currentSession.getCurrentCycle() == 4) {
                            // Use long break (25 minutes) instead of short break (5 minutes)
                            breakDuration = 25;
                            isLongBreak = true;
                        }
                        
                        // Start break timer (with long break duration if applicable)
                        startBreakTimer(breakDuration, isLongBreak);
                        // Update state to ACTIVE_BREAK (done in startBreakTimer)
                    }
                }
            }, transitionDelay);
        } else {
            // No break (Deadline Simulation or Custom Goal with no break)
            // Show completion screen directly
            updateSessionState(SessionState.COMPLETED);
            
            if (currentSession != null) {
                currentSession.setRemainingTimeMillis(0);
            }

            // Update UI to show completion
            if (tvTimerDisplay != null) {
                tvTimerDisplay.setText("00:00");
            }
            
            if (tvTimeRemaining != null) {
                tvTimeRemaining.setText("0 seconds remaining");
            }
            
            if (progressBar != null) {
                progressBar.setScaleX(0.0f);
                progressBar.setScaleY(0.0f);
            }

            // Show completion notification
            Toast.makeText(this, "Work Session Complete! 🎉", Toast.LENGTH_LONG).show();
            
            // Show completion dialog for no-break techniques
            showCompletionDialog(false);
        }
    }

    /**
     * Updates the session state and syncs UI accordingly
     * @param newState The new state to transition to
     */
    private void updateSessionState(SessionState newState) {
        // Update current state variable
        currentState = newState;
        
        // Update session state if session exists
        if (currentSession != null) {
            currentSession.setState(newState);
        }
        
        // Update UI based on new state
        updateUIForState(newState);
    }

    /**
     * Updates UI elements based on the current session state
     * @param state The current session state
     */
    private void updateUIForState(SessionState state) {
        // Update phase indicator based on state
        if (tvPhaseIndicator != null) {
            tvPhaseIndicator.setText(state.getDisplayName());
            
            // Set color based on state
            int colorRes;
            switch (state) {
                case ACTIVE_WORK:
                    colorRes = R.color.phase_work;
                    break;
                case ACTIVE_BREAK:
                    colorRes = R.color.phase_break;
                    break;
                case PAUSED:
                    colorRes = R.color.phase_paused;
                    break;
                case COMPLETED:
                    colorRes = R.color.success_color;
                    break;
                default:
                    colorRes = R.color.text_secondary;
                    break;
            }
            tvPhaseIndicator.setTextColor(getResources().getColor(colorRes, null));
        }
        
        // Step 4.7: Update button visibility based on state
        // Show work session buttons (Cancel, Pause) or break action buttons (Skip, Extend)
        if (buttonRow != null) {
            if (state == SessionState.ACTIVE_WORK || state == SessionState.PAUSED) {
                buttonRow.setVisibility(View.VISIBLE); // Show work session buttons
            } else {
                buttonRow.setVisibility(View.GONE); // Hide work session buttons
            }
        }
        
        if (breakButtonRow != null) {
            if (state == SessionState.ACTIVE_BREAK) {
                breakButtonRow.setVisibility(View.VISIBLE); // Show break action buttons
            } else {
                breakButtonRow.setVisibility(View.GONE); // Hide break action buttons
            }
        }
        
        // Update button visibility and text based on state
        if (btnPause != null) {
            if (state == SessionState.ACTIVE_WORK) {
                // Pause button only visible during work session
                btnPause.setVisibility(View.VISIBLE);
                btnPause.setText("Pause");
            } else if (state == SessionState.PAUSED) {
                // Resume button visible when paused
                btnPause.setVisibility(View.VISIBLE);
                btnPause.setText("Resume");
            } else {
                // Hide pause button for break, completed, and other states
                btnPause.setVisibility(View.GONE);
            }
        }
        
        if (btnCancelSession != null) {
            // Cancel button only visible during work session and paused work
            // NOT visible during break (user should use Skip Break or Mark as Done instead)
            if (state == SessionState.ACTIVE_WORK || 
                state == SessionState.PAUSED) {
                btnCancelSession.setVisibility(View.VISIBLE);
            } else {
                // Hide during break, completed, and other states
                btnCancelSession.setVisibility(View.GONE);
            }
        }
        
        // Step 4.7: Break action buttons visibility
        if (btnSkipBreak != null) {
            btnSkipBreak.setVisibility(state == SessionState.ACTIVE_BREAK ? View.VISIBLE : View.GONE);
        }
        
        if (btnExtendBreak != null) {
            btnExtendBreak.setVisibility(state == SessionState.ACTIVE_BREAK ? View.VISIBLE : View.GONE);
        }
        
        if (btnMarkDone != null) {
            // Mark as Done button visible for:
            // - Work session (user can mark done early)
            // - Paused (user can mark done)
            // - Completed (will be changed to "Start Next Session" in break completion)
            // NOT visible during break session (user should use Skip Break or wait for break to complete)
            if (state == SessionState.ACTIVE_WORK || 
                state == SessionState.PAUSED ||
                state == SessionState.COMPLETED) {
                btnMarkDone.setVisibility(View.VISIBLE);
            } else {
                btnMarkDone.setVisibility(View.GONE);
            }
        }
    }
    
    /**
     * Step 5.6: Toggles between minimized and maximized states of the active session card
     */
    private void toggleMinimizeMaximize() {
        if (isMinimized) {
            // Expand to maximized state
            maximizeCard();
        } else {
            // Collapse to minimized state
            minimizeCard();
        }
    }
    
    /**
     * Step 5.6: Minimizes the active session card - shows only essential info
     */
    private void minimizeCard() {
        isMinimized = true;
        
        // Hide detailed elements
        if (progressContainer != null) {
            progressContainer.setVisibility(View.GONE);
        }
        if (tvTimeRemaining != null) {
            tvTimeRemaining.setVisibility(View.GONE);
        }
        if (buttonRow != null) {
            buttonRow.setVisibility(View.GONE);
        }
        if (breakButtonRow != null) {
            breakButtonRow.setVisibility(View.GONE);
        }
        if (btnMarkDone != null) {
            btnMarkDone.setVisibility(View.GONE);
        }
        if (tvCycleCounter != null) {
            tvCycleCounter.setVisibility(View.GONE);
        }
        
        // Show timer display in smaller format (positioned below phase indicator)
        if (tvTimerDisplay != null) {
            tvTimerDisplay.setVisibility(View.VISIBLE);
            tvTimerDisplay.setTextSize(24); // Smaller size for minimized view (24sp)
            // Position it below phase indicator
            ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) tvTimerDisplay.getLayoutParams();
            params.topToBottom = R.id.tvPhaseIndicator;
            params.topToTop = ConstraintLayout.LayoutParams.UNSET;
            params.startToStart = ConstraintLayout.LayoutParams.PARENT_ID;
            params.endToEnd = ConstraintLayout.LayoutParams.PARENT_ID;
            params.bottomToBottom = ConstraintLayout.LayoutParams.UNSET;
            params.topMargin = (int) (16 * getResources().getDisplayMetrics().density); // 16dp
            tvTimerDisplay.setLayoutParams(params);
        }
        
        // Change minimize button icon to maximize (arrow up)
        if (btnMinimize != null) {
            btnMinimize.setImageResource(android.R.drawable.arrow_up_float);
            btnMinimize.setContentDescription("Maximize");
        }
    }
    
    /**
     * Step 5.6: Maximizes the active session card - shows all details
     */
    private void maximizeCard() {
        isMinimized = false;
        
        // Show all elements
        if (progressContainer != null) {
            progressContainer.setVisibility(View.VISIBLE);
        }
        if (tvTimeRemaining != null) {
            tvTimeRemaining.setVisibility(View.VISIBLE);
        }
        if (tvCycleCounter != null) {
            // Cycle counter visibility is managed by updateCycleCounterDisplay()
            updateCycleCounterDisplay();
        }
        
        // Restore timer display to original position and size
        if (tvTimerDisplay != null) {
            tvTimerDisplay.setTextSize(48); // Original large size (48sp)
            // Position it back in the center of progress container
            ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) tvTimerDisplay.getLayoutParams();
            params.topToTop = R.id.progressContainer;
            params.topToBottom = ConstraintLayout.LayoutParams.UNSET;
            params.startToStart = R.id.progressContainer;
            params.endToEnd = R.id.progressContainer;
            params.bottomToBottom = R.id.progressContainer;
            params.topMargin = 0;
            tvTimerDisplay.setLayoutParams(params);
        }
        
        // Restore button visibility based on current state
        updateUIForState(currentState);
        
        // Change maximize button icon back to minimize (arrow down)
        if (btnMinimize != null) {
            btnMinimize.setImageResource(android.R.drawable.arrow_down_float);
            btnMinimize.setContentDescription("Minimize");
        }
    }
    
    /**
     * Step 1.3: Shows the technique information dialog
     * @param techniqueName The name of the technique to display info for
     * @param fromActiveDialog If true, called from active session dialog (show only Ok button). If false, called from create dialog (show Close and Use buttons).
     */
    private void showTechniqueInfoDialog(String techniqueName, boolean fromActiveDialog) {
        // Don't show dialog for Custom Goal
        if (techniqueName == null || techniqueName.equals(TECHNIQUE_CUSTOM)) {
            return;
        }
        
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_technique_info, null);
        builder.setView(dialogView);
        
        android.app.AlertDialog dialog = builder.create();
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        
        // Get views from dialog
        TextView tvTechniqueName = dialogView.findViewById(R.id.tvTechniqueName);
        TextView tvWorkDuration = dialogView.findViewById(R.id.tvWorkDuration);
        TextView tvBreakDuration = dialogView.findViewById(R.id.tvBreakDuration);
        TextView tvDescription = dialogView.findViewById(R.id.tvDescription);
        MaterialButton btnCancel = dialogView.findViewById(R.id.btnCancel);
        MaterialButton btnUseTechnique = dialogView.findViewById(R.id.btnUseTechnique);
        MaterialButton btnOk = dialogView.findViewById(R.id.btnOk);
        LinearLayout buttonRow = dialogView.findViewById(R.id.buttonRow);
        
        // Get technique data
        int workDuration = getTechniqueDuration(techniqueName);
        int breakDuration = getTechniqueBreakDuration(techniqueName);
        String description = getTechniqueDescription(techniqueName);
        
        // Populate dialog with technique data
        tvTechniqueName.setText(techniqueName + " Technique");
        tvWorkDuration.setText(workDuration + " minutes");
        
        // Handle break duration display (special case for Sprint: 1.5 minutes)
        if (techniqueName.equals(TECHNIQUE_SPRINT)) {
            tvBreakDuration.setText("1.5 minutes");
        } else if (breakDuration == 0) {
            tvBreakDuration.setText("No break");
        } else {
            tvBreakDuration.setText(breakDuration + " minutes");
        }
        
        tvDescription.setText(description);
        
        // Configure buttons based on context
        if (fromActiveDialog) {
            // Active dialog: Show only Ok button (full width), hide Close and Use buttons
            if (buttonRow != null) {
                buttonRow.setVisibility(View.GONE);
            }
            if (btnCancel != null) {
                btnCancel.setVisibility(View.GONE);
            }
            if (btnUseTechnique != null) {
                btnUseTechnique.setVisibility(View.GONE);
            }
            if (btnOk != null) {
                btnOk.setVisibility(View.VISIBLE);
                btnOk.setOnClickListener(v -> {
                    dialog.dismiss();
                });
            }
        } else {
            // Create dialog: Show Close and Use buttons, hide Ok button
            if (buttonRow != null) {
                buttonRow.setVisibility(View.VISIBLE);
            }
            if (btnCancel != null) {
                btnCancel.setVisibility(View.VISIBLE);
                btnCancel.setText("Close"); // Changed from "Cancel" to "Close"
                btnCancel.setOnClickListener(v -> {
                    dialog.dismiss();
                });
            }
            if (btnUseTechnique != null) {
                btnUseTechnique.setVisibility(View.VISIBLE);
                btnUseTechnique.setOnClickListener(v -> {
                    // Select this technique
                    selectedTechnique = techniqueName;
                    highlightSelectedCard(techniqueName);
                    
                    // Close the dialog
                    dialog.dismiss();
                    
                    // Show toast notification
                    Toast.makeText(this, techniqueName + " technique selected", Toast.LENGTH_SHORT).show();
                });
            }
            if (btnOk != null) {
                btnOk.setVisibility(View.GONE);
            }
        }
        
        dialog.show();
    }
    
    /**
     * Step 1.3: Gets the break duration for a technique
     * @param techniqueName The name of the technique
     * @return Break duration in minutes
     */
    private int getTechniqueBreakDuration(String techniqueName) {
        if (techniqueName == null) {
            return 0;
        }
        
        switch (techniqueName) {
            case TECHNIQUE_POMODORO:
                return 5; // Short break (long break is 25, handled separately)
            case TECHNIQUE_5217:
                return 17;
            case TECHNIQUE_90MINUTE:
                return 25;
            case TECHNIQUE_SPRINT:
                return 1; // 1.5 minutes, rounded to 1 for display (actual is 1.5)
            case TECHNIQUE_DEADLINE:
                return 0; // No break
            default:
                return 0;
        }
    }
    
    /**
     * Step 1.3: Gets the description for a technique
     * @param techniqueName The name of the technique
     * @return Description text
     */
    private String getTechniqueDescription(String techniqueName) {
        if (techniqueName == null) {
            return "";
        }
        
        switch (techniqueName) {
            case TECHNIQUE_POMODORO:
                return "Work for 25 minutes, then take a 5-minute break. Repeat for 4 cycles, then take a longer 25-minute break. This technique helps maintain focus and prevents burnout.";
            case TECHNIQUE_5217:
                return "Work intensively for 52 minutes, then take a 17-minute break. This technique is ideal for deep work sessions that require sustained concentration.";
            case TECHNIQUE_90MINUTE:
                return "Work for 90 minutes (one full sleep cycle), then take a 25-minute break. This technique aligns with your natural ultradian rhythm for optimal productivity.";
            case TECHNIQUE_SPRINT:
                return "Work for 12 minutes, then take a 1.5-minute break. Perfect for quick tasks and maintaining high energy throughout your work session.";
            case TECHNIQUE_DEADLINE:
                return "Set a custom work duration with no scheduled breaks. Ideal for time-sensitive tasks or when you need to power through without interruptions.";
            default:
                return "";
        }
    }
    
    /**
     * Step 5.3: Calculates the actual time spent from session start to completion
     * @return Formatted time spent string (e.g., "25 minutes", "1 hour 30 minutes")
     */
    private String calculateTimeSpent() {
        if (currentSession == null) {
            return "0 minutes";
        }
        
        // Calculate time spent: current time - start time
        long currentTime = System.currentTimeMillis();
        long startTime = currentSession.getStartTime();
        long timeSpentMillis = currentTime - startTime;
        
        // Convert to seconds
        long totalSeconds = timeSpentMillis / 1000;
        
        // Calculate hours, minutes, and seconds
        long hours = totalSeconds / 3600;
        long minutes = (totalSeconds % 3600) / 60;
        long seconds = totalSeconds % 60;
        
        // Format time spent with smart labels
        StringBuilder text = new StringBuilder();
        
        if (hours > 0) {
            // Has hours: show "X hour(s) and Y minute(s)"
            text.append(hours);
            text.append(hours == 1 ? " hour" : " hours");
            
            if (minutes > 0) {
                text.append(" and ");
                text.append(minutes);
                text.append(minutes == 1 ? " minute" : " minutes");
            }
        } else if (minutes > 0) {
            // Only minutes: show "X minute(s)"
            text.append(minutes);
            text.append(minutes == 1 ? " minute" : " minutes");
            
            // Optionally show seconds if less than 1 minute total or for precision
            // For now, we'll just show minutes to keep it clean
        } else {
            // Only seconds: show "X second(s)" (for very short sessions)
            text.append(seconds);
            text.append(seconds == 1 ? " second" : " seconds");
        }
        
        return text.toString();
    }
}
