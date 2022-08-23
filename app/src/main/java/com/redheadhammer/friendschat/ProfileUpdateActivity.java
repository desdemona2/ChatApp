package com.redheadhammer.friendschat;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.redheadhammer.friendschat.databinding.ActivityProfileUpdateBinding;

public class ProfileUpdateActivity extends AppCompatActivity {

    ActivityProfileUpdateBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityProfileUpdateBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
    }
}