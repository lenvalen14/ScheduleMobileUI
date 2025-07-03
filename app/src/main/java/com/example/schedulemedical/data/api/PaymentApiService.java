package com.example.schedulemedical.data.api;

import com.example.schedulemedical.model.dto.request.MomoPaymentRequest;
import com.example.schedulemedical.model.dto.request.VnpayPaymentRequest;
import com.example.schedulemedical.model.dto.response.ApiResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface PaymentApiService {
    
    // Get monthly payment statistics
    @GET("payment/statistics/monthly")
    Call<ApiResponse<Object>> getMonthlyStatistics(
        @Query("year") int year,
        @Query("month") int month
    );
    
    // Get quarterly payment statistics
    @GET("payment/statistics/quarterly")
    Call<ApiResponse<Object>> getQuarterlyStatistics(
        @Query("year") int year,
        @Query("quarter") int quarter
    );
    
    // Get yearly payment statistics
    @GET("payment/statistics/yearly")
    Call<ApiResponse<Object>> getYearlyStatistics(@Query("year") int year);
    
    // Get custom period statistics
    @GET("payment/statistics/custom")
    Call<ApiResponse<Object>> getCustomPeriodStatistics(
        @Query("startDate") String startDate,
        @Query("endDate") String endDate
    );
    
    // Create MoMo payment
    @POST("payment/momo")
    Call<ApiResponse<Object>> createMomoPayment(@Body MomoPaymentRequest request);
    
    // Create VNPay payment
    @POST("payment/vnpay")
    Call<ApiResponse<Object>> createVnpayPayment(@Body VnpayPaymentRequest request);
    
    // VNPay callback endpoints
    @GET("payment/vnpay/return")
    Call<ApiResponse<Object>> vnpayReturn(@Query("vnp_ResponseCode") String responseCode);
    
    // Get payments for admin
    @GET("payment/admin/payments")
    Call<ApiResponse<Object>> getPaymentsForAdmin(
        @Query("page") int page,
        @Query("limit") int limit,
        @Query("status") String status,
        @Query("method") String method
    );
    
    // Get payment by ID for admin
    @GET("payment/admin/payments/{id}")
    Call<ApiResponse<Object>> getPaymentByIdForAdmin(@Path("id") int paymentId);
    
    // Update payment status
    @PATCH("payment/admin/payments/{id}/status")
    Call<ApiResponse<Object>> updatePaymentStatus(
        @Path("id") int paymentId,
        @Body Object statusUpdateRequest
    );
} 