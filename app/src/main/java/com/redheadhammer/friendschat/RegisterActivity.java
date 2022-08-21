package com.redheadhammer.friendschat;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.redheadhammer.friendschat.databinding.ActivityRegisterBinding;

public class RegisterActivity extends AppCompatActivity {

    ActivityRegisterBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityRegisterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


    }
}