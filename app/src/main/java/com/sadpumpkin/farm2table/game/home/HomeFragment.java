package com.sadpumpkin.farm2table.game.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SwitchCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.sadpumpkin.farm2table.R;
import com.sadpumpkin.farm2table.util.BaseFragment;

public class HomeFragment extends BaseFragment {

    private RecyclerView _producerListView = null;
    private RecyclerView _converterListView = null;
    private RecyclerView _consumerListView = null;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        _producerListView = view.findViewById(R.id.producerListView);
        _producerListView.setLayoutManager(new LinearLayoutManager(_activity));
        _producerListView.setAdapter(new ProducerInstanceAdapter(this, _userData.farm(), _gameData));

        SwitchCompat producerSwitch = view.findViewById(R.id.producerVisibilityToggle);
        producerSwitch.setOnCheckedChangeListener((compoundButton, b) -> _producerListView.setVisibility(b ? View.VISIBLE : View.GONE));
        producerSwitch.setChecked(true);

        _converterListView = view.findViewById(R.id.converterListView);
        _converterListView.setLayoutManager(new LinearLayoutManager(_activity));
        _converterListView.setAdapter(new ConverterInstanceAdapter(this, _userData.farm(), _gameData));

        SwitchCompat converterSwitch = view.findViewById(R.id.converterVisibilityToggle);
        converterSwitch.setOnCheckedChangeListener((compoundButton, b) -> _converterListView.setVisibility(b ? View.VISIBLE : View.GONE));
        converterSwitch.setChecked(true);

        _consumerListView = view.findViewById(R.id.consumerListView);
        _consumerListView.setLayoutManager(new LinearLayoutManager(_activity));
        _consumerListView.setAdapter(new ConsumerInstanceAdapter(this, _userData.farm(), _gameData));

        SwitchCompat consumerSwitch = view.findViewById(R.id.consumerVisibilityToggle);
        consumerSwitch.setOnCheckedChangeListener((compoundButton, b) -> _consumerListView.setVisibility(b ? View.VISIBLE : View.GONE));
        consumerSwitch.setChecked(true);
    }
}
