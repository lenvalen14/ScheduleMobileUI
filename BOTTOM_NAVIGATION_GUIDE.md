# Hướng dẫn Bottom Navigation trong ScheduleMedical App

## Tổng quan

Ứng dụng ScheduleMedical sử dụng bottom navigation để điều hướng giữa các màn hình chính. Bottom navigation được triển khai thông qua `BaseActivity` và `BottomNavigationView` từ Material Design Components.

## Cấu trúc Bottom Navigation

### 1. BaseActivity
- **Vị trí**: `app/src/main/java/com/example/schedulemedical/ui/base/BaseActivity.java`
- **Chức năng**: Base class cho tất cả các Activity chính có bottom navigation
- **Tính năng**:
  - Tự động setup bottom navigation
  - Xử lý navigation giữa các màn hình
  - Tự động highlight item hiện tại

### 2. Bottom Navigation Menu
- **Vị trí**: `app/src/main/res/menu/bottom_nav_menu.xml`
- **Các item**:
  - **Home** (`nav_home`): Màn hình chính
  - **Explore** (`nav_explore`): Danh sách bệnh viện
  - **Calendar** (`nav_calendar`): Lịch hẹn đã đặt
  - **Profile** (`nav_profile`): Thông tin cá nhân

## Cách sử dụng BaseActivity

### 1. Kế thừa từ BaseActivity
```java
public class YourActivity extends BaseActivity {
    
    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_your_layout;
    }

    @Override
    protected void setupViews() {
        // Setup các view khác của Activity
    }
}
```

### 2. Layout Requirements
Layout XML phải có `BottomNavigationView` với ID `bottom_navigation`:

```xml
<com.google.android.material.bottomnavigation.BottomNavigationView
    android:id="@+id/bottom_navigation"
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    android:layout_alignParentBottom="true"
    android:background="#FFFFFF"
    android:elevation="8dp"
    app:menu="@menu/bottom_nav_menu"
    app:labelVisibilityMode="unlabeled"
    app:itemActiveIndicatorStyle="@style/App.Custom.Indicator" />
```

## Các Activity đã được cập nhật

### ✅ Có Bottom Navigation
1. **HomeActivity** - Màn hình chính
2. **HospitalActivity** - Danh sách bệnh viện
3. **UserProfileActivity** - Thông tin cá nhân
4. **MyScheduledActivity** - Lịch hẹn đã đặt
5. **FilterDoctorActivity** - Tìm kiếm bác sĩ
6. **DoctorProfileActivity** - Thông tin bác sĩ
7. **ScheduleActivity** - Đặt lịch hẹn
8. **ChatActivity** - Chat với bác sĩ

### ❌ Không có Bottom Navigation (Auth Activities)
1. **MainActivity** - Splash screen
2. **LoginActivity** - Đăng nhập
3. **RegisterActivity** - Đăng ký
4. **ForgotPasswordActivity** - Quên mật khẩu
5. **SendOTPActivity** - Gửi OTP
6. **VerifyCodeActivity** - Xác thực OTP

## Navigation Flow

```
Home (nav_home)
    ↓
Explore (nav_explore) → FilterDoctor → DoctorProfile → Schedule
    ↓
Calendar (nav_calendar) → UserProfile
    ↓
Profile (nav_profile) → MyScheduled
```

## Tính năng của BaseActivity

### 1. Tự động Navigation
- Khi click vào item trong bottom navigation, tự động chuyển đến Activity tương ứng
- Không chuyển nếu đang ở Activity đó

### 2. Highlight Item Hiện Tại
- Tự động highlight item tương ứng với Activity hiện tại
- Sử dụng `setSelectedItemId()` để set item được chọn

### 3. Visibility Control
```java
// Ẩn/hiện bottom navigation
setBottomNavigationVisibility(false); // Ẩn
setBottomNavigationVisibility(true);  // Hiện
```

## Layout Structure

### Cấu trúc layout chuẩn với Bottom Navigation:
```xml
<RelativeLayout>
    <!-- Content Area -->
    <ScrollView
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        android:layout_above="@+id/bottom_navigation">
        <!-- Your content here -->
    </ScrollView>

    <!-- Bottom Navigation -->
    <com.google.android.material.bottomnavigation.BottomNavigationView
        android:id="@+id/bottom_navigation"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:layout_alignParentBottom="true"
        app:menu="@menu/bottom_nav_menu" />
</RelativeLayout>
```

## Styling

### 1. Menu Item Icons
- **Home**: `@drawable/ic_home`
- **Explore**: `@drawable/ic_map`
- **Calendar**: `@drawable/ic_calendar`
- **Profile**: `@drawable/ic_profile`

### 2. Active Indicator Style
```xml
<style name="App.Custom.Indicator" parent="Widget.Material3.BottomNavigationView.ActiveIndicator">
    <item name="android:color">@color/primary</item>
</style>
```

### 3. Label Visibility
- `app:labelVisibilityMode="unlabeled"` - Chỉ hiển thị icon
- `app:labelVisibilityMode="labeled"` - Hiển thị cả icon và text

## Best Practices

### 1. Layout Design
- Sử dụng `RelativeLayout` làm root layout
- Content area phải có `android:layout_above="@+id/bottom_navigation"`
- Bottom navigation phải có `android:layout_alignParentBottom="true"`

### 2. Navigation Logic
- Luôn sử dụng `NavigationHelper` để điều hướng
- Kiểm tra instance type trước khi navigate
- Sử dụng `setSelectedItemId()` để highlight item

### 3. Performance
- Tránh tạo quá nhiều Activity instances
- Sử dụng `finish()` khi cần thiết
- Reuse existing activities khi có thể

## Troubleshooting

### 1. Bottom Navigation không hiển thị
- Kiểm tra ID `bottom_navigation` trong layout
- Đảm bảo Activity kế thừa từ `BaseActivity`
- Kiểm tra menu resource `@menu/bottom_nav_menu`

### 2. Navigation không hoạt động
- Kiểm tra `NavigationHelper` methods
- Đảm bảo Activity đích đã được declare trong `AndroidManifest.xml`
- Kiểm tra instance type checking trong `handleBottomNavigationItemSelected()`

### 3. Item không được highlight
- Kiểm tra `setSelectedNavigationItem()` method
- Đảm bảo Activity được add vào instance checking
- Kiểm tra menu item IDs

## Ví dụ sử dụng

### Tạo Activity mới với Bottom Navigation:
```java
public class NewActivity extends BaseActivity {
    
    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_new;
    }

    @Override
    protected void setupViews() {
        // Setup your views here
        setupClickListeners();
        loadData();
    }

    private void setupClickListeners() {
        // Your click listeners
    }

    private void loadData() {
        // Load your data
    }
}
```

### Layout tương ứng:
```xml
<?xml version="1.0" encoding="utf-8"?>
<RelativeLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="match_parent">

    <ScrollView
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        android:layout_above="@+id/bottom_navigation">
        
        <LinearLayout
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:orientation="vertical"
            android:padding="16dp">
            
            <!-- Your content here -->
            
        </LinearLayout>
    </ScrollView>

    <com.google.android.material.bottomnavigation.BottomNavigationView
        android:id="@+id/bottom_navigation"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:layout_alignParentBottom="true"
        android:background="#FFFFFF"
        app:menu="@menu/bottom_nav_menu"
        app:labelVisibilityMode="unlabeled" />

</RelativeLayout>
```

## Lưu ý

1. **Consistency**: Tất cả Activity chính phải có bottom navigation
2. **Auth Flow**: Các Activity auth không có bottom navigation
3. **Navigation**: Luôn sử dụng NavigationHelper để điều hướng
4. **Layout**: Tuân thủ cấu trúc layout chuẩn
5. **Testing**: Test tất cả navigation flows
6. **Accessibility**: Đảm bảo accessibility cho bottom navigation 