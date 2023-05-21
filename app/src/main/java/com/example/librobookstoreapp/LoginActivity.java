package com.example.librobookstoreapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {
    EditText username, password;
    Button loginButton;
    TextView signup_link;
    DatabaseHelper db;
    FirebaseAuth fAuth;
    ProgressBar progressBar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        username = findViewById(R.id.username);
        password = findViewById(R.id.password);
        loginButton = findViewById(R.id.loginButton);
        signup_link = findViewById(R.id.signup_link);
        progressBar = findViewById(R.id.progressBar);
        db = new DatabaseHelper(this);
        fAuth = FirebaseAuth.getInstance();

        loginButton.setOnClickListener(v -> userLogin());

        signup_link.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });
    }

    private void userLogin() {
        String user = username.getText().toString().trim();
        String pass = password.getText().toString().trim();
        progressBar.setVisibility(View.VISIBLE);

        if(user.isEmpty()) {
            username.setError("Please enter username");
            username.requestFocus();
            return;
        }
        if(pass.isEmpty()) {
            password.setError("Please enter password");
            password.requestFocus();
            return;
        }
        if(!Patterns.EMAIL_ADDRESS.matcher(user).matches()) {
            username.setError("Please enter valid email");
            username.requestFocus();
            return;
        }
        if(pass.length() < 6) {
            password.setError("Password must be at least 6 characters");
            password.requestFocus();
            return;
        }

        Boolean result = db.checkUsernamePassword(user, pass);
        fAuth.signInWithEmailAndPassword(user, pass).addOnCompleteListener(task -> {
            if(task.isSuccessful() && result == true) {
                Toast.makeText(LoginActivity.this, "Login Successful", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(intent);
            } else {
                Toast.makeText(LoginActivity.this, "Invalid Credentials, Please Signup", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(LoginActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });
    }
}
