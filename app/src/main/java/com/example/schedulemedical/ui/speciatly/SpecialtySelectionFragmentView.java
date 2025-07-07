package com.example.schedulemedical.ui.speciatly;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.schedulemedical.Adapter.SpecialtyAdapter;
import com.example.schedulemedical.R;
import com.example.schedulemedical.data.api.ApiClient;
import com.example.schedulemedical.data.api.DoctorApiService;
import com.example.schedulemedical.model.Specialty;
import com.example.schedulemedical.model.dto.response.ApiResponse;
import com.example.schedulemedical.model.dto.response.SpecialtyResponse;
import com.example.schedulemedical.ui.booking.BookingWizardActivity;
import com.example.schedulemedical.ui.booking.fragments.SpecialtySelectionFragment;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

public class SpecialtySelectionFragmentView extends Fragment implements SpecialtyAdapter.OnSpecialtySelectedListener {

    private static final String TAG = "SpecialtySelection";
    private static final String ARG_BOOKING_DATA = "booking_data";

    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private TextView tvNoSpecialties;

    private BookingWizardActivity.BookingData bookingData;
    private List<Specialty> specialtiesList;
    private SpecialtyAdapter specialtyAdapter;

    private DoctorApiService doctorApiService;

    public static SpecialtySelectionFragment newInstance(BookingWizardActivity.BookingData bookingData) {
        SpecialtySelectionFragment fragment = new SpecialtySelectionFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_BOOKING_DATA, bookingData);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            bookingData = (BookingWizardActivity.BookingData) getArguments().getSerializable(ARG_BOOKING_DATA);
        }

        ApiClient.init(requireContext());
        doctorApiService = ApiClient.getDoctorApiService();
        specialtiesList = new ArrayList<>();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_specialty_selection, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initializeViews(view);
        setupRecyclerView();
        setupSwipeRefresh();
        loadSpecialties();
    }

    private void initializeViews(View view) {
        recyclerView = view.findViewById(R.id.recyclerViewSpecialties);
        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout);
        tvNoSpecialties = view.findViewById(R.id.tvNoSpecialties); // Bạn cần thêm textview này trong XML nếu chưa có
    }

    private void setupRecyclerView() {
        specialtyAdapter = new SpecialtyAdapter(requireContext(), specialtiesList);
        specialtyAdapter.setOnSpecialtySelectedListener(this);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerView.setAdapter(specialtyAdapter);
    }

    private void setupSwipeRefresh() {
        swipeRefreshLayout.setOnRefreshListener(this::loadSpecialties);
    }

    private void loadSpecialties() {
        showLoading(true);

        doctorApiService.getAllSpecialties(1, 50).enqueue(new Callback<ApiResponse<List<SpecialtyResponse>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<SpecialtyResponse>>> call, Response<ApiResponse<List<SpecialtyResponse>>> response) {
                swipeRefreshLayout.setRefreshing(false);
                showLoading(false);

                if (response.isSuccessful() && response.body() != null) {
                    List<SpecialtyResponse> responseList = response.body().getData();
                    List<Specialty> loadedSpecialties = new ArrayList<>();

                    for (SpecialtyResponse res : responseList) {
                        loadedSpecialties.add(new Specialty(
                                res.getSpecialtyId(),
                                res.getName(),
                                res.getDescription(),
                                null
                        ));
                    }

                    updateSpecialtiesList(loadedSpecialties);
                } else {
                    showError("Không thể tải danh sách chuyên khoa");
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<List<SpecialtyResponse>>> call, Throwable t) {
                Log.e(TAG, "API call failed", t);
                swipeRefreshLayout.setRefreshing(false);
                showLoading(false);
                showError("Lỗi kết nối mạng");
            }
        });
    }

    private void updateSpecialtiesList(List<Specialty> specialties) {
        specialtiesList.clear();
        specialtiesList.addAll(specialties);
        specialtyAdapter.updateSpecialties(specialtiesList);

        if (specialties.isEmpty()) {
            recyclerView.setVisibility(View.GONE);
            tvNoSpecialties.setVisibility(View.VISIBLE);
            tvNoSpecialties.setText("Không có chuyên khoa nào.");
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            tvNoSpecialties.setVisibility(View.GONE);
        }
    }

    private void showLoading(boolean isLoading) {
        swipeRefreshLayout.setRefreshing(isLoading);
    }

    private void showError(String message) {
        recyclerView.setVisibility(View.GONE);
        tvNoSpecialties.setVisibility(View.VISIBLE);
        tvNoSpecialties.setText(message);
    }

    @Override
    public void onSpecialtySelected(Specialty specialty, int position) {
        Toast.makeText(requireContext(), "Đã chọn: " + specialty.getName(), Toast.LENGTH_SHORT).show();

        bookingData.specialtyId = specialty.getSpecialtyId();
        bookingData.specialtyName = specialty.getName();

        if (getActivity() instanceof BookingWizardActivity) {
            ((BookingWizardActivity) getActivity()).onStepDataChanged();
        }
    }
}
