package com.example.schedulemedical.utils;

import com.example.schedulemedical.R;

public class SpecialtyIconUtil {
    public static int getIconResId(String specialtyName) {
        if (specialtyName == null) return R.drawable.ic_specialty_placeholder;
        switch (specialtyName.toLowerCase()) {
            case "tim mạch":
                return R.drawable.ic_heart;
            case "da liễu":
                return R.drawable.ic_skin;
            case "nhi khoa":
                return R.drawable.ic_baby;
            case "thần kinh":
                return R.drawable.ic_brain;
            case "ung bướu":
                return R.drawable.ic_specialty_placeholder;
            case "chấn thương chỉnh hình":
                return R.drawable.ic_bone;
            case "mắt":
                return R.drawable.ic_eye;
            case "nha khoa":
                return R.drawable.ic_tooth;
            case "phụ sản":
                return R.drawable.ic_pregnant;
            case "tiết niệu":
                return R.drawable.ic_droplet;
            case "tiêu hoá":
                return R.drawable.ic_specialty_stomach;
            case "nội tiết":
                return R.drawable.ic_droplet;
            default:
                return R.drawable.ic_specialty_placeholder;
        }
    }

    public static int getBackgroundResId(String specialtyName) {
        if (specialtyName == null) return R.drawable.bg_icon_circle_soft;
        switch (specialtyName.toLowerCase()) {
            case "tim mạch":
                return R.drawable.bg_specialty_heart;
            case "da liễu":
                return R.drawable.bg_specialty_derma;
            case "nhi khoa":
                return R.drawable.bg_specialty_pediatrics;
            case "thần kinh":
                return R.drawable.bg_specialty_brain;
            case "ung bướu":
                return R.drawable.bg_specialty_general;
            case "chấn thương chỉnh hình":
                return R.drawable.bg_specialty_bone;
            case "mắt":
                return R.drawable.bg_specialty_general;
            case "nha khoa":
                return R.drawable.bg_specialty_dental;
            case "phụ sản":
                return R.drawable.bg_specialty_obstetrics;
            case "tiết niệu":
                return R.drawable.bg_specialty_droplet;
            case "tiêu hoá":
                return R.drawable.bg_specialty_digestive;
            case "nội tiết":
                return R.drawable.bg_specialty_droplet;
            default:
                return R.drawable.bg_icon_circle_soft;
        }
    }
} 