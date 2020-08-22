package com.sadpumpkin.farm2table.game.home;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;
import androidx.recyclerview.widget.RecyclerView;

import com.sadpumpkin.farm2table.R;
import com.sadpumpkin.farm2table.util.FarmData;
import com.sadpumpkin.farm2table.util.GameDataWrapper;
import com.sadpumpkin.farm2table.util.factory.ConverterInstance;
import com.sadpumpkin.farm2table.util.factory.definition.ConverterDefinition;

import java.util.ArrayList;

public class ConverterInstanceAdapter extends RecyclerView.Adapter<ConverterInstanceViewHolder> {

    private LifecycleOwner _lifecycleOwner = null;
    private FarmData _farmData = null;
    private GameDataWrapper _gameData = null;

    private ArrayList<ConverterInstance> _allInstances = new ArrayList<>();

    public ConverterInstanceAdapter(LifecycleOwner lifecycleOwner, FarmData farmData, GameDataWrapper gameDataWrapper) {
        _lifecycleOwner = lifecycleOwner;
        _farmData = farmData;
        _gameData = gameDataWrapper;

        setData(farmData.getConverters());
    }

    @NonNull
    @Override
    public ConverterInstanceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ConverterInstanceViewHolder(LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.view_factory_converter, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ConverterInstanceViewHolder holder, int position) {
        ConverterInstance instance = (ConverterInstance) _allInstances.get(position);
        ConverterDefinition definition = _gameData.getConverterDefinition(instance.getFactoryType());
        holder.setInstance(instance, definition, _farmData, _gameData);

        instance.getProgressLive().observe(_lifecycleOwner, holder::updateProgress);
        instance.getActiveLive().observe(_lifecycleOwner, newActive ->
        {
            holder.updateActive(newActive);
            holder.updateView(instance, definition, _farmData, _gameData);
        });
        instance.getCountLive().observe(_lifecycleOwner, newCount ->
                holder.updateView(instance, definition, _farmData, _gameData));
    }

    @Override
    public int getItemCount() {
        return _allInstances.size();
    }

    public void setData(ArrayList<ConverterInstance> converters) {
        _allInstances = new ArrayList<>(converters);
        _allInstances.sort((lhs, rhs) -> Long.compare(rhs.getCount(), lhs.getCount()));
        notifyDataSetChanged();
    }
}
