package com.sadpumpkin.farm2table.util.factory.definition;

import com.google.gson.annotations.SerializedName;

public abstract class BaseFactoryDefinition {

    @SerializedName("id")
    private String _id = "";
    @SerializedName("name")
    private String _name = "";
    @SerializedName("duration")
    private Long _durationMills = 30000L;

    protected BaseFactoryDefinition(String id, String name, Long duration) {
        _id = id;
        _name = name;
        _durationMills = duration;
    }

    public String getId() {
        return _id;
    }

    public String getName() {
        return _name;
    }

    public Long getDurationMills() {
        return _durationMills;
    }
}
