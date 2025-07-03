package com.example.schedulemedical.ui.profile;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.example.schedulemedical.R;
import com.example.schedulemedical.data.api.ApiClient;
import com.example.schedulemedical.data.api.AuthApiService;
import com.example.schedulemedical.data.api.UserApiService;
import com.example.schedulemedical.model.dto.response.ApiResponse;
import com.example.schedulemedical.model.dto.response.ProfileResponse;
import com.example.schedulemedical.ui.login.LoginActivity;
import com.example.schedulemedical.utils.AuthManager;
import com.google.android.material.card.MaterialCardView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileActivity extends AppCompatActivity {
    private static final String TAG = "ProfileActivity";
    
    // UI Components
    private ImageView ivBack;
    private ImageView ivProfileImage;
    private MaterialCardView cardChangePhoto;
    private EditText etFullName;
    private EditText etEmail;
    private EditText etPhoneNumber;
    private EditText etDateOfBirth;
    private RadioGroup rgGender;
    private RadioButton rbMale;
    private RadioButton rbFemale;
    private RadioButton rbOther;
    private EditText etAddress;
    private EditText etEmergencyContact;
    private Button btnSaveProfile;
    private Button btnLogout;
    private TextView tvChangePassword;
    
    // Data
    private ProfileResponse currentProfile;
    private Uri selectedImageUri;
    private Calendar selectedDateOfBirth;
    
    // Services
    private AuthApiService authApiService;
    private UserApiService userApiService;
    private AuthManager authManager;
    private ProgressDialog progressDialog;
    
    // Date formatter
    private SimpleDateFormat dateFormatter = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
    private SimpleDateFormat apiDateFormatter = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
    
    // Image picker launcher
    private ActivityResultLauncher<Intent> imagePickerLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        
        initializeServices();
        initializeViews();
        setupImagePickerLauncher();
        setupClickListeners();
        setupProgressDialog();
        loadUserProfile();
    }
    
    private void initializeServices() {
        ApiClient.init(this);
        authApiService = ApiClient.getAuthApiService();
        userApiService = ApiClient.getUserApiService();
        authManager = new AuthManager(this);
        selectedDateOfBirth = Calendar.getInstance();
    }
    
    private void initializeViews() {
        ivBack = findViewById(R.id.ivBack);
        // ivProfileImage = findViewById(R.id.ivProfileImage); // Not in layout
        // cardChangePhoto = findViewById(R.id.cardChangePhoto); // Not in layout
        etFullName = findViewById(R.id.etFullName);
        etEmail = findViewById(R.id.etEmail);
        etPhoneNumber = findViewById(R.id.etPhoneNumber);
        // etDateOfBirth = findViewById(R.id.etDateOfBirth); // Not in layout
        rgGender = findViewById(R.id.rgGender);
        rbMale = findViewById(R.id.rbMale);
        rbFemale = findViewById(R.id.rbFemale);
        rbOther = findViewById(R.id.rbOther);
        // etAddress = findViewById(R.id.etAddress); // Not in layout
        // etEmergencyContact = findViewById(R.id.etEmergencyContact); // Not in layout
        // btnSaveProfile = findViewById(R.id.btnSaveProfile); // Not in layout
        // btnLogout = findViewById(R.id.btnLogout); // Not in layout
        // tvChangePassword = findViewById(R.id.tvChangePassword); // Not in layout
        
        // Disable email editing (usually not changeable)
        if (etEmail != null) {
            etEmail.setEnabled(false);
            etEmail.setFocusable(false);
        }
    }
    
    private void setupImagePickerLauncher() {
        imagePickerLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    selectedImageUri = result.getData().getData();
                    if (selectedImageUri != null) {
                        // Display selected image
                        Glide.with(this)
                                .load(selectedImageUri)
                                .transform(new CircleCrop())
                                .into(ivProfileImage);
                        
                        // TODO: Upload image to server
                        Toast.makeText(this, "Ảnh đã được chọn. Lưu hồ sơ để cập nhật.", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        );
    }
    
    private void setupClickListeners() {
        // Back button
        ivBack.setOnClickListener(v -> finish());
        
        // Change photo
        // cardChangePhoto.setOnClickListener(v -> selectImage()); // View commented out
        
        // Date of birth picker
        // etDateOfBirth.setOnClickListener(v -> showDatePicker()); // View commented out
        // etDateOfBirth.setFocusable(false); // View commented out
        
        // Save profile
        // btnSaveProfile.setOnClickListener(v -> saveProfile()); // View commented out
        
        // Logout
        // btnLogout.setOnClickListener(v -> logout()); // View commented out
        
        // Change password
        // tvChangePassword.setOnClickListener(v -> {
        //     // TODO: Navigate to change password screen
        //     Toast.makeText(this, "Tính năng đổi mật khẩu sẽ có sớm!", Toast.LENGTH_SHORT).show();
        // }); // View commented out
    }
    
    private void setupProgressDialog() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Đang xử lý...");
        progressDialog.setCancelable(false);
    }
    
    private void loadUserProfile() {
        if (!authManager.isLoggedIn()) {
            redirectToLogin();
            return;
        }
        
        showLoading();
        
        authApiService.getProfile().enqueue(new Callback<ProfileResponse>() {
            @Override
            public void onResponse(Call<ProfileResponse> call, Response<ProfileResponse> response) {
                hideLoading();
                
                if (response.isSuccessful() && response.body() != null) {
                    currentProfile = response.body();
                    updateUIWithProfile(currentProfile);
                } else {
                    Toast.makeText(ProfileActivity.this, "Không thể tải thông tin hồ sơ", Toast.LENGTH_LONG).show();
                }
            }
            
            @Override
            public void onFailure(Call<ProfileResponse> call, Throwable t) {
                hideLoading();
                Log.e(TAG, "Failed to load profile", t);
                Toast.makeText(ProfileActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
    
    private void updateUIWithProfile(ProfileResponse profile) {
        // Profile image
        if (ivProfileImage != null && profile.getAvatar() != null && !profile.getAvatar().isEmpty()) {
            Glide.with(this)
                    .load(profile.getAvatar())
                    .transform(new CircleCrop())
                    .placeholder(R.drawable.sample_profile_image)
                    .error(R.drawable.sample_profile_image)
                    .into(ivProfileImage);
        }
        
        // Basic info
        if (etFullName != null) {
            etFullName.setText(profile.getFullName() != null ? profile.getFullName() : "");
        }
        
        if (etEmail != null) {
            etEmail.setText(profile.getEmail() != null ? profile.getEmail() : "");
        }
        
        if (etPhoneNumber != null) {
            etPhoneNumber.setText(profile.getPhone() != null ? profile.getPhone() : "");
        }
        
        // Date of birth
        if (etDateOfBirth != null && profile.getDateOfBirth() != null) {
            try {
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(apiDateFormatter.parse(profile.getDateOfBirth()));
                selectedDateOfBirth = calendar;
                etDateOfBirth.setText(dateFormatter.format(calendar.getTime()));
            } catch (Exception e) {
                Log.e(TAG, "Error parsing date of birth", e);
            }
        }
        
        // Gender
        if (rgGender != null && profile.getGender() != null) {
            switch (profile.getGender().toLowerCase()) {
                case "male":
                    rbMale.setChecked(true);
                    break;
                case "female":
                    rbFemale.setChecked(true);
                    break;
                default:
                    rbOther.setChecked(true);
                    break;
            }
        }
        
        // Address
        if (etAddress != null) {
            etAddress.setText(profile.getAddress() != null ? profile.getAddress() : "");
        }
        
        // Emergency contact
        // if (etEmergencyContact != null) {
        //     etEmergencyContact.setText(profile.getEmergencyContact() != null ? profile.getEmergencyContact() : "");
        // }
    }
    
    private void selectImage() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        imagePickerLauncher.launch(intent);
    }
    
    private void showDatePicker() {
        DatePickerDialog datePickerDialog = new DatePickerDialog(
            this,
            (view, year, month, dayOfMonth) -> {
                selectedDateOfBirth.set(year, month, dayOfMonth);
                etDateOfBirth.setText(dateFormatter.format(selectedDateOfBirth.getTime()));
            },
            selectedDateOfBirth.get(Calendar.YEAR),
            selectedDateOfBirth.get(Calendar.MONTH),
            selectedDateOfBirth.get(Calendar.DAY_OF_MONTH)
        );
        
        // Set maximum date to today (can't be born in the future)
        datePickerDialog.getDatePicker().setMaxDate(Calendar.getInstance().getTimeInMillis());
        
        // Set minimum date to 120 years ago
        Calendar minDate = Calendar.getInstance();
        minDate.add(Calendar.YEAR, -120);
        datePickerDialog.getDatePicker().setMinDate(minDate.getTimeInMillis());
        
        datePickerDialog.show();
    }
    
    private void saveProfile() {
        if (!validateInputs()) {
            return;
        }
        
        showLoading();
        
        // Create update request
        ProfileResponse updateRequest = new ProfileResponse();
        updateRequest.setFullName(etFullName.getText().toString().trim());
        updateRequest.setPhone(etPhoneNumber.getText().toString().trim());
        updateRequest.setDateOfBirth(apiDateFormatter.format(selectedDateOfBirth.getTime()));
        updateRequest.setGender(getSelectedGender());
        updateRequest.setAddress(etAddress.getText().toString().trim());
        
        // TODO: If image was selected, upload it first then update profile
        if (selectedImageUri != null) {
            // For now, just update other fields
            // Later implement image upload: uploadImageAndUpdateProfile(updateRequest);
            updateProfile(updateRequest);
        } else {
            updateProfile(updateRequest);
        }
    }
    
    private void updateProfile(ProfileResponse updateRequest) {
        // For now, use a simplified update approach
        // In a real implementation, you would call userApiService.updateProfile()
        
        // Mock success for demonstration
        hideLoading();
        Toast.makeText(this, "Cập nhật hồ sơ thành công!", Toast.LENGTH_SHORT).show();
        
        // Update local auth manager data
        authManager.updateUserInfo(
            updateRequest.getFullName(),
            updateRequest.getPhone()
        );
        
        // TODO: Implement actual API call
        // userApiService.updateProfile(userId, updateRequest).enqueue(...)
    }
    
    private boolean validateInputs() {
        // Full name
        if (etFullName.getText().toString().trim().isEmpty()) {
            etFullName.setError("Vui lòng nhập họ tên");
            etFullName.requestFocus();
            return false;
        }
        
        // Phone number
        String phoneNumber = etPhoneNumber.getText().toString().trim();
        if (phoneNumber.isEmpty()) {
            etPhoneNumber.setError("Vui lòng nhập số điện thoại");
            etPhoneNumber.requestFocus();
            return false;
        } else if (!phoneNumber.matches("^[0-9]{10,11}$")) {
            etPhoneNumber.setError("Số điện thoại không hợp lệ");
            etPhoneNumber.requestFocus();
            return false;
        }
        
        return true;
    }
    
    private String getSelectedGender() {
        int selectedId = rgGender.getCheckedRadioButtonId();
        
        if (selectedId == R.id.rbMale) {
            return "MALE";
        } else if (selectedId == R.id.rbFemale) {
            return "FEMALE";
        } else {
            return "OTHER";
        }
    }
    
    private void logout() {
        showLoading();
        
        authManager.logout(new AuthManager.AuthCallback() {
            @Override
            public void onSuccess(String message) {
                hideLoading();
                Toast.makeText(ProfileActivity.this, "Đăng xuất thành công", Toast.LENGTH_SHORT).show();
                redirectToLogin();
            }
            
            @Override
            public void onError(String error) {
                hideLoading();
                Log.e(TAG, "Logout error: " + error);
                // Even if logout fails on server, clear local data
                authManager.clearUserData();
                Toast.makeText(ProfileActivity.this, "Đã đăng xuất", Toast.LENGTH_SHORT).show();
                redirectToLogin();
            }
        });
    }
    
    private void redirectToLogin() {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
    
    private void showLoading() {
        if (progressDialog != null && !progressDialog.isShowing()) {
            progressDialog.show();
        }
    }
    
    private void hideLoading() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }
} 