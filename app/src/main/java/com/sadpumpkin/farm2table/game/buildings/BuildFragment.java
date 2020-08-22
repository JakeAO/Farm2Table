package com.sadpumpkin.farm2table.game.buildings;

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
import com.sadpumpkin.farm2table.util.factory.definition.ConsumerDefinition;
import com.sadpumpkin.farm2table.util.factory.definition.ConverterDefinition;

public class BuildFragment extends BaseFragment {

    private RecyclerView _converterListView = null;
    private RecyclerView _consumerListView = null;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_build, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        _converterListView = view.findViewById(R.id.converterListView);
        _converterListView.setLayoutManager(new LinearLayoutManager(_activity));
        _converterListView.setHasFixedSize(true);
        _converterListView.setAdapter(new ConverterAdapter(_userData.farm(), _gameData, this::onAttemptPurchaseConverter));

        SwitchCompat converterSwitch = view.findViewById(R.id.converterVisibilityToggle);
        converterSwitch.setOnCheckedChangeListener((compoundButton, b) -> _converterListView.setVisibility(b ? View.VISIBLE : View.GONE));
        converterSwitch.setChecked(true);

        _consumerListView = view.findViewById(R.id.consumerListView);
        _consumerListView.setLayoutManager(new LinearLayoutManager(_activity));
        _consumerListView.setHasFixedSize(true);
        _consumerListView.setAdapter(new ConsumerAdapter(_userData.farm(), _gameData, this::onAttemptPurchaseConsumer));

        SwitchCompat consumerSwitch = view.findViewById(R.id.consumerVisibilityToggle);
        consumerSwitch.setOnCheckedChangeListener((compoundButton, b) -> _consumerListView.setVisibility(b ? View.VISIBLE : View.GONE));
        consumerSwitch.setChecked(true);
    }

    private void onAttemptPurchaseConverter(String converterId) {
        ConverterDefinition definition = _gameData.getConverterDefinition(converterId);
        long currentCoins = _userData.farm().getCoins();

        if (definition != null && currentCoins >= definition.getCost()) {
            _userData.farm().addConverter(definition);
            _userData.farm().setCoins(currentCoins - definition.getCost());

            _firebase.getUserDocRef(_userData.user()).set(_userData.farm());
        }
    }

    private void onAttemptPurchaseConsumer(String consumerId) {
        ConsumerDefinition definition = _gameData.getConsumerDefinition(consumerId);
        long currentCoins = _userData.farm().getCoins();

        if (definition != null && currentCoins >= definition.getCost()) {
            _userData.farm().addConsumer(definition);
            _userData.farm().setCoins(currentCoins - definition.getCost());

            _firebase.getUserDocRef(_userData.user()).set(_userData.farm());
        }
    }
}
