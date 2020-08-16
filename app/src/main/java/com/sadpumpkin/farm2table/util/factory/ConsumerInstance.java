package com.sadpumpkin.farm2table.util.factory;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.IgnoreExtraProperties;

@IgnoreExtraProperties
public class ConsumerInstance extends BaseFactoryInstance {

    public ConsumerInstance() {
        super();
    }
    public ConsumerInstance(String factoryType, Timestamp lastStart, Long count) {
        super(factoryType, lastStart, count);
    }
}
