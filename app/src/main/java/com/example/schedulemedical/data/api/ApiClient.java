package com.example.schedulemedical.data.api;

import android.content.Context;
import android.content.SharedPreferences;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import java.io.IOException;

public class ApiClient {
    // Updated to match the NestJS backend URL
    private static final String BASE_URL = "http://10.0.2.2:3000/";
    private static Retrofit retrofit;
    private static Context appContext;

    public static void init(Context context) {
        appContext = context.getApplicationContext();
    }

    private static Retrofit getRetrofit() {
        if (retrofit == null) {
            HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
            logging.setLevel(HttpLoggingInterceptor.Level.BODY);

            // Authentication Interceptor
            Interceptor authInterceptor = new Interceptor() {
                @Override
                public Response intercept(Chain chain) throws IOException {
                    Request original = chain.request();
                    
                    // Get token from SharedPreferences
                    SharedPreferences prefs = appContext.getSharedPreferences("app_prefs", Context.MODE_PRIVATE);
                    String accessToken = prefs.getString("access_token", null);
                    
                    Request.Builder builder = original.newBuilder()
                            .header("Content-Type", "application/json");
                    
                    // Add Authorization header if token exists
                    if (accessToken != null && !accessToken.isEmpty()) {
                        builder.header("Authorization", "Bearer " + accessToken);
                    }
                    
                    Request request = builder.build();
                    return chain.proceed(request);
                }
            };

            OkHttpClient client = new OkHttpClient.Builder()
                    .addInterceptor(authInterceptor)
                    .addInterceptor(logging)
                    .build();

            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(client)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }

    // Create API service instances
    public static ApiService getApiService() {
        return getRetrofit().create(ApiService.class);
    }

    public static AuthApiService getAuthApiService() {
        return getRetrofit().create(AuthApiService.class);
    }

    public static DoctorApiService getDoctorApiService() {
        return getRetrofit().create(DoctorApiService.class);
    }

    public static HospitalApiService getHospitalApiService() {
        return getRetrofit().create(HospitalApiService.class);
    }

    public static AppointmentApiService getAppointmentApiService() {
        return getRetrofit().create(AppointmentApiService.class);
    }

    public static PaymentApiService getPaymentApiService() {
        return getRetrofit().create(PaymentApiService.class);
    }

    public static UserApiService getUserApiService() {
        return getRetrofit().create(UserApiService.class);
    }

    public static ServiceApiService getServiceApiService() {
        return getRetrofit().create(ServiceApiService.class);
    }

    // Method to clear and recreate retrofit instance (useful for logout)
    public static void clearInstance() {
        retrofit = null;
    }
}

