package com.example.schedulemedical.ui.doctorprofile;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.example.schedulemedical.R;
import com.example.schedulemedical.model.dto.request.UpdateUserRequest;
import com.example.schedulemedical.model.dto.request.doctor.UpdateDoctorDTO;
import com.example.schedulemedical.model.dto.response.DoctorResponse;
import com.example.schedulemedical.model.dto.response.UserResponse;
import com.example.schedulemedical.ui.base.BaseActivity;
import com.example.schedulemedical.ui.home.HomeActivity;
import com.example.schedulemedical.ui.profile.ProfileViewModel;
import com.example.schedulemedical.utils.AuthManager;
import com.example.schedulemedical.utils.NavigationHelper;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

public class DoctorEditProfileActivity extends BaseActivity {

    private DoctorViewModel viewModel;
    private ProfileViewModel profileViewModel;

    private int doctorId = -1;
    private int userId = -1;

    private TextView tvDateOfBirth;
    private CircleImageView ivProfileImage;
    private TextView tvChangePhoto;
    private Uri selectedImageUri;
    private ActivityResultLauncher<Intent> imagePickerLauncher;

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_edit_doctor_profile;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("DoctorEditProfile", "onCreate called");
    }

    @Override
    protected void setupViews() {
        Log.d("DoctorEditProfile", "setupViews called");

        viewModel = new ViewModelProvider(this).get(DoctorViewModel.class);
        profileViewModel = new ViewModelProvider(this).get(ProfileViewModel.class);

        tvDateOfBirth = findViewById(R.id.tvDateOfBirth);
        ivProfileImage = findViewById(R.id.profile_image);
        tvChangePhoto = findViewById(R.id.tvChangePhoto);

        setupImagePickerLauncher();
        setupChangePhotoClick();
        setupNavigation();
        setupDatePicker();
        loadDoctorProfileByUserId();
    }

    private void setupChangePhotoClick() {
        tvChangePhoto.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            intent.setType("image/*");
            imagePickerLauncher.launch(intent);
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

    private void setupNavigation() {
        ImageView backButton = findViewById(R.id.btnBack);
        if (backButton != null) {
            backButton.setOnClickListener(v -> finish());
        }

        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        if (bottomNav != null) {
            bottomNav.setOnItemSelectedListener(item -> {
                if (item.getItemId() == R.id.nav_home) {
                    NavigationHelper.navigateToHome(this);
                    return true;
                }
                return false;
            });
        }

        Button editProfileButton = findViewById(R.id.btn_save);
        if (editProfileButton != null) {
            editProfileButton.setOnClickListener(view -> updateDoctorProfile());
        }
    }

    private void loadDoctorProfileByUserId() {
        AuthManager authManager = new AuthManager(this);
        int userId = authManager.getUserId() != null ? authManager.getUserId() : -1;
        if (userId == -1) {
            Toast.makeText(this, "Không lấy được userId, quay về Home", Toast.LENGTH_LONG).show();
            NavigationHelper.navigateToHome(this);
            return;
        }

        viewModel.loadDoctorProfileByUserId(userId);
        viewModel.doctorProfile.observe(this, doctor -> {
            if (doctor != null) {
                doctorId = doctor.getDoctorId();
                this.userId = doctor.getUserId();
                mapDoctorProfileToUI(doctor);
            }
        });
    }

    private void mapDoctorProfileToUI(DoctorResponse doctorResponse) {
        if (doctorResponse == null) return;

        UserResponse user = doctorResponse.getUser();

        if (user != null && user.getAvatar() != null && !user.getAvatar().isEmpty()) {
            Glide.with(this)
                    .load(user.getAvatar())
                    .placeholder(R.drawable.sample_profile_image)
                    .error(R.drawable.sample_profile_image)
                    .into(ivProfileImage);
        }

        ((EditText) findViewById(R.id.etFullName)).setText(user != null ? user.getFullName() : "");
        ((EditText) findViewById(R.id.etPhoneNumber)).setText(user != null ? user.getPhone() : "");
        ((EditText) findViewById(R.id.etEmail)).setText(user != null ? user.getEmail() : "");
        tvDateOfBirth.setText(formatIsoDate(user != null ? user.getDateOfBirth() : ""));
        ((EditText) findViewById(R.id.etAddress)).setText(user != null ? user.getAddress() : "");
        ((EditText) findViewById(R.id.etNationalId)).setText(user != null ? user.getNationalId() : "");

        if (user != null && user.getGender() != null) {
            RadioGroup rgGender = findViewById(R.id.rgGender);
            switch (user.getGender()) {
                case "Male":
                    rgGender.check(R.id.rbMale);
                    break;
                case "Female":
                    rgGender.check(R.id.rbFemale);
                    break;
                default:
                    rgGender.check(R.id.rbOther);
                    break;
            }
        }

        ((EditText) findViewById(R.id.et_bio)).setText(doctorResponse.getBio() != null ? doctorResponse.getBio() : "");
        ((EditText) findViewById(R.id.et_education)).setText(doctorResponse.getEducation() != null ? doctorResponse.getEducation() : "");
        ((EditText) findViewById(R.id.et_experience)).setText(String.valueOf(doctorResponse.getYearsOfExperience()));
        ((EditText) findViewById(R.id.et_clinic)).setText(doctorResponse.getClinic() != null ? doctorResponse.getClinic() : "");

        TextView tvSpecialty = findViewById(R.id.tv_specialty);
        TextView tvHospital = findViewById(R.id.tv_hospital);

        if (doctorResponse.getSpecialty() != null && doctorResponse.getSpecialty().getName() != null) {
            tvSpecialty.setText(doctorResponse.getSpecialty().getName());
        } else {
            tvSpecialty.setText("N/A");
        }

        if (doctorResponse.getHospital() != null && doctorResponse.getHospital().getName() != null) {
            tvHospital.setText(doctorResponse.getHospital().getName());
        } else {
            tvHospital.setText("N/A");
        }
    }

    private void updateDoctorProfile() {
        EditText etFullName = findViewById(R.id.etFullName);
        EditText etPhoneNumber = findViewById(R.id.etPhoneNumber);
        EditText etEmail = findViewById(R.id.etEmail);
        EditText etAddress = findViewById(R.id.etAddress);
        EditText etNationalId = findViewById(R.id.etNationalId);
        RadioGroup rgGender = findViewById(R.id.rgGender);

        EditText etBio = findViewById(R.id.et_bio);
        EditText etEducation = findViewById(R.id.et_education);
        EditText etExperience = findViewById(R.id.et_experience);
        EditText etClinic = findViewById(R.id.et_clinic);

        String fullName = etFullName.getText().toString().trim();
        String phone = etPhoneNumber.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String dateOfBirth = tvDateOfBirth.getText().toString().trim();
        String address = etAddress.getText().toString().trim();
        String nationalId = etNationalId.getText().toString().trim();

        String gender = "Other";
        int selectedGenderId = rgGender.getCheckedRadioButtonId();
        if (selectedGenderId == R.id.rbMale) gender = "Male";
        else if (selectedGenderId == R.id.rbFemale) gender = "Female";

        String bio = etBio.getText().toString().trim();
        String education = etEducation.getText().toString().trim();
        String experienceStr = etExperience.getText().toString().trim();
        String clinic = etClinic.getText().toString().trim();

        if (doctorId == -1 || userId == -1) {
            Toast.makeText(this, "Không tìm thấy ID để cập nhật", Toast.LENGTH_SHORT).show();
            return;
        }

        UpdateUserRequest updateUserRequest = new UpdateUserRequest();
        updateUserRequest.setFullName(fullName);
        updateUserRequest.setPhone(phone);
        updateUserRequest.setEmail(email);
        updateUserRequest.setDateOfBirth(dateOfBirth);
        updateUserRequest.setAddress(address);
        updateUserRequest.setNationalId(nationalId);
        updateUserRequest.setGender(gender);

        UpdateDoctorDTO dto = new UpdateDoctorDTO();
        dto.setBio(bio);
        dto.setEducation(education);
        dto.setClinic(clinic);
        dto.setYearsOfExperience(experienceStr);
        dto.setSpecialtyId(null);
        dto.setHospitalId(null);

        profileViewModel.updateUser(userId, updateUserRequest).observe(this, userResponse -> {
            if (userResponse != null && userResponse.getCode() == 200) {
                viewModel.updateDoctorProfile(doctorId, dto).observe(this, doctorResponse -> {
                    if (doctorResponse != null && doctorResponse.getCode() == 200) {
                        Toast.makeText(this, "Cập nhật thành công", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        Toast.makeText(this, "Cập nhật bác sĩ thất bại", Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                Toast.makeText(this, "Cập nhật người dùng thất bại", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupDatePicker() {
        LinearLayout layoutDatePicker = findViewById(R.id.layoutDatePicker);
        if (layoutDatePicker != null) {
            layoutDatePicker.setOnClickListener(v -> showDatePickerDialog());
        }
    }

    public String formatIsoDate(String isoDate) {
        try {
            SimpleDateFormat isoFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault());
            isoFormat.setTimeZone(TimeZone.getTimeZone("UTC"));

            Date date = isoFormat.parse(isoDate);
            SimpleDateFormat outputFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
            return outputFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
            return isoDate;
        }
    }

    private void showDatePickerDialog() {
        final Calendar calendar = Calendar.getInstance();

        String currentDate = tvDateOfBirth.getText().toString();
        if (currentDate != null && !currentDate.isEmpty() && currentDate.matches("\\d{4}-\\d{2}-\\d{2}")) {
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                Date date = sdf.parse(currentDate);
                if (date != null) {
                    calendar.setTime(date);
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                (view, selectedYear, selectedMonth, selectedDay) -> {
                    String selectedDate = String.format(Locale.getDefault(), "%04d-%02d-%02d",
                            selectedYear, selectedMonth + 1, selectedDay);
                    tvDateOfBirth.setText(selectedDate);
                }, year, month, day);

        datePickerDialog.show();
    }
}
