package com.sadpumpkin.farm2table.util.factory.definition;

import com.google.gson.annotations.SerializedName;

public class ConsumerDefinition extends BaseBuildingDefinition {

    @SerializedName("valueMultiplier")
    private float _valueMultiplier = 1f;

    public ConsumerDefinition(String id, String name, Long duration, Long cost, String[] consumedIds, float valueMultiplier) {
        super(id, name, duration, cost, consumedIds);
        _valueMultiplier = valueMultiplier;
    }

    public float getValueMultiplier() {
        return _valueMultiplier;
    }
}
