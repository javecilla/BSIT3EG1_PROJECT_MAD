package com.example.project;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.button.MaterialButton;

public class MainActivity extends AppCompatActivity {

    private TextView tvUsername, tvSubject, tvTask, tvDuration;
    private MaterialButton btnSetupGoal;
    private ActivityResultLauncher<Intent> goalSetupLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize views
        tvUsername = findViewById(R.id.tvUsername);
        tvSubject = findViewById(R.id.tvSubject);
        tvTask = findViewById(R.id.tvTask);
        tvDuration = findViewById(R.id.tvDuration);
        btnSetupGoal = findViewById(R.id.btnSetupGoal);

        // Get username from intent
        Intent intent = getIntent();
        if (intent.hasExtra("username")) {
            String username = intent.getStringExtra("username");
            tvUsername.setText("Hello, " + username + "!");
        }

        // Register activity result launcher
        goalSetupLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        Intent data = result.getData();
                        String subject = data.getStringExtra("subject");
                        String task = data.getStringExtra("task");
                        int duration = data.getIntExtra("duration", 0);

                        // Update UI with goal data
                        tvSubject.setText(subject);
                        tvTask.setText(task);
                        tvTask.setVisibility(View.VISIBLE);
                        tvDuration.setText("Duration: " + duration + " minutes");
                        tvDuration.setVisibility(View.VISIBLE);
                    }
                }
        );

        // Setup goal button click
        btnSetupGoal.setOnClickListener(v -> {
            Intent goalIntent = new Intent(MainActivity.this, GoalSetupActivity.class);
            goalSetupLauncher.launch(goalIntent);
        });
    }
}