package com.sadpumpkin.farm2table.util;

import com.google.gson.annotations.SerializedName;

public class ResourceDefinition {

    @SerializedName("id")
    private String _id = "";
    @SerializedName("name")
    private String _name = "";
    @SerializedName("price")
    private Long _basePrice = 1L;
    @SerializedName("icon")
    private String _storagePath = null;

    public ResourceDefinition() {
        _id = null;
        _name = "ERR";
        _basePrice = 1L;
        _storagePath = null;
    }

    public ResourceDefinition(String id, String name, Long basePrice, String storagePath) {
        _id = id;
        _name = name;
        _basePrice = basePrice;
        _storagePath = storagePath;
    }

    public String getId() {
        return _id;
    }

    public String getName() {
        return _name;
    }

    public Long getBasePrice() {
        return _basePrice;
    }

    public String getPath() {
        return _storagePath;
    }
}
