package com.sadpumpkin.farm2table.util;

import com.google.gson.annotations.SerializedName;

public class SeedDefinition {

    @SerializedName("id")
    private String _id = "";
    @SerializedName("name")
    private String _name = "";
    @SerializedName("icon")
    private String _storagePath = null;
    @SerializedName("rarity")
    private double _rarity = 1d;

    public SeedDefinition(String id, String name, String storagePath, double rarity) {
        _id = id;
        _name = name;
        _storagePath = storagePath;
        _rarity = rarity;
    }

    public String getId() {
        return _id;
    }

    public String getName() {
        return _name;
    }

    public String getPath() {
        return _storagePath;
    }

    public double getRarity() {
        return _rarity;
    }
}
