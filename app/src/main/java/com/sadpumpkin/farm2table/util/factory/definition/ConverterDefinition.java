package com.sadpumpkin.farm2table.util.factory.definition;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class ConverterDefinition extends BaseFactoryDefinition {

    @SerializedName("cost")
    private Long _baseCost = 100L;
    @SerializedName("conversionMap")
    private ArrayList<String> _conversionMap = new ArrayList<>();

    public ConverterDefinition(String id, String name, Long duration, Long cost, ArrayList<String> conversionMap) {
        super(id, name, duration);
        _baseCost = cost;
        _conversionMap = conversionMap;
    }

    public Long getCost() {
        return _baseCost;
    }

    public List<String> getConversionMap() {
        return _conversionMap;
    }

    public List<String> getConsumedIds() {
        List<String> consumed = new ArrayList<>(_conversionMap.size() / 2);
        for (int i = 0; i < _conversionMap.size(); i += 2) {
            consumed.add(_conversionMap.get(i)); // Even = Consumed
        }
        return consumed;
    }

    public List<String> getProducedIds() {
        List<String> consumed = new ArrayList<>(_conversionMap.size() / 2);
        for (int i = 1; i < _conversionMap.size(); i += 2) {
            consumed.add(_conversionMap.get(i)); // Odd = Produced
        }
        return consumed;
    }

    public String getProducedIdForConsumed(String consumedId) {
        for (int i = 0; i < _conversionMap.size(); i += 2) {
            if (_conversionMap.get(i).equals(consumedId)) {
                return _conversionMap.get(i + 1);
            }
        }
        return null;
    }
}
