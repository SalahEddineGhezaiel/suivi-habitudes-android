package com.example.suivi_habitudes.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.suivi_habitudes.R;
import com.example.suivi_habitudes.models.CategoryStatistic;

import java.util.List;

public class CategoryStatisticsAdapter extends RecyclerView.Adapter<CategoryStatisticsAdapter.CategoryViewHolder> {

    private List<CategoryStatistic> categories;

    public CategoryStatisticsAdapter(List<CategoryStatistic> categories) {
        this.categories = categories;
    }

    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.category_statistics_item, parent, false);
        return new CategoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder holder, int position) {
        CategoryStatistic category = categories.get(position);
        holder.bind(category);
    }

    @Override
    public int getItemCount() {
        return categories.size();
    }

    public void updateCategories(List<CategoryStatistic> newCategories) {
        this.categories = newCategories;
        notifyDataSetChanged();
    }

    static class CategoryViewHolder extends RecyclerView.ViewHolder {
        private TextView textCategoryIcon;
        private TextView textCategoryName;
        private TextView textCategoryProgress;
        private TextView textCategoryPercentage;
        private TextView textSuccessIndicator;
        private ProgressBar progressBarCategory;

        public CategoryViewHolder(@NonNull View itemView) {
            super(itemView);
            textCategoryIcon = itemView.findViewById(R.id.textCategoryIcon);
            textCategoryName = itemView.findViewById(R.id.textCategoryName);
            textCategoryProgress = itemView.findViewById(R.id.textCategoryProgress);
            textCategoryPercentage = itemView.findViewById(R.id.textCategoryPercentage);
            textSuccessIndicator = itemView.findViewById(R.id.textSuccessIndicator);
            progressBarCategory = itemView.findViewById(R.id.progressBarCategory);
        }

        public void bind(CategoryStatistic category) {

            textCategoryName.setText(category.getName());
            

            textCategoryProgress.setText(category.getCompletedCount() + " / " + category.getTotalCount());
            

            int percentage = (int) Math.round(category.getCompletionPercentage());
            textCategoryPercentage.setText(percentage + "% de réussite");
            

            progressBarCategory.setProgress(percentage);
            

            String categoryIcon = getCategoryIcon(category.getName());
            textCategoryIcon.setText(categoryIcon);
            

            if (category.getCompletionPercentage() >= 80.0) {
                textSuccessIndicator.setVisibility(View.VISIBLE);
                textSuccessIndicator.setText("🎯");
            } else if (category.getCompletionPercentage() >= 60.0) {
                textSuccessIndicator.setVisibility(View.VISIBLE);
                textSuccessIndicator.setText("👍");
            } else if (category.getCompletionPercentage() >= 30.0) {
                textSuccessIndicator.setVisibility(View.VISIBLE);
                textSuccessIndicator.setText("📈");
            } else {
                textSuccessIndicator.setVisibility(View.GONE);
            }
            

            int progressColor;
            if (category.getCompletionPercentage() >= 80.0) {
                progressColor = 0xFF4CAF50; 
            } else if (category.getCompletionPercentage() >= 60.0) {
                progressColor = 0xFF2196F3; 
            } else if (category.getCompletionPercentage() >= 30.0) {
                progressColor = 0xFFFF9800; 
            } else {
                progressColor = 0xFFF44336; 
            }
            progressBarCategory.setProgressTintList(android.content.res.ColorStateList.valueOf(progressColor));
        }
        

        private String getCategoryIcon(String categoryName) {
            if (categoryName == null) return "📁";
            
            String lowerCategory = categoryName.toLowerCase();
            

            if (lowerCategory.contains("santé") || lowerCategory.contains("health")) {
                return "🏥";
            } else if (lowerCategory.contains("sport") || lowerCategory.contains("fitness") || 
                      lowerCategory.contains("exercice") || lowerCategory.contains("exercise")) {
                return "💪";
            } else if (lowerCategory.contains("course") || lowerCategory.contains("running") ||
                      lowerCategory.contains("jogging")) {
                return "🏃";
            } else if (lowerCategory.contains("yoga") || lowerCategory.contains("méditation") ||
                      lowerCategory.contains("meditation")) {
                return "🧘";
            }
            

            else if (lowerCategory.contains("étude") || lowerCategory.contains("study") ||
                    lowerCategory.contains("apprentissage") || lowerCategory.contains("learning")) {
                return "📚";
            } else if (lowerCategory.contains("lecture") || lowerCategory.contains("reading") ||
                      lowerCategory.contains("livre") || lowerCategory.contains("book")) {
                return "📖";
            } else if (lowerCategory.contains("travail") || lowerCategory.contains("work") ||
                      lowerCategory.contains("productivité") || lowerCategory.contains("productivity")) {
                return "💼";
            } else if (lowerCategory.contains("code") || lowerCategory.contains("programming") ||
                      lowerCategory.contains("développement") || lowerCategory.contains("development")) {
                return "💻";
            }
            

            else if (lowerCategory.contains("sommeil") || lowerCategory.contains("sleep")) {
                return "😴";
            } else if (lowerCategory.contains("eau") || lowerCategory.contains("water") ||
                      lowerCategory.contains("hydrat")) {
                return "💧";
            } else if (lowerCategory.contains("alimentation") || lowerCategory.contains("nutrition") ||
                      lowerCategory.contains("diet") || lowerCategory.contains("manger")) {
                return "🥗";
            } else if (lowerCategory.contains("ménage") || lowerCategory.contains("nettoyage") ||
                      lowerCategory.contains("cleaning") || lowerCategory.contains("house")) {
                return "🏠";
            }
            

            else if (lowerCategory.contains("famille") || lowerCategory.contains("family") ||
                    lowerCategory.contains("ami") || lowerCategory.contains("friend")) {
                return "👨‍👩‍👧‍👦";
            } else if (lowerCategory.contains("créativité") || lowerCategory.contains("creativity") ||
                      lowerCategory.contains("art") || lowerCategory.contains("créatif")) {
                return "🎨";
            } else if (lowerCategory.contains("musique") || lowerCategory.contains("music")) {
                return "🎵";
            } else if (lowerCategory.contains("finance") || lowerCategory.contains("argent") ||
                      lowerCategory.contains("money") || lowerCategory.contains("budget")) {
                return "💰";
            }
            

            else if (lowerCategory.contains("jardin") || lowerCategory.contains("garden") ||
                    lowerCategory.contains("plante") || lowerCategory.contains("plant")) {
                return "🌱";
            } else if (lowerCategory.contains("cuisine") || lowerCategory.contains("cooking") ||
                      lowerCategory.contains("cook")) {
                return "👨‍🍳";
            } else if (lowerCategory.contains("voyage") || lowerCategory.contains("travel")) {
                return "✈️";
            }
            

            else {
                return "📁";
            }
        }
    }
}