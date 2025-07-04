package com.example.schedulemedical.model;

public class TimeSlot {
    private String startTime;
    private String endTime;
    private String displayTime;
    private boolean isAvailable;
    private boolean isSelected;
    private boolean isPastTime;
    private int currentBookings;
    private int capacity;
    
    public TimeSlot() {}
    
    public TimeSlot(String startTime, String endTime, boolean isAvailable) {
        this.startTime = startTime;
        this.endTime = endTime;
        this.displayTime = startTime + " - " + endTime;
        this.isAvailable = isAvailable;
        this.isSelected = false;
        this.isPastTime = false;
        this.currentBookings = 0;
        this.capacity = 5; // Default capacity
    }
    
    public TimeSlot(String startTime, String endTime, boolean isAvailable, 
                   int currentBookings, int capacity, boolean isPastTime) {
        this.startTime = startTime;
        this.endTime = endTime;
        this.displayTime = startTime + " - " + endTime;
        this.isAvailable = isAvailable;
        this.isSelected = false;
        this.isPastTime = isPastTime;
        this.currentBookings = currentBookings;
        this.capacity = capacity;
    }
    
    public TimeSlot(String timeSlotRange, int capacity, int currentBookings, boolean isAvailable) {
        this.displayTime = timeSlotRange;
        String[] times = timeSlotRange.split(" - ");
        if (times.length == 2) {
            this.startTime = times[0];
            this.endTime = times[1];
        }
        this.capacity = capacity;
        this.currentBookings = currentBookings;
        this.isAvailable = isAvailable;
        this.isSelected = false;
        this.isPastTime = false;
    }
    
    // Getters and Setters
    public String getStartTime() {
        return startTime;
    }
    
    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }
    
    public String getEndTime() {
        return endTime;
    }
    
    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }
    
    public String getDisplayTime() {
        return displayTime;
    }
    
    public void setDisplayTime(String displayTime) {
        this.displayTime = displayTime;
    }
    
    public boolean isAvailable() {
        return isAvailable && !isPastTime && currentBookings < capacity;
    }
    
    public void setAvailable(boolean available) {
        isAvailable = available;
    }
    
    public boolean isSelected() {
        return isSelected;
    }
    
    public void setSelected(boolean selected) {
        isSelected = selected;
    }
    
    public boolean isPastTime() {
        return isPastTime;
    }
    
    public void setPastTime(boolean pastTime) {
        isPastTime = pastTime;
    }
    
    public int getCurrentBookings() {
        return currentBookings;
    }
    
    public void setCurrentBookings(int currentBookings) {
        this.currentBookings = currentBookings;
    }
    
    public int getCapacity() {
        return capacity;
    }
    
    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }
    
    public String getAvailabilityText() {
        if (isPastTime) {
            return "Đã qua";
        } else if (currentBookings >= capacity) {
            return "Đã đầy";
        } else {
            return (capacity - currentBookings) + " slot còn lại";
        }
    }
    
    public String getTimeRange() {
        return displayTime;
    }
} 