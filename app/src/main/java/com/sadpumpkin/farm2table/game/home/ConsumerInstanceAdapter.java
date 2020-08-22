package com.sadpumpkin.farm2table.game.home;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;
import androidx.recyclerview.widget.RecyclerView;

import com.sadpumpkin.farm2table.R;
import com.sadpumpkin.farm2table.util.FarmData;
import com.sadpumpkin.farm2table.util.GameDataWrapper;
import com.sadpumpkin.farm2table.util.factory.BaseFactoryInstance;
import com.sadpumpkin.farm2table.util.factory.ConsumerInstance;
import com.sadpumpkin.farm2table.util.factory.definition.ConsumerDefinition;

import java.util.ArrayList;

public class ConsumerInstanceAdapter extends RecyclerView.Adapter<ConsumerInstanceViewHolder> {

    private LifecycleOwner _lifecycleOwner = null;
    private FarmData _farmData = null;
    private GameDataWrapper _gameData = null;

    private ArrayList<BaseFactoryInstance> _allInstances = new ArrayList<>();

    public ConsumerInstanceAdapter(LifecycleOwner lifecycleOwner, FarmData farmData, GameDataWrapper gameDataWrapper) {
        _lifecycleOwner = lifecycleOwner;
        _farmData = farmData;
        _gameData = gameDataWrapper;

        setData(farmData.getConsumers());
    }

    @NonNull
    @Override
    public ConsumerInstanceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ConsumerInstanceViewHolder(LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.view_factory_consumer, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ConsumerInstanceViewHolder holder, int position) {
        ConsumerInstance instance = (ConsumerInstance) _allInstances.get(position);
        ConsumerDefinition definition = _gameData.getConsumerDefinition(instance.getFactoryType());
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

    public void setData(ArrayList<ConsumerInstance> consumers) {
        _allInstances = new ArrayList<>(consumers);
        _allInstances.sort((lhs, rhs) -> Long.compare(rhs.getCount(), lhs.getCount()));
        notifyDataSetChanged();
    }
}
