package com.example.schedulemedical.ui.login;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.schedulemedical.data.repository.LoginRepository;
import com.example.schedulemedical.model.dto.request.AccountLoginRequest;
import com.example.schedulemedical.model.dto.response.ResponseWrapper;
import com.example.schedulemedical.model.dto.response.TokenDTO;

public class LoginViewModel extends ViewModel {
    private final LoginRepository repository = new LoginRepository();
    private final MutableLiveData<ResponseWrapper<TokenDTO>> loginResult = new MutableLiveData<>();

    public LiveData<ResponseWrapper<TokenDTO>> getLoginResult() {
        return loginResult;
    }

    public void login(String email, String password) {
        repository.login(new AccountLoginRequest(email, password), loginResult);
    }
}