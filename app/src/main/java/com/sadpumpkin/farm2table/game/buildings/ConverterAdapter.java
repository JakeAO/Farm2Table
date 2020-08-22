package com.sadpumpkin.farm2table.game.buildings;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.sadpumpkin.farm2table.R;
import com.sadpumpkin.farm2table.util.FarmData;
import com.sadpumpkin.farm2table.util.GameDataWrapper;
import com.sadpumpkin.farm2table.util.callback.ICallback1;
import com.sadpumpkin.farm2table.util.factory.definition.ConverterDefinition;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;

public class ConverterAdapter extends RecyclerView.Adapter<ConverterViewHolder> {

    private FarmData _farmData;
    private GameDataWrapper _gameData;
    private ICallback1<String> _attemptPurchase;

    private List<ConverterDefinition> _converterDefinitions = null;

    public ConverterAdapter(FarmData farmData,
                           GameDataWrapper gameDataWrapper,
                           ICallback1<String> attemptPurchase) {
        _farmData = farmData;
        _gameData = gameDataWrapper;
        _attemptPurchase = attemptPurchase;

        setData(gameDataWrapper.getAllConverters());
    }

    @NonNull
    @Override
    public ConverterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ConverterViewHolder(LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.view_building, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ConverterViewHolder holder, int position) {
        ConverterDefinition converterDefinition = _converterDefinitions.get(position);
        holder.setData(converterDefinition,
                _farmData,
                _gameData,
                () -> _attemptPurchase.onInvoke(converterDefinition.getId()));
    }

    @Override
    public int getItemCount() {
        return _converterDefinitions.size();
    }

    public void setData(Collection<ConverterDefinition> allConverters) {
        _converterDefinitions = new ArrayList<>(allConverters);
        _converterDefinitions.sort((lhs, rhs) -> lhs.getCost().compareTo(rhs.getCost()));
        notifyDataSetChanged();
    }
}
