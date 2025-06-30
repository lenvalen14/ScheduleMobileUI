package com.example.schedulemedical.data.api;

import com.example.schedulemedical.model.dto.request.doctor.AchievementDTO;
import com.example.schedulemedical.model.dto.request.doctor.CreateDoctorDTO;
import com.example.schedulemedical.model.dto.request.doctor.DoctorScheduleDTO;
import com.example.schedulemedical.model.dto.request.doctor.SpecialtyDTO;
import com.example.schedulemedical.model.dto.request.doctor.UpdateDoctorDTO;
import com.example.schedulemedical.model.dto.response.ResponseWrapper;
import com.example.schedulemedical.model.dto.response.doctor.AchievementResponseDTO;
import com.example.schedulemedical.model.dto.response.doctor.DoctorResponseDTO;
import com.example.schedulemedical.model.dto.response.doctor.DoctorScheduleResponseDTO;
import com.example.schedulemedical.model.dto.response.doctor.SpecialtyResponseDTO;

import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiDoctor {
    // Doctor APIs
    @GET("doctor/top-rated")
    Call<List<DoctorResponseDTO>> getTopRatedDoctors();

    @GET("doctor/by-specialty/{specialtyId}")
    Call<ResponseWrapper<List<DoctorResponseDTO>>> getDoctorsBySpecialty(
            @Path("specialtyId") int specialtyId,
            @Query("page") int page,
            @Query("limit") int limit
    );

    @GET("doctor/user/{id}")
    Call<ResponseWrapper<DoctorResponseDTO>> getDoctorByUserId(@Path("id") int id);

    @POST("doctor")
    Call<ResponseWrapper<DoctorResponseDTO>> createDoctor(@Body CreateDoctorDTO doctor);

    @PUT("doctor/{id}")
    Call<ResponseWrapper<DoctorResponseDTO>> updateDoctor(@Path("id") int id, @Body UpdateDoctorDTO doctor);

    @PUT("doctor/rating/{id}")
    Call<ResponseWrapper<Void>> updateDoctorRating(
            @Path("id") int id,
            @Body RequestBody rating // Số rating dạng text/plain
    );

    // Schedule APIs
    @POST("doctor/doctorSchedule")
    Call<ResponseWrapper<DoctorScheduleResponseDTO>> createDoctorSchedule(@Body DoctorScheduleDTO schedule);

    @GET("doctor/doctorSchedule")
    Call<ResponseWrapper<List<DoctorScheduleResponseDTO>>> getAllSchedules(
            @Query("page") int page,
            @Query("limit") int limit
    );

    // Certification APIs
    @Multipart
    @POST("doctor/certification")
    Call<ResponseWrapper<Void>> uploadCertification(
            @Part MultipartBody.Part file,
            @Part("doctorId") RequestBody doctorId
    );

    // Achievement APIs
    @POST("doctor/achievement")
    Call<ResponseWrapper<AchievementResponseDTO>> createAchievement(@Body AchievementDTO achievement);

    @GET("doctor/achievement/{id}")
    Call<ResponseWrapper<AchievementResponseDTO>> getAchievementById(@Path("id") int id);

    @PUT("doctor/achievement/{id}")
    Call<ResponseWrapper<AchievementResponseDTO>> updateAchievement(
            @Path("id") int id,
            @Body AchievementDTO achievement
    );

    @DELETE("doctor/achievement/{id}")
    Call<ResponseWrapper<Void>> deleteAchievement(@Path("id") int id);

    // Specialty APIs
    @POST("doctor/specialty")
    Call<ResponseWrapper<SpecialtyResponseDTO>> createSpecialty(@Body SpecialtyDTO specialty);

    @GET("doctor/specialty/{id}")
    Call<ResponseWrapper<SpecialtyResponseDTO>> getSpecialtyById(@Path("id") int id);

    @PUT("doctor/specialty/{id}")
    Call<ResponseWrapper<SpecialtyResponseDTO>> updateSpecialty(
            @Path("id") int id,
            @Body SpecialtyDTO specialty
    );

    @DELETE("doctor/specialty/{id}")
    Call<ResponseWrapper<Void>> deleteSpecialty(@Path("id") int id);
}
