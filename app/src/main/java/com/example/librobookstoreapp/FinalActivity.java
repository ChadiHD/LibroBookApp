package com.example.librobookstoreapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import com.example.librobookstoreapp.R;

public class FinalActivity extends AppCompatActivity {
    Button back_button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_final);

        back_button = findViewById(R.id.back_button);

        back_button.setOnClickListener(v -> {
            Intent intent = new Intent(FinalActivity.this, MainActivity.class);
            startActivity(intent);
        });
    }
}