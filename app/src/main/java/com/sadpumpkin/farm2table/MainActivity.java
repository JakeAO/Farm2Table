package com.sadpumpkin.farm2table;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;

import com.google.android.gms.ads.MobileAds;
import com.sadpumpkin.farm2table.util.FirebaseWrapper;

public class MainActivity extends AppCompatActivity {

    private NavHostFragment _navHost = null;
    private NavController _navController = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        new ViewModelProvider(this).get(FirebaseWrapper.class);

        MobileAds.initialize(this);

        _navHost = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.main_nav_host);
        _navController = Navigation.findNavController(this, R.id.main_nav_host);
    }

    @Override
    protected void onStart() {
        super.onStart();

        _navController.navigate(R.id.splash_navigation);
    }

    public NavController getNavController() {
        return _navController;
    }
}