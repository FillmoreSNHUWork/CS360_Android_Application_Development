package com.example.afillmore_weight_tracking_app;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

/**
 * MainActivity handles the user interface for user authentication including login and account creation.
 * It provides fields for entering username and password, and buttons to submit the login credentials or to create a new account.
 * This activity interacts with the UserDbHelper to authenticate users against the database and to add new user records.
 */
public class MainActivity extends AppCompatActivity {

    private EditText usernameEditText;
    private EditText passwordEditText;
    private Button loginButton;
    private Button createAccountButton;
    private UserDbHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize UI components
        usernameEditText = findViewById(R.id.usernameEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        loginButton = findViewById(R.id.loginButton);
        createAccountButton = findViewById(R.id.createAccountButton);
        db = new UserDbHelper(this); // Initialize database helper

        // Handle login button click
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = usernameEditText.getText().toString().trim();
                String password = passwordEditText.getText().toString().trim();

                if (username.isEmpty() || password.isEmpty()) {
                    Toast.makeText(MainActivity.this, "Please enter both username and password", Toast.LENGTH_SHORT).show();
                } else {
                    if (db.checkUser(username, password)) {
                        int userId = db.getUserId(username);
                        if (userId != -1) {
                            Toast.makeText(MainActivity.this, "Login successful!", Toast.LENGTH_SHORT).show();
                            // getting here for sure.....

                            Intent intent = new Intent(MainActivity.this, WeightDisplayActivity.class);
                            intent.putExtra("user_id", userId);
                            startActivity(intent);
                        } else {
                            Toast.makeText(MainActivity.this, "Failed to retrieve user details.", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(MainActivity.this, "Invalid credentials", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        // Handle create account button click
        createAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newUsername = usernameEditText.getText().toString().trim();
                String newPassword = passwordEditText.getText().toString().trim();

                if (newUsername.isEmpty() || newPassword.isEmpty()) {
                    Toast.makeText(MainActivity.this, "Please enter both username and password", Toast.LENGTH_SHORT).show();
                } else {
                    if (db.checkUser(newUsername, "")) {
                        Toast.makeText(MainActivity.this, "User already exists. Please login or choose a different username.", Toast.LENGTH_SHORT).show();
                    } else {
                        boolean isAdded = db.addUser(newUsername, newPassword);
                        if (isAdded) {
                            Toast.makeText(MainActivity.this, "Account created successfully!", Toast.LENGTH_SHORT).show();
                            // Optionally reset fields or navigate to login
                            usernameEditText.setText("");
                            passwordEditText.setText("");
                        } else {
                            Toast.makeText(MainActivity.this, "Failed to create account", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }
        });
    }
}
