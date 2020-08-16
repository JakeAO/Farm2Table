package com.sadpumpkin.farm2table.game;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.NavDestination;

import com.google.android.material.tabs.TabLayout;
import com.sadpumpkin.farm2table.R;
import com.sadpumpkin.farm2table.util.BaseFragment;

public class ToolbarFragment extends BaseFragment {

    private TabLayout _tabLayout = null;
    private TabLayout.Tab _homeTab = null;
    private TabLayout.Tab _forageTab = null;
    private TabLayout.Tab _buildTab = null;
    private TabLayout.Tab _inventoryTab = null;
    private TabLayout.Tab _settingsTab = null;

    private NavController _navController = null;
    private View _headerGroup = null;
    private TextView _headerLabel = null;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_game_toolbar, container, false);

        _tabLayout = root.findViewById(R.id.toolbarTabLayout);
        _homeTab = _tabLayout.getTabAt(0);
        _forageTab = _tabLayout.getTabAt(1);
        _buildTab = _tabLayout.getTabAt(2);
        _inventoryTab = _tabLayout.getTabAt(3);
        _settingsTab = _tabLayout.getTabAt(4);

        return root;
    }

    public void injectDependencies(NavController navController, View headerGroup, TextView headerLabel) {
        _navController = navController;
        _headerGroup = headerGroup;
        _headerLabel = headerLabel;

        // Header changes when GameFragment navigates to a new page.
        _navController.addOnDestinationChangedListener(new NavController.OnDestinationChangedListener() {
            @Override
            public void onDestinationChanged(@NonNull NavController controller, @NonNull NavDestination destination, @Nullable Bundle arguments) {
                switch (destination.getId()) {
                    case R.id.home_navigation:
                        _headerLabel.setText(R.string.title_home);
                        _headerGroup.setVisibility(View.INVISIBLE);
                        break;
                    case R.id.forage_navigation:
                        _headerLabel.setText(R.string.title_forage);
                        _headerGroup.setVisibility(View.VISIBLE);
                        break;
                    case R.id.build_navigation:
                        _headerLabel.setText(R.string.title_build);
                        _headerGroup.setVisibility(View.VISIBLE);
                        break;
                    case R.id.inventory_navigation:
                        _headerLabel.setText(R.string.title_inventory);
                        _headerGroup.setVisibility(View.VISIBLE);
                        break;
                    case R.id.settings_navigation:
                        _headerLabel.setText(R.string.title_settings);
                        _headerGroup.setVisibility(View.VISIBLE);
                        break;
                }
            }
        });

        // Tabs trigger a Navigation event in GameFragment.
        _tabLayout.clearOnTabSelectedListeners();
        _tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (tab == _homeTab) {
                    _navController.navigate(R.id.home_navigation);
                } else if (tab == _forageTab) {
                    _navController.navigate(R.id.forage_navigation);
                } else if (tab == _buildTab) {
                    _navController.navigate(R.id.build_navigation);
                } else if (tab == _inventoryTab) {
                    _navController.navigate(R.id.inventory_navigation);
                } else if (tab == _settingsTab) {
                    _navController.navigate(R.id.settings_navigation);
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });
    }

    public void jumpToTab(int navigationId) {
        switch (navigationId) {
            case R.id.home_navigation:
                _tabLayout.selectTab(_homeTab);
                break;
            case R.id.forage_navigation:
                _tabLayout.selectTab(_forageTab);
                break;
            case R.id.build_navigation:
                _tabLayout.selectTab(_buildTab);
                break;
            case R.id.inventory_navigation:
                _tabLayout.selectTab(_inventoryTab);
                break;
            case R.id.settings_navigation:
                _tabLayout.selectTab(_settingsTab);
                break;
        }
    }
}