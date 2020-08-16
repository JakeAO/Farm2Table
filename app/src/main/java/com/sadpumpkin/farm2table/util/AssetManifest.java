package com.sadpumpkin.farm2table.util;

import com.google.gson.annotations.SerializedName;

public class AssetManifest {

    @SerializedName("seedManifest")
    private String _seedManifest;
    @SerializedName("resourceManifest")
    private String _resourceManifest;
    @SerializedName("producersManifest")
    private String _producerManifest;
    @SerializedName("convertersManifest")
    private String _converterManifest;
    @SerializedName("consumersManifest")
    private String _consumerManifest;

    public String getSeedManifest() {
        return _seedManifest;
    }

    public String getResourceManifest() {
        return _resourceManifest;
    }

    public String getProducerManifest() {
        return _producerManifest;
    }

    public String getConverterManifest() {
        return _converterManifest;
    }

    public String getConsumerManifest() {
        return _consumerManifest;
    }
}
