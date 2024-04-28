package com.example.afillmore_weight_tracking_app;
import android.content.Intent;
import android.util.Log;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

/**
 * Activity for displaying user's weight records and managing interactions such as editing and deleting weights,
 * and setting goal weights. It interfaces with UserDbHelper to perform database operations and updates the
 * UI dynamically based on user interactions and permissions granted for sending SMS.
 */
public class WeightDisplayActivity extends AppCompatActivity implements WeightDataAdapter.OnItemClickListener {
    private RecyclerView recyclerView;
    private WeightDataAdapter adapter;
    private List<WeightData> weightDataList = new ArrayList<>();
    private static final int SMS_PERMISSION_REQUEST_CODE = 101; // Request code for SMS permission
    private UserDbHelper dbHelper;
    private int userId; // Class member to store user ID

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_display);
        // instantiate dbhelper
        dbHelper = new UserDbHelper(this);

        userId = getIntent().getIntExtra("user_id", -1);
        if (userId == -1) {
            // Handle error
            Toast.makeText(this, "Invalid user ID", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        weightDataList = dbHelper.getLastTenWeights(userId);

        recyclerView = findViewById(R.id.weightDataRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new WeightDataAdapter(weightDataList, this, this);
        recyclerView.setAdapter(adapter);

        // Button setup - Adding a goal weight and a regular weight entry
        Button addGoalWeightButton = findViewById(R.id.addGoalWeightButton);
        addGoalWeightButton.setOnClickListener(v -> {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.SEND_SMS}, SMS_PERMISSION_REQUEST_CODE);
            } else {
                showAddGoalWeightDialog();
            }
        });

        Button addWeightButton = findViewById(R.id.addWeightButton);
        addWeightButton.setOnClickListener(v -> {
            Intent intent = new Intent(WeightDisplayActivity.this, WeightDataEntryActivity.class);
            intent.putExtra("user_id", userId);
            startActivity(intent);
        });

        Button logoutButton = findViewById(R.id.logoutButton);
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle logout here
                logout();
            }
        });
    }

    @Override
    public void onEditClicked(int position) {
        WeightData weightData = weightDataList.get(position);
        showEditWeightDialog(weightData, position);
    }

    /**
     * Displays a dialog for the user to edit the selected weight record.
     * @param weightData The weight data to be edited.
     * @param position The position in the adapter of the weight data.
     */
    private void showEditWeightDialog(WeightData weightData, int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Edit Weight");

        // Set up the input
        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        input.setText(String.valueOf(weightData.getWeight()));
        builder.setView(input);

        // Set up the buttons
        builder.setPositiveButton("Update", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                float newWeight;
                try {
                    newWeight = Float.parseFloat(input.getText().toString());
                } catch (NumberFormatException e) {
                    Toast.makeText(WeightDisplayActivity.this, "Invalid number format", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Update the weight in the database
                if (dbHelper.updateWeight(weightData.getId(), newWeight)) {
                    // Refresh the entire list from the database
                    refreshWeightDataList();
                } else {
                    Toast.makeText(WeightDisplayActivity.this, "Failed to update weight.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
        builder.show();
    }

    /**
     * Takes user to main activity page
     */
    private void logout() {
        Intent intent = new Intent(WeightDisplayActivity.this, MainActivity.class);
        startActivity(intent);
        finish(); //
    }

    /**
     * Refreshes the data in the RecyclerView by re-fetching from the database.
     */
    private void refreshWeightDataList() {
        weightDataList = dbHelper.getLastTenWeights(userId);
        adapter = new WeightDataAdapter(weightDataList, this, this);
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    public void onDeleteClicked(int position) {
        WeightData weightData = weightDataList.get(position);
        new AlertDialog.Builder(this)
                .setTitle("Confirm Delete")
                .setMessage("Are you sure you want to delete this weight record?")
                .setPositiveButton("Delete", (dialog, which) -> {
                    if (dbHelper.deleteWeight(weightData.getId())) {
                        weightDataList.remove(position);
                        adapter.notifyItemRemoved(position);
                        Toast.makeText(this, "Weight deleted successfully", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(this, "Failed to delete weight", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == SMS_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                showAddGoalWeightDialog();
            } else {
                Toast.makeText(this, "SMS permission denied. Goal notifications will not be sent via SMS.", Toast.LENGTH_LONG).show();
            }
        }
    }

    /**
     * Displays a dialog for setting a new goal weight.
     */
    private void showAddGoalWeightDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Set Goal Weight");

        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        builder.setView(input);

        builder.setPositiveButton("Set", (dialog, which) -> {
            try {
                float goalWeight = Float.parseFloat(input.getText().toString());
                boolean success = dbHelper.setGoalWeight(userId, goalWeight);
                if (success) {
                    Toast.makeText(this, "Goal weight set successfully!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Failed to set goal weight.", Toast.LENGTH_SHORT).show();
                }
            } catch (NumberFormatException e) {
                Toast.makeText(this, "Invalid weight entered.", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
        builder.show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (dbHelper != null) {
            weightDataList = dbHelper.getLastTenWeights(userId);
            adapter = new WeightDataAdapter(weightDataList, this, this);
            recyclerView.setAdapter(adapter);
        }
    }
}
