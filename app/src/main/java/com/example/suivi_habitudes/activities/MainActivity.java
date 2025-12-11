
package com.example.suivi_habitudes.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.suivi_habitudes.R;
import com.example.suivi_habitudes.adapters.HabitAdapter;
import com.example.suivi_habitudes.api.ApiClient;
import com.example.suivi_habitudes.models.Habit;
import com.example.suivi_habitudes.models.DashboardResponse;
import com.example.suivi_habitudes.models.ApiResponse;
import android.widget.LinearLayout;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements HabitAdapter.OnHabitListener {

    private RecyclerView recyclerView;
    private HabitAdapter habitAdapter;
    private List<Habit> habitList = new ArrayList<>();
    private ImageButton buttonAdd;
    private ImageButton buttonFilter;
    private ImageButton buttonRefresh;
    private TextView toolbarTitle;
    private TextView filterIndicator;
    private LinearLayout emptyStateLayout;
    private BottomNavigationView bottomNavigation;
    private int userId;
    private String currentFilter = "today";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE);
        userId = sharedPreferences.getInt("user_id", -1);
        String userName = sharedPreferences.getString("user_name", "");

        if (userId == -1) {
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
            return;
        }

        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.recyclerView);
        emptyStateLayout = findViewById(R.id.emptyStateLayout);
        toolbarTitle = findViewById(R.id.toolbarTitle);
        filterIndicator = findViewById(R.id.filterIndicator);
        buttonAdd = findViewById(R.id.buttonAdd);
        buttonFilter = findViewById(R.id.buttonFilter);
        buttonRefresh = findViewById(R.id.buttonRefresh);
        bottomNavigation = findViewById(R.id.bottomNavigation);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));


        if (!userName.isEmpty()) {
            toolbarTitle.setText("Bienvenue " + userName + "!");
        }

        buttonAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, AddHabitActivity.class);
                startActivity(intent);
            }
        });

        buttonFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showFilterDialog();
            }
        });

        buttonRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Toast.makeText(MainActivity.this, "Actualisation...", Toast.LENGTH_SHORT).show();
                fetchHabits(userId, currentFilter);
            }
        });


        bottomNavigation.setSelectedItemId(R.id.nav_home);
        bottomNavigation.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_home) {

                return true;
            } else if (itemId == R.id.nav_statistics) {

                Intent intent = new Intent(MainActivity.this, StatisticsActivity.class);
                startActivity(intent);
                finish();
                return true;
            } else if (itemId == R.id.nav_settings) {

                Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
                startActivity(intent);
                return false;
            }
            return false;
        });


        habitAdapter = new HabitAdapter(habitList, this);
        recyclerView.setAdapter(habitAdapter);


    }

    @Override
    protected void onResume() {
        super.onResume();
        if (userId != -1) {
            fetchHabits(userId, currentFilter);
        }
    }

    private void fetchHabits(int userId, String filter) {
        Call<DashboardResponse> call = ApiClient.getApiService().getDashboardWithFilter(userId, filter);
        call.enqueue(new Callback<DashboardResponse>() {
            @Override
            public void onResponse(Call<DashboardResponse> call, Response<DashboardResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    DashboardResponse dashboardResponse = response.body();
                    if (dashboardResponse.isSuccess() && dashboardResponse.getHabits() != null) {
                        habitList.clear();
                        habitList.addAll(dashboardResponse.getHabits());
                        
                        if (habitList.isEmpty()) {
                            emptyStateLayout.setVisibility(View.VISIBLE);
                            recyclerView.setVisibility(View.GONE);
                        } else {
                            emptyStateLayout.setVisibility(View.GONE);
                            recyclerView.setVisibility(View.VISIBLE);
                        }
                        

                        habitAdapter.notifyDataSetChanged();
                        

                        displayProgressStats(dashboardResponse);


                        
                    } else {
                        Toast.makeText(MainActivity.this, dashboardResponse.getMessage() != null ? dashboardResponse.getMessage() : "Échec de la récupération des habitudes", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(MainActivity.this, "Erreur de connexion", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<DashboardResponse> call, Throwable t) {
                Toast.makeText(MainActivity.this, "Erreur: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onEditClick(int position) {
        Habit habit = habitList.get(position);
        Intent intent = new Intent(MainActivity.this, AddHabitActivity.class);
        intent.putExtra("habit_id", habit.getId());
        intent.putExtra("habit_title", habit.getTitle());
        intent.putExtra("habit_description", habit.getDescription());
        intent.putExtra("habit_category", habit.getCategory());
        
        
        intent.putExtra("habit_monday", habit.isMonday());
        intent.putExtra("habit_tuesday", habit.isTuesday());
        intent.putExtra("habit_wednesday", habit.isWednesday());
        intent.putExtra("habit_thursday", habit.isThursday());
        intent.putExtra("habit_friday", habit.isFriday());
        intent.putExtra("habit_saturday", habit.isSaturday());
        intent.putExtra("habit_sunday", habit.isSunday());
        
        startActivity(intent);
    }

    @Override
    public void onDeleteClick(int position) {
        showDeleteConfirmationDialog(position);
    }

    private void showDeleteConfirmationDialog(final int position) {
        new AlertDialog.Builder(this)
                .setTitle("Supprimer l'habitude")
                .setMessage(getString(R.string.delete_confirmation))
                .setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        deleteHabit(position);
                    }
                })
                .setNegativeButton(getString(R.string.no), null)
                .show();
    }

    private void deleteHabit(final int position) {
        int habitId = habitList.get(position).getId();
        Call<ApiResponse> call = ApiClient.getApiService().deleteHabit(habitId);
        call.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse apiResponse = response.body();
                    if (apiResponse.isSuccess()) {
                        Toast.makeText(MainActivity.this, getString(R.string.habit_deleted), Toast.LENGTH_SHORT).show();
                        habitList.remove(position);
                        habitAdapter.notifyItemRemoved(position);
                        habitAdapter.notifyItemRangeChanged(position, habitList.size());
                    } else {
                        Toast.makeText(MainActivity.this, apiResponse.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(MainActivity.this, "Erreur de connexion", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                Toast.makeText(MainActivity.this, "Erreur: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showFilterDialog() {
        String[] filterOptions = {
            "Aujourd'hui", 
            "Tous les habitudes", 
            "Lundi", 
            "Mardi", 
            "Mercredi", 
            "Jeudi", 
            "Vendredi", 
            "Samedi", 
            "Dimanche"
        };
        
        String[] filterValues = {
            "today", 
            "all", 
            "monday", 
            "tuesday", 
            "wednesday", 
            "thursday", 
            "friday", 
            "saturday", 
            "sunday"
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Filtrer les habitudes");
        builder.setItems(filterOptions, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                currentFilter = filterValues[which];
                updateFilterIndicator(filterOptions[which]);
                fetchHabits(userId, currentFilter);
            }
        });
        builder.show();
    }

    private void updateFilterIndicator(String filterName) {
        if ("Aujourd'hui".equals(filterName)) {
            
            filterIndicator.setVisibility(View.GONE);
        } else {
            
            filterIndicator.setText("📅 " + filterName);
            filterIndicator.setVisibility(View.VISIBLE);
        }
    }
    
    private void displayProgressStats(DashboardResponse dashboardResponse) {
        if (dashboardResponse.getTodayProgress() != null) {
            DashboardResponse.TodayProgress progress = dashboardResponse.getTodayProgress();

            if (progress.getTotalDueToday() > 0) {
                String progressMessage = String.format("Aujourd'hui: %d/%d habitudes terminées (%.1f%%)", 
                    progress.getCompletedToday(), 
                    progress.getTotalDueToday(), 
                    progress.getCompletionRate());

            }
        }
    }
}