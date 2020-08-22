package com.sadpumpkin.farm2table.game.home;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;
import androidx.recyclerview.widget.RecyclerView;

import com.sadpumpkin.farm2table.R;
import com.sadpumpkin.farm2table.util.FarmData;
import com.sadpumpkin.farm2table.util.GameDataWrapper;
import com.sadpumpkin.farm2table.util.factory.ProducerInstance;
import com.sadpumpkin.farm2table.util.factory.definition.ProducerDefinition;

import java.util.ArrayList;

public class ProducerInstanceAdapter extends RecyclerView.Adapter<ProducerInstanceViewHolder> {

    private LifecycleOwner _lifecycleOwner = null;
    private FarmData _farmData = null;
    private GameDataWrapper _gameData = null;

    private ArrayList<ProducerInstance> _allInstances = new ArrayList<>();

    public ProducerInstanceAdapter(LifecycleOwner lifecycleOwner, FarmData farmData, GameDataWrapper gameDataWrapper) {
        _lifecycleOwner = lifecycleOwner;
        _farmData = farmData;
        _gameData = gameDataWrapper;

        setData(farmData.getProducers());
    }

    @NonNull
    @Override
    public ProducerInstanceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ProducerInstanceViewHolder(LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.view_factory_producer, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ProducerInstanceViewHolder holder, int position) {
        ProducerInstance instance = (ProducerInstance) _allInstances.get(position);
        ProducerDefinition definition = _gameData.getProducerDefinition(instance.getFactoryType());
        holder.setInstance(instance, definition, _gameData);

        instance.getProgressLive().observe(_lifecycleOwner, holder::updateProgress);
        instance.getActiveLive().observe(_lifecycleOwner, newActive ->
        {
            holder.updateActive(newActive);
            holder.updateView(instance, definition, _gameData);
        });
        instance.getCountLive().observe(_lifecycleOwner, newCount ->
                holder.updateView(instance, definition, _gameData));
    }

    @Override
    public int getItemCount() {
        return _allInstances.size();
    }

    public void setData(ArrayList<ProducerInstance> producers) {
        _allInstances = new ArrayList<>(producers);
        _allInstances.sort((lhs, rhs) -> Long.compare(rhs.getCount(), lhs.getCount()));
        notifyDataSetChanged();
    }
}
