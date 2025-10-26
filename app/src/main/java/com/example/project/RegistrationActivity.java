package com.example.project;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import java.util.HashMap;

public class RegistrationActivity extends AppCompatActivity {

    private static final String TAG = "RegistrationActivity";
    private TextInputEditText etFullName, etEmail, etPassword, etConfirmPassword;
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

        // Validate Values
        if (fullName.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        if (userMap.containsKey(email)) {
            Toast.makeText(this, "An account with this email already exists", Toast.LENGTH_SHORT).show();
            return;
        }

        String emailRegex = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
        if (!email.matches(emailRegex)) {
            Toast.makeText(this, "Invalid email address", Toast.LENGTH_SHORT).show();
            return;
        }
        if (password.length() < 6) {
            Toast.makeText(this, "Password must be at least 6 characters long", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!password.matches(".*[A-Z].*")) {
            Toast.makeText(this, "Password must contain at least one uppercase letter", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!password.matches(".*[a-z].*")) {
            Toast.makeText(this, "Password must contain at least one lowercase letter", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!password.matches(".*\\d.*")) {
            Toast.makeText(this, "Password must contain at least one number", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!password.matches(".*[@$!%*?&]+.*")) {
            Toast.makeText(this, "Password must contain at least one special character", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!password.equals(confirmPassword)) {
            Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show();
            return;
        }

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
