package com.applovin.me.floatview;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.applovin.me.floatview.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        onClick();
    }

    private void onClick() {
        binding.showBall.setOnClickListener(v -> {

        });
        binding.hideBall.setOnClickListener(v -> {

        });
    }


}