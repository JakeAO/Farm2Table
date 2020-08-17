package com.sadpumpkin.farm2table.game.buildings;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.sadpumpkin.farm2table.R;
import com.sadpumpkin.farm2table.util.FarmData;
import com.sadpumpkin.farm2table.util.GameDataWrapper;
import com.sadpumpkin.farm2table.util.callback.ICallback1;
import com.sadpumpkin.farm2table.util.factory.definition.BaseBuildingDefinition;
import com.sadpumpkin.farm2table.util.factory.definition.ConsumerDefinition;
import com.sadpumpkin.farm2table.util.factory.definition.ConverterDefinition;

import java.util.ArrayList;
import java.util.Collection;

public class BuildingAdapter extends RecyclerView.Adapter<BuildingViewHolder> {

    private static final int TYPE_ID_CONVERTER = 894;
    private static final int TYPE_ID_CONSUMER = 408;

    private FarmData _farmData;
    private GameDataWrapper _gameData;
    private ICallback1<String> _attemptPurchaseConverter;
    private ICallback1<String> _attemptPurchaseConsumer;

    private ArrayList<BaseBuildingDefinition> _buildings;

    public BuildingAdapter(FarmData farmData,
                           GameDataWrapper gameDataWrapper,
                           ICallback1<String> attemptPurchaseConverter,
                           ICallback1<String> attemptPurchaseConsumer) {
        _farmData = farmData;
        _gameData = gameDataWrapper;
        _attemptPurchaseConverter = attemptPurchaseConverter;
        _attemptPurchaseConsumer = attemptPurchaseConsumer;

        setData(gameDataWrapper.getAllConverters(),
                gameDataWrapper.getAllConsumers());
    }

    @NonNull
    @Override
    public BuildingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new BuildingViewHolder(LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.view_building, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull BuildingViewHolder holder, int position) {
        BaseBuildingDefinition definition = _buildings.get(position);
        switch (getItemViewType(position)) {
            case TYPE_ID_CONVERTER:
                holder.setData(
                        (ConverterDefinition) definition,
                        _farmData,
                        _gameData,
                        () -> _attemptPurchaseConverter.onInvoke(definition.getId()));
                break;
            case TYPE_ID_CONSUMER:
                holder.setData(
                        (ConsumerDefinition) definition,
                        _farmData,
                        _gameData,
                        () -> _attemptPurchaseConsumer.onInvoke(definition.getId()));
                break;
        }
    }

    @Override
    public int getItemViewType(int position) {
        BaseBuildingDefinition definition = _buildings.get(position);
        if (definition instanceof ConverterDefinition) {
            return TYPE_ID_CONVERTER;
        }
        if (definition instanceof ConsumerDefinition) {
            return TYPE_ID_CONSUMER;
        }

        return super.getItemViewType(position);
    }

    @Override
    public int getItemCount() {
        return _buildings.size();
    }

    public void setData(Collection<ConverterDefinition> converterBuildings,
                        Collection<ConsumerDefinition> consumerBuildings) {
        _buildings = new ArrayList<>(converterBuildings.size() + consumerBuildings.size());
        _buildings.addAll(converterBuildings);
        _buildings.addAll(consumerBuildings);
        notifyDataSetChanged();
    }
}
