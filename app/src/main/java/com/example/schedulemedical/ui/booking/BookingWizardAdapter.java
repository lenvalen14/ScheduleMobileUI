package com.example.schedulemedical.ui.booking;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.schedulemedical.ui.booking.fragments.DoctorSelectionFragment;
import com.example.schedulemedical.ui.booking.fragments.ScheduleSelectionFragment;
import com.example.schedulemedical.ui.booking.fragments.ServiceSelectionFragment;
import com.example.schedulemedical.ui.booking.fragments.SpecialtySelectionFragment;

public class BookingWizardAdapter extends FragmentStateAdapter {
    private BookingWizardActivity.BookingData bookingData;
    
    public BookingWizardAdapter(@NonNull FragmentActivity fragmentActivity, 
                               BookingWizardActivity.BookingData bookingData) {
        super(fragmentActivity);
        this.bookingData = bookingData;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0: return SpecialtySelectionFragment.newInstance(bookingData);
            case 1: return DoctorSelectionFragment.newInstance(bookingData);
            case 2: return ScheduleSelectionFragment.newInstance(bookingData);
            case 3: return ServiceSelectionFragment.newInstance(bookingData);
            default: return SpecialtySelectionFragment.newInstance(bookingData);
        }
    }

    @Override
    public int getItemCount() {
        return 4; // Total steps
    }
} 