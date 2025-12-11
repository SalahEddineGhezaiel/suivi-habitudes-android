package com.example.suivi_habitudes.activities;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.suivi_habitudes.R;
import com.example.suivi_habitudes.api.ApiClient;
import com.example.suivi_habitudes.models.Habit;
import com.example.suivi_habitudes.models.ApiResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddHabitActivity extends AppCompatActivity {

    private EditText editTextTitle, editTextDescription;
    private AutoCompleteTextView editTextCategory;
    private Button buttonSave;
    private TextView textViewTitle;
    private ImageView toolbarIcon;
    
    // Day checkboxes
    private CheckBox checkBoxMonday, checkBoxTuesday, checkBoxWednesday, checkBoxThursday;
    private CheckBox checkBoxFriday, checkBoxSaturday, checkBoxSunday;
    
    private boolean isEditMode = false;
    private int habitId = -1;
    private int userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_habit);

        editTextTitle = findViewById(R.id.editTextHabitTitle);
        editTextDescription = findViewById(R.id.editTextHabitDescription);
        editTextCategory = findViewById(R.id.editTextHabitCategory);
        buttonSave = findViewById(R.id.buttonSave);
        textViewTitle = findViewById(R.id.textViewTitle);
        toolbarIcon = findViewById(R.id.toolbarIcon);


        checkBoxMonday = findViewById(R.id.checkBoxMonday);
        checkBoxTuesday = findViewById(R.id.checkBoxTuesday);
        checkBoxWednesday = findViewById(R.id.checkBoxWednesday);
        checkBoxThursday = findViewById(R.id.checkBoxThursday);
        checkBoxFriday = findViewById(R.id.checkBoxFriday);
        checkBoxSaturday = findViewById(R.id.checkBoxSaturday);
        checkBoxSunday = findViewById(R.id.checkBoxSunday);


        String[] categories = {"Santé", "Sport", "Étude", "Travail", "Personnel", "Alimentation", "Sommeil", "Méditation", "Lecture", "Créativité"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, categories);
        editTextCategory.setAdapter(adapter);

        SharedPreferences sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE);
        userId = sharedPreferences.getInt("user_id", -1);

        if (getIntent().hasExtra("habit_id")) {
            isEditMode = true;
            habitId = getIntent().getIntExtra("habit_id", -1);
            textViewTitle.setText(getString(R.string.edit_habit));
            

            toolbarIcon.setImageResource(R.drawable.ic_edit_simple);
            editTextTitle.setText(getIntent().getStringExtra("habit_title"));
            editTextDescription.setText(getIntent().getStringExtra("habit_description"));
            editTextCategory.setText(getIntent().getStringExtra("habit_category"), false);
            

            checkBoxMonday.setChecked(getIntent().getBooleanExtra("habit_monday", false));
            checkBoxTuesday.setChecked(getIntent().getBooleanExtra("habit_tuesday", false));
            checkBoxWednesday.setChecked(getIntent().getBooleanExtra("habit_wednesday", false));
            checkBoxThursday.setChecked(getIntent().getBooleanExtra("habit_thursday", false));
            checkBoxFriday.setChecked(getIntent().getBooleanExtra("habit_friday", false));
            checkBoxSaturday.setChecked(getIntent().getBooleanExtra("habit_saturday", false));
            checkBoxSunday.setChecked(getIntent().getBooleanExtra("habit_sunday", false));
        } else {
            textViewTitle.setText(getString(R.string.add_new_habit));
            

            toolbarIcon.setImageResource(R.drawable.ic_add_simple);
        }

        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveHabit();
            }
        });


        findViewById(R.id.buttonBack).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish(); 
            }
        });
    }

    private void saveHabit() {
        String title = editTextTitle.getText().toString().trim();
        String description = editTextDescription.getText().toString().trim();
        String category = editTextCategory.getText().toString().trim();

        if (title.isEmpty()) {
            Toast.makeText(this, "Veuillez saisir un titre", Toast.LENGTH_SHORT).show();
            return;
        }


        boolean monday = checkBoxMonday.isChecked();
        boolean tuesday = checkBoxTuesday.isChecked();
        boolean wednesday = checkBoxWednesday.isChecked();
        boolean thursday = checkBoxThursday.isChecked();
        boolean friday = checkBoxFriday.isChecked();
        boolean saturday = checkBoxSaturday.isChecked();
        boolean sunday = checkBoxSunday.isChecked();

        if (!monday && !tuesday && !wednesday && !thursday && !friday && !saturday && !sunday) {
            Toast.makeText(this, "Veuillez sélectionner au moins un jour", Toast.LENGTH_SHORT).show();
            return;
        }

        if (isEditMode) {
            updateHabit(habitId, title, description, category, monday, tuesday, wednesday, thursday, friday, saturday, sunday);
        } else {
            createHabit(userId, title, description, category, monday, tuesday, wednesday, thursday, friday, saturday, sunday);
        }
    }

    private void createHabit(int userId, String title, String description, String category, boolean monday, boolean tuesday, boolean wednesday, boolean thursday, boolean friday, boolean saturday, boolean sunday) {
        Call<ApiResponse> call = ApiClient.getApiService().createHabit(userId, title, description, category, monday, tuesday, wednesday, thursday, friday, saturday, sunday);
        call.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse apiResponse = response.body();
                    if (apiResponse.isSuccess()) {
                        Toast.makeText(AddHabitActivity.this, getString(R.string.habit_created), Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        Toast.makeText(AddHabitActivity.this, apiResponse.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(AddHabitActivity.this, getString(R.string.connection_error), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                Toast.makeText(AddHabitActivity.this, "Erreur: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateHabit(int habitId, String title, String description, String category, boolean monday, boolean tuesday, boolean wednesday, boolean thursday, boolean friday, boolean saturday, boolean sunday) {
        Call<ApiResponse> call = ApiClient.getApiService().updateHabit(habitId, title, description, category, monday, tuesday, wednesday, thursday, friday, saturday, sunday);
        call.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse apiResponse = response.body();
                    if (apiResponse.isSuccess()) {
                        Toast.makeText(AddHabitActivity.this, "Habitude mise à jour avec succès", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        Toast.makeText(AddHabitActivity.this, apiResponse.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(AddHabitActivity.this, "Erreur de connexion", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                Toast.makeText(AddHabitActivity.this, "Erreur: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
