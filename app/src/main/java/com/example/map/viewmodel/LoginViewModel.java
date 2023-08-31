package com.example.map.viewmodel;

import androidx.databinding.Observable;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;

@Getter
public class LoginViewModel extends ViewModel implements Observable {
    List<OnPropertyChangedCallback> callbacks = new ArrayList<>();

    @Getter
    String username = "gg";
    @Getter
    String password = "123";

    public void setUsername(String username) {
        this.username = username;
        notifyPropertyChanged();
    }

    public void setPassword(String password) {
        this.password = password;
        notifyPropertyChanged();
    }

    public void notifyPropertyChanged() {
        for (var callback : callbacks) {
            callback.onPropertyChanged(this, 0);
        }
    }

    @Override
    public void addOnPropertyChangedCallback(OnPropertyChangedCallback callback) {
        callbacks.add(callback);
    }

    @Override
    public void removeOnPropertyChangedCallback(OnPropertyChangedCallback callback) {
        callbacks.remove(callback);
    }
}
