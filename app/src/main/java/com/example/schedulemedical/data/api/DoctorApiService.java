package com.example.schedulemedical.data.api;

import com.example.schedulemedical.model.dto.response.DoctorResponse;
import com.example.schedulemedical.model.dto.response.doctor.CertificationResponseDTO;
import com.example.schedulemedical.model.dto.response.ApiResponse;
import com.example.schedulemedical.model.dto.response.ResponseWrapper;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface DoctorApiService {
    
    // Get top rated doctors
    @GET("doctor/top-rated")
    Call<ApiResponse<Object>> getTopRatedDoctors();
    
    // Get doctors by specialty
    @GET("doctor/by-specialty/{specialtyId}")
    Call<ApiResponse<Object>> getDoctorsBySpecialty(
        @Path("specialtyId") int specialtyId,
        @Query("page") int page,
        @Query("limit") int limit
    );
    
    // Get doctor by ID
    @GET("doctor/{id}")
    Call<ApiResponse<Object>> getDoctorById(@Path("id") int doctorId);
    
    // Get doctor by user ID
    @GET("doctor/user/{id}")
    Call<ApiResponse<Object>> getDoctorByUserId(@Path("id") int userId);

    @GET("doctor/filter/doctor")
    Call<ApiResponse<List<DoctorResponse>>> filterDoctors(
            @Query("specialty") Integer specialtyId,
            @Query("minRating") Float minRating,
            @Query("hospital") Integer hospitalId,
            @Query("page") Integer page,
            @Query("limit") Integer limit
    );

    
    // Get all specialties
    @GET("doctor/specialty/get-all")
    Call<ApiResponse<Object>> getAllSpecialties(
        @Query("page") int page,
        @Query("limit") int limit
    );
    
    // Get specialties by hospital
    @GET("doctor/specialty/{hospitalId}")
    Call<ApiResponse<Object>> getSpecialtiesByHospital(
        @Path("hospitalId") int hospitalId,
        @Query("page") int page,
        @Query("limit") int limit
    );
    
    // Get top specialties by doctor count
    @GET("doctor/specialty/top/by-doctor-count")
    Call<ApiResponse<Object>> getTopSpecialtiesByDoctorCount(@Query("limit") int limit);
    
    // Get doctor schedules
    @GET("doctor/doctorSchedule")
    Call<ApiResponse<Object>> getDoctorSchedules(
        @Query("page") int page,
        @Query("limit") int limit
    );
    
    // Get doctor certifications
    @GET("doctor/certification")
    Call<ResponseWrapper<List<CertificationResponseDTO>>> getDoctorCertifications(
        @Query("doctorId") int doctorId,
        @Query("page") int page,
        @Query("limit") int limit
    );
    
    // Get doctor achievements
    @GET("doctor/achievement")
    Call<ApiResponse<Object>> getDoctorAchievements(
        @Query("page") int page,
        @Query("limit") int limit
    );
} 