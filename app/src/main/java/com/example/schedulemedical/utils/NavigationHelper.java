package com.example.schedulemedical.utils;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.example.schedulemedical.ui.booking.BookingWizardActivity;
import com.example.schedulemedical.ui.chat.ChatActivity;
import com.example.schedulemedical.ui.doctorprofile.DoctorProfileActivity;
import com.example.schedulemedical.ui.filterDoctor.FilterDoctorActivity;
import com.example.schedulemedical.ui.forgotPassword.ForgotPasswordActivity;
import com.example.schedulemedical.ui.forgotPassword.SendOTPActivity;
import com.example.schedulemedical.ui.forgotPassword.VerifyCodeActivity;
import com.example.schedulemedical.ui.home.HomeActivity;
import com.example.schedulemedical.ui.hospital.HospitalActivity;
import com.example.schedulemedical.ui.login.LoginActivity;
import com.example.schedulemedical.ui.register.RegisterActivity;
import com.example.schedulemedical.ui.schedule.MyScheduledActivity;
import com.example.schedulemedical.ui.schedule.ScheduleActivity;
import com.example.schedulemedical.ui.userprofile.UserProfileActivity;

/**
 * Helper class để quản lý tất cả các Intent điều hướng trong ứng dụng
 */
public class NavigationHelper {

    // Constants cho Intent extras
    public static final String EXTRA_DOCTOR_ID = "doctor_id";
    public static final String EXTRA_HOSPITAL_ID = "hospital_id";
    public static final String EXTRA_SPECIALTY_ID = "specialty_id";
    public static final String EXTRA_APPOINTMENT_ID = "appointment_id";
    public static final String EXTRA_EMAIL = "email";
    public static final String EXTRA_OTP_CODE = "otp_code";
    public static final String EXTRA_FILTER_DATA = "filter_data";

    /**
     * Điều hướng đến LoginActivity
     */
    public static void navigateToLogin(Context context) {
        Intent intent = new Intent(context, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        context.startActivity(intent);
    }

    /**
     * Điều hướng đến RegisterActivity
     */
    public static void navigateToRegister(Context context) {
        Intent intent = new Intent(context, RegisterActivity.class);
        context.startActivity(intent);
    }

    /**
     * Điều hướng đến HomeActivity
     */
    public static void navigateToHome(Context context) {
        Intent intent = new Intent(context, HomeActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        context.startActivity(intent);
    }

    /**
     * Điều hướng đến ForgotPasswordActivity
     */
    public static void navigateToForgotPassword(Context context) {
        Intent intent = new Intent(context, SendOTPActivity.class);
        context.startActivity(intent);
    }

    /**
     * Điều hướng đến SendOTPActivity
     */
    public static void navigateToSendOTP(Context context, String email) {
        Intent intent = new Intent(context, SendOTPActivity.class);
        intent.putExtra(EXTRA_EMAIL, email);
        context.startActivity(intent);
    }

    /**
     * Điều hướng đến VerifyCodeActivity
     */
    public static void navigateToVerifyCode(Context context, String email, String otpCode) {
        Intent intent = new Intent(context, VerifyCodeActivity.class);
        intent.putExtra(EXTRA_EMAIL, email);
        intent.putExtra(EXTRA_OTP_CODE, otpCode);
        context.startActivity(intent);
    }

    /**
     * Điều hướng đến HospitalActivity
     */
    public static void navigateToHospital(Context context) {
        Intent intent = new Intent(context, HospitalActivity.class);
        context.startActivity(intent);
    }

    /**
     * Điều hướng đến HospitalActivity với ID cụ thể
     */
    public static void navigateToHospital(Context context, int hospitalId) {
        Intent intent = new Intent(context, HospitalActivity.class);
        intent.putExtra(EXTRA_HOSPITAL_ID, hospitalId);
        context.startActivity(intent);
    }

    /**
     * Điều hướng đến FilterDoctorActivity
     */
    public static void navigateToFilterDoctor(Context context) {
        Intent intent = new Intent(context, FilterDoctorActivity.class);
        context.startActivity(intent);
    }

    /**
     * Điều hướng đến FilterDoctorActivity với dữ liệu filter
     */
    public static void navigateToFilterDoctor(Context context, Bundle filterData) {
        Intent intent = new Intent(context, FilterDoctorActivity.class);
        intent.putExtra(EXTRA_FILTER_DATA, filterData);
        context.startActivity(intent);
    }

    /**
     * Điều hướng đến DoctorProfileActivity
     */
    public static void navigateToDoctorProfile(Context context, int doctorId) {
        Intent intent = new Intent(context, DoctorProfileActivity.class);
        intent.putExtra(EXTRA_DOCTOR_ID, doctorId);
        context.startActivity(intent);
    }

    /**
     * Điều hướng đến ScheduleActivity
     */
    public static void navigateToSchedule(Context context) {
        Intent intent = new Intent(context, ScheduleActivity.class);
        context.startActivity(intent);
    }

    /**
     * Điều hướng đến ScheduleActivity với ID bác sĩ
     */
    public static void navigateToSchedule(Context context, int doctorId) {
        Intent intent = new Intent(context, ScheduleActivity.class);
        intent.putExtra(EXTRA_DOCTOR_ID, doctorId);
        context.startActivity(intent);
    }

    /**
     * Điều hướng đến MyScheduledActivity
     */
    public static void navigateToMyScheduled(Context context) {
        Intent intent = new Intent(context, MyScheduledActivity.class);
        context.startActivity(intent);
    }

    /**
     * Điều hướng đến UserProfileActivity
     */
    public static void navigateToUserProfile(Context context) {
        Intent intent = new Intent(context, UserProfileActivity.class);
        context.startActivity(intent);
    }

    /**
     * Điều hướng đến ChatActivity
     */
    public static void navigateToChat(Context context) {
        Intent intent = new Intent(context, ChatActivity.class);
        context.startActivity(intent);
    }

    /**
     * Điều hướng đến ChatActivity với ID cuộc trò chuyện
     */
    public static void navigateToChat(Context context, int appointmentId) {
        Intent intent = new Intent(context, ChatActivity.class);
        intent.putExtra(EXTRA_APPOINTMENT_ID, appointmentId);
        context.startActivity(intent);
    }

    /**
     * Điều hướng đến BookingWizardActivity - Booking appointment flow
     */
    public static void navigateToBookingWizard(Context context) {
        Intent intent = new Intent(context, BookingWizardActivity.class);
        context.startActivity(intent);
    }

    /**
     * Quay lại màn hình trước đó
     */
    public static void goBack(Context context) {
        if (context instanceof android.app.Activity) {
            ((android.app.Activity) context).finish();
        }
    }

    /**
     * Điều hướng với kết quả trả về
     */
    public static void navigateForResult(android.app.Activity activity, Intent intent, int requestCode) {
        activity.startActivityForResult(intent, requestCode);
    }
} 