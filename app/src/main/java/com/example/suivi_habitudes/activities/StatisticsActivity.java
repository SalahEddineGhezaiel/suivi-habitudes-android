package com.example.suivi_habitudes.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.suivi_habitudes.R;
import com.example.suivi_habitudes.api.ApiClient;
import com.example.suivi_habitudes.models.StatisticsResponse;
import com.example.suivi_habitudes.models.Statistics;
import com.example.suivi_habitudes.models.CategoryStatistic;
import com.example.suivi_habitudes.adapters.CategoryStatisticsAdapter;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class StatisticsActivity extends AppCompatActivity {

    
    private TextView toolbarTitle;
    private TextView textTotalHabits;
    private TextView textActiveToday;
    private TextView textLongestStreak;
    private TextView textTotalCompletions;
    private TextView textWeeklyProgress;
    private TextView textWeeklyPercentage;
    private ProgressBar progressBarWeekly;
    private RecyclerView recyclerViewCategories;
    private BottomNavigationView bottomNavigation;


    private CategoryStatisticsAdapter categoryAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics);

        initializeViews();
        setupToolbar();
        setupRecyclerView();
        setupBottomNavigation();
        loadStatistics();
    }

    private void initializeViews() {
        toolbarTitle = findViewById(R.id.toolbarTitle);
        textTotalHabits = findViewById(R.id.textTotalHabits);
        textActiveToday = findViewById(R.id.textActiveToday);
        textLongestStreak = findViewById(R.id.textLongestStreak);
        textTotalCompletions = findViewById(R.id.textTotalCompletions);
        textWeeklyProgress = findViewById(R.id.textWeeklyProgress);
        textWeeklyPercentage = findViewById(R.id.textWeeklyPercentage);
        progressBarWeekly = findViewById(R.id.progressBarWeekly);
        recyclerViewCategories = findViewById(R.id.recyclerViewCategories);
        bottomNavigation = findViewById(R.id.bottomNavigation);
    }

    private void setupToolbar() {
        toolbarTitle.setText("Statistiques");
    }

    private void setupRecyclerView() {
        categoryAdapter = new CategoryStatisticsAdapter(new ArrayList<>());
        recyclerViewCategories.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewCategories.setAdapter(categoryAdapter);
    }

    private void setupBottomNavigation() {
        bottomNavigation.setSelectedItemId(R.id.nav_statistics);
        bottomNavigation.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_home) {

                Intent intent = new Intent(StatisticsActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
                return true;
            } else if (itemId == R.id.nav_statistics) {

                return true;
            } else if (itemId == R.id.nav_settings) {

                Intent intent = new Intent(StatisticsActivity.this, SettingsActivity.class);
                startActivity(intent);
                finish();
                return true;
            }
            return false;
        });
    }

    private void loadStatistics() {
        SharedPreferences prefs = getSharedPreferences("user_prefs", MODE_PRIVATE);
        int userId = prefs.getInt("user_id", -1);

        if (userId == -1) {
            Toast.makeText(this, "Erreur: Utilisateur non identifié", Toast.LENGTH_SHORT).show();
            return;
        }


        Toast.makeText(this, "Chargement des statistiques...", Toast.LENGTH_SHORT).show();


        Call<StatisticsResponse> call = ApiClient.getApiService().getStatistics(userId);
        call.enqueue(new Callback<StatisticsResponse>() {
            @Override
            public void onResponse(Call<StatisticsResponse> call, Response<StatisticsResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    StatisticsResponse statisticsResponse = response.body();
                    if (statisticsResponse.isSuccess()) {
                        displayStatistics(statisticsResponse.getStatistics());
                    } else {
                        Toast.makeText(StatisticsActivity.this, 
                            statisticsResponse.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(StatisticsActivity.this, 
                        "Erreur de connexion", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<StatisticsResponse> call, Throwable t) {
                Toast.makeText(StatisticsActivity.this, 
                    "Erreur réseau: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void displayStatistics(Statistics statistics) {
        if (statistics == null) {
            Toast.makeText(this, "Aucune donnée statistique disponible", Toast.LENGTH_SHORT).show();
            setEmptyState();
            return;
        }


        textTotalHabits.setText(String.valueOf(statistics.getTotalHabits()));
        textActiveToday.setText(String.valueOf(statistics.getActiveToday()));
        

        int streak = statistics.getLongestStreak();
        if (streak == 0) {
            textLongestStreak.setText("Aucune série");
        } else if (streak == 1) {
            textLongestStreak.setText("1 jour");
        } else {
            textLongestStreak.setText(streak + " jours");
        }


        textTotalCompletions.setText(String.valueOf(statistics.getTotalCompletions()));


        int weekCompleted = statistics.getWeekCompleted();
        int weekTotal = statistics.getWeekTotal();
        textWeeklyProgress.setText(weekCompleted + " / " + weekTotal);
        

        double weekPercentage = weekTotal > 0 ? statistics.getWeekPercentage() : 0.0;
        textWeeklyPercentage.setText((int) Math.round(weekPercentage) + "%");
        progressBarWeekly.setProgress((int) Math.round(weekPercentage));


        List<CategoryStatistic> categories = statistics.getCategories();
        if (categories != null && !categories.isEmpty()) {
            categoryAdapter.updateCategories(categories);
            recyclerViewCategories.setVisibility(RecyclerView.VISIBLE);
        } else {

            categoryAdapter.updateCategories(new ArrayList<>());
            recyclerViewCategories.setVisibility(RecyclerView.GONE);
            Toast.makeText(this, "Aucune habitude trouvée pour calculer les statistiques", Toast.LENGTH_SHORT).show();
        }
    }

    private void setEmptyState() {

        textTotalHabits.setText("0");
        textActiveToday.setText("0");
        textLongestStreak.setText("Aucune série");
        textTotalCompletions.setText("0");
        textWeeklyProgress.setText("0 / 0");
        textWeeklyPercentage.setText("0%");
        progressBarWeekly.setProgress(0);
        

        categoryAdapter.updateCategories(new ArrayList<>());
        recyclerViewCategories.setVisibility(RecyclerView.GONE);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Only refresh if we're returning from another activity (not initial creation)
        // loadStatistics() is already called in onCreate(), so we don't need it here initially
        // Uncomment the line below if you want auto-refresh when returning to this screen
        // loadStatistics();
    }
}