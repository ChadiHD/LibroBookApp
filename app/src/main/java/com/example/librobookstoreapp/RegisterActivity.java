package com.example.librobookstoreapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.w3c.dom.Text;

public class RegisterActivity extends AppCompatActivity {

    EditText username, password, confirmPassword;
    Button registerButton;
    TextView login_link;
    DatabaseHelper db;
    FirebaseAuth fAuth;
    ProgressBar progressBar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        username = findViewById(R.id.username);
        password = findViewById(R.id.password);
        confirmPassword = findViewById(R.id.confirmPassword);
        registerButton = findViewById(R.id.register_button);
        login_link = findViewById(R.id.login_link);
        progressBar = findViewById(R.id.progressBar);
        fAuth = FirebaseAuth.getInstance();
        db = new DatabaseHelper(this);

        registerButton.setOnClickListener(v -> {
            progressBar.setVisibility(View.VISIBLE);
            String user = username.getText().toString().trim();
            String pass = password.getText().toString().trim();
            String confirmPass = confirmPassword.getText().toString().trim();

            ValidateForm(user, pass, confirmPass);
        });

        login_link.setOnClickListener(v -> {
            Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
            startActivity(intent);
        });
    }

    private void ValidateForm(String user, String pass, String confirmPass){
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
        if(confirmPass.isEmpty()) {
            confirmPassword.setError("Please confirm password");
            confirmPassword.requestFocus();
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
        if (pass.equals(confirmPass)) {
            Boolean checkUser = db.checkUsername(user);
            if (checkUser == false) {
            Boolean insert = db.addUser(user, pass);
            fAuth.createUserWithEmailAndPassword(user, pass)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        progressBar.setVisibility(View.GONE);
                        if (task.isSuccessful() && insert) {
                            // Sign in success
                            Toast.makeText(RegisterActivity.this, "Registered Successfully", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                            startActivity(intent);

                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(RegisterActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            } else {
                Toast.makeText(RegisterActivity.this, "User already exists! Please login", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(RegisterActivity.this, "Password is not matching", Toast.LENGTH_SHORT).show();
        }
    }
}
