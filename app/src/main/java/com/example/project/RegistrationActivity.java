package com.example.project;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.HashMap;

public class RegistrationActivity extends AppCompatActivity {

    private static final String TAG = "RegistrationActivity";
    private TextInputEditText etFullName, etEmail, etPassword, etConfirmPassword;
    private TextInputLayout tilFullName, tilEmail, tilPassword, tilConfirmPassword;
    private MaterialButton btnRegister;

    private TextView tvLoginLink;
    public static HashMap<String, User> userMap = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        Log.d(TAG, "RegistrationActivity created");

        etFullName = findViewById(R.id.etRegFullName);
        etEmail = findViewById(R.id.etRegEmail);
        etPassword = findViewById(R.id.etRegPassword);
        etConfirmPassword = findViewById(R.id.etRegConfirmPassword);
        tilFullName = findViewById(R.id.tilRegFullName);
        tilEmail = findViewById(R.id.tilRegEmail);
        tilPassword = findViewById(R.id.tilRegPassword);
        tilConfirmPassword = findViewById(R.id.tilRegConfirmPassword);
        btnRegister = findViewById(R.id.btnRegister);
        tvLoginLink = findViewById(R.id.tvLoginLink);

        // Register button click
        btnRegister.setOnClickListener(v -> handleRegistration());

        tvLoginLink.setOnClickListener(v -> {
            Intent loginIntent = new Intent(RegistrationActivity.this, LoginActivity.class);
            startActivity(loginIntent);
        });
    }

    private void handleRegistration() {
        String fullName = etFullName.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String confirmPassword = etConfirmPassword.getText().toString().trim();

        Log.d(TAG, "Registration attempt:");
        Log.d(TAG, "  Full Name: '" + fullName + "'");
        Log.d(TAG, "  Email: '" + email + "'");
        Log.d(TAG, "  Password length: " + password.length());
        Log.d(TAG, "  Confirm Password: '" + confirmPassword + "'");

        // Clear previous errors
        tilFullName.setError(null);
        tilEmail.setError(null);
        tilPassword.setError(null);
        tilConfirmPassword.setError(null);

        // Validate full name field
        if (fullName.isEmpty()) {
            tilFullName.setError("Full name is required");
            etFullName.requestFocus();
            Toast.makeText(this, "Please enter your full name", Toast.LENGTH_SHORT).show();
            return;
        }

        // Validate email field
        if (email.isEmpty()) {
            tilEmail.setError("Email is required");
            etEmail.requestFocus();
            Toast.makeText(this, "Please enter your email address", Toast.LENGTH_SHORT).show();
            return;
        }

        // Validate email format
        String emailRegex = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
        if (!email.matches(emailRegex)) {
            tilEmail.setError("Invalid email format");
            etEmail.requestFocus();
            Toast.makeText(this, "Please enter a valid email address", Toast.LENGTH_SHORT).show();
            return;
        }

        // Check if email already exists
        if (userMap.containsKey(email)) {
            tilEmail.setError("Email already registered");
            etEmail.requestFocus();
            Toast.makeText(this, "An account with this email already exists", Toast.LENGTH_SHORT).show();
            return;
        }

        // Validate password field
        if (password.isEmpty()) {
            tilPassword.setError("Password is required");
            etPassword.requestFocus();
            Toast.makeText(this, "Please enter a password", Toast.LENGTH_SHORT).show();
            return;
        }

        // Validate password length
        if (password.length() < 6) {
            tilPassword.setError("Password must be at least 6 characters");
            etPassword.requestFocus();
            Toast.makeText(this, "Password must be at least 6 characters long", Toast.LENGTH_SHORT).show();
            return;
        }

        // Validate password contains uppercase letter
        if (!password.matches(".*[A-Z].*")) {
            tilPassword.setError("Must contain at least one uppercase letter");
            etPassword.requestFocus();
            Toast.makeText(this, "Password must contain at least one uppercase letter", Toast.LENGTH_SHORT).show();
            return;
        }

        // Validate password contains lowercase letter
        if (!password.matches(".*[a-z].*")) {
            tilPassword.setError("Must contain at least one lowercase letter");
            etPassword.requestFocus();
            Toast.makeText(this, "Password must contain at least one lowercase letter", Toast.LENGTH_SHORT).show();
            return;
        }

        // Validate password contains number
        if (!password.matches(".*\\d.*")) {
            tilPassword.setError("Must contain at least one number");
            etPassword.requestFocus();
            Toast.makeText(this, "Password must contain at least one number", Toast.LENGTH_SHORT).show();
            return;
        }

        // Validate password contains special character
        if (!password.matches(".*[@$!%*?&]+.*")) {
            tilPassword.setError("Must contain at least one special character");
            etPassword.requestFocus();
            Toast.makeText(this, "Password must contain at least one special character (@$!%*?&)", Toast.LENGTH_SHORT).show();
            return;
        }

        // Validate confirm password field
        if (confirmPassword.isEmpty()) {
            tilConfirmPassword.setError("Please confirm your password");
            etConfirmPassword.requestFocus();
            Toast.makeText(this, "Please confirm your password", Toast.LENGTH_SHORT).show();
            return;
        }

        // Validate passwords match
        if (!password.equals(confirmPassword)) {
            tilConfirmPassword.setError("Passwords do not match");
            etConfirmPassword.requestFocus();
            Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show();
            return;
        }

        // Clear all errors on successful validation
        tilFullName.setError(null);
        tilEmail.setError(null);
        tilPassword.setError(null);
        tilConfirmPassword.setError(null);

        // Register new user
        User newUser = new User(fullName, email, password);
        userMap.put(email, newUser);

        Intent intent = new Intent(RegistrationActivity.this, LoginActivity.class);
        intent.putExtra("email", email);
        intent.putExtra("fullName", fullName);
        startActivity(intent);
        Toast.makeText(this, "Registration successful!", Toast.LENGTH_SHORT).show();

    }
}
