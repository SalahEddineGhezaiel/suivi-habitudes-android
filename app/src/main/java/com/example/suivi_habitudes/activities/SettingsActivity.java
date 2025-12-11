package com.example.suivi_habitudes.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.suivi_habitudes.R;
import com.example.suivi_habitudes.api.ApiClient;
import com.example.suivi_habitudes.models.ApiResponse;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SettingsActivity extends AppCompatActivity {

    private TextView toolbarTitle;
    private LinearLayout settingsContainer;
    private BottomNavigationView bottomNavigation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        initializeViews();
        setupToolbar();
        setupSettingsOptions();
    }

    private void initializeViews() {
        toolbarTitle = findViewById(R.id.toolbarTitle);
        settingsContainer = findViewById(R.id.settingsContainer);
        bottomNavigation = findViewById(R.id.bottomNavigation);
    }

    private void setupToolbar() {
        toolbarTitle.setText("Paramètres");
        

        bottomNavigation.setSelectedItemId(R.id.nav_settings);
        bottomNavigation.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_home) {

                Intent intent = new Intent(SettingsActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
                return true;
            } else if (itemId == R.id.nav_statistics) {

                Intent intent = new Intent(SettingsActivity.this, StatisticsActivity.class);
                startActivity(intent);
                finish();
                return true;
            } else if (itemId == R.id.nav_settings) {

                return true;
            }
            return false;
        });
    }

    private void setupSettingsOptions() {

        addAccountManagementSection();
        
        addSpace(32);
        

        addPreferencesSection();
    }


    private void addAccountManagementSection() {
        addSectionTitle("Gestion du Compte");
        
        addSpace(16);
        

        addUserInfoCard();
        
        addSpace(24);
        

        addSettingsItem("Changer le mot de passe", "Modifier votre mot de passe actuel", R.drawable.ic_edit_simple, this::showChangePasswordDialog);
        addSettingsItem("Déconnexion", "Se déconnecter de l'application", R.drawable.ic_delete_simple, this::showLogoutDialog);
    }


    private void addPreferencesSection() {
        addSectionTitle("⚙️ Préférences");
        
        addSpace(16);
        

        addSettingsItem("Effacer toutes les habitudes", "Supprimer définitivement toutes vos habitudes", R.drawable.ic_delete_simple, this::showClearAllHabitsDialog);
        addSettingsItem("Réinitialiser les statistiques", "Conserver les habitudes mais effacer les progrès", R.drawable.ic_settings_simple, this::showResetStatisticsDialog);
    }


    private void addSectionTitle(String title) {
        LinearLayout titleRow = new LinearLayout(this);
        titleRow.setOrientation(LinearLayout.HORIZONTAL);
        titleRow.setGravity(android.view.Gravity.CENTER_VERTICAL);
        
        LinearLayout.LayoutParams titleRowParams = new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        );
        titleRowParams.setMargins(0, 24, 0, 12);
        titleRow.setLayoutParams(titleRowParams);
        

        if (title.contains("Gestion du Compte")) {
            TextView userIcon = new TextView(this);
            userIcon.setText("👤");
            userIcon.setTextSize(24);
            titleRow.addView(userIcon);
        }
        

        TextView titleView = new TextView(this);
        titleView.setText(title);
        titleView.setTextSize(22);
        titleView.setTextColor(Color.parseColor("#2C3E50"));
        titleView.setTypeface(null, android.graphics.Typeface.BOLD);
        
        LinearLayout.LayoutParams titleParams = new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        );

        if (title.contains("Gestion du Compte")) {
            titleParams.setMargins(8, 0, 0, 0);
        } else {
            titleParams.setMargins(0, 0, 0, 0);
        }
        titleView.setLayoutParams(titleParams);
        
        titleRow.addView(titleView);
        settingsContainer.addView(titleRow);
    }

    private void addUserInfoCard() {
        SharedPreferences prefs = getSharedPreferences("user_prefs", MODE_PRIVATE);
        String userName = prefs.getString("user_name", "Utilisateur");
        String userEmail = prefs.getString("user_email", "Non disponible");


        LinearLayout card = new LinearLayout(this);
        card.setOrientation(LinearLayout.VERTICAL);
        card.setPadding(20, 16, 20, 16);
        

        GradientDrawable greyBackground = new GradientDrawable();
        greyBackground.setColor(Color.parseColor("#F5F5F5"));
        greyBackground.setCornerRadius(12);
        card.setBackground(greyBackground);
        card.setElevation(2);
        
        LinearLayout.LayoutParams cardParams = new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        );
        cardParams.setMargins(0, 0, 0, 20);
        card.setLayoutParams(cardParams);


        LinearLayout nameRow = new LinearLayout(this);
        nameRow.setOrientation(LinearLayout.HORIZONTAL);
        nameRow.setGravity(android.view.Gravity.CENTER_VERTICAL);
        
        TextView nameLabel = new TextView(this);
        nameLabel.setText("Nom:");
        nameLabel.setTextSize(14);
        nameLabel.setTextColor(Color.parseColor("#2C3E50"));
        
        TextView nameValue = new TextView(this);
        nameValue.setText(userName);
        nameValue.setTextSize(16);
        nameValue.setTextColor(Color.parseColor("#2C3E50"));
        nameValue.setTypeface(null, android.graphics.Typeface.BOLD);
        LinearLayout.LayoutParams nameValueParams = new LinearLayout.LayoutParams(
            0, LinearLayout.LayoutParams.WRAP_CONTENT, 1
        );
        nameValueParams.setMargins(16, 0, 0, 0);
        nameValue.setLayoutParams(nameValueParams);
        
        nameRow.addView(nameLabel);
        nameRow.addView(nameValue);


        LinearLayout emailRow = new LinearLayout(this);
        emailRow.setOrientation(LinearLayout.HORIZONTAL);
        emailRow.setGravity(android.view.Gravity.CENTER_VERTICAL);
        LinearLayout.LayoutParams emailRowParams = new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        );
        emailRowParams.setMargins(0, 8, 0, 0);
        emailRow.setLayoutParams(emailRowParams);
        
        TextView emailLabel = new TextView(this);
        emailLabel.setText("Email:");
        emailLabel.setTextSize(14);
        emailLabel.setTextColor(Color.parseColor("#2C3E50"));
        
        TextView emailValue = new TextView(this);
        emailValue.setText(userEmail);
        emailValue.setTextSize(16);
        emailValue.setTextColor(Color.parseColor("#2C3E50"));
        emailValue.setTypeface(null, android.graphics.Typeface.BOLD);
        LinearLayout.LayoutParams emailValueParams = new LinearLayout.LayoutParams(
            0, LinearLayout.LayoutParams.WRAP_CONTENT, 1
        );
        emailValueParams.setMargins(16, 0, 0, 0);
        emailValue.setLayoutParams(emailValueParams);
        
        emailRow.addView(emailLabel);
        emailRow.addView(emailValue);

        card.addView(nameRow);
        card.addView(emailRow);
        settingsContainer.addView(card);
    }

    private void addSettingsItem(String title, String subtitle, int iconRes, Runnable onTap) {
        LinearLayout item = new LinearLayout(this);
        item.setOrientation(LinearLayout.HORIZONTAL);
        item.setPadding(16, 12, 16, 12);
        item.setGravity(android.view.Gravity.CENTER_VERTICAL);
        

        GradientDrawable itemBackground = new GradientDrawable();
        itemBackground.setColor(Color.WHITE);
        itemBackground.setCornerRadius(8);
        item.setBackground(itemBackground);
        item.setElevation(1);
        
        LinearLayout.LayoutParams itemParams = new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        );
        itemParams.setMargins(0, 8, 0, 8);
        item.setLayoutParams(itemParams);


        TextView icon = new TextView(this);
        icon.setText("•");
        icon.setTextSize(16);
        icon.setTextColor(Color.parseColor("#667eea"));
        
        LinearLayout.LayoutParams iconParams = new LinearLayout.LayoutParams(16, 24);
        icon.setLayoutParams(iconParams);


        LinearLayout textContainer = new LinearLayout(this);
        textContainer.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams textParams = new LinearLayout.LayoutParams(
            0, LinearLayout.LayoutParams.WRAP_CONTENT, 1
        );
        textParams.setMargins(0, 0, 16, 0); 
        textContainer.setLayoutParams(textParams);


        TextView titleText = new TextView(this);
        titleText.setText(title);
        titleText.setTextSize(16);
        titleText.setTextColor(Color.parseColor("#2C3E50"));


        TextView subtitleText = new TextView(this);
        subtitleText.setText(subtitle);
        subtitleText.setTextSize(12);
        subtitleText.setTextColor(Color.parseColor("#7F8C8D"));

        textContainer.addView(titleText);
        textContainer.addView(subtitleText);


        TextView arrow = new TextView(this);
        arrow.setText(">");
        arrow.setTextSize(18);
        arrow.setTextColor(Color.parseColor("#000000"));
        arrow.setTypeface(null, android.graphics.Typeface.BOLD);
        
        LinearLayout.LayoutParams arrowParams = new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        );
        arrowParams.setMargins(0, 0, 8, 0); 
        arrow.setLayoutParams(arrowParams);

        item.addView(icon);
        item.addView(textContainer);
        item.addView(arrow);
        
        if (onTap != null) {
            item.setOnClickListener(v -> onTap.run());
        }
        
        settingsContainer.addView(item);
    }

    private void addSpace(int height) {
        View space = new View(this);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT, height
        );
        space.setLayoutParams(params);
        settingsContainer.addView(space);
    }


    private void showChangePasswordDialog() {

        LinearLayout dialogLayout = new LinearLayout(this);
        dialogLayout.setOrientation(LinearLayout.VERTICAL);
        dialogLayout.setPadding(24, 24, 24, 24);


        android.view.LayoutInflater inflater = getLayoutInflater();
        

        TextInputLayout currentPasswordLayout = new TextInputLayout(new androidx.appcompat.view.ContextThemeWrapper(this, com.google.android.material.R.style.Widget_MaterialComponents_TextInputLayout_OutlinedBox), null, 0);
        currentPasswordLayout.setBoxBackgroundMode(TextInputLayout.BOX_BACKGROUND_OUTLINE);
        LinearLayout.LayoutParams currentLayoutParams = new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        );
        currentLayoutParams.setMargins(0, 0, 0, 16);
        currentPasswordLayout.setLayoutParams(currentLayoutParams);

        TextInputEditText currentPasswordInput = new TextInputEditText(currentPasswordLayout.getContext());
        currentPasswordInput.setHint("Mot de passe actuel");
        currentPasswordInput.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);

        currentPasswordInput.setTypeface(android.graphics.Typeface.DEFAULT);
        currentPasswordInput.setTextCursorDrawable(null);
        currentPasswordInput.setHighlightColor(getResources().getColor(R.color.primary_blue));
        currentPasswordLayout.setBoxStrokeColor(getResources().getColor(R.color.primary_blue));
        currentPasswordLayout.setHintTextColor(android.content.res.ColorStateList.valueOf(getResources().getColor(R.color.primary_blue)));
        currentPasswordLayout.addView(currentPasswordInput);
        currentPasswordLayout.setPasswordVisibilityToggleEnabled(true);


        TextInputLayout newPasswordLayout = new TextInputLayout(new androidx.appcompat.view.ContextThemeWrapper(this, com.google.android.material.R.style.Widget_MaterialComponents_TextInputLayout_OutlinedBox), null, 0);
        newPasswordLayout.setBoxBackgroundMode(TextInputLayout.BOX_BACKGROUND_OUTLINE);
        newPasswordLayout.setPasswordVisibilityToggleEnabled(true);
        LinearLayout.LayoutParams newLayoutParams = new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        );
        newLayoutParams.setMargins(0, 0, 0, 16);
        newPasswordLayout.setLayoutParams(newLayoutParams);

        TextInputEditText newPasswordInput = new TextInputEditText(newPasswordLayout.getContext());
        newPasswordInput.setHint("Nouveau mot de passe");
        newPasswordInput.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);

        newPasswordInput.setTypeface(android.graphics.Typeface.DEFAULT);
        newPasswordInput.setTextCursorDrawable(null);
        newPasswordInput.setHighlightColor(getResources().getColor(R.color.primary_blue));
        newPasswordLayout.setBoxStrokeColor(getResources().getColor(R.color.primary_blue));
        newPasswordLayout.setHintTextColor(android.content.res.ColorStateList.valueOf(getResources().getColor(R.color.primary_blue)));
        newPasswordLayout.addView(newPasswordInput);


        TextInputLayout confirmPasswordLayout = new TextInputLayout(new androidx.appcompat.view.ContextThemeWrapper(this, com.google.android.material.R.style.Widget_MaterialComponents_TextInputLayout_OutlinedBox), null, 0);
        confirmPasswordLayout.setBoxBackgroundMode(TextInputLayout.BOX_BACKGROUND_OUTLINE);
        confirmPasswordLayout.setPasswordVisibilityToggleEnabled(true);
        LinearLayout.LayoutParams confirmLayoutParams = new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        );
        confirmLayoutParams.setMargins(0, 0, 0, 24);
        confirmPasswordLayout.setLayoutParams(confirmLayoutParams);

        TextInputEditText confirmPasswordInput = new TextInputEditText(confirmPasswordLayout.getContext());
        confirmPasswordInput.setHint("Confirmer le mot de passe");
        confirmPasswordInput.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);

        confirmPasswordInput.setTypeface(android.graphics.Typeface.DEFAULT);
        confirmPasswordInput.setTextCursorDrawable(null);
        confirmPasswordInput.setHighlightColor(getResources().getColor(R.color.primary_blue));
        confirmPasswordLayout.setBoxStrokeColor(getResources().getColor(R.color.primary_blue));
        confirmPasswordLayout.setHintTextColor(android.content.res.ColorStateList.valueOf(getResources().getColor(R.color.primary_blue)));
        confirmPasswordLayout.addView(confirmPasswordInput);
        

        dialogLayout.addView(currentPasswordLayout);
        dialogLayout.addView(newPasswordLayout);
        dialogLayout.addView(confirmPasswordLayout);


        new AlertDialog.Builder(this)
            .setTitle("Changer le mot de passe")
            .setView(dialogLayout)
            .setPositiveButton("Changer", (dialog, which) -> {
                String currentPassword = currentPasswordInput.getText().toString().trim();
                String newPassword = newPasswordInput.getText().toString().trim();
                String confirmPassword = confirmPasswordInput.getText().toString().trim();
                

                if (currentPassword.isEmpty() || newPassword.isEmpty() || confirmPassword.isEmpty()) {
                    Toast.makeText(this, "Veuillez remplir tous les champs", Toast.LENGTH_SHORT).show();
                    return;
                }
                
                if (!newPassword.equals(confirmPassword)) {
                    Toast.makeText(this, "Les mots de passe ne correspondent pas", Toast.LENGTH_SHORT).show();
                    return;
                }
                
                if (newPassword.length() < 6) {
                    Toast.makeText(this, "Minimum 6 caractères", Toast.LENGTH_SHORT).show();
                    return;
                }
                
                changePassword(currentPassword, newPassword);
            })
            .setNegativeButton("Annuler", null)
            .show();
    }

    private void changePassword(String currentPassword, String newPassword) {
        SharedPreferences prefs = getSharedPreferences("user_prefs", MODE_PRIVATE);
        int userId = prefs.getInt("user_id", -1);
        String userEmail = prefs.getString("user_email", "");
        
        if (userId == -1) {
            Toast.makeText(this, "Erreur: ID utilisateur manquant", Toast.LENGTH_SHORT).show();
            return;
        }
        

        if (userEmail.isEmpty() || userEmail.equals("Non disponible")) {

            userEmail = "temp@email.com";
        }


        Toast.makeText(this, "Changement du mot de passe...", Toast.LENGTH_SHORT).show();


        Call<ApiResponse> call = ApiClient.getApiService().changePassword(userId, userEmail, currentPassword, newPassword);
        call.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse apiResponse = response.body();
                    if (apiResponse.isSuccess()) {
                        Toast.makeText(SettingsActivity.this, "Mot de passe changé avec succès", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(SettingsActivity.this,apiResponse.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(SettingsActivity.this, "Erreur de connexion", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                Toast.makeText(SettingsActivity.this, "Erreur réseau: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showLogoutDialog() {
        new AlertDialog.Builder(this)
            .setTitle("Déconnexion")
            .setMessage("Êtes-vous sûr de vouloir vous déconnecter ?")
            .setPositiveButton("Oui", (dialog, which) -> performLogout())
            .setNegativeButton("Annuler", null)
            .show();
    }

    private void performLogout() {
        SharedPreferences prefs = getSharedPreferences("user_prefs", MODE_PRIVATE);
        prefs.edit().clear().apply();
        
        Intent intent = new Intent(SettingsActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }


    private void showClearAllHabitsDialog() {
        new AlertDialog.Builder(this)
            .setTitle("⚠️ Effacer toutes les habitudes")
            .setMessage("Cette action supprimera définitivement toutes vos habitudes et leurs données. Cette action est irréversible.\n\nÊtes-vous vraiment sûr ?")
            .setPositiveButton("Tout effacer", (dialog, which) -> clearAllHabitsData())
            .setNegativeButton("Annuler", null)
            .show();
    }

    private void showResetStatisticsDialog() {
        new AlertDialog.Builder(this)
            .setTitle("Réinitialiser les statistiques")
            .setMessage("⚠️ ATTENTION ⚠️\n\nCette action va supprimer définitivement :\n\n• Tout votre historique de progression\n• Toutes vos séries (streaks)\n• Tous vos totaux de complétions\n\nVos habitudes resteront intactes, mais toutes les statistiques seront remises à zéro.\n\nÊtes-vous absolument sûr(e) de vouloir continuer ?")
            .setPositiveButton("Oui, réinitialiser", (dialog, which) -> performResetStatistics())
            .setNegativeButton("Annuler", (dialog, which) -> dialog.dismiss())
            .setIcon(android.R.drawable.ic_dialog_alert)
            .show();
    }
    
    private void performResetStatistics() {
        SharedPreferences prefs = getSharedPreferences("user_prefs", MODE_PRIVATE);
        int userId = prefs.getInt("user_id", -1);

        if (userId == -1) {
            Toast.makeText(this, "Erreur: Utilisateur non identifié", Toast.LENGTH_SHORT).show();
            return;
        }


        Toast.makeText(this, "Réinitialisation en cours...", Toast.LENGTH_SHORT).show();

        Call<ApiResponse> call = ApiClient.getApiService().resetStatistics(userId);
        call.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse apiResponse = response.body();
                    if (apiResponse.isSuccess()) {
                        Toast.makeText(SettingsActivity.this, 
                            "✅ Statistiques réinitialisées avec succès !", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(SettingsActivity.this, 
                            "❌ Erreur: " + apiResponse.getMessage(), Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(SettingsActivity.this, 
                        "❌ Erreur de connexion", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                Toast.makeText(SettingsActivity.this, 
                    "❌ Erreur réseau: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void clearAllHabitsData() {
        SharedPreferences prefs = getSharedPreferences("user_prefs", MODE_PRIVATE);
        int userId = prefs.getInt("user_id", -1);
        
        if (userId == -1) {
            Toast.makeText(this, "Erreur: Utilisateur non identifié", Toast.LENGTH_SHORT).show();
            return;
        }


        Toast.makeText(this, "Suppression en cours...", Toast.LENGTH_SHORT).show();


        Call<ApiResponse> call = ApiClient.getApiService().clearAllHabits(userId);
        call.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse apiResponse = response.body();
                    if (apiResponse.isSuccess()) {
                        Toast.makeText(SettingsActivity.this, "Toutes les habitudes ont été supprimées", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(SettingsActivity.this,apiResponse.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(SettingsActivity.this, "Erreur de connexion", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                Toast.makeText(SettingsActivity.this, "Erreur réseau: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}