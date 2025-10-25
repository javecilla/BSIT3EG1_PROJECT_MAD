package com.example.project;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

public class RegistrationActivity extends AppCompatActivity {

    private static final String TAG = "RegistrationActivity";
    private TextInputEditText etFullName, etEmail, etPassword, etConfirmPassword;
    private MaterialButton btnRegister;

    private TextView tvLoginLink;

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

        //validation logic here...
    }
}
