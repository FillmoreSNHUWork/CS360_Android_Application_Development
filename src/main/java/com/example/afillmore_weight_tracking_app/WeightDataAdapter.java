package com.example.afillmore_weight_tracking_app;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

/**
 * Adapter for displaying weight data in a RecyclerView.
 * This adapter binds weight data to views that are displayed within a RecyclerView, managing the
 * display of weight records including their dates and weights. It also handles user interactions
 * for editing and deleting individual weight records via a defined OnItemClickListener interface.
 */
public class WeightDataAdapter extends RecyclerView.Adapter<WeightDataAdapter.ViewHolder> {

    private List<WeightData> weightDataList;
    private Context context;
    private OnItemClickListener listener;

    /**
     * Interface to handle click events on items within the data adapter.
     * Provides methods for responding to edit and delete actions.
     */
    public interface OnItemClickListener {
        void onEditClicked(int position);
        void onDeleteClicked(int position);
    }

    /**
     * Constructs a new WeightDataAdapter.
     * @param weightDataList The list of weight data objects that this adapter will manage.
     * @param context The current context. Used to inflate the layout file.
     * @param listener The listener that handles edit and delete click events.
     */
    public WeightDataAdapter(List<WeightData> weightDataList, Context context, OnItemClickListener listener) {
        this.weightDataList = weightDataList;
        this.context = context;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.weight_data, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        WeightData data = weightDataList.get(position);
        holder.dateTextView.setText(data.getDate());
        holder.weightTextView.setText(String.valueOf(data.getWeight()));
        holder.editButton.setOnClickListener(v -> listener.onEditClicked(position));
        holder.deleteButton.setOnClickListener(v -> listener.onDeleteClicked(position));
    }

    @Override
    public int getItemCount() {
        return weightDataList.size();
    }

    /**
     * ViewHolder for weight data items. Holds references to the individual views within the list item layout.
     */
    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView dateTextView, weightTextView;
        Button editButton, deleteButton;

        /**
         * Constructs a ViewHolder for the given list item view.
         * @param itemView The view representing the list item.
         */
        ViewHolder(View itemView) {
            super(itemView);
            dateTextView = itemView.findViewById(R.id.dateTextView);
            weightTextView = itemView.findViewById(R.id.weightTextView);
            editButton = itemView.findViewById(R.id.editButton);
            deleteButton = itemView.findViewById(R.id.deleteButton);
        }
    }
}

