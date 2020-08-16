package com.sadpumpkin.farm2table.util.factory;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.IgnoreExtraProperties;

@IgnoreExtraProperties
public class ConverterInstance extends BaseFactoryInstance {

    public ConverterInstance() {
        super();
    }
    public ConverterInstance(String factoryType, Timestamp lastStart, Long count) {
        super(factoryType, lastStart, count);
    }
}
