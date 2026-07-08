package com.example.mealplannerapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.mealplannerapp.R;
import com.google.firebase.auth.FirebaseAuth;

public class SignupActivity extends AppCompatActivity {

    EditText email, password, confirmPassword;
    Button signupBtn;
    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        auth = FirebaseAuth.getInstance();

        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        confirmPassword = findViewById(R.id.confirmPassword);
        signupBtn = findViewById(R.id.signupBtn);

        signupBtn.setOnClickListener(v -> {

            String emailText = email.getText().toString().trim();
            String passwordText = password.getText().toString().trim();
            String confirmText = confirmPassword.getText().toString().trim();

            // ✅ Validation
            if (emailText.isEmpty()) {
                email.setError("Email required");
                return;
            }

            if (passwordText.isEmpty()) {
                password.setError("Password required");
                return;
            }

            if (!passwordText.equals(confirmText)) {
                confirmPassword.setError("Passwords do not match");
                return;
            }

            if (passwordText.length() < 6) {
                password.setError("Min 6 characters required");
                return;
            }

            // ✅ Firebase Signup
            auth.createUserWithEmailAndPassword(emailText, passwordText)
                    .addOnSuccessListener(authResult -> {
                        Toast.makeText(this, "Account Created!", Toast.LENGTH_SHORT).show();

                        // Go to MainActivity
                        startActivity(new Intent(SignupActivity.this, MainActivity.class));
                        finish();
                    })
                    .addOnFailureListener(e ->
                            Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show()
                    );
        });
    }
}