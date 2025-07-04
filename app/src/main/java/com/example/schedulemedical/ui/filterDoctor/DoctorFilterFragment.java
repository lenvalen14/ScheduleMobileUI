package com.example.schedulemedical.ui.filterDoctor;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.schedulemedical.R;
import com.example.schedulemedical.data.api.ApiClient;
import com.example.schedulemedical.data.repository.DoctorFilterRepository;
import com.example.schedulemedical.model.dto.response.HospitalResponse;
import com.example.schedulemedical.model.dto.response.SpecialtyResponse;

import java.util.ArrayList;
import java.util.List;

public class DoctorFilterFragment extends Fragment {

    private DoctorFilterOptionsViewModel viewModel;
    private Spinner spinnerSpecialty, spinnerHospital;
    private TextView tvRatingValue;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        Log.d("DoctorFilterFragment", "onCreateView called");
        return inflater.inflate(R.layout.bottom_sheet_doctor_filter, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.d("DoctorFilterFragment", "onViewCreated called");

        spinnerSpecialty = view.findViewById(R.id.spinnerSpecialty);
        spinnerHospital = view.findViewById(R.id.spinnerHospital);
        tvRatingValue = view.findViewById(R.id.tvRatingValue);
        SeekBar seekBarRating = view.findViewById(R.id.seekBarRating);

        // Gán giá trị rating khi thay đổi
        seekBarRating.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                float rating = progress / 10f;
                tvRatingValue.setText(rating == 0 ? "All ratings" : rating + "★+");
            }

            @Override public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        // Tạo repository và ViewModel trực tiếp
        DoctorFilterRepository repository = new DoctorFilterRepository(
                ApiClient.getDoctorApiService(),
                ApiClient.getHospitalApiService()
        );

        viewModel = new DoctorFilterOptionsViewModel(repository);

        // Lắng nghe dữ liệu chuyên khoa
        viewModel.specialties.observe(getViewLifecycleOwner(), specialties -> {
            List<String> names = new ArrayList<>();
            for (SpecialtyResponse s : specialties) names.add(s.getName());

            ArrayAdapter<String> adapter = new ArrayAdapter<>(
                    requireContext(),
                    android.R.layout.simple_spinner_item,
                    names
            );
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerSpecialty.setAdapter(adapter);
        });

        // Lắng nghe dữ liệu bệnh viện
        viewModel.hospitals.observe(getViewLifecycleOwner(), hospitals -> {
            List<String> names = new ArrayList<>();
            for (HospitalResponse h : hospitals) names.add(h.getName());

            ArrayAdapter<String> adapter = new ArrayAdapter<>(
                    requireContext(),
                    android.R.layout.simple_spinner_item,
                    names
            );
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerHospital.setAdapter(adapter);
        });
    }
}
