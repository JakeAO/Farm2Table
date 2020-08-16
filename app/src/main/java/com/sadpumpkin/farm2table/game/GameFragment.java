package com.sadpumpkin.farm2table.game;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;

import com.google.android.gms.ads.AdView;
import com.sadpumpkin.farm2table.R;
import com.sadpumpkin.farm2table.util.BaseFragment;

public class GameFragment extends BaseFragment {

    private AdView _bannerAdView = null;

    private NavController _navController = null;
    private NavHostFragment _navHost = null;
    private ToolbarFragment _navBar = null;

    private View _headerGroup = null;
    private TextView _headerLabel = null;

    private TextView _coinsLabel = null;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_game, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Gather
        _bannerAdView = view.findViewById(R.id.adView);
        _navHost = (NavHostFragment) getChildFragmentManager().findFragmentById(R.id.game_nav_host);
        _navBar = (ToolbarFragment) getChildFragmentManager().findFragmentById(R.id.game_nav_bar);
        _headerGroup = view.findViewById(R.id.pageTitleGroup);
        _headerLabel = view.findViewById(R.id.pageTitleLabel);
        _coinsLabel = view.findViewById(R.id.coinCountLabel);

        // Setup
        _navController = Navigation.findNavController(getActivity(), R.id.game_nav_host);
        _navBar.injectDependencies(_navController, _headerGroup, _headerLabel);
        _navBar.jumpToTab(R.id.home_navigation);

        //LiveData<Long> coinsData = _userData.farm().getCoins();
        _coinsLabel.setText(String.valueOf(_userData.farm().getCoins()));
//        coinsData.observe(getViewLifecycleOwner(), newCoinsValue ->
//                _coinsLabel.setText(String.valueOf(newCoinsValue))
//        );

    }

    public NavController getNavController() {
        return _navController;
    }
}