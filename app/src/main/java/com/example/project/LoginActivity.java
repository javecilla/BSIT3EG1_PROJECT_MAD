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

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";
    private TextInputEditText etEmail, etPassword;
    private TextInputLayout tilEmail, tilPassword;
    private MaterialButton btnLogin;
    private TextView tvRegisterLink;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // For Testing
        User user = new User("admin","admin@gmail.com","admin123");

        RegistrationActivity.userMap.put("admin@gmail.com", user);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Log.d(TAG, "LoginActivity created");

        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        tilEmail = findViewById(R.id.tilEmail);
        tilPassword = findViewById(R.id.tilPassword);
        btnLogin = findViewById(R.id.btnLogin);
        tvRegisterLink = findViewById(R.id.tvRegisterLink);

        // Pre-fill email if passed from RegistrationActivity
        Intent intent = getIntent();
        if (intent.hasExtra("email")) {
            String email = intent.getStringExtra("email");
            etEmail.setText(email);
            Log.d(TAG, "Pre-filled email from registration: " + email);
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
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        Log.d(TAG, "Login button clicked");
        Log.d(TAG, "Entered email: '" + email + "'");
        Log.d(TAG, "Entered password: '" + password + "'");

        // Clear previous errors
        tilEmail.setError(null);
        tilPassword.setError(null);

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

        // Validate password field
        if (password.isEmpty()) {
            tilPassword.setError("Password is required");
            etPassword.requestFocus();
            Toast.makeText(this, "Please enter your password", Toast.LENGTH_SHORT).show();
            return;
        }

        // Check if user exists
        if (RegistrationActivity.userMap.containsKey(email)){
            // Match user input with stored data of users
            User storedUser = RegistrationActivity.userMap.get(email);
            if(storedUser.password.equals(password)){
                // Clear errors on successful validation
                tilEmail.setError(null);
                tilPassword.setError(null);
                
                Intent loginIntent = new Intent(LoginActivity.this, MainActivity.class);
                // Pass fullName to MainActivity for sidebar display
                loginIntent.putExtra("fullName", storedUser.fullName);
                loginIntent.putExtra("email", email);
                startActivity(loginIntent);
                finish(); // Close LoginActivity after successful login
            }else {
                tilPassword.setError("Incorrect password");
                etPassword.requestFocus();
                Toast.makeText(this,"Invalid Email or Password", Toast.LENGTH_SHORT).show();
                return;
            }
        } else{
            tilEmail.setError("Email not found");
            etEmail.requestFocus();
            Toast.makeText(this,"Invalid Email or Password", Toast.LENGTH_SHORT).show();
            return;
        }
    }
}