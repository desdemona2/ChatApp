package com.redheadhammer.friendschat;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.redheadhammer.friendschat.databinding.ActivityForgotBinding;

import java.util.Objects;

public class ForgotActivity extends AppCompatActivity {

    private static final String TAG = "ForgotActivity";
    private ActivityForgotBinding binding;
    private final FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityForgotBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.submit.setOnClickListener(this::forgotPass);
    }

    private void forgotPass(View view) {
        String email = String.valueOf(binding.emailForgot.getText());
        if (email.isEmpty()) {
            Toast.makeText(this,
                    "Enter an Email Address", Toast.LENGTH_SHORT).show();
        } else {
            forgotPassProcess(email);
            binding.progressBar.setVisibility(View.VISIBLE);
        }
    }

    private void forgotPassProcess(String email) {
        firebaseAuth.sendPasswordResetEmail(email)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(this,
                                R.string.forgot_email_sent, Toast.LENGTH_SHORT).show();
                    } else {
                        try {
                            throw Objects.requireNonNull(task.getException());
                        } catch (FirebaseAuthInvalidCredentialsException e) {
                            Toast.makeText(this,
                                    R.string.email_bad_format, Toast.LENGTH_SHORT).show();
                        } catch (FirebaseAuthInvalidUserException e) {
                            Toast.makeText(this, R.string.not_registered,
                                    Toast.LENGTH_SHORT).show();
                        } catch (Exception e) {
                            Log.d(TAG, "onComplete: " + e);
                            Toast.makeText(this,
                                    R.string.error_occurred, Toast.LENGTH_SHORT).show();
                        }
                    }
                    binding.progressBar.setVisibility(View.GONE);
                });
    }
}