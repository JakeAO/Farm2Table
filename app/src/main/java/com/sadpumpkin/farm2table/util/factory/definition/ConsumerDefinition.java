package com.sadpumpkin.farm2table.util.factory.definition;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class ConsumerDefinition extends BaseFactoryDefinition {

    @SerializedName("cost")
    private Long _baseCost = 100L;
    @SerializedName("consumedIds")
    private ArrayList<String> _consumedIds = new ArrayList<>();
    @SerializedName("valueMultiplier")
    private float _valueMultiplier = 1f;

    public ConsumerDefinition(String id, String name, Long duration, Long cost, ArrayList<String> consumedIds, float valueMultiplier) {
        super(id, name, duration);
        _baseCost = cost;
        _consumedIds = consumedIds;
        _valueMultiplier = valueMultiplier;
    }

    public Long getCost(){
        return _baseCost;
    }

    public List<String> getConsumedIds(){
        return _consumedIds;
    }

    public float getValueMultiplier() {
        return _valueMultiplier;
    }
}
