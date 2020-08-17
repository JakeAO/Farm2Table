package com.sadpumpkin.farm2table.game.buildings;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.sadpumpkin.farm2table.R;
import com.sadpumpkin.farm2table.util.BaseFragment;
import com.sadpumpkin.farm2table.util.factory.ConsumerInstance;
import com.sadpumpkin.farm2table.util.factory.ConverterInstance;
import com.sadpumpkin.farm2table.util.factory.definition.ConsumerDefinition;
import com.sadpumpkin.farm2table.util.factory.definition.ConverterDefinition;

public class BuildFragment extends BaseFragment {

    private RecyclerView _recyclerView = null;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_build, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        _recyclerView = view.findViewById(R.id.recyclerView);
        _recyclerView.setLayoutManager(new LinearLayoutManager(_activity));
        _recyclerView.setHasFixedSize(true);
        _recyclerView.setAdapter(
                new BuildingAdapter(
                        _userData.farm(),
                        _gameData,
                        this::onAttemptPurchaseConverter,
                        this::onAttemptPurchaseConsumer));
    }

    private void onAttemptPurchaseConverter(String converterId) {
        ConverterDefinition definition = _gameData.getConverterDefinition(converterId);
        long currentCoins = _userData.farm().getCoins();

        if (definition != null && currentCoins >= definition.getBaseCost()) {
            _userData.farm().addConverter(definition);
            _userData.farm().setCoins(currentCoins - definition.getBaseCost());
        }
    }

    private void onAttemptPurchaseConsumer(String consumerId) {
        ConsumerDefinition definition = _gameData.getConsumerDefinition(consumerId);
        long currentCoins = _userData.farm().getCoins();

        if (definition != null && currentCoins >= definition.getBaseCost()) {
            _userData.farm().addConsumer(definition);
            _userData.farm().setCoins(currentCoins - definition.getBaseCost());
        }
    }
}
