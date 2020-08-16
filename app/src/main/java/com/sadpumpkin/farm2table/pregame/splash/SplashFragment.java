package com.sadpumpkin.farm2table.pregame.splash;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.FirebaseUiException;
import com.firebase.ui.auth.IdpResponse;
import com.google.firebase.auth.FirebaseUser;
import com.sadpumpkin.farm2table.R;
import com.sadpumpkin.farm2table.util.BaseFragment;
import com.sadpumpkin.farm2table.util.LoginAnonymous;
import com.sadpumpkin.farm2table.util.LoginReturning;

import java.util.Arrays;

import static android.app.Activity.RESULT_OK;

public class SplashFragment extends BaseFragment {

    private static final int RC_SIGN_IN = 1125;

    private ImageButton _creditsButton = null;
    private Button _playButton = null;
    private Button _newUserButton = null;
    private Button _loginButton = null;
    private TextView _accountIdLabel = null;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_splash, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        _creditsButton = view.findViewById(R.id.creditsButton);
        _playButton = view.findViewById(R.id.playButton);
        _newUserButton = view.findViewById(R.id.newUserButton);
        _loginButton = view.findViewById(R.id.loginButton);
        _accountIdLabel = view.findViewById(R.id.accountIdLabel);

        _creditsButton.setOnClickListener(clickedView -> _mainNavController.navigate(R.id.credits_navigation));
        _loginButton.setOnClickListener(clickedView -> onLoginAttempt());
        _playButton.setOnClickListener(clickedView -> new Thread(new LoginReturning(_handler, _activity, _firebase, _userData, _gameData, this::onLoginResult)).start());
        _newUserButton.setOnClickListener(clickedView -> new Thread(new LoginAnonymous(_handler, _activity, _firebase, _userData, _gameData, this::onLoginResult)).start());
    }

    @Override
    public void onResume() {
        super.onResume();

        FirebaseUser user = _firebase.auth().getCurrentUser();
        boolean loggedIn = user != null;
        _newUserButton.setVisibility(loggedIn ? View.INVISIBLE : View.VISIBLE);
        _loginButton.setVisibility(loggedIn ? View.INVISIBLE : View.VISIBLE);
        _playButton.setVisibility(loggedIn ? View.VISIBLE : View.INVISIBLE);
        _accountIdLabel.setVisibility(View.VISIBLE);
        _accountIdLabel.setText(user == null ? "Not logged in." :
                TextUtils.isEmpty(user.getDisplayName()) ? "UID: " + user.getUid() :
                        "UserName: " + user.getDisplayName());
    }

    private void onLoginAttempt() {
        Intent signInIntent = AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setAvailableProviders(Arrays.asList(
                        new AuthUI.IdpConfig.AnonymousBuilder().build(),
                        new AuthUI.IdpConfig.EmailBuilder().build(),
                        new AuthUI.IdpConfig.GoogleBuilder().build(),
                        new AuthUI.IdpConfig.PhoneBuilder().build()
                ))
                .build();

        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Unknown result
        if (requestCode != RC_SIGN_IN) {
            return;
        }

        // Failure
        if (resultCode != RESULT_OK) {
            FirebaseUiException exception = null;
            IdpResponse response = IdpResponse.fromResultIntent(data);
            if (response != null) {
                exception = response.getError();
            }

            // Only show error message if there's an error, not just a cancellation.
            if (exception != null) {
                Log.e("LOGIN", "onActivityResult: Failed Login", exception);
                new AlertDialog.Builder(_activity)
                        .setTitle("Login Failed")
                        .setMessage("There was an error during the login process, please try again.")
                        .setNegativeButton("Cancel", null)
                        .show();
            }
            return;
        }

        // Success
        new Thread(new LoginReturning(_handler, _activity, _firebase, _userData, _gameData, this::onLoginResult)).start();
    }

    private void onLoginResult(boolean success) {
        if (success) {

            _mainNavController.navigate(R.id.game_navigation);
        } else {
            onFailedLogin();
        }
    }

    private void onFailedLogin() {
        new AlertDialog.Builder(_activity)
                .setTitle("Login Failed")
                .setMessage("There was an error during the login process, please try again.")
                .setNegativeButton("Cancel", null)
                .show();
    }
}
