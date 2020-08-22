package com.sadpumpkin.farm2table.game.buildings;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.sadpumpkin.farm2table.R;
import com.sadpumpkin.farm2table.util.FarmData;
import com.sadpumpkin.farm2table.util.GameDataWrapper;
import com.sadpumpkin.farm2table.util.callback.ICallback1;
import com.sadpumpkin.farm2table.util.factory.definition.ConsumerDefinition;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class ConsumerAdapter extends RecyclerView.Adapter<ConsumerViewHolder> {

    private FarmData _farmData;
    private GameDataWrapper _gameData;
    private ICallback1<String> _attemptPurchase;

    private List<ConsumerDefinition> _consumerDefinitions = null;

    public ConsumerAdapter(FarmData farmData,
                           GameDataWrapper gameDataWrapper,
                           ICallback1<String> attemptPurchase) {
        _farmData = farmData;
        _gameData = gameDataWrapper;
        _attemptPurchase = attemptPurchase;

        setData(gameDataWrapper.getAllConsumers());
    }

    @NonNull
    @Override
    public ConsumerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ConsumerViewHolder(LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.view_building, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ConsumerViewHolder holder, int position) {
        ConsumerDefinition consumerDefinition = _consumerDefinitions.get(position);
        holder.setData(consumerDefinition,
                _farmData,
                _gameData,
                () -> _attemptPurchase.onInvoke(consumerDefinition.getId()));
    }

    @Override
    public int getItemCount() {
        return _consumerDefinitions.size();
    }

    public void setData(Collection<ConsumerDefinition> allConsumers) {
        _consumerDefinitions = new ArrayList<>(allConsumers);
        _consumerDefinitions.sort((lhs, rhs) -> lhs.getCost().compareTo(rhs.getCost()));
        notifyDataSetChanged();
    }
}
