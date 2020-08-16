package com.sadpumpkin.farm2table.util;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;

import com.sadpumpkin.farm2table.MainActivity;
import com.sadpumpkin.farm2table.R;
import com.sadpumpkin.farm2table.game.GameFragment;

public class BaseFragment extends Fragment {

    protected MainActivity _activity;
    protected GameFragment _gameFragment;

    protected Handler _handler;
    protected FirebaseWrapper _firebase;
    protected UserDataWrapper _userData;
    protected GameDataWrapper _gameData;
    protected NavController _mainNavController;
    protected NavController _gameNavController;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        _handler = new Handler(Looper.getMainLooper());

        _activity = (MainActivity) getActivity();
        _mainNavController = _activity.getNavController();

        ViewModelProvider vmp = new ViewModelProvider(_activity);
        _firebase = vmp.get(FirebaseWrapper.class);
        _userData = vmp.get(UserDataWrapper.class);
        _gameData = vmp.get(GameDataWrapper.class);

        Fragment parentFragment = getParentFragment();
        while (parentFragment != null) {
            if (parentFragment instanceof GameFragment) {
                _gameFragment = (GameFragment) parentFragment;
                _gameNavController = _gameFragment.getNavController();
                break;
            } else {
                parentFragment = parentFragment.getParentFragment();
            }
        }
    }
}
