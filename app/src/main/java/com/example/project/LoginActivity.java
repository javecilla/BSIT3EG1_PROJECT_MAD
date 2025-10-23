package com.example.project;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

public class LoginActivity extends AppCompatActivity {

    private TextInputEditText etUsername, etPassword;
    private MaterialButton btnLogin;
    private TextView tvRegisterLink;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Initialize views
        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        tvRegisterLink = findViewById(R.id.tvRegisterLink);

        // Pre-fill username if passed from RegistrationActivity
        Intent intent = getIntent();
        if (intent.hasExtra("username")) {
            String username = intent.getStringExtra("username");
            etUsername.setText(username);
        }

        // Login button click
        btnLogin.setOnClickListener(v -> handleLogin());

        // Register link click
        tvRegisterLink.setOnClickListener(v -> {
            Intent registerIntent = new Intent(LoginActivity.this, RegistrationActivity.class);
            startActivity(registerIntent);
        });
    }

    private void handleLogin() {
        String username = etUsername.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        // Validation
        if (username.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please enter both username and password", Toast.LENGTH_SHORT).show();
            return;
        }

        // Mock validation
        if (username.equals("user") && password.equals("pass")) {
            Toast.makeText(this, "Login successful!", Toast.LENGTH_SHORT).show();

            // Navigate to MainActivity with username
            Intent mainIntent = new Intent(LoginActivity.this, MainActivity.class);
            mainIntent.putExtra("username", username);
            startActivity(mainIntent);
            finish();
        } else {
            Toast.makeText(this, "Invalid credentials. Try user/pass", Toast.LENGTH_SHORT).show();
        }
    }
}