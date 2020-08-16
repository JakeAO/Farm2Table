package com.sadpumpkin.farm2table.game.home;

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

public class HomeFragment extends BaseFragment {

    private RecyclerView _recyclerView = null;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        _recyclerView = view.findViewById(R.id.recyclerView);
        _recyclerView.setLayoutManager(new LinearLayoutManager(_activity));
        _recyclerView.setHasFixedSize(true);
        _recyclerView.setAdapter(new FactoryInstanceAdapter(_userData.farm(), _gameData));
    }
}
