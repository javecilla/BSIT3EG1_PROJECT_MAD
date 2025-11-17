package com.example.project;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;

public class AboutProjectActivity extends AppCompatActivity {

    // Sidebar Navigation Menu
    private View sidebarMenu;
    private View overlayDim;
    private boolean isSidebarOpen = false;
    private TextView tvUserName;
    private MaterialButton btnNavHome, btnNavAboutProject, btnNavAboutTeam, btnNavLogout;
    private MaterialButton btnCloseSidebar;
    private String currentUserName; // Store current user's full name

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.about_project_activity);

        // Initialize Sidebar Navigation Menu
        initializeSidebar();
    }

    /**
     * Initialize Sidebar Navigation Menu
     */
    private void initializeSidebar() {
        // Initialize views
        MaterialButton menuButton = findViewById(R.id.menuButton);
        sidebarMenu = findViewById(R.id.sidebarMenu);
        overlayDim = findViewById(R.id.overlayDim);
        btnCloseSidebar = findViewById(R.id.btnCloseSidebar);
        tvUserName = findViewById(R.id.tvUserName);
        btnNavHome = findViewById(R.id.btnNavHome);
        btnNavAboutProject = findViewById(R.id.btnNavAboutProject);
        btnNavAboutTeam = findViewById(R.id.btnNavAboutTeam);
        btnNavLogout = findViewById(R.id.btnNavLogout);

        // Get user name (from Intent or placeholder)
        currentUserName = getIntent().getStringExtra("fullName");
        if (currentUserName == null || currentUserName.isEmpty()) {
            // Try alternative key for backward compatibility
            currentUserName = getIntent().getStringExtra("userName");
            if (currentUserName == null || currentUserName.isEmpty()) {
                currentUserName = "John Doe"; // Placeholder
            }
        }
        tvUserName.setText(currentUserName);

        // Initially hide sidebar and position it off-screen
        sidebarMenu.setVisibility(View.GONE);
        sidebarMenu.post(() -> {
            // Position sidebar off-screen to the right
            sidebarMenu.setTranslationX(sidebarMenu.getWidth());
        });
        if (overlayDim != null) {
            overlayDim.setVisibility(View.GONE);
        }

        // Set click listeners
        menuButton.setOnClickListener(v -> openSidebar());
        btnCloseSidebar.setOnClickListener(v -> closeSidebar());
        if (overlayDim != null) {
            overlayDim.setOnClickListener(v -> closeSidebar());
        }

        // Navigation item click listeners
        btnNavHome.setOnClickListener(v -> {
            closeSidebar();
            // Navigate back to MainActivity
            android.content.Intent intent = new android.content.Intent(AboutProjectActivity.this, MainActivity.class);
            intent.setFlags(android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP | android.content.Intent.FLAG_ACTIVITY_SINGLE_TOP);
            intent.putExtra("fullName", currentUserName); // Pass full name back
            startActivity(intent);
            finish();
        });

        btnNavAboutProject.setOnClickListener(v -> {
            closeSidebar();
            // Already on About Project page, just close sidebar
        });

        btnNavAboutTeam.setOnClickListener(v -> {
            closeSidebar();
            // Navigate to AboutTeamActivity
            android.content.Intent intent = new android.content.Intent(AboutProjectActivity.this, AboutTeamActivity.class);
            intent.putExtra("fullName", currentUserName); // Pass full name
            startActivity(intent);
        });

        btnNavLogout.setOnClickListener(v -> {
            closeSidebar();
            handleLogout();
        });
    }

    /**
     * Opens the sidebar menu with slide-in animation
     */
    private void openSidebar() {
        if (isSidebarOpen) return;

        sidebarMenu.setVisibility(View.VISIBLE);
        sidebarMenu.post(() -> {
            sidebarMenu.animate()
                .translationX(0)
                .setDuration(300)
                .setInterpolator(new android.view.animation.AccelerateDecelerateInterpolator())
                .start();
        });

        if (overlayDim != null) {
            overlayDim.setVisibility(View.VISIBLE);
            overlayDim.animate()
                .alpha(0.5f)
                .setDuration(300)
                .start();
        }

        isSidebarOpen = true;
    }

    /**
     * Closes the sidebar menu with slide-out animation
     */
    private void closeSidebar() {
        if (!isSidebarOpen) return;

        float sidebarWidth = sidebarMenu.getWidth();
        sidebarMenu.animate()
            .translationX(sidebarWidth)
            .setDuration(300)
            .setInterpolator(new android.view.animation.AccelerateDecelerateInterpolator())
            .withEndAction(() -> sidebarMenu.setVisibility(View.GONE))
            .start();

        if (overlayDim != null) {
            overlayDim.animate()
                .alpha(0f)
                .setDuration(300)
                .withEndAction(() -> overlayDim.setVisibility(View.GONE))
                .start();
        }

        isSidebarOpen = false;
    }

    /**
     * Override back button to close sidebar if open
     */
    @Override
    public void onBackPressed() {
        if (isSidebarOpen) {
            closeSidebar();
        } else {
            super.onBackPressed();
        }
    }

    /**
     * Shows About Team dialog
     */
    private void showAboutTeamDialog() {
        new android.app.AlertDialog.Builder(this)
            .setTitle("About Team")
            .setMessage("FocusFlow Development Team:\n\n" +
                       "• Jerome Avecilla (Dev Lead)\n" +
                       "• Mico Oleriana\n" +
                       "• Rensen Dela Cruz\n" +
                       "• Francis Palma\n" +
                       "• Ralp Andre Giga")
            .setPositiveButton("OK", null)
            .show();
    }

    /**
     * Handles logout action with confirmation dialog
     */
    private void handleLogout() {
        new android.app.AlertDialog.Builder(this)
            .setTitle("Logout")
            .setMessage("Are you sure you want to logout?")
            .setPositiveButton("Yes", (dialog, which) -> {
                // Navigate to LoginActivity
                android.content.Intent intent = new android.content.Intent(AboutProjectActivity.this, LoginActivity.class);
                intent.setFlags(android.content.Intent.FLAG_ACTIVITY_NEW_TASK | android.content.Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            })
            .setNegativeButton("No", null)
            .show();
    }
}

