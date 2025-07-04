package com.example.schedulemedical.data.repository;

import android.content.Context;
import android.util.Log;

import com.example.schedulemedical.data.api.ApiClient;
import com.example.schedulemedical.data.api.AppointmentApiService;
import com.example.schedulemedical.data.api.AuthApiService;
import com.example.schedulemedical.data.api.DoctorApiService;
import com.example.schedulemedical.data.api.HospitalApiService;
import com.example.schedulemedical.model.dto.response.ApiResponse;
import com.example.schedulemedical.model.dto.response.HospitalListResponse;
import com.example.schedulemedical.model.dto.response.ProfileResponse;
import com.example.schedulemedical.model.dto.response.HospitalResponse;
import com.example.schedulemedical.utils.AuthManager;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.util.List;

public class HomeRepository {
    private static final String TAG = "HomeRepository";
    
    private Context context;
    private AuthManager authManager;
    private AuthApiService authApi;
    private AppointmentApiService appointmentApi;
    private DoctorApiService doctorApi;
    private HospitalApiService hospitalApi;
    
    public HomeRepository(Context context) {
        this.context = context;
        this.authManager = new AuthManager(context);
        this.authApi = ApiClient.getAuthApiService();
        this.appointmentApi = ApiClient.getAppointmentApiService();
        this.doctorApi = ApiClient.getDoctorApiService();
        this.hospitalApi = ApiClient.getHospitalApiService();
    }
    
    // Callback interfaces
    public interface DataCallback<T> {
        void onSuccess(T data);
        void onError(String error);
    }
    
    public interface DashboardCallback {
        void onDashboardDataLoaded(DashboardData data);
        void onError(String error);
    }
    
    // Dashboard data model
    public static class DashboardData {
        private ProfileResponse userProfile;
        private Object appointmentCounts;
        private Object upcomingAppointments;
        private Object topDoctors;
        private Object nearbyHospitals;
        
        public DashboardData() {}
        
        // Getters and Setters
        public ProfileResponse getUserProfile() {
            return userProfile;
        }
        
        public void setUserProfile(ProfileResponse userProfile) {
            this.userProfile = userProfile;
        }
        
        public Object getAppointmentCounts() {
            return appointmentCounts;
        }
        
        public void setAppointmentCounts(Object appointmentCounts) {
            this.appointmentCounts = appointmentCounts;
        }
        
        public Object getUpcomingAppointments() {
            return upcomingAppointments;
        }
        
        public void setUpcomingAppointments(Object upcomingAppointments) {
            this.upcomingAppointments = upcomingAppointments;
        }
        
        public Object getTopDoctors() {
            return topDoctors;
        }
        
        public void setTopDoctors(Object topDoctors) {
            this.topDoctors = topDoctors;
        }
        
        public Object getNearbyHospitals() {
            return nearbyHospitals;
        }
        
        public void setNearbyHospitals(Object nearbyHospitals) {
            this.nearbyHospitals = nearbyHospitals;
        }
    }
    
    // Load complete dashboard data
    public void loadDashboardData(DashboardCallback callback) {
        DashboardData dashboardData = new DashboardData();
        
        // Load user profile first
        loadUserProfile(new DataCallback<ProfileResponse>() {
            @Override
            public void onSuccess(ProfileResponse profile) {
                dashboardData.setUserProfile(profile);
                
                // Load appointment counts
                loadAppointmentCounts(new DataCallback<Object>() {
                    @Override
                    public void onSuccess(Object counts) {
                        dashboardData.setAppointmentCounts(counts);
                        
                        // Load upcoming appointments
                        loadUpcomingAppointments(new DataCallback<Object>() {
                            @Override
                            public void onSuccess(Object appointments) {
                                dashboardData.setUpcomingAppointments(appointments);
                                
                                // Load top doctors
                                loadTopDoctors(new DataCallback<Object>() {
                                    @Override
                                    public void onSuccess(Object doctors) {
                                        dashboardData.setTopDoctors(doctors);
                                        
                                        // Load nearby hospitals - final call
                                        loadNearbyHospitals(new DataCallback<List<HospitalResponse>>() {
                                            @Override
                                            public void onSuccess(List<HospitalResponse> hospitals) {
                                                dashboardData.setNearbyHospitals(hospitals);
                                                callback.onDashboardDataLoaded(dashboardData);
                                            }
                                            
                                            @Override
                                            public void onError(String error) {
                                                // Even if this fails, return data
                                                callback.onDashboardDataLoaded(dashboardData);
                                            }
                                        });
                                    }
                                    
                                    @Override
                                    public void onError(String error) {
                                        Log.e(TAG, "Failed to load top doctors: " + error);
                                        callback.onDashboardDataLoaded(dashboardData);
                                    }
                                });
                            }
                            
                            @Override
                            public void onError(String error) {
                                Log.e(TAG, "Failed to load upcoming appointments: " + error);
                                callback.onDashboardDataLoaded(dashboardData);
                            }
                        });
                    }
                    
                    @Override
                    public void onError(String error) {
                        Log.e(TAG, "Failed to load appointment counts: " + error);
                        callback.onDashboardDataLoaded(dashboardData);
                    }
                });
            }
            
            @Override
            public void onError(String error) {
                Log.e(TAG, "Failed to load user profile: " + error);
                callback.onError(error);
            }
        });
    }
    
    // Load user profile
    public void loadUserProfile(DataCallback<ProfileResponse> callback) {
        authApi.getProfile().enqueue(new Callback<ApiResponse<ProfileResponse>>() {
            @Override
            public void onResponse(Call<ApiResponse<ProfileResponse>> call, Response<ApiResponse<ProfileResponse>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getData() != null) {
                    callback.onSuccess(response.body().getData());
                } else {
                    callback.onError("Failed to load profile");
                }
            }
            @Override
            public void onFailure(Call<ApiResponse<ProfileResponse>> call, Throwable t) {
                Log.e(TAG, "Profile API call failed", t);
                callback.onError("Network error: " + t.getMessage());
            }
        });
    }
    
    // Load appointment counts for current user
    public void loadAppointmentCounts(DataCallback<Object> callback) {
        Integer userId = Integer.valueOf(authManager.getUserId());
        if (userId == null) {
            callback.onError("User not logged in");
            return;
        }
        
        appointmentApi.getAppointmentCounts(userId, null).enqueue(new Callback<ApiResponse<Object>>() {
            @Override
            public void onResponse(Call<ApiResponse<Object>> call, Response<ApiResponse<Object>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body().getData());
                } else {
                    callback.onError("Failed to load appointment counts");
                }
            }
            
            @Override
            public void onFailure(Call<ApiResponse<Object>> call, Throwable t) {
                Log.e(TAG, "Appointment counts API call failed", t);
                callback.onError("Network error: " + t.getMessage());
            }
        });
    }
    
    // Load upcoming appointments for current user
    public void loadUpcomingAppointments(DataCallback<Object> callback) {
        Integer userId = Integer.valueOf(authManager.getUserId());
        if (userId == null) {
            callback.onError("User not logged in");
            return;
        }
        
        appointmentApi.getAppointments(1, 5, userId, null, "SCHEDULED").enqueue(new Callback<ApiResponse<Object>>() {
            @Override
            public void onResponse(Call<ApiResponse<Object>> call, Response<ApiResponse<Object>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body().getData());
                } else {
                    callback.onError("Failed to load upcoming appointments");
                }
            }
            
            @Override
            public void onFailure(Call<ApiResponse<Object>> call, Throwable t) {
                Log.e(TAG, "Upcoming appointments API call failed", t);
                callback.onError("Network error: " + t.getMessage());
            }
        });
    }
    
    // Load top rated doctors
    public void loadTopDoctors(DataCallback<Object> callback) {
        doctorApi.getTopRatedDoctors().enqueue(new Callback<ApiResponse<Object>>() {
            @Override
            public void onResponse(Call<ApiResponse<Object>> call, Response<ApiResponse<Object>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body().getData());
                } else {
                    callback.onError("Failed to load top doctors");
                }
            }
            
            @Override
            public void onFailure(Call<ApiResponse<Object>> call, Throwable t) {
                Log.e(TAG, "Top doctors API call failed", t);
                callback.onError("Network error: " + t.getMessage());
            }
        });
    }
    
    // Load nearby hospitals (mock data for now since we don't have location)
    public void loadNearbyHospitals(DataCallback<List<HospitalResponse>> callback) {
        hospitalApi.getAllHospitals(1, 5, null, null, null, null, null, null, null)
                .enqueue(new Callback<HospitalListResponse>() {
            @Override
            public void onResponse(Call<HospitalListResponse> call, Response<HospitalListResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body().getData());
                } else {
                    callback.onError("Failed to load hospitals");
                }
            }
            @Override
            public void onFailure(Call<HospitalListResponse> call, Throwable t) {
                Log.e(TAG, "Hospitals API call failed", t);
                callback.onError("Network error: " + t.getMessage());
            }
        });
    }
    
    // Helper methods
    public boolean isUserLoggedIn() {
        return authManager.isLoggedIn();
    }
    
    public String getUserName() {
        return authManager.getUserName();
    }
    
    public String getUserEmail() {
        return authManager.getUserEmail();
    }
} 