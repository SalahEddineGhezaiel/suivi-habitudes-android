package com.example.suivi_habitudes.adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.suivi_habitudes.R;
import com.example.suivi_habitudes.models.Habit;
import com.example.suivi_habitudes.api.ApiClient;
import com.example.suivi_habitudes.models.ApiResponse;
import com.example.suivi_habitudes.models.HabitCheckResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HabitAdapter extends RecyclerView.Adapter<HabitAdapter.HabitViewHolder> {

    private List<Habit> habitList;
    private OnHabitListener onHabitListener;
    private Context context;

    public HabitAdapter(List<Habit> habitList, OnHabitListener onHabitListener) {
        this.habitList = habitList;
        this.onHabitListener = onHabitListener;
    }

    @NonNull
    @Override
    public HabitViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.habit_item_layout, parent, false);
        return new HabitViewHolder(view, onHabitListener);
    }

    @Override
    public void onBindViewHolder(@NonNull HabitViewHolder holder, int position) {
        Habit habit = habitList.get(position);
        context = holder.itemView.getContext();
        
        holder.textViewTitle.setText(habit.getTitle());
        holder.textViewDescription.setText(habit.getDescription());
        

        if (habit.getCategory() != null && !habit.getCategory().trim().isEmpty()) {
            holder.textViewCategory.setText(habit.getCategory());
            holder.textViewCategory.setVisibility(View.VISIBLE);
        } else {
            holder.textViewCategory.setVisibility(View.GONE);
        }


        holder.textViewStreak.setText("🔥 Série: " + getHabitStreak(habit));
        holder.textViewTotal.setText("⭐ Total: " + habit.getTotalCompletions());
        

        holder.textViewDays.setText("📅 " + getSelectedDaysText(habit));


        boolean isCompletedToday = getHabitCompletionStatus(habit);
        holder.updateCheckButton(isCompletedToday);
        

        holder.currentHabit = habit;
    }
    
    private int getHabitStreak(Habit habit) {

        return habit.getCurrentStreak();
    }
    
    private boolean getHabitCompletionStatus(Habit habit) {

        return habit.isCompletedToday();
    }
    
    private String getSelectedDaysText(Habit habit) {
        java.util.List<String> selectedDays = new java.util.ArrayList<>();
        
        if (habit.isMonday()) selectedDays.add("Lun");
        if (habit.isTuesday()) selectedDays.add("Mar");
        if (habit.isWednesday()) selectedDays.add("Mer");
        if (habit.isThursday()) selectedDays.add("Jeu");
        if (habit.isFriday()) selectedDays.add("Ven");
        if (habit.isSaturday()) selectedDays.add("Sam");
        if (habit.isSunday()) selectedDays.add("Dim");
        
        if (selectedDays.isEmpty()) {
            return "Aucun jour sélectionné";
        } else if (selectedDays.size() == 7) {
            return "Tous les jours";
        } else {
            return String.join(", ", selectedDays);
        }
    }

    @Override
    public int getItemCount() {
        return habitList.size();
    }

    public static class HabitViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView textViewTitle, textViewDescription, textViewCategory;
        TextView textViewStreak, textViewTotal, textViewDays;
        ImageView buttonEdit, buttonDelete;
        ImageView buttonCheck;
        OnHabitListener onHabitListener;
        Habit currentHabit;
        private boolean isChecked = false;

        public HabitViewHolder(@NonNull View itemView, OnHabitListener onHabitListener) {
            super(itemView);
            textViewTitle = itemView.findViewById(R.id.textViewTitle);
            textViewDescription = itemView.findViewById(R.id.textViewDescription);
            textViewCategory = itemView.findViewById(R.id.textViewCategory);
            textViewStreak = itemView.findViewById(R.id.textViewStreak);
            textViewTotal = itemView.findViewById(R.id.textViewTotal);
            textViewDays = itemView.findViewById(R.id.textViewDays);
            buttonEdit = itemView.findViewById(R.id.buttonEdit);
            buttonDelete = itemView.findViewById(R.id.buttonDelete);
            buttonCheck = itemView.findViewById(R.id.buttonCheck);
            this.onHabitListener = onHabitListener;

            buttonEdit.setOnClickListener(this);
            buttonDelete.setOnClickListener(this);
            buttonCheck.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (v.getId() == R.id.buttonEdit) {
                onHabitListener.onEditClick(getAdapterPosition());
            } else if (v.getId() == R.id.buttonDelete) {
                onHabitListener.onDeleteClick(getAdapterPosition());
            } else if (v.getId() == R.id.buttonCheck) {
                toggleHabitCheck();
            }
        }

        public void updateCheckButton(boolean isCompleted) {
            isChecked = isCompleted;
            if (isCompleted) {
                buttonCheck.setImageResource(R.drawable.ic_check_circle_24);
                buttonCheck.setBackground(itemView.getContext().getDrawable(R.drawable.circle_background));
                buttonCheck.setBackgroundTintList(ColorStateList.valueOf(itemView.getContext().getResources().getColor(R.color.accent_green)));
            } else {
                buttonCheck.setImageDrawable(null);
                buttonCheck.setBackground(itemView.getContext().getDrawable(R.drawable.white_circle_with_border));
                buttonCheck.setBackgroundTintList(null);
            }
        }

        private void toggleHabitCheck() {
            if (currentHabit == null) return;
            
            SharedPreferences sharedPreferences = itemView.getContext().getSharedPreferences("user_prefs", Context.MODE_PRIVATE);
            int userId = sharedPreferences.getInt("user_id", -1);
            
            boolean newCompletionStatus = !isChecked;
            

            updateCheckButton(newCompletionStatus);
            currentHabit.setCompletedToday(newCompletionStatus);
            

            Call<HabitCheckResponse> call = ApiClient.getApiService().checkHabit(userId, currentHabit.getId(), newCompletionStatus);
            call.enqueue(new Callback<HabitCheckResponse>() {
                @Override
                public void onResponse(Call<HabitCheckResponse> call, Response<HabitCheckResponse> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        HabitCheckResponse checkResponse = response.body();
                        if (checkResponse.isSuccess()) {

                            currentHabit.setTotalCompletions(checkResponse.getTotalCompletions());
                            currentHabit.setCurrentStreak(checkResponse.getCurrentStreak());
                            

                            textViewStreak.setText("🔥 Série: " + checkResponse.getCurrentStreak());
                            textViewTotal.setText("⭐ Total: " + checkResponse.getTotalCompletions());
                            
                            Toast.makeText(itemView.getContext(), 
                                newCompletionStatus ? "Habitude marquée comme terminée!" : "Habitude démarquée", 
                                Toast.LENGTH_SHORT).show();
                        } else {

                            updateCheckButton(!newCompletionStatus);
                            currentHabit.setCompletedToday(!newCompletionStatus);
                            
                            Toast.makeText(itemView.getContext(), checkResponse.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    } else {

                        updateCheckButton(!newCompletionStatus);
                        currentHabit.setCompletedToday(!newCompletionStatus);
                        
                        Toast.makeText(itemView.getContext(), "Erreur de connexion", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<HabitCheckResponse> call, Throwable t) {

                    updateCheckButton(!newCompletionStatus);
                    currentHabit.setCompletedToday(!newCompletionStatus);
                    
                    Toast.makeText(itemView.getContext(), "Erreur réseau", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    public interface OnHabitListener {
        void onEditClick(int position);
        void onDeleteClick(int position);
    }
}
