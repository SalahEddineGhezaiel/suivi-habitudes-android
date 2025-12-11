package com.example.suivi_habitudes.api;

import com.example.suivi_habitudes.models.Habit;
import com.example.suivi_habitudes.models.User;
import com.example.suivi_habitudes.models.LoginResponse;
import com.example.suivi_habitudes.models.DashboardResponse;
import com.example.suivi_habitudes.models.ApiResponse;
import com.example.suivi_habitudes.models.HabitCheckResponse;
import com.example.suivi_habitudes.models.StatisticsResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface ApiService {

    @FormUrlEncoded
    @POST("register.php")
    Call<User> register(
            @Field("name") String name,
            @Field("email") String email,
            @Field("password") String password
    );

    @FormUrlEncoded
    @POST("login.php")
    Call<LoginResponse> login(
            @Field("email") String email,
            @Field("password") String password
    );

    @GET("get_dashboard.php")
    Call<DashboardResponse> getDashboard(@Query("user_id") int userId);

    @GET("get_dashboard.php")
    Call<DashboardResponse> getDashboardWithFilter(@Query("user_id") int userId, @Query("filter") String filter);

    @FormUrlEncoded
    @POST("change_password.php")
    Call<ApiResponse> changePassword(@Field("user_id") int userId, @Field("email") String email, @Field("current_password") String currentPassword, @Field("new_password") String newPassword);

    @FormUrlEncoded
    @POST("clear_all_habits.php")
    Call<ApiResponse> clearAllHabits(@Field("user_id") int userId);

    @FormUrlEncoded
    @POST("create_habit.php")
    Call<ApiResponse> createHabit(
            @Field("user_id") int userId,
            @Field("title") String title,
            @Field("description") String description,
            @Field("category") String category,
            @Field("monday") boolean monday,
            @Field("tuesday") boolean tuesday,
            @Field("wednesday") boolean wednesday,
            @Field("thursday") boolean thursday,
            @Field("friday") boolean friday,
            @Field("saturday") boolean saturday,
            @Field("sunday") boolean sunday
    );

    @FormUrlEncoded
    @POST("update_habit.php")
    Call<ApiResponse> updateHabit(
            @Field("habit_id") int habitId,
            @Field("title") String title,
            @Field("description") String description,
            @Field("category") String category,
            @Field("monday") boolean monday,
            @Field("tuesday") boolean tuesday,
            @Field("wednesday") boolean wednesday,
            @Field("thursday") boolean thursday,
            @Field("friday") boolean friday,
            @Field("saturday") boolean saturday,
            @Field("sunday") boolean sunday
    );

    @FormUrlEncoded
    @POST("delete_habit.php")
    Call<ApiResponse> deleteHabit(@Field("habit_id") int habitId);

    @FormUrlEncoded
    @POST("habit_check.php")
    Call<HabitCheckResponse> checkHabit(
            @Field("user_id") int userId,
            @Field("habit_id") int habitId,
            @Field("is_completed") boolean isCompleted
    );

    @FormUrlEncoded
    @POST("get_statistics.php")
    Call<StatisticsResponse> getStatistics(@Field("user_id") int userId);

    @FormUrlEncoded
    @POST("reset_statistics.php")
    Call<ApiResponse> resetStatistics(@Field("user_id") int userId);
}
