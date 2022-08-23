package com.redheadhammer.friendschat;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;
import com.redheadhammer.friendschat.databinding.ActivityLoginBinding;

import java.util.Objects;

public class LoginActivity extends AppCompatActivity {

    private ActivityLoginBinding binding;
    private FirebaseAuth firebaseAuth;
    private static final String TAG = "LoginActivity";
    private FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        firebaseAuth = FirebaseAuth.getInstance();

        binding.signUp.setOnClickListener(this::signUp);
        binding.forgot.setOnClickListener(this::forgotPass);
        binding.signIn.setOnClickListener(this::signIn);
    }

    private void signIn(View view) {
        String username = String.valueOf(binding.userName.getText());
        String password = String.valueOf(binding.password.getText());

        if (username.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, R.string.empty_upass,
                    Toast.LENGTH_SHORT).show();
        } else {
            signInProcess(username, password);
        }
    }

    private void signUp(View view) {
        Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
        startActivity(intent);
    }

    private void forgotPass(View view) {
        Intent intent = new Intent(LoginActivity.this, ForgotActivity.class);
        startActivity(intent);
    }

    private void signInProcess(String username, String password) {
        firebaseAuth.signInWithEmailAndPassword(username, password)
                .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                startActivity(intent);
                                finish();
                            } else {
                                try {
                                    throw Objects.requireNonNull(task.getException());
                                } catch (FirebaseAuthInvalidUserException e) {
                                    Toast.makeText(LoginActivity.this, R.string.not_registered,
                                            Toast.LENGTH_SHORT).show();
                                } catch (FirebaseAuthInvalidCredentialsException invalid) {
                                    Toast.makeText(LoginActivity.this,
                                            R.string.email_bad_format, Toast.LENGTH_LONG).show();
                                } catch (Exception exception) {
                                    Log.d(TAG, "onComplete: " + exception);
                                    Toast.makeText(LoginActivity.this,
                                            R.string.error_occurred, Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                );
    }

    @Override
    protected void onStart() {
        super.onStart();
        user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }
    }
}