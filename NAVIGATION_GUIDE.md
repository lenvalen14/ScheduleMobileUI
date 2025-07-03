# Hướng dẫn Navigation trong ScheduleMedical App

## Tổng quan

Ứng dụng ScheduleMedical sử dụng hệ thống navigation tập trung thông qua class `NavigationHelper` để quản lý tất cả các Intent điều hướng giữa các Activity.

## Cấu trúc Navigation

### 1. NavigationHelper Class
- **Vị trí**: `app/src/main/java/com/example/schedulemedical/utils/NavigationHelper.java`
- **Chức năng**: Quản lý tất cả các Intent điều hướng
- **Constants**: Định nghĩa các key cho Intent extras

### 2. Luồng điều hướng chính

```
MainActivity (Splash) 
    ↓
LoginActivity
    ↓ (đăng nhập thành công)
HomeActivity
    ↓
├── HospitalActivity
├── FilterDoctorActivity
│   └── DoctorProfileActivity
│       └── ScheduleActivity
├── UserProfileActivity
├── MyScheduledActivity
└── ChatActivity
```

## Cách sử dụng NavigationHelper

### 1. Import NavigationHelper
```java
import com.example.schedulemedical.utils.NavigationHelper;
```

### 2. Điều hướng cơ bản
```java
// Điều hướng đến Home
NavigationHelper.navigateToHome(this);

// Điều hướng đến Login (clear stack)
NavigationHelper.navigateToLogin(this);

// Quay lại màn hình trước
NavigationHelper.goBack(this);
```

### 3. Điều hướng với dữ liệu
```java
// Điều hướng với doctor ID
NavigationHelper.navigateToDoctorProfile(this, doctorId);

// Điều hướng với hospital ID
NavigationHelper.navigateToHospital(this, hospitalId);

// Điều hướng với appointment ID
NavigationHelper.navigateToChat(this, appointmentId);
```

### 4. Nhận dữ liệu từ Intent
```java
// Trong Activity đích
int doctorId = getIntent().getIntExtra(NavigationHelper.EXTRA_DOCTOR_ID, -1);
if (doctorId != -1) {
    // Xử lý doctor ID
}
```

## Các Activity và chức năng

### 1. MainActivity
- **Chức năng**: Màn hình splash, tự động chuyển đến LoginActivity sau 3 giây
- **Navigation**: `MainActivity → LoginActivity`

### 2. LoginActivity
- **Chức năng**: Đăng nhập người dùng
- **Navigation**: 
  - `LoginActivity → HomeActivity` (đăng nhập thành công)
  - `LoginActivity → RegisterActivity` (đăng ký)
  - `LoginActivity → ForgotPasswordActivity` (quên mật khẩu)

### 3. RegisterActivity
- **Chức năng**: Đăng ký tài khoản mới
- **Navigation**: `RegisterActivity → LoginActivity` (đăng ký thành công)

### 4. HomeActivity
- **Chức năng**: Màn hình chính của ứng dụng
- **Navigation**:
  - `HomeActivity → HospitalActivity`
  - `HomeActivity → FilterDoctorActivity`
  - `HomeActivity → UserProfileActivity`
  - `HomeActivity → MyScheduledActivity`
  - `HomeActivity → ChatActivity`

### 5. HospitalActivity
- **Chức năng**: Hiển thị danh sách bệnh viện
- **Navigation**:
  - `HospitalActivity → FilterDoctorActivity`
  - `HospitalActivity → HomeActivity`

### 6. FilterDoctorActivity
- **Chức năng**: Tìm kiếm và lọc bác sĩ
- **Navigation**: `FilterDoctorActivity → DoctorProfileActivity`

### 7. DoctorProfileActivity
- **Chức năng**: Hiển thị thông tin chi tiết bác sĩ
- **Navigation**:
  - `DoctorProfileActivity → ScheduleActivity`
  - `DoctorProfileActivity → ChatActivity`

### 8. ScheduleActivity
- **Chức năng**: Đặt lịch hẹn với bác sĩ
- **Navigation**: `ScheduleActivity → MyScheduledActivity` (đặt lịch thành công)

### 9. MyScheduledActivity
- **Chức năng**: Xem danh sách lịch hẹn đã đặt
- **Navigation**: `MyScheduledActivity → UserProfileActivity`

### 10. UserProfileActivity
- **Chức năng**: Quản lý thông tin cá nhân
- **Navigation**:
  - `UserProfileActivity → MyScheduledActivity`
  - `UserProfileActivity → LoginActivity` (logout)

### 11. ChatActivity
- **Chức năng**: Chat với bác sĩ
- **Navigation**: `ChatActivity → HomeActivity`

### 12. ForgotPasswordActivity
- **Chức năng**: Quên mật khẩu
- **Navigation**: `ForgotPasswordActivity → SendOTPActivity`

### 13. SendOTPActivity
- **Chức năng**: Gửi mã OTP
- **Navigation**: `SendOTPActivity → VerifyCodeActivity`

### 14. VerifyCodeActivity
- **Chức năng**: Xác thực mã OTP
- **Navigation**: `VerifyCodeActivity → LoginActivity` (xác thực thành công)

## Intent Extras Constants

```java
public static final String EXTRA_DOCTOR_ID = "doctor_id";
public static final String EXTRA_HOSPITAL_ID = "hospital_id";
public static final String EXTRA_SPECIALTY_ID = "specialty_id";
public static final String EXTRA_APPOINTMENT_ID = "appointment_id";
public static final String EXTRA_EMAIL = "email";
public static final String EXTRA_OTP_CODE = "otp_code";
public static final String EXTRA_FILTER_DATA = "filter_data";
```

## Best Practices

### 1. Sử dụng NavigationHelper
- Luôn sử dụng `NavigationHelper` thay vì tạo Intent trực tiếp
- Đảm bảo tính nhất quán trong toàn bộ ứng dụng

### 2. Xử lý Intent Extras
- Luôn kiểm tra null và giá trị mặc định khi nhận extras
- Sử dụng constants từ NavigationHelper

### 3. Back Navigation
- Sử dụng `NavigationHelper.goBack(this)` thay vì `finish()`
- Đảm bảo luồng điều hướng hợp lý

### 4. Error Handling
- Xử lý trường hợp không tìm thấy view trong layout
- Hiển thị thông báo phù hợp cho người dùng

## Ví dụ sử dụng

### Trong LoginActivity
```java
// Đăng nhập thành công
loginViewModel.getLoginResult().observe(this, result -> {
    if (result.getData() != null) {
        NavigationHelper.navigateToHome(this);
    }
});

// Chuyển đến đăng ký
registerLink.setOnClickListener(view -> {
    NavigationHelper.navigateToRegister(this);
});
```

### Trong HomeActivity
```java
// Chuyển đến tìm bác sĩ
searchIcon.setOnClickListener(view -> {
    NavigationHelper.navigateToFilterDoctor(this);
});

// Chuyển đến profile
profileIcon.setOnClickListener(view -> {
    NavigationHelper.navigateToUserProfile(this);
});
```

### Trong DoctorProfileActivity
```java
// Đặt lịch với bác sĩ
scheduleButton.setOnClickListener(view -> {
    int doctorId = getIntent().getIntExtra(NavigationHelper.EXTRA_DOCTOR_ID, -1);
    NavigationHelper.navigateToSchedule(this, doctorId);
});
```

## Lưu ý

1. **Layout IDs**: Đảm bảo các ID trong layout XML khớp với code Java
2. **Permissions**: Một số chức năng có thể cần permissions (camera, storage, etc.)
3. **Error Handling**: Luôn xử lý các trường hợp lỗi và hiển thị thông báo phù hợp
4. **Performance**: Tránh tạo quá nhiều Activity instances, sử dụng flags phù hợp
5. **Testing**: Test tất cả các luồng điều hướng để đảm bảo hoạt động đúng 