package com.example.schedulemedical.data.api;

import com.example.schedulemedical.model.dto.request.CreateAppointmentRequest;
import com.example.schedulemedical.model.dto.request.UpdateAppointmentStatusRequest;
import com.example.schedulemedical.model.dto.response.ApiResponse;
import com.example.schedulemedical.model.dto.response.AppointmentResponse;
import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface AppointmentApiService {
    
    // Create new appointment
    @POST("appointment")
    Call<ApiResponse<Object>> createAppointment(@Body CreateAppointmentRequest request);
    
    // Get appointments with filters
    @GET("appointment")
    Call<ApiResponse> getAppointments(
        @Query("page") Integer page,
        @Query("limit") Integer limit,
        @Query("userId") Integer userId,
        @Query("doctorId") Integer doctorId,
        @Query("status") String status
    );
    
    // Get appointment counts
    @GET("appointment/counts")
    Call<ApiResponse> getAppointmentCounts(
        @Query("userId") Integer userId,
        @Query("doctorId") Integer doctorId
    );
    
    // Get appointment by ID
    @GET("appointment/{id}")
    Call<ApiResponse> getAppointmentById(@Path("id") int appointmentId);
    
    // Update appointment status
    @PATCH("appointment/{id}/status")
    Call<ApiResponse> updateAppointmentStatus(
        @Path("id") int appointmentId,
        @Body UpdateAppointmentStatusRequest request
    );
    
    // Cancel appointment
    @DELETE("appointment/{id}")
    Call<ApiResponse> cancelAppointment(@Path("id") int appointmentId);
    
    // Get appointment statistics
    @GET("appointment/statistics")
    Call<ApiResponse> getAppointmentStatistics();
    
    // Get doctor with most appointments
    @GET("appointment/statistics/doctor-most-appointments")
    Call<ApiResponse> getDoctorWithMostAppointments();
    
    // Feedback endpoints
    @POST("appointment/feedback")
    Call<ApiResponse> createFeedback(@Body Object feedbackRequest);
    
    @GET("appointment/feedback/get-all")
    Call<ApiResponse> getAllFeedbacks(
        @Query("page") int page,
        @Query("limit") int limit
    );
    
    @GET("appointment/feedback/{id}")
    Call<ApiResponse> getFeedbackById(@Path("id") int feedbackId);
    
    // Follow-up endpoints
    @POST("appointment/follow-up")
    Call<ApiResponse> createFollowUp(@Body Object followUpRequest);
    
    @GET("appointment/follow-up/get-all")
    Call<ApiResponse> getAllFollowUps(
        @Query("page") int page,
        @Query("limit") int limit
    );
    
    @GET("appointment/follow-up/{id}")
    Call<ApiResponse> getFollowUpById(@Path("id") int followUpId);
    
    @GET("appointment/follow-up/appointment/{appointmentId}")
    Call<ApiResponse> getFollowUpsByAppointmentId(@Path("appointmentId") int appointmentId);
    
    // Notification endpoints
    @POST("appointment/notification")
    Call<ApiResponse> createNotification(@Body Object notificationRequest);
    
    @GET("appointment/notification/get-all")
    Call<ApiResponse> getAllNotifications(
        @Query("page") int page,
        @Query("limit") int limit
    );
    
    @GET("appointment/notification/{id}")
    Call<ApiResponse> getNotificationById(@Path("id") int notificationId);
    
    @GET("appointment/notification/user/{userId}")
    Call<ApiResponse> getNotificationsByUserId(@Path("userId") int userId);
    
    @GET("appointment/notification/unread-count/{userId}")
    Call<ApiResponse> getUnreadNotificationCount(@Path("userId") int userId);
    
    @PATCH("appointment/notification/{id}/read/{userId}")
    Call<ApiResponse> markNotificationAsRead(
        @Path("id") int notificationId,
        @Path("userId") int userId
    );
    
    @PATCH("appointment/notification/mark-all-read/{userId}")
    Call<ApiResponse> markAllNotificationsAsRead(@Path("userId") int userId);

    @DELETE("appointment/{id}")
    Call<ApiResponse<Object>> deleteAppointment(@Path("id") int appointmentId);
} 