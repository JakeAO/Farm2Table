package com.sadpumpkin.farm2table.util.factory;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.Exclude;
import com.google.firebase.firestore.IgnoreExtraProperties;
import com.sadpumpkin.farm2table.util.factory.definition.ProducerDefinition;

@IgnoreExtraProperties
public class ProducerInstance extends BaseFactoryInstance {

    @Exclude
    public ProducerDefinition getDefinition() {
        return (ProducerDefinition) _definition;
    }

    public ProducerInstance() {
        super();
    }

    public ProducerInstance(String factoryType, Timestamp lastStart, Long count) {
        super(factoryType, lastStart, count);
    }

    public ProducerInstance(ProducerDefinition definition) {
        super(definition.getId(), Timestamp.now(), 1L);
        setDefinition(definition);
    }
}