package com.example.mealplannerapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.mealplannerapp.R;
import com.google.android.gms.auth.api.signin.*;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.*;

public class LoginActivity extends AppCompatActivity {

    private static final int RC_SIGN_IN = 100;

    private FirebaseAuth mAuth;
    private GoogleSignInClient googleSignInClient;

    private EditText emailInput, passwordInput;
    private Button loginBtn, signupBtn, googleBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Firebase
        mAuth = FirebaseAuth.getInstance();
        if (mAuth.getCurrentUser() != null) {
            startActivity(new Intent(LoginActivity.this, HomeActivity.class));
            finish();
            return;
        }
        // Views
        emailInput = findViewById(R.id.email);
        passwordInput = findViewById(R.id.password);

        loginBtn = findViewById(R.id.loginBtn);
        signupBtn = findViewById(R.id.signupBtn);
        googleBtn = findViewById(R.id.signInWithGoogleBtn);

        // ================= GOOGLE CONFIG =================
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        googleSignInClient = GoogleSignIn.getClient(this, gso);

        // ================= EMAIL LOGIN =================
        loginBtn.setOnClickListener(v -> {

            String email = emailInput.getText().toString().trim();
            String password = passwordInput.getText().toString().trim();

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Enter email & password", Toast.LENGTH_SHORT).show();
                return;
            }

            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(task -> {

                        if (task.isSuccessful()) {
                            Toast.makeText(this, "Login Successful", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(LoginActivity.this, HomeActivity.class));
                            finish();
                        } else {
                            Toast.makeText(this, "Login Failed: " +
                                    task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        });

        // ================= SIGNUP =================
        signupBtn.setOnClickListener(v -> {
            startActivity(new Intent(LoginActivity.this, SignupActivity.class));
        });

        // ================= GOOGLE LOGIN =================
        googleBtn.setOnClickListener(v -> {
            Intent intent = googleSignInClient.getSignInIntent();
            startActivityForResult(intent, RC_SIGN_IN);
        });
    }

    // ================= GOOGLE RESULT =================
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task =
                    GoogleSignIn.getSignedInAccountFromIntent(data);

            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account.getIdToken());
            } catch (ApiException e) {
                Toast.makeText(this, "Google Sign-In Failed", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // ================= FIREBASE GOOGLE AUTH =================
    private void firebaseAuthWithGoogle(String idToken) {

        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);

        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {

                    if (task.isSuccessful()) {
                        Toast.makeText(this, "Login Successful", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(LoginActivity.this, MainActivity.class));
                        finish();
                    } else {
                        Toast.makeText(this, "Auth Failed", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}