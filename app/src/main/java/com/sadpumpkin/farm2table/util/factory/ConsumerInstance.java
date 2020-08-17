package com.sadpumpkin.farm2table.util.factory;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.Exclude;
import com.google.firebase.firestore.IgnoreExtraProperties;
import com.sadpumpkin.farm2table.util.factory.definition.ConsumerDefinition;

@IgnoreExtraProperties
public class ConsumerInstance extends BaseFactoryInstance {

    @Exclude
    public ConsumerDefinition getDefinition() {
        return (ConsumerDefinition) _definition;
    }

    public ConsumerInstance() {
        super();
    }

    public ConsumerInstance(String factoryType, Timestamp lastStart, Long count) {
        super(factoryType, lastStart, count);
    }

    public ConsumerInstance(ConsumerDefinition definition){
        super(definition.getId(), Timestamp.now(), 1L);
        setDefinition(definition);
    }
}