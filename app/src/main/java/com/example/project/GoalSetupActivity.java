package com.example.project;

import android.content.Intent;
import android.os.Bundle;
import android.widget.NumberPicker;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

public class GoalSetupActivity extends AppCompatActivity {

    private TextInputEditText etSubject, etTaskDescription;
    private NumberPicker npDuration;
    private MaterialButton btnConfirmGoal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_goal_setup);

        // Initialize views
        etSubject = findViewById(R.id.etSubject);
        etTaskDescription = findViewById(R.id.etTaskDescription);
        npDuration = findViewById(R.id.npDuration);
        btnConfirmGoal = findViewById(R.id.btnConfirmGoal);

        // Configure NumberPicker
        npDuration.setMinValue(5);
        npDuration.setMaxValue(180);
        npDuration.setValue(30);
        npDuration.setWrapSelectorWheel(false);

        // Confirm button click
        btnConfirmGoal.setOnClickListener(v -> handleConfirmGoal());
    }

    private void handleConfirmGoal() {
        String subject = etSubject.getText().toString().trim();
        String task = etTaskDescription.getText().toString().trim();
        int duration = npDuration.getValue();

        // Validation
        if (subject.isEmpty() || task.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // Package data and return to MainActivity
        Intent resultIntent = new Intent();
        resultIntent.putExtra("subject", subject);
        resultIntent.putExtra("task", task);
        resultIntent.putExtra("duration", duration);

        setResult(RESULT_OK, resultIntent);
        finish();
    }
}