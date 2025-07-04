package com.example.schedulemedical.model;

import java.util.Date;

public class CalendarDay {
    private int day;
    private Date date;
    private boolean isEnabled;
    private boolean isSelected;
    private boolean isToday;
    private boolean isEmpty;
    
    public CalendarDay() {
        this.isEmpty = true;
        this.isEnabled = false;
        this.isSelected = false;
        this.isToday = false;
    }
    
    public CalendarDay(int day, Date date) {
        this.day = day;
        this.date = date;
        this.isEmpty = false;
        this.isEnabled = true;
        this.isSelected = false;
        this.isToday = isDateToday(date);
    }
    
    public CalendarDay(Date date, int day, boolean isToday, boolean isEnabled, boolean isAvailable) {
        this.date = date;
        this.day = day;
        this.isToday = isToday;
        this.isEnabled = isEnabled && isAvailable;
        this.isEmpty = false;
        this.isSelected = false;
    }
    
    private boolean isDateToday(Date date) {
        if (date == null) return false;
        
        Date today = new Date();
        return date.getYear() == today.getYear() &&
               date.getMonth() == today.getMonth() &&
               date.getDate() == today.getDate();
    }
    
    // Getters and Setters
    public int getDay() {
        return day;
    }
    
    public void setDay(int day) {
        this.day = day;
    }
    
    public Date getDate() {
        return date;
    }
    
    public void setDate(Date date) {
        this.date = date;
        this.isToday = isDateToday(date);
    }
    
    public boolean isEnabled() {
        return isEnabled && !isEmpty;
    }
    
    public void setEnabled(boolean enabled) {
        isEnabled = enabled;
    }
    
    public boolean isSelected() {
        return isSelected;
    }
    
    public void setSelected(boolean selected) {
        isSelected = selected;
    }
    
    public boolean isToday() {
        return isToday;
    }
    
    public void setToday(boolean today) {
        isToday = today;
    }
    
    public boolean isEmpty() {
        return isEmpty;
    }
    
    public void setEmpty(boolean empty) {
        isEmpty = empty;
    }
} 