package com.example.project;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";
    private TextInputEditText etEmail, etPassword;
    private MaterialButton btnLogin;
    private TextView tvRegisterLink;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Log.d(TAG, "LoginActivity created");

        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
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

        // Validate Values
        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        if (RegistrationActivity.userMap.containsKey(email)){
            // Match user input with stored data of users
            User storedUser = RegistrationActivity.userMap.get(email);
            if(storedUser.password.equals(password)){
                Toast.makeText(this, "Login Successful", Toast.LENGTH_SHORT).show();
                // dito activity para mapunta sa next stuff real
            }else {
                Toast.makeText(this,"Invalid Email or Password", Toast.LENGTH_SHORT).show();
                return;
            }
        } else{
            Toast.makeText(this,"Invalid Email or Password", Toast.LENGTH_SHORT).show();
            return;
        }
    }
}