package com.sadpumpkin.farm2table.game.inventory;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.sadpumpkin.farm2table.R;
import com.sadpumpkin.farm2table.util.BaseFragment;

public class InventoryFragment extends BaseFragment {

    private RecyclerView _recyclerView = null;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_inventory, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ResourceAdapter resourceAdapter = new ResourceAdapter(_userData.farm(), _gameData);

        _recyclerView = view.findViewById(R.id.producerListView);
        _recyclerView.setLayoutManager(new LinearLayoutManager(_activity));
        _recyclerView.setHasFixedSize(true);
        _recyclerView.setAdapter(resourceAdapter);

        _userData.farm().getInventoryLive().observe(getViewLifecycleOwner(), newMap ->
                resourceAdapter.setData(newMap.entrySet()));
    }
}
