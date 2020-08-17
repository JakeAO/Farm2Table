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
import com.sadpumpkin.farm2table.util.factory.ConverterInstance;
import com.sadpumpkin.farm2table.util.factory.ProducerInstance;
import com.sadpumpkin.farm2table.util.factory.definition.ConsumerDefinition;
import com.sadpumpkin.farm2table.util.factory.definition.ConverterDefinition;
import com.sadpumpkin.farm2table.util.factory.definition.ProducerDefinition;

import java.util.ArrayList;

public class FactoryInstanceAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int ITEM_TYPE_PRODUCER = 119;
    private static final int ITEM_TYPE_CONVERTER = 694;
    private static final int ITEM_TYPE_CONSUMER = 48;

    private LifecycleOwner _lifecycleOwner = null;
    private FarmData _farmData = null;
    private GameDataWrapper _gameData = null;

    private ArrayList<BaseFactoryInstance> _allInstances = new ArrayList<>();

    public FactoryInstanceAdapter(LifecycleOwner lifecycleOwner, FarmData farmData, GameDataWrapper gameDataWrapper) {
        _lifecycleOwner = lifecycleOwner;
        _farmData = farmData;
        _gameData = gameDataWrapper;

        setData(farmData.getProducers(),
                farmData.getConverters(),
                farmData.getConsumers());
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        switch (viewType) {
            case ITEM_TYPE_PRODUCER:
                return new ProducerInstanceViewHolder(LayoutInflater
                        .from(parent.getContext())
                        .inflate(R.layout.view_factory_producer, parent, false));
            case ITEM_TYPE_CONVERTER:
                return new ConverterInstanceViewHolder(LayoutInflater
                        .from(parent.getContext())
                        .inflate(R.layout.view_factory_converter, parent, false));
            case ITEM_TYPE_CONSUMER:
                return new ConsumerInstanceViewHolder(LayoutInflater
                        .from(parent.getContext())
                        .inflate(R.layout.view_factory_consumer, parent, false));
        }
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        switch (getItemViewType(position)) {
            case ITEM_TYPE_PRODUCER: {
                ProducerInstanceViewHolder typedHolder = (ProducerInstanceViewHolder) holder;
                ProducerInstance instance = (ProducerInstance) _allInstances.get(position);
                ProducerDefinition definition = _gameData.getProducerDefinition(instance.getFactoryType());
                typedHolder.setInstance(instance, definition, _gameData);

                instance.getProgressLive().observe(_lifecycleOwner, typedHolder::updateProgress);
                instance.getCountLive().observe(_lifecycleOwner, newCount ->
                        typedHolder.updateView(instance, definition, _gameData));
                break;
            }
            case ITEM_TYPE_CONVERTER: {
                ConverterInstanceViewHolder typedHolder = (ConverterInstanceViewHolder) holder;
                ConverterInstance instance = (ConverterInstance) _allInstances.get(position);
                ConverterDefinition definition = _gameData.getConverterDefinition(instance.getFactoryType());
                typedHolder.setInstance(instance, definition, _farmData, _gameData);

                instance.getProgressLive().observe(_lifecycleOwner, typedHolder::updateProgress);
                instance.getCountLive().observe(_lifecycleOwner, newCount ->
                        typedHolder.updateView(instance, definition, _farmData, _gameData));
                break;
            }
            case ITEM_TYPE_CONSUMER: {
                ConsumerInstanceViewHolder typedHolder = (ConsumerInstanceViewHolder) holder;
                ConsumerInstance instance = (ConsumerInstance) _allInstances.get(position);
                ConsumerDefinition definition = _gameData.getConsumerDefinition(instance.getFactoryType());
                typedHolder.setInstance(instance, definition, _farmData, _gameData);

                instance.getProgressLive().observe(_lifecycleOwner, typedHolder::updateProgress);
                instance.getCountLive().observe(_lifecycleOwner, newCount ->
                        typedHolder.updateView(instance, definition, _farmData, _gameData));
                break;
            }
        }
    }

    @Override
    public int getItemViewType(int position) {
        BaseFactoryInstance instance = _allInstances.get(position);
        if (instance instanceof ProducerInstance) {
            return ITEM_TYPE_PRODUCER;
        }
        if (instance instanceof ConverterInstance) {
            return ITEM_TYPE_CONVERTER;
        }
        if (instance instanceof ConsumerInstance) {
            return ITEM_TYPE_CONSUMER;
        }

        return super.getItemViewType(position);
    }

    @Override
    public int getItemCount() {
        return _allInstances.size();
    }

    public void setData(ArrayList<ProducerInstance> producers,
                        ArrayList<ConverterInstance> converters,
                        ArrayList<ConsumerInstance> consumers) {
        _allInstances = new ArrayList<>(producers.size() + converters.size() + consumers.size());
        _allInstances.addAll(producers);
        _allInstances.addAll(converters);
        _allInstances.addAll(consumers);
        notifyDataSetChanged();
    }
}