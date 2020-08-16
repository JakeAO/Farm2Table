package com.sadpumpkin.farm2table.game.settings;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import androidx.lifecycle.ViewModelProvider;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

import com.sadpumpkin.farm2table.MainActivity;
import com.sadpumpkin.farm2table.R;
import com.sadpumpkin.farm2table.util.AsyncLogout;
import com.sadpumpkin.farm2table.util.FirebaseWrapper;
import com.sadpumpkin.farm2table.util.UserDataWrapper;

public class SettingsFragment extends PreferenceFragmentCompat {

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.settings, rootKey);
    }

    @Override
    public boolean onPreferenceTreeClick(Preference preference) {
        if (preference.hasKey() &&
                preference.getKey().equals("")) {

            final MainActivity mainActivity = (MainActivity) getActivity();
            final ViewModelProvider vmp = new ViewModelProvider(mainActivity);
            final FirebaseWrapper firebase = vmp.get(FirebaseWrapper.class);
            final UserDataWrapper userData = vmp.get(UserDataWrapper.class);

            new Thread(
                    new AsyncLogout(
                            new Handler(Looper.getMainLooper()),
                            mainActivity,
                            firebase,
                            userData,
                            () -> {
                                userData.clear();
                                mainActivity.getNavController().navigate(R.id.splash_navigation);
                            })).start();
        }
        return super.onPreferenceTreeClick(preference);
    }
}
