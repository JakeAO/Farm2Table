package com.sadpumpkin.farm2table.util;

import android.text.TextUtils;

import androidx.lifecycle.ViewModel;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;

public class UserDataWrapper extends ViewModel {
    private static final int TICK_FREQUENCY = 1000;

    private GameDataWrapper _gameData;

    private FirebaseUser _user;
    private FarmData _farm;

    private Thread _activeTickThread = null;

    public void setGameDataDependency(GameDataWrapper gameDataWrapper){
        _gameData = gameDataWrapper;
    }

    public void setUser(FirebaseUser user){
        _user = user;
    }

    public void setFarm(FarmData farm) {
        _farm = farm;

        // Clear old Tick
        if (_activeTickThread != null) {
            _activeTickThread.stop();
            _activeTickThread = null;
        }

        // Start new Tick
        _activeTickThread = new Thread(() -> {
            while (true) {
                try {
                    Thread.sleep(TICK_FREQUENCY);
                    _farm.tick(TICK_FREQUENCY, _gameData);
                } catch (InterruptedException ex) {
                    break;
                }
            }
        });
        _activeTickThread.start();
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
