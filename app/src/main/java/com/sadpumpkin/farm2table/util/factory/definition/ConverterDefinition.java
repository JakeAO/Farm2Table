package com.sadpumpkin.farm2table.util.factory.definition;

import com.google.gson.annotations.SerializedName;

public class ConverterDefinition extends BaseBuildingDefinition {

    @SerializedName("producedId")
    private String _producedId = "";

    public ConverterDefinition(String id, String name, Long duration, Long cost, String[] consumedIds, String producedId) {
        super(id, name, duration, cost, consumedIds);
        _producedId = producedId;
    }

    public String getProducedId() {
        return _producedId;
    }
}
