package com.sadpumpkin.farm2table.util;

import android.text.TextUtils;

import androidx.lifecycle.ViewModel;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;

public class UserDataWrapper extends ViewModel {
    private FirebaseUser _user;
    private FarmData _farm;

    public void setUser(FirebaseUser user) {
        _user = user;
    }

    public void setFarm(FarmData farm) {
        _farm = farm;
    }

    public FirebaseUser user() {
        return _user;
    }

    public FarmData farm() {
        return _farm;
    }

    public String getDisplayName() {
        if (_user == null) {
            return "";
        }

        String displayName = _user.getDisplayName();
        if (!TextUtils.isEmpty(displayName)) {
            return displayName;
        }

        for (UserInfo profile : _user.getProviderData()) {
            displayName = profile.getDisplayName();
            if (!TextUtils.isEmpty(displayName)) {
                return displayName;
            }
        }

        return "";
    }

    public void clear() {
        _user = null;
        _farm = null;
    }
}
