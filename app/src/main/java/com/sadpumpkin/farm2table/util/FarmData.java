package com.sadpumpkin.farm2table.util;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.Exclude;
import com.sadpumpkin.farm2table.util.factory.ConsumerInstance;
import com.sadpumpkin.farm2table.util.factory.ConverterInstance;
import com.sadpumpkin.farm2table.util.factory.ProducerInstance;
import com.sadpumpkin.farm2table.util.factory.definition.ConsumerDefinition;
import com.sadpumpkin.farm2table.util.factory.definition.ConverterDefinition;
import com.sadpumpkin.farm2table.util.factory.definition.ProducerDefinition;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FarmData {

    private static final long STARTING_GOLD = 500L;

    public static FarmData BuildDefault(Collection<ConsumerDefinition> allConsumerDefs) {

        long lowestCost = Long.MAX_VALUE;
        ConsumerDefinition defaultConsumerDef = null;
        for(ConsumerDefinition consumerDef : allConsumerDefs) {
            if (consumerDef.getCost() < lowestCost) {
                lowestCost = consumerDef.getCost();
                defaultConsumerDef = consumerDef;
            }
        }

        ArrayList<ConsumerInstance> defaultConsumerList = new ArrayList<>(1);
        defaultConsumerList.add(new ConsumerInstance(defaultConsumerDef));

        return new FarmData(
                STARTING_GOLD,
                new ArrayList<>(),
                new ArrayList<>(),
                defaultConsumerList,
                new HashMap<>()
        );
    }

    private Long coins = 1000L;
    private ArrayList<ProducerInstance> producers = new ArrayList<>();
    private ArrayList<ConverterInstance> converters = new ArrayList<>();
    private ArrayList<ConsumerInstance> consumers = new ArrayList<>();
    private HashMap<String, Long> inventory = new HashMap<>();

    @Exclude
    private MutableLiveData<HashMap<String, Long>> _inventoryLiveData = new MutableLiveData<>();
    @Exclude
    private MutableLiveData<Long> _coinsLiveData = new MutableLiveData<>();

    @Exclude
    public LiveData<Long> getCoinsLive() {
        if (_coinsLiveData == null) {
            _coinsLiveData = new MutableLiveData<>(coins);
        }
        return _coinsLiveData;
    }

    public long getCoins() {
        return coins;
    }

    public void setCoins(Long newCoins) {
        coins = newCoins;
        if (_coinsLiveData == null) {
            _coinsLiveData = new MutableLiveData<>(coins);
        }
        _coinsLiveData.postValue(coins);
    }

    public ArrayList<ProducerInstance> getProducers() {
        return producers;
    }

    public ArrayList<ConverterInstance> getConverters() {
        return converters;
    }

    public ArrayList<ConsumerInstance> getConsumers() {
        return consumers;
    }

    @Exclude
    public LiveData<HashMap<String, Long>> getInventoryLive() {
        if (_inventoryLiveData == null) {
            _inventoryLiveData = new MutableLiveData<>(inventory);
        }
        return _inventoryLiveData;
    }

    public HashMap<String, Long> getInventory() {
        return inventory;
    }

    public void setInventory(HashMap<String, Long> newInventory) {
        inventory = newInventory;
        if (_inventoryLiveData == null) {
            _inventoryLiveData = new MutableLiveData<>(inventory);
        }
        _inventoryLiveData.postValue(inventory);
    }

    public void addForage(List<String> seedIds, GameDataWrapper gameDataWrapper) {
        for (String seedId : seedIds) {
            ProducerInstance existingInstance = null;
            for (ProducerInstance producerInst : producers) {
                if (producerInst.getDefinition().getSeedId().equals(seedId)) {
                    existingInstance = producerInst;
                    existingInstance.setCount(existingInstance.getCount() + 1);
                    break;
                }
            }

            if (existingInstance == null) {
                for (ProducerDefinition producerDef : gameDataWrapper.getAllProducers()) {
                    if (producerDef.getSeedId().equals(seedId)) {
                        ProducerInstance newInstance = new ProducerInstance(
                                producerDef.getId(),
                                Timestamp.now(),
                                1L);
                        newInstance.setDefinition(producerDef);

                        producers.add(newInstance);
                        break;
                    }
                }
            }
        }
    }

    public void addResource(String resourceId, long count) {
        inventory.put(resourceId, inventory.getOrDefault(resourceId, 0L) + count);
        _inventoryLiveData.postValue(inventory);
    }

    public void addResources(List<String> resourcesIds) {
        for (String resourceId : resourcesIds) {
            inventory.put(resourceId, inventory.getOrDefault(resourceId, 0L) + 1L);
        }
        _inventoryLiveData.postValue(inventory);
    }

    public void addConverter(ConverterDefinition definition) {
        ConverterInstance existingInstance = null;
        for (ConverterInstance converterInst : converters) {
            if (converterInst.getFactoryType().equals(definition.getId())) {
                existingInstance = converterInst;
                existingInstance.setCount(existingInstance.getCount() + 1L);
            }
        }

        if (existingInstance == null) {
            ConverterInstance newInstance = new ConverterInstance(definition);
            newInstance.setDefinition(definition);

            converters.add(newInstance);
        }
    }

    public void addConsumer(ConsumerDefinition definition) {
        ConsumerInstance existingInstance = null;
        for (ConsumerInstance consumerInst : consumers) {
            if (consumerInst.getFactoryType().equals(definition.getId())) {
                existingInstance = consumerInst;
                existingInstance.setCount(existingInstance.getCount() + 1L);
            }
        }

        if (existingInstance == null) {
            ConsumerInstance newInstance = new ConsumerInstance(definition);
            newInstance.setDefinition(definition);

            consumers.add(newInstance);
        }
    }

    public FarmData() {
        _inventoryLiveData = new MutableLiveData<>(inventory);
        _coinsLiveData = new MutableLiveData<>(coins);
    }

    public FarmData(Long coins, List<ProducerInstance> producers, List<ConverterInstance> converters, List<ConsumerInstance> consumers, Map<String, Long> inventory) {
        this.coins = coins;
        this.producers = new ArrayList<>(producers);
        this.converters = new ArrayList<>(converters);
        this.consumers = new ArrayList<>(consumers);
        this.inventory = new HashMap<>(inventory);

        _inventoryLiveData = new MutableLiveData<>(this.inventory);
        _coinsLiveData = new MutableLiveData<>(coins);
    }

    public void tick(long deltaMills, GameDataWrapper gameDataWrapper) {
        Timestamp now = Timestamp.now();
        long nowMills = timeStampToMills(now);

        sortProducers(gameDataWrapper);
        for (ProducerInstance producer : producers) {
            updateFactoryInstance(producer, nowMills, gameDataWrapper);
        }

        sortConverters(gameDataWrapper);
        for (ConverterInstance converter : converters) {
            updateFactoryInstance(converter, nowMills, gameDataWrapper);
        }

        sortConsumers(gameDataWrapper);
        for (ConsumerInstance consumer : consumers) {
            updateFactoryInstance(consumer, nowMills, gameDataWrapper);
        }
    }

    private void sortProducers(GameDataWrapper gameDataWrapper) {
        // Prioritize producers based on their count.
        producers.sort((lhs, rhs) -> Long.compare(lhs.getCount(), rhs.getCount()));
    }

    private void updateFactoryInstance(ProducerInstance instance, long nowMills, GameDataWrapper gameDataWrapper) {
        long lastStartSec = instance.getLastStart().getSeconds();
        double progressSec = nowMills / 1000D - lastStartSec;
        double durationSec = instance.getDefinition().getDurationMills() / 1000D;

        long completions = (long) (progressSec / durationSec);
        if (completions > 0) {
            // Add produced resource to inventory
            LiveData<Long> getCount = instance.getCountLive();
            long producedCount = (getCount.getValue() == null ? 1L : getCount.getValue()) * completions;
            String producedId = instance.getDefinition().getProducedId();

            addResource(producedId, producedCount);

            // Reset start time
            long overflowSeconds = (int) (progressSec % durationSec);
            Timestamp newStartTime = new Timestamp(Timestamp.now().getSeconds() - overflowSeconds, 0);
            instance.setLastStart(newStartTime);
        }
        instance.updateMutableProgress(true);
    }

    private void sortConverters(GameDataWrapper gameDataWrapper) {
        // Prioritize converters based on how many resources are available to convert.
        converters.sort((lhs, rhs) -> {
            List<String> lhsConsumed = lhs.getDefinition().getConsumedIds();
            List<String> rhsConsumed = rhs.getDefinition().getConsumedIds();

            long lhsTotalConsumable = countOfResourcesInInventory(lhsConsumed);
            long rhsTotalConsumable = countOfResourcesInInventory(rhsConsumed);

            return Long.compare(rhsTotalConsumable, lhsTotalConsumable);
        });
    }

    private void updateFactoryInstance(ConverterInstance instance, long nowMills, GameDataWrapper gameDataWrapper) {

        List<String> consumedIds = instance.getDefinition().getConsumedIds();

        long lastStartSec = instance.getLastStart().getSeconds();
        double progressSec = nowMills / 1000D - lastStartSec;
        double durationSec = instance.getDefinition().getDurationMills() / 1000D;

        long completions = (long) (progressSec / durationSec);
        if (completions > 0) {
            // Convert 1:1 the lowest cost consumedId for the producedId
            long maxConvertedCount = instance.getCount() * completions;
            for (int i = 0; i < maxConvertedCount; i++) {
                String consumedId = findLowestCostOwnedResourceInList(consumedIds, gameDataWrapper, inventory);
                if (consumedId == null)
                    break;

                Long consumedCurrentCount = inventory.get(consumedId);
                if (consumedCurrentCount > 1) {
                    inventory.put(consumedId, consumedCurrentCount - 1L);
                } else {
                    inventory.remove(consumedId);
                }

                String producedId = instance.getDefinition().getProducedIdForConsumed(consumedId);

                addResource(producedId, 1L);
            }

            // Reset start time
            long overflowSeconds = (int) (progressSec % durationSec);
            Timestamp newStartTime = new Timestamp(Timestamp.now().getSeconds() - overflowSeconds, 0);
            instance.setLastStart(newStartTime);
        }

        boolean active = findLowestCostOwnedResourceInList(consumedIds, gameDataWrapper, inventory) != null;
        instance.updateMutableProgress(active);
    }

    private void sortConsumers(GameDataWrapper gameDataWrapper) {
        // Prioritize consumers based on their possible output value.
        consumers.sort((lhs, rhs) -> {
            float lhsMultiplier = lhs.getDefinition().getValueMultiplier();
            float rhsMultiplier = rhs.getDefinition().getValueMultiplier();

            return Float.compare(rhsMultiplier, lhsMultiplier);
        });
    }

    private void updateFactoryInstance(ConsumerInstance instance, long nowMills, GameDataWrapper gameDataWrapper) {
        long lastStartSec = instance.getLastStart().getSeconds();
        double progressSec = nowMills / 1000D - lastStartSec;
        double durationSec = instance.getDefinition().getDurationMills() / 1000D;

        List<String> consumedIds = instance.getDefinition().getConsumedIds();
        float valueMultiplier = instance.getDefinition().getValueMultiplier();

        long completions = (long) (progressSec / durationSec);
        if (completions > 0) {
            // Convert the highest cost consumedId to the multiplied Gold value.
            long maxConvertedCount = instance.getCount() * completions;
            for (int i = 0; i < maxConvertedCount; i++) {
                String consumedId = findHighestCostOwnedResourceInList(consumedIds, gameDataWrapper, inventory);
                if (consumedId == null)
                    break;

                Long consumedCurrentCount = inventory.get(consumedId);
                if (consumedCurrentCount > 1) {
                    inventory.put(consumedId, consumedCurrentCount - 1L);
                } else {
                    inventory.remove(consumedId);
                }
                _inventoryLiveData.postValue(inventory);

                ResourceDefinition consumedResource = gameDataWrapper.getResourceDefinition(consumedId);
                coins += Math.round(consumedResource.getBasePrice() * valueMultiplier);
                _coinsLiveData.postValue(coins);
            }

            // Reset start time
            long overflowSeconds = (long) (progressSec % durationSec);
            Timestamp newStartTime = new Timestamp(Timestamp.now().getSeconds() - overflowSeconds, 0);
            instance.setLastStart(newStartTime);
        }

        boolean active = findHighestCostOwnedResourceInList(consumedIds, gameDataWrapper, inventory) != null;
        instance.updateMutableProgress(active);
    }

    private Long countOfResourceInInventory(String resourceId) {
        return inventory.getOrDefault(resourceId, 0L);
    }

    private Long countOfResourcesInInventory(Collection<String> resourceIds) {
        Long total = 0L;
        for (String resourceId : resourceIds) {
            total += countOfResourceInInventory(resourceId);
        }
        return total;
    }

    public static long timeStampToMills(Timestamp timestamp) {
        return timestamp.getSeconds() * 1000;
    }

    public static String findLowestCostOwnedResourceInList(Collection<String> resourceIds, GameDataWrapper gameDataWrapper, Map<String, Long> inventory) {
        String currentLowestId = null;
        long currentLowestCost = Long.MAX_VALUE;
        for (String resourceId : resourceIds) {
            ResourceDefinition resDef = gameDataWrapper.getResourceDefinition(resourceId);
            if (resDef != null &&
                    (inventory == null || inventory.getOrDefault(resourceId, 0L) > 0L) &&
                    resDef.getBasePrice() < currentLowestCost) {
                currentLowestCost = resDef.getBasePrice();
                currentLowestId = resourceId;
            }
        }
        return currentLowestId;
    }

    public static String findHighestCostOwnedResourceInList(Collection<String> resourceIds, GameDataWrapper gameDataWrapper, Map<String, Long> inventory) {
        String currentHighestId = null;
        long currentHighestCost = Long.MIN_VALUE;
        for (String resourceId : resourceIds) {
            ResourceDefinition resDef = gameDataWrapper.getResourceDefinition(resourceId);
            if (resDef != null &&
                    (inventory == null || inventory.getOrDefault(resourceId, 0L) > 0L) &&
                    resDef.getBasePrice() > currentHighestCost) {
                currentHighestCost = resDef.getBasePrice();
                currentHighestId = resourceId;
            }
        }
        return currentHighestId;
    }
}
