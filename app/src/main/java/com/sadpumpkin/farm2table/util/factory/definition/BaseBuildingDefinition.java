package com.sadpumpkin.farm2table.util.factory.definition;

import com.google.gson.annotations.SerializedName;

public abstract class BaseBuildingDefinition extends BaseFactoryDefinition {

    @SerializedName("cost")
    private Long _baseCost = 100L;
    @SerializedName("consumedId")
    private String[] _consumedIds = new String[0];

    protected BaseBuildingDefinition(String id, String name, Long duration, Long cost, String[] consumedIds) {
        super(id, name, duration);
        _baseCost = cost;
        _consumedIds = consumedIds;
    }

    public Long getBaseCost() {
        return _baseCost;
    }

    public String[] getConsumedIds() {
        return _consumedIds;
    }
}
