package com.example.schedulemedical.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.example.schedulemedical.data.api.ApiClient;
import com.example.schedulemedical.data.api.AuthApiService;
import com.example.schedulemedical.model.dto.request.LoginRequest;
import com.example.schedulemedical.model.dto.request.LogoutRequest;
import com.example.schedulemedical.model.dto.request.RefreshTokenRequest;
import com.example.schedulemedical.model.dto.response.LoginResponse;
import com.example.schedulemedical.model.dto.response.RefreshTokenResponse;
import com.example.schedulemedical.model.dto.response.ApiResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AuthManager {
    private static final String TAG = "AuthManager";
    private static final String PREFS_NAME = "app_prefs";
    private static final String KEY_ACCESS_TOKEN = "access_token";
    private static final String KEY_REFRESH_TOKEN = "refresh_token";
    private static final String KEY_USER_ID = "user_id";
    private static final String KEY_USER_EMAIL = "user_email";
    private static final String KEY_USER_ROLE = "user_role";
    private static final String KEY_USER_NAME = "user_name";
    private static final String KEY_IS_LOGGED_IN = "is_logged_in";
    
    private Context context;
    private SharedPreferences prefs;
    private AuthApiService authApiService;
    
    public AuthManager(Context context) {
        this.context = context;
        this.prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        this.authApiService = ApiClient.getAuthApiService();
    }
    
    // Login method
    public void login(String email, String password, AuthCallback callback) {
        LoginRequest request = new LoginRequest(email, password);
        
        authApiService.login(request).enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    LoginResponse loginResponse = response.body();
                    
                    Log.d(TAG, "Login response received: " + loginResponse.getMessage());
                    
                    if (loginResponse.getData() != null) {
                        LoginResponse.LoginData data = loginResponse.getData();
                        
                        // Debug logging
                        Log.d(TAG, "Access token: " + (data.getAccessToken() != null ? "Present" : "NULL"));
                        Log.d(TAG, "Refresh token: " + (data.getRefreshToken() != null ? "Present" : "NULL"));
                        Log.d(TAG, "User ID (sub): " + data.getSub());
                        
                        // Save tokens and user info
                        saveTokens(
                            data.getAccessToken(),
                            data.getRefreshToken(),
                            data.getSub()
                        );
                        
                        // Get user profile after successful login
                        fetchUserProfile(callback);
                    } else {
                        Log.e(TAG, "Login response data is null");
                        callback.onError("Invalid login response");
                    }
                } else {
                    String errorMessage = "Login failed";
                    if (response.errorBody() != null) {
                        try {
                            errorMessage = response.errorBody().string();
                        } catch (Exception e) {
                            Log.e(TAG, "Error reading error body", e);
                        }
                    }
                    Log.e(TAG, "Login failed with response code: " + response.code());
                    callback.onError(errorMessage);
                }
            }
            
            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                Log.e(TAG, "Login request failed", t);
                callback.onError("Network error: " + t.getMessage());
            }
        });
    }
    
    // Fetch user profile after login
    private void fetchUserProfile(AuthCallback callback) {
        authApiService.getProfile().enqueue(new Callback<com.example.schedulemedical.model.dto.response.ProfileResponse>() {
            @Override
            public void onResponse(Call<com.example.schedulemedical.model.dto.response.ProfileResponse> call, 
                                 Response<com.example.schedulemedical.model.dto.response.ProfileResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    com.example.schedulemedical.model.dto.response.ProfileResponse profile = response.body();
                    
                    // Save user info
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putString(KEY_USER_EMAIL, profile.getEmail());
                    editor.putString(KEY_USER_ROLE, profile.getRole());
                    editor.putString(KEY_USER_NAME, profile.getFullName());
                    editor.putBoolean(KEY_IS_LOGGED_IN, true);
                    editor.apply();
                    
                    callback.onSuccess("Login successful");
                } else {
                    // Even if profile fetch fails, consider login successful
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putBoolean(KEY_IS_LOGGED_IN, true);
                    editor.apply();
                    
                    callback.onSuccess("Login successful");
                }
            }
            
            @Override
            public void onFailure(Call<com.example.schedulemedical.model.dto.response.ProfileResponse> call, Throwable t) {
                Log.e(TAG, "Profile fetch failed", t);
                // Even if profile fetch fails, consider login successful
                SharedPreferences.Editor editor = prefs.edit();
                editor.putBoolean(KEY_IS_LOGGED_IN, true);
                editor.apply();
                
                callback.onSuccess("Login successful");
            }
        });
    }
    
    // Logout method
    public void logout(AuthCallback callback) {
        String refreshToken = getRefreshToken();
        
        if (refreshToken != null) {
            LogoutRequest request = new LogoutRequest(refreshToken);
            
            authApiService.logout(request).enqueue(new Callback<ApiResponse<String>>() {
                @Override
                public void onResponse(Call<ApiResponse<String>> call, Response<ApiResponse<String>> response) {
                    // Clear local data regardless of server response
                    clearUserData();
                    callback.onSuccess("Logout successful");
                }
                
                @Override
                public void onFailure(Call<ApiResponse<String>> call, Throwable t) {
                    Log.e(TAG, "Logout request failed", t);
                    // Clear local data even if server request fails
                    clearUserData();
                    callback.onSuccess("Logout successful");
                }
            });
        } else {
            clearUserData();
            callback.onSuccess("Logout successful");
        }
    }
    
    // Refresh token method
    public void refreshToken(AuthCallback callback) {
        String refreshToken = getRefreshToken();
        Integer userId = getUserId();
        
        if (refreshToken != null && userId != null) {
            RefreshTokenRequest request = new RefreshTokenRequest(refreshToken, userId);
            
            authApiService.refreshToken(request).enqueue(new Callback<RefreshTokenResponse>() {
                @Override
                public void onResponse(Call<RefreshTokenResponse> call, Response<RefreshTokenResponse> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        RefreshTokenResponse refreshResponse = response.body();
                        
                        // Save new tokens
                        saveTokens(
                            refreshResponse.getAccessToken(),
                            refreshResponse.getRefreshToken(),
                            userId
                        );
                        
                        // Update user info if available
                        if (refreshResponse.getUser() != null) {
                            SharedPreferences.Editor editor = prefs.edit();
                            editor.putString(KEY_USER_EMAIL, refreshResponse.getUser().getEmail());
                            editor.putString(KEY_USER_ROLE, refreshResponse.getUser().getRole());
                            editor.putString(KEY_USER_NAME, refreshResponse.getUser().getFullName());
                            editor.apply();
                        }
                        
                        callback.onSuccess("Token refreshed successfully");
                    } else {
                        // If refresh fails, logout user
                        clearUserData();
                        callback.onError("Session expired. Please login again.");
                    }
                }
                
                @Override
                public void onFailure(Call<RefreshTokenResponse> call, Throwable t) {
                    Log.e(TAG, "Token refresh failed", t);
                    // If refresh fails, logout user
                    clearUserData();
                    callback.onError("Session expired. Please login again.");
                }
            });
        } else {
            clearUserData();
            callback.onError("No valid session found. Please login again.");
        }
    }
    
    // Save tokens to SharedPreferences
    private void saveTokens(String accessToken, String refreshToken, Integer userId) {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(KEY_ACCESS_TOKEN, accessToken);
        editor.putString(KEY_REFRESH_TOKEN, refreshToken);
        
        // Handle null userId - try to extract from JWT token if null
        if (userId != null) {
            editor.putInt(KEY_USER_ID, userId);
            Log.d(TAG, "Saved user ID: " + userId);
        } else {
            // Try to extract user ID from JWT token
            Integer extractedUserId = extractUserIdFromJWT(accessToken);
            if (extractedUserId != null) {
                editor.putInt(KEY_USER_ID, extractedUserId);
                Log.d(TAG, "Extracted and saved user ID from JWT: " + extractedUserId);
            } else {
                editor.putInt(KEY_USER_ID, -1); // Default value
                Log.w(TAG, "Could not extract user ID from JWT, using default value -1");
            }
        }
        
        editor.apply();
        
        Log.d(TAG, "Tokens saved successfully");
    }
    
    // Extract user ID from JWT token
    private Integer extractUserIdFromJWT(String token) {
        if (token == null || token.isEmpty()) {
            return null;
        }
        
        try {
            // Split JWT token into parts
            String[] parts = token.split("\\.");
            if (parts.length != 3) {
                Log.e(TAG, "Invalid JWT token format");
                return null;
            }
            
            // Decode payload (base64)
            String payload = parts[1];
            // Add padding if needed
            while (payload.length() % 4 != 0) {
                payload += "=";
            }
            
            byte[] decodedBytes = android.util.Base64.decode(payload, android.util.Base64.URL_SAFE);
            String decodedPayload = new String(decodedBytes);
            
            Log.d(TAG, "JWT Payload: " + decodedPayload);
            
            // Simple JSON parsing to extract "sub" field
            if (decodedPayload.contains("\"sub\":")) {
                int subIndex = decodedPayload.indexOf("\"sub\":");
                int commaIndex = decodedPayload.indexOf(",", subIndex);
                int braceIndex = decodedPayload.indexOf("}", subIndex);
                
                int endIndex = (commaIndex != -1 && commaIndex < braceIndex) ? commaIndex : braceIndex;
                if (endIndex != -1) {
                    String subValue = decodedPayload.substring(subIndex + 6, endIndex).trim();
                    return Integer.parseInt(subValue);
                }
            }
            
        } catch (Exception e) {
            Log.e(TAG, "Error extracting user ID from JWT", e);
        }
        
        return null;
    }
    
    // Clear all user data
    public void clearUserData() {
        // Stop notification service before clearing data
        try {
            com.example.schedulemedical.services.NotificationService.stopService(context);
        } catch (Exception e) {
            Log.e(TAG, "Error stopping notification service", e);
        }
        
        SharedPreferences.Editor editor = prefs.edit();
        editor.remove(KEY_ACCESS_TOKEN);
        editor.remove(KEY_REFRESH_TOKEN);
        editor.remove(KEY_USER_ID);
        editor.remove(KEY_USER_EMAIL);
        editor.remove(KEY_USER_ROLE);
        editor.remove(KEY_USER_NAME);
        editor.putBoolean(KEY_IS_LOGGED_IN, false);
        editor.apply();
        
        // Clear API client instance to remove cached tokens
        ApiClient.clearInstance();
        
        Log.d(TAG, "User data cleared");
    }
    
    // Getter methods
    public String getAccessToken() {
        return prefs.getString(KEY_ACCESS_TOKEN, null);
    }
    
    public String getRefreshToken() {
        return prefs.getString(KEY_REFRESH_TOKEN, null);
    }
    
    public Integer getUserId() {
        int userId = prefs.getInt(KEY_USER_ID, -1);
        return userId != -1 ? userId : null;
    }
    
    public String getUserEmail() {
        return prefs.getString(KEY_USER_EMAIL, null);
    }
    
    public String getUserRole() {
        return prefs.getString(KEY_USER_ROLE, null);
    }
    
    public String getUserName() {
        return prefs.getString(KEY_USER_NAME, null);
    }
    
    public boolean isLoggedIn() {
        return prefs.getBoolean(KEY_IS_LOGGED_IN, false) && getAccessToken() != null;
    }
    
    public boolean isAdmin() {
        return "ADMIN".equals(getUserRole());
    }
    
    public boolean isDoctor() {
        return "DOCTOR".equals(getUserRole());
    }
    
    public boolean isUser() {
        return "USER".equals(getUserRole());
    }
    
    // Update user info
    public void updateUserInfo(String fullName, String phoneNumber) {
        SharedPreferences.Editor editor = prefs.edit();
        if (fullName != null) {
            editor.putString(KEY_USER_NAME, fullName);
        }
        // You can add phone number key if needed
        editor.apply();
        Log.d(TAG, "User info updated");
    }
    
    // Callback interface for authentication operations
    public interface AuthCallback {
        void onSuccess(String message);
        void onError(String error);
    }
} 