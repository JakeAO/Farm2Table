package com.sadpumpkin.farm2table.util.factory;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.Exclude;
import com.google.firebase.firestore.IgnoreExtraProperties;
import com.sadpumpkin.farm2table.util.factory.definition.ConverterDefinition;

@IgnoreExtraProperties
public class ConverterInstance extends BaseFactoryInstance {

    @Exclude
    public ConverterDefinition getDefinition() {
        return (ConverterDefinition) _definition;
    }

    public ConverterInstance() {
        super();
    }

    public ConverterInstance(String factoryType, Timestamp lastStart, Long count) {
        super(factoryType, lastStart, count);
    }
}