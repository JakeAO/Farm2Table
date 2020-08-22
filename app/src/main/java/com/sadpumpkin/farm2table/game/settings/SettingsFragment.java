package com.sadpumpkin.farm2table.game.settings;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

import com.sadpumpkin.farm2table.MainActivity;
import com.sadpumpkin.farm2table.R;
import com.sadpumpkin.farm2table.util.AsyncLogout;
import com.sadpumpkin.farm2table.util.FirebaseWrapper;
import com.sadpumpkin.farm2table.util.UserDataWrapper;

public class SettingsFragment extends PreferenceFragmentCompat {

    private MainActivity _activity = null;
    private ViewModelProvider _vmp = null;
    private FirebaseWrapper _firebase = null;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        _activity = (MainActivity) getActivity();
        _vmp = new ViewModelProvider((MainActivity) getActivity());
        _firebase = _vmp.get(FirebaseWrapper.class);
    }

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.settings, rootKey);
    }

    @Override
    public boolean onPreferenceTreeClick(Preference preference) {
        if (preference.hasKey() &&
                preference.getKey().equals("sign_out")) {

            final UserDataWrapper userData = _vmp.get(UserDataWrapper.class);

            new Thread(
                    new AsyncLogout(
                            new Handler(Looper.getMainLooper()),
                            _activity,
                            _firebase,
                            userData,
                            () -> {
                                userData.clear();
                                _activity.getNavController().navigate(R.id.splash_navigation);
                            })).start();
        }
        return super.onPreferenceTreeClick(preference);
    }
}
