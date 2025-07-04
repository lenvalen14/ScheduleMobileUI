package com.example.schedulemedical.data.api;

import com.example.schedulemedical.model.dto.response.ApiResponse;
import com.example.schedulemedical.model.dto.response.HospitalResponse;
import com.example.schedulemedical.model.dto.response.HospitalListResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface HospitalApiService {
    
    // Get all hospitals with filters and pagination
    @GET("hospital/hospitals")
    Call<HospitalListResponse> getAllHospitals(
        @Query("page") int page,
        @Query("limit") int limit,
        @Query("search") String search,
        @Query("type") String type,
        @Query("location") String location,
        @Query("specialty") String specialty,
        @Query("latitude") Double latitude,
        @Query("longitude") Double longitude,
        @Query("radius") Double radius
    );
    
    // Get hospital by ID
    @GET("hospital/{id}")
    Call<ApiResponse<HospitalResponse>> getHospitalById(@Path("id") int hospitalId);
    
    // Search hospitals by string
    @GET("hospital/search/{searchString}")
    Call<HospitalListResponse> searchHospitals(
        @Path("searchString") String searchString,
        @Query("skip") int skip,
        @Query("take") int take
    );
    
    // Filter hospitals
    @GET("hospital/filter")
    Call<HospitalListResponse> filterHospitals(
        @Query("name") String name,
        @Query("type") String type,
        @Query("location") String location,
        @Query("minRating") Float minRating,
        @Query("maxRating") Float maxRating,
        @Query("verified") Boolean verified,
        @Query("skip") int skip,
        @Query("take") int take
    );
    
    // Search hospitals by location
    @GET("hospital/search-by-location")
    Call<HospitalListResponse> searchHospitalsByLocation(
        @Query("latitude") double latitude,
        @Query("longitude") double longitude,
        @Query("radius") double radius,
        @Query("limit") int limit
    );
    
    // Get hospitals by specialty
    @GET("hospital/by-specialty/{specialtyId}")
    Call<HospitalListResponse> getHospitalsBySpecialty(
        @Path("specialtyId") int specialtyId,
        @Query("page") int page,
        @Query("limit") int limit
    );
    
    // Get nearby hospitals
    @GET("hospital/nearby")
    Call<HospitalListResponse> getNearbyHospitals(
        @Query("latitude") double latitude,
        @Query("longitude") double longitude,
        @Query("limit") int limit
    );
    
    // Get featured hospitals
    @GET("hospital/featured")
    Call<HospitalListResponse> getFeaturedHospitals(@Query("limit") int limit);
    
    // Get hospital statistics
    @GET("hospital/statistics")
    Call<ApiResponse<Object>> getHospitalStatistics();
    
    // Get hospital images
    @GET("hospital/{id}/images")
    Call<ApiResponse<Object>> getHospitalImages(@Path("id") int hospitalId);
} 