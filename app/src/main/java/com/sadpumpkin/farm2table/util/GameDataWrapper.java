package com.sadpumpkin.farm2table.util;

import androidx.lifecycle.ViewModel;

import com.google.firebase.storage.StorageReference;
import com.sadpumpkin.farm2table.util.factory.definition.BaseFactoryDefinition;
import com.sadpumpkin.farm2table.util.factory.definition.ConsumerDefinition;
import com.sadpumpkin.farm2table.util.factory.definition.ConverterDefinition;
import com.sadpumpkin.farm2table.util.factory.definition.ProducerDefinition;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class GameDataWrapper extends ViewModel {

    private FirebaseWrapper _firebase;
    private Map<String, SeedDefinition> _knownSeeds;
    private Map<String, ResourceDefinition> _knownResources;
    private Map<String, ProducerDefinition> _knownProducers;
    private Map<String, ConverterDefinition> _knownConverters;
    private Map<String, ConsumerDefinition> _knownConsumers;

    public void setFirebaseWrapper(FirebaseWrapper firebaseWrapper) {
        _firebase = firebaseWrapper;
    }

    public void setSeedData(List<SeedDefinition> seedData) {
        _knownSeeds = seedData.stream().collect(Collectors.toMap(SeedDefinition::getId, seed -> seed));
    }

    public void setResourceData(List<ResourceDefinition> resourceData) {
        _knownResources = resourceData.stream().collect(Collectors.toMap(ResourceDefinition::getId, resource -> resource));
    }

    public void setProducerData(List<ProducerDefinition> producerData) {
        _knownProducers = producerData.stream().collect(Collectors.toMap(BaseFactoryDefinition::getId, producer -> producer));
    }

    public void setConverterData(List<ConverterDefinition> converterData) {
        _knownConverters = converterData.stream().collect(Collectors.toMap(BaseFactoryDefinition::getId, converter -> converter));
    }

    public void setConsumerData(List<ConsumerDefinition> consumerData) {
        _knownConsumers = consumerData.stream().collect(Collectors.toMap(BaseFactoryDefinition::getId, consumer -> consumer));
    }

    public Collection<SeedDefinition> getAllSeeds() {
        return _knownSeeds.values();
    }

    public SeedDefinition getSeedDefinition(String id) {
        return _knownSeeds.getOrDefault(id, null);
    }

    public Collection<ResourceDefinition> getAllResources() {
        return _knownResources.values();
    }

    public ResourceDefinition getResourceDefinition(String id) {
        return _knownResources.getOrDefault(id, null);
    }

    public Collection<ProducerDefinition> getAllProducers() {
        return _knownProducers.values();
    }

    public ProducerDefinition getProducerDefinition(String id) {
        return _knownProducers.getOrDefault(id, null);
    }

    public Collection<ConverterDefinition> getAllConverters() {
        return _knownConverters.values();
    }

    public ConverterDefinition getConverterDefinition(String id) {
        return _knownConverters.getOrDefault(id, null);
    }

    public Collection<ConsumerDefinition> getAllConsumers() {
        return _knownConsumers.values();
    }

    public ConsumerDefinition getConsumerDefinition(String id) {
        return _knownConsumers.getOrDefault(id, null);
    }

    public StorageReference getImageReference(String path) {
        return _firebase.storage().getReference(path);
    }
}
