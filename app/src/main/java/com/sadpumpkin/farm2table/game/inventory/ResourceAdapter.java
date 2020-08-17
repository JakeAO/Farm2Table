package com.sadpumpkin.farm2table.game.inventory;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.sadpumpkin.farm2table.R;
import com.sadpumpkin.farm2table.util.FarmData;
import com.sadpumpkin.farm2table.util.GameDataWrapper;
import com.sadpumpkin.farm2table.util.ResourceDefinition;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

public class ResourceAdapter extends RecyclerView.Adapter<ResourceViewHolder> {

    private FarmData _farmData = null;
    private GameDataWrapper _gameData = null;

    private ArrayList<Map.Entry<String, Long>> _inventory;

    public ResourceAdapter(FarmData farmData, GameDataWrapper gameDataWrapper) {
        _farmData = farmData;
        _gameData = gameDataWrapper;
    }

    @NonNull
    @Override
    public ResourceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ResourceViewHolder(LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.view_resource, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ResourceViewHolder holder, int position) {
        Map.Entry<String, Long> entry = _inventory.get(position);
        ResourceDefinition definition = _gameData.getResourceDefinition(entry.getKey());
        Long count = entry.getValue();

        holder.setData(definition, count, _gameData);
    }

    @Override
    public int getItemCount() {
        return _inventory.size();
    }

    public void setData(Collection<Map.Entry<String, Long>> entries) {
        _inventory = new ArrayList<>(entries);
        notifyDataSetChanged();
    }
}
