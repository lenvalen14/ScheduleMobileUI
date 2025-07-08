package com.example.schedulemedical.ui.register;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.AutoCompleteTextView;
import android.widget.CheckBox;

import androidx.activity.ComponentActivity;
import androidx.lifecycle.MutableLiveData;

import com.example.schedulemedical.R;
import com.example.schedulemedical.utils.NavigationHelper;
import com.example.schedulemedical.data.repository.RegisterRepository;
import com.example.schedulemedical.model.dto.request.RegisterRequest;
import com.example.schedulemedical.model.dto.response.RegisterResponse;

public class RegisterActivity extends ComponentActivity {

    private RegisterRepository registerRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Khởi tạo repository
        registerRepository = new RegisterRepository();

         // 1. Lấy ra AutoCompleteTextView
    AutoCompleteTextView genderEdt = findViewById(R.id.autoCompleteGender);

    // 2. Tạo danh sách các lựa chọn giới tính
    String[] genderOptions = new String[]{"Nam", "Nữ", "Khác"};

    // 3. Tạo một ArrayAdapter để kết nối danh sách với AutoCompleteTextView
    //    Sử dụng layout mặc định của Android cho item trong danh sách thả xuống
    ArrayAdapter<String> adapter = new ArrayAdapter<>(
            this,
            android.R.layout.simple_dropdown_item_1line,
            genderOptions
    );

    // 4. Set adapter cho AutoCompleteTextView
    genderEdt.setAdapter(adapter);

        EditText nameEdt = findViewById(R.id.editTextName);
        EditText emailEdt = findViewById(R.id.editTextEmail);
        EditText passwordEdt = findViewById(R.id.editTextPassword);
        EditText phoneEdt = findViewById(R.id.editTextPhone);
        // AutoCompleteTextView genderEdt = findViewById(R.id.autoCompleteGender);
        CheckBox cbTerms = findViewById(R.id.cbTerms);
        Button registerBtn = findViewById(R.id.btnRegister);
        ImageView btn_back = findViewById(R.id.ivBack);

        registerBtn.setOnClickListener(view -> {
            String name = nameEdt.getText().toString().trim();
            String email = emailEdt.getText().toString().trim();
            String password = passwordEdt.getText().toString().trim();
            String phone = phoneEdt.getText().toString().trim();
            String gender = genderEdt.getText().toString().trim();
            boolean acceptedTerms = cbTerms.isChecked();

            if (!acceptedTerms) {
                Toast.makeText(this, "Bạn phải đồng ý với điều khoản và chính sách.", Toast.LENGTH_SHORT).show();
                return;
            }

            if (validateInput(name, email, password, phone, gender)) {
                RegisterRequest request = new RegisterRequest(
                        name,
                        email,
                        phone,
                        password,
                        mapGenderToApi(gender),
                        "USER"
                );

                registerBtn.setEnabled(false);

                // Sử dụng repository
                MutableLiveData<RegisterResponse> registerResult = new MutableLiveData<>();
                registerResult.observe(this, response -> {
                    registerBtn.setEnabled(true);

                    if (response != null && Boolean.TRUE.equals(response.getSuccess())) {
                        Toast.makeText(RegisterActivity.this, "Đăng ký thành công!", Toast.LENGTH_SHORT).show();

                        // Chuyển về trang login và đóng activity hiện tại
                        NavigationHelper.navigateToLogin(RegisterActivity.this);
                        finish(); // Đóng RegisterActivity để không quay lại được

                        // Hoặc có thể clear toàn bộ back stack
                        // NavigationHelper.navigateToLoginAndClearStack(RegisterActivity.this);
                    } else {
                        String msg = "Đăng ký thất bại!";
                        if (response != null && response.getMessage() != null) {
                            msg = response.getMessage();
                        }
                        Toast.makeText(RegisterActivity.this, msg, Toast.LENGTH_SHORT).show();
                    }
                });

                registerRepository.register(request, registerResult);
            }
        });

        btn_back.setOnClickListener(v -> {
            // Chuyển về trang login khi bấm back
            NavigationHelper.navigateToLogin(RegisterActivity.this);
            finish();
        });
    }

    private boolean validateInput(String name, String email, String password, String phone, String gender) {
        if (name.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập tên", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (email.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập email", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (!isValidEmail(email)) {
            Toast.makeText(this, "Email không hợp lệ", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (phone.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập số điện thoại", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (!isValidPhone(phone)) {
            Toast.makeText(this, "Số điện thoại không hợp lệ", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (gender.isEmpty()) {
            Toast.makeText(this, "Vui lòng chọn giới tính", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (password.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập mật khẩu", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (password.length() < 6) {
            Toast.makeText(this, "Mật khẩu phải có ít nhất 6 ký tự", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private boolean isValidEmail(String email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private boolean isValidPhone(String phone) {
        // Kiểm tra số điện thoại Việt Nam (10-11 số)
        return phone.matches("^[0-9]{10,11}$");
    }

    private String mapGenderToApi(String gender) {
        if (gender.equalsIgnoreCase("Nam")) return "Male";
        if (gender.equalsIgnoreCase("Nữ")) return "Female";
        if (gender.equalsIgnoreCase("Khác")) return "Other";
        return gender;
    }
}