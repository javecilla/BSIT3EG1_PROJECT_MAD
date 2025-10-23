package com.example.project;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

public class RegistrationActivity extends AppCompatActivity {

    private TextInputEditText etUsername, etEmail, etPassword;
    private MaterialButton btnRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        // Initialize views
        etUsername = findViewById(R.id.etRegUsername);
        etEmail = findViewById(R.id.etRegEmail);
        etPassword = findViewById(R.id.etRegPassword);
        btnRegister = findViewById(R.id.btnRegister);

        // Register button click
        btnRegister.setOnClickListener(v -> handleRegistration());
    }

    private void handleRegistration() {
        String username = etUsername.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        // Validation
        if (username.isEmpty() || email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "All fields are required", Toast.LENGTH_SHORT).show();
            return;
        }

        if (password.length() < 6) {
            Toast.makeText(this, "Password must be at least 6 characters", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!email.contains("@")) {
            Toast.makeText(this, "Please enter a valid email", Toast.LENGTH_SHORT).show();
            return;
        }

        // Registration successful - pass username back to LoginActivity
        Toast.makeText(this, "Registration successful!", Toast.LENGTH_SHORT).show();

        Intent intent = new Intent(RegistrationActivity.this, LoginActivity.class);
        intent.putExtra("username", username);
        startActivity(intent);
        finish();
    }
}