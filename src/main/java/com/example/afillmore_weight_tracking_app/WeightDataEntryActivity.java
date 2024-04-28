package com.example.afillmore_weight_tracking_app;

import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.content.Intent;

import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Activity for entering and submitting new weight data.
 * This activity provides a user interface for users to enter their weight, which is then saved to the database.
 * It includes input validation and redirects back to the weight display activity upon successful submission.
 */
public class WeightDataEntryActivity extends AppCompatActivity {

    private EditText weightEditText;
    private Button submitButton;

    private Button cancelButton;
    private UserDbHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weight_data_entry);

        dbHelper = new UserDbHelper(this);
        weightEditText = findViewById(R.id.weightEditText);
        submitButton = findViewById(R.id.submitButton);
        cancelButton = findViewById(R.id.cancelButton);

        final int userId = getIntent().getIntExtra("user_id", -1);

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String weightStr = weightEditText.getText().toString();
                if (!weightStr.isEmpty()) {
                    float weight = Float.parseFloat(weightStr);
                    String date = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
                    boolean success = dbHelper.addWeight(userId, date, weight);
                    if (success) {
                        Toast.makeText(WeightDataEntryActivity.this, "Weight added successfully", Toast.LENGTH_SHORT).show();
                        checkGoalWeight(userId, weight);
                        Intent intent = new Intent(WeightDataEntryActivity.this, WeightDisplayActivity.class);
                        intent.putExtra("user_id", userId);
                        startActivity(intent);
                        finish(); // Finish this activity
                    } else {
                        Toast.makeText(WeightDataEntryActivity.this, "Failed to add weight", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(WeightDataEntryActivity.this, "Please enter a weight", Toast.LENGTH_SHORT).show();
                }
            }
        });
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish(); // Just close this activity and go back
            }
        });
    }

    /*
    Method to check goal weight after adding a weight - invokes send SMS if the user reached their goal
     */
    private void checkGoalWeight(int userId, float weight) {
        float goalWeight = dbHelper.getGoalWeight(userId);
        if (goalWeight == weight) {
            sendCongratulatorySMS();
        }
    }

    /*
    Method to send a congratulations text when a user reaches goal weight
     */
    private void sendCongratulatorySMS() {
        SmsManager smsManager = SmsManager.getDefault();
        smsManager.sendTextMessage("123456789", null, "Congratulations! You've reached your goal weight!", null, null);
        Toast.makeText(this, "Goal weight achieved! Congratulations message sent.", Toast.LENGTH_LONG).show();
    }
}


