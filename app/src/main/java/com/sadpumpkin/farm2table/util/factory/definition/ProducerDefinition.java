package com.sadpumpkin.farm2table.util.factory.definition;

import com.google.gson.annotations.SerializedName;

public class ProducerDefinition extends BaseFactoryDefinition {

    @SerializedName("seedId")
    private String _seedId = "";
    @SerializedName("producedId")
    private String _producedId = "";

    public ProducerDefinition(String id, String name, Long duration, String seedId, String producedId) {
        super(id, name, duration);
        _seedId = seedId;
        _producedId = producedId;
    }

    public String getSeedId() {
        return _seedId;
    }

    public String getProducedId() {
        return _producedId;
    }
}
