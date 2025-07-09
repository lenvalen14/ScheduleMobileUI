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
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.example.schedulemedical.R;
import com.example.schedulemedical.data.api.ApiClient;
import com.example.schedulemedical.data.api.AuthApiService;
import com.example.schedulemedical.data.api.UserApiService;
import com.example.schedulemedical.model.dto.response.ApiResponse;
import com.example.schedulemedical.model.dto.response.ProfileResponse;
import com.example.schedulemedical.model.dto.response.PatientProfileResponse;
import com.example.schedulemedical.model.dto.request.UpdateUserRequest;
import com.example.schedulemedical.model.dto.request.UpdatePatientProfileRequest;
import com.example.schedulemedical.model.dto.response.UserResponse;
import com.example.schedulemedical.ui.base.BaseActivity;
import com.example.schedulemedical.ui.home.HomeActivity;
import com.example.schedulemedical.ui.login.LoginActivity;
import com.example.schedulemedical.utils.AuthManager;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.button.MaterialButton;
import com.example.schedulemedical.ui.schedule.MyScheduledActivity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileActivity extends BaseActivity {
    private static final String TAG = "ProfileActivity";
    
    // UI Components
    private ImageView btnBack;
    private CircleImageView ivProfileImage;
    private TextView ivChangePhoto;
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
    private Button btnLogout;
    private TextView tvChangePassword;
    private Spinner spinnerEthnicity;
    private EditText etInsurance, etAllergies, etChronicDiseases, etObstetricHistory, etSurgicalHistory, etFamilyHistory, etSocialHistory, etMedicationHistory;
    private EditText etNationalId;
    private MaterialButton btnMyAppointments;
    
    // Data
    private ProfileResponse currentProfile;
    private Uri selectedImageUri;
    private Calendar selectedDateOfBirth;
    private PatientProfileResponse currentPatientProfile;
    
    // Services
    private AuthApiService authApiService;
    private UserApiService userApiService;
    private AuthManager authManager;
    private ProgressDialog progressDialog;
    private ProfileViewModel profileViewModel;
    
    // Date formatter
    private SimpleDateFormat dateFormatter = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
    private SimpleDateFormat apiDateFormatter = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
    
    // Image picker launcher
    private ActivityResultLauncher<Intent> imagePickerLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_profile;
    }

    @Override
    protected void setupViews() {
        initializeServices();
        initializeViews();
        setupImagePickerLauncher();
        setupClickListeners();
        setupProgressDialog();
        loadUserProfile();
        loadPatientProfile();

        profileViewModel = new ViewModelProvider(this).get(ProfileViewModel.class);

    }

    private void initializeServices() {
        ApiClient.init(this);
        authApiService = ApiClient.getAuthApiService();
        userApiService = ApiClient.getUserApiService();
        authManager = new AuthManager(this);
        selectedDateOfBirth = Calendar.getInstance();
    }
    
    private void initializeViews() {
        btnBack = findViewById(R.id.btnBack);
        ivProfileImage = findViewById(R.id.ivProfilePicture);
        ivChangePhoto = findViewById(R.id.tvChangePhoto);
        etFullName = findViewById(R.id.etFullName);
        etEmail = findViewById(R.id.etEmail);
        etPhoneNumber = findViewById(R.id.etPhoneNumber);
        // etDateOfBirth = findViewById(R.id.etDateOfBirth);
        rgGender = findViewById(R.id.rgGender);
        rbMale = findViewById(R.id.rbMale);
        rbFemale = findViewById(R.id.rbFemale);
        rbOther = findViewById(R.id.rbOther);
        etAddress = findViewById(R.id.etAddress);
        spinnerEthnicity = findViewById(R.id.spinnerEthnicity);
        etInsurance = findViewById(R.id.etInsurance);
        etAllergies = findViewById(R.id.etAllergies);
        etChronicDiseases = findViewById(R.id.etChronicDiseases);
        etObstetricHistory = findViewById(R.id.etObstetricHistory);
        etSurgicalHistory = findViewById(R.id.etSurgicalHistory);
        etFamilyHistory = findViewById(R.id.etFamilyHistory);
        etSocialHistory = findViewById(R.id.etSocialHistory);
        etMedicationHistory = findViewById(R.id.etMedicationHistory);
        etNationalId = findViewById(R.id.etNationalId);
        Button btnSaveProfile = findViewById(R.id.btnUpdateProfile);

        // Disable email editing (usually not changeable)
        if (etEmail != null) {
            etEmail.setEnabled(false);
            etEmail.setFocusable(false);
        }
        spinnerEthnicity.setAdapter(android.widget.ArrayAdapter.createFromResource(this, R.array.ethnicities, android.R.layout.simple_spinner_item));
        
        btnSaveProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveProfile();
            }
        });
        
    }

    private void setupImagePickerLauncher() {
        imagePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        selectedImageUri = result.getData().getData();
                        if (selectedImageUri != null) {
                            Glide.with(this).load(selectedImageUri).into(ivProfileImage);
                            uploadAvatarToServer();
                        }
                    }
                }
        );
    }

    private void uploadAvatarToServer() {
        int userId = authManager.getUserId();
        if (selectedImageUri == null || userId == -1) return;

        try {
            File file = getFileFromUri(selectedImageUri);
            if (file == null) {
                Toast.makeText(this, "Không thể xử lý file ảnh", Toast.LENGTH_SHORT).show();
                return;
            }

            String mimeType = getContentResolver().getType(selectedImageUri); // e.g., image/jpeg

            RequestBody requestFile = RequestBody.create(MediaType.parse(mimeType), file);
            MultipartBody.Part body = MultipartBody.Part.createFormData("file", file.getName(), requestFile);

            profileViewModel.uploadAvatar(userId, body).observe(this, response -> {
                if (response != null && response.getCode() == 201) {
                    Toast.makeText(this, "Cập nhật ảnh thành công!", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(this, HomeActivity.class);
                    startActivity(intent);
                } else {
                    Toast.makeText(this, "Lỗi cập nhật ảnh: " + (response != null ? response.getMessage() : "Không rõ lỗi"), Toast.LENGTH_LONG).show();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Lỗi xử lý ảnh: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private File getFileFromUri(Uri uri) throws IOException {
        InputStream inputStream = getContentResolver().openInputStream(uri);
        File file = new File(getCacheDir(), "avatar_temp.jpg");
        FileOutputStream outputStream = new FileOutputStream(file);

        byte[] buffer = new byte[1024];
        int len;
        while ((len = inputStream.read(buffer)) > 0) {
            outputStream.write(buffer, 0, len);
        }

        outputStream.close();
        inputStream.close();

        return file;
    }
    
    private void setupClickListeners() {
        // Back button
        btnBack.setOnClickListener(v -> finish());

        // Change photo
        ivChangePhoto.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            intent.setType("image/*");
            imagePickerLauncher.launch(intent);
        });
        
        // Date of birth picker
        // etDateOfBirth.setOnClickListener(v -> showDatePicker()); // View commented out
        // etDateOfBirth.setFocusable(false); // View commented out
        
        // Save profile
        // btnSaveProfile.setOnClickListener(v -> saveProfile()); // View commented out
        
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
        authApiService.getProfile().enqueue(new Callback<ApiResponse<ProfileResponse>>() {
            @Override
            public void onResponse(Call<ApiResponse<ProfileResponse>> call, Response<ApiResponse<ProfileResponse>> response) {
                hideLoading();
                if (response.isSuccessful() && response.body() != null && response.body().getData() != null) {
                    currentProfile = response.body().getData();
                    updateUIWithProfile(currentProfile);
                } else {
                    Toast.makeText(ProfileActivity.this, "Không thể tải thông tin hồ sơ", Toast.LENGTH_LONG).show();
                }
            }
            @Override
            public void onFailure(Call<ApiResponse<ProfileResponse>> call, Throwable t) {
                hideLoading();
                Log.e(TAG, "Failed to load profile", t);
                Toast.makeText(ProfileActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
    
    private void loadPatientProfile() {
        int userId = authManager.getUserId();
        userApiService.getPatientProfile(userId).enqueue(new Callback<ApiResponse<PatientProfileResponse>>() {
            @Override
            public void onResponse(Call<ApiResponse<PatientProfileResponse>> call, Response<ApiResponse<PatientProfileResponse>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getData() != null) {
                    currentPatientProfile = response.body().getData();
                    updateUIWithPatientProfile(currentPatientProfile);
                }
            }
            @Override
            public void onFailure(Call<ApiResponse<PatientProfileResponse>> call, Throwable t) { }
        });
    }
    
    private void updateUIWithProfile(ProfileResponse profile) {
        Log.d("Profile", "fullName: " + profile.getFullName());
        Log.d("Profile", "email: " + profile.getEmail());
        Log.d("Profile", "phone: " + profile.getPhone());
        Log.d("Profile", "address: " + profile.getAddress());
        Log.d("Profile", "dateOfBirth: " + profile.getDateOfBirth());
        Log.d("Profile", "nationalId: " + profile.getNationalId());
        Log.d("Profile", "ethnicity: " + profile.getEthnicity());
        Log.d("Profile", "role: " + profile.getRole());
        Log.d("Profile", "gender: " + profile.getGender());
        Log.d("Profile", "createdAt: " + profile.getCreatedAt());
        Log.d("Profile", "avatar: " + profile.getAvatar());
        
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
    
    private void updateUIWithPatientProfile(PatientProfileResponse profile) {
        UserResponse user = profile.getUser();
        if (user != null) {
            if (etFullName != null) etFullName.setText(user.getFullName() != null ? user.getFullName() : "");
            if (etEmail != null) etEmail.setText(user.getEmail() != null ? user.getEmail() : "");
            if (etPhoneNumber != null) etPhoneNumber.setText(user.getPhone() != null ? user.getPhone() : "");
            if (etAddress != null) etAddress.setText(user.getAddress() != null ? user.getAddress() : "");
            if (etNationalId != null) etNationalId.setText(user.getNationalId() != null ? user.getNationalId() : "");
            if (spinnerEthnicity != null && user.getEthnicity() != null) {
                String[] ethnicities = getResources().getStringArray(R.array.ethnicities);
                for (int i = 0; i < ethnicities.length; i++) {
                    if (ethnicities[i].equalsIgnoreCase(user.getEthnicity())) {
                        spinnerEthnicity.setSelection(i);
                        break;
                    }
                }
            }
            if (rgGender != null && user.getGender() != null) {
                switch (user.getGender().toLowerCase()) {
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
            // Avatar nếu có
            if (ivProfileImage != null && user.getAvatar() != null && !user.getAvatar().isEmpty()) {
                Glide.with(this)
                    .load(user.getAvatar())
                    .transform(new CircleCrop())
                    .placeholder(R.drawable.sample_profile_image)
                    .error(R.drawable.sample_profile_image)
                    .into(ivProfileImage);
            }
        }
        // Các trường patient profile
        if (etInsurance != null) etInsurance.setText(profile.getInsurance());
        if (etAllergies != null) etAllergies.setText(profile.getAllergies());
        if (etChronicDiseases != null) etChronicDiseases.setText(profile.getChronicDiseases());
        if (etObstetricHistory != null) etObstetricHistory.setText(profile.getObstetricHistory());
        if (etSurgicalHistory != null) etSurgicalHistory.setText(profile.getSurgicalHistory());
        if (etFamilyHistory != null) etFamilyHistory.setText(profile.getFamilyHistory());
        if (etSocialHistory != null) etSocialHistory.setText(profile.getSocialHistory());
        if (etMedicationHistory != null) etMedicationHistory.setText(profile.getMedicationHistory());
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
        if (!validateInputs()) return;
        showLoading();
        // Update user
        UpdateUserRequest updateUserRequest = new UpdateUserRequest();
        updateUserRequest.setFullName(etFullName.getText().toString().trim());
        updateUserRequest.setPhone(etPhoneNumber.getText().toString().trim());
        updateUserRequest.setDateOfBirth(apiDateFormatter.format(selectedDateOfBirth.getTime()));
        updateUserRequest.setGender(getSelectedGender());
        updateUserRequest.setAddress(etAddress.getText().toString().trim());
        updateUserRequest.setNationalId(etNationalId.getText().toString().trim());
        updateUserRequest.setEthnicity(spinnerEthnicity.getSelectedItem().toString());
        updateUserRequest.setEmail(etEmail.getText().toString().trim());
        // Update patient profile
        UpdatePatientProfileRequest patientRequest = new UpdatePatientProfileRequest();
        patientRequest.setInsurance(etInsurance.getText().toString().trim());
        patientRequest.setAllergies(etAllergies.getText().toString().trim());
        patientRequest.setChronicDiseases(etChronicDiseases.getText().toString().trim());
        patientRequest.setObstetricHistory(etObstetricHistory.getText().toString().trim());
        patientRequest.setSurgicalHistory(etSurgicalHistory.getText().toString().trim());
        patientRequest.setFamilyHistory(etFamilyHistory.getText().toString().trim());
        patientRequest.setSocialHistory(etSocialHistory.getText().toString().trim());
        patientRequest.setMedicationHistory(etMedicationHistory.getText().toString().trim());
        int userId = authManager.getUserId();
        userApiService.updateUser(userId, updateUserRequest).enqueue(new Callback<ApiResponse<Object>>() {
            @Override
            public void onResponse(Call<ApiResponse<Object>> call, Response<ApiResponse<Object>> response) {
                hideLoading();
                if (response.isSuccessful()) {
                    Toast.makeText(ProfileActivity.this, "Cập nhật thông tin cá nhân thành công!", Toast.LENGTH_SHORT).show();
                } else {
                    // Log error body
                    try {
                        String errorBody = response.errorBody() != null ? response.errorBody().string() : "null";
                        Log.e(TAG, "updateUser error: " + errorBody);
                        Toast.makeText(ProfileActivity.this, "Lỗi cập nhật: " + errorBody, Toast.LENGTH_LONG).show();
                    } catch (Exception e) {
                        Log.e(TAG, "Exception reading error body", e);
                    }
                }
            }
            @Override
            public void onFailure(Call<ApiResponse<Object>> call, Throwable t) {
                hideLoading();
                Log.e(TAG, "updateUser failed", t);
                Toast.makeText(ProfileActivity.this, "Lỗi cập nhật thông tin cá nhân", Toast.LENGTH_SHORT).show();
            }
        });
        userApiService.updatePatientProfile(userId, patientRequest).enqueue(new Callback<ApiResponse<PatientProfileResponse>>() {
            @Override
            public void onResponse(Call<ApiResponse<PatientProfileResponse>> call, Response<ApiResponse<PatientProfileResponse>> response) {
                hideLoading();
                if (response.isSuccessful()) {
                    Toast.makeText(ProfileActivity.this, "Cập nhật hồ sơ bệnh nhân thành công!", Toast.LENGTH_SHORT).show();
                } else {
                    // Log error body
                    try {
                        String errorBody = response.errorBody() != null ? response.errorBody().string() : "null";
                        Log.e(TAG, "updatePatientProfile error: " + errorBody);
                        Toast.makeText(ProfileActivity.this, "Lỗi cập nhật: " + errorBody, Toast.LENGTH_LONG).show();
                    } catch (Exception e) {
                        Log.e(TAG, "Exception reading error body", e);
                    }
                }
            }
            @Override
            public void onFailure(Call<ApiResponse<PatientProfileResponse>> call, Throwable t) {
                hideLoading();
                Log.e(TAG, "updatePatientProfile failed", t);
                Toast.makeText(ProfileActivity.this, "Lỗi cập nhật hồ sơ bệnh nhân", Toast.LENGTH_SHORT).show();
            }
        });
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
            return "Male";
        } else if (selectedId == R.id.rbFemale) {
            return "Female";
        } else {
            return "Other";
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