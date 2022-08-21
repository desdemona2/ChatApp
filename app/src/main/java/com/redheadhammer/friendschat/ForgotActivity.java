package com.redheadhammer.friendschat;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.redheadhammer.friendschat.databinding.ActivityForgotBinding;

public class ForgotActivity extends AppCompatActivity {

    ActivityForgotBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityForgotBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


    }
}