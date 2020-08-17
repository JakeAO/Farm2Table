package com.sadpumpkin.farm2table.util;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.Exclude;
import com.sadpumpkin.farm2table.util.factory.ConsumerInstance;
import com.sadpumpkin.farm2table.util.factory.ConverterInstance;
import com.sadpumpkin.farm2table.util.factory.ProducerInstance;
import com.sadpumpkin.farm2table.util.factory.definition.ProducerDefinition;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;

public class FarmData {

    private static final long STARTING_GOLD = 500L;

    public static FarmData BuildDefault(Collection<SeedDefinition> allSeeds,
                                        Collection<ProducerDefinition> allProducers) {
        Random random = new Random();

        ArrayList<SeedDefinition> seeds = new ArrayList<>(allSeeds);
        List<ProducerInstance> startingProducers = new ArrayList<>(3);
        for (int i = 0; i < 3; i++) {
            while (true) {
                int index = random.nextInt(seeds.size());
                SeedDefinition seedDef = seeds.get(index);
                boolean alreadyExists = startingProducers
                        .stream()
                        .anyMatch(x -> x.getDefinition() instanceof ProducerDefinition &&
                                ((ProducerDefinition) x.getDefinition()).getSeedId() == seedDef.getId());
                if (alreadyExists)
                    continue;

                ProducerDefinition producerDef = allProducers
                        .stream()
                        .filter(x -> x.getSeedId().equals(seedDef.getId()))
                        .findFirst()
                        .orElse(null);
                if (producerDef == null)
                    continue;

                startingProducers.add(new ProducerInstance(producerDef));
                break;
            }
        }

        return new FarmData(
                STARTING_GOLD,
                startingProducers,
                List.of(),
                List.of(),
                Map.of()
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

    public long getCoins(){
        return coins;
    }

    public void setCoins(Long newCoins){
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
        if(_inventoryLiveData == null){
            _inventoryLiveData = new MutableLiveData<>(inventory);
        }
        return _inventoryLiveData;
    }

    public HashMap<String, Long> getInventory(){
        return inventory;
    }

    public void setInventory(HashMap<String, Long> newInventory){
        inventory = newInventory;
        if(_inventoryLiveData == null){
            _inventoryLiveData = new MutableLiveData<>(inventory);
        }
        _inventoryLiveData.postValue(inventory);
    }

    public void addForage(List<String> seedIds, GameDataWrapper gameDataWrapper) {
        for (String seedId : seedIds) {
            Optional<ProducerInstance> findInstance = producers
                    .stream()
                    .filter(x -> x.getDefinition() instanceof ProducerDefinition &&
                            ((ProducerDefinition) x.getDefinition()).getSeedId().equals(seedId))
                    .findFirst();
            if (findInstance.isPresent()) {
                ProducerInstance instance = findInstance.get();
                instance.setCount(instance.getCountLive().getValue() + 1);
            } else {
                Optional<ProducerDefinition> findDefinition = gameDataWrapper
                        .getAllProducers()
                        .stream()
                        .filter(x -> x.getSeedId().equals(seedId))
                        .findFirst();
                if (findDefinition.isPresent()) {
                    ProducerDefinition definition = findDefinition.get();
                    ProducerInstance newInstance = new ProducerInstance(
                            definition.getId(),
                            Timestamp.now(),
                            1L);
                    newInstance.setDefinition(definition);

                    producers.add(newInstance);
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
        // Producers don't need any specific sorting since they execute first
    }

    private void updateFactoryInstance(ProducerInstance instance, long nowMills, GameDataWrapper gameDataWrapper) {
        long lastStartMills = timeStampToMills(instance.getLastStart());
        long progressMills = nowMills - lastStartMills;
        long durationMills = instance.getDefinition().getDurationMills();

        long completions = progressMills / durationMills;
        if (completions > 0) {
            // Add produced resource to inventory
            LiveData<Long> getCount = instance.getCountLive();
            long producedCount = (getCount.getValue() == null ? 1L : getCount.getValue()) * completions;
            String producedId = instance.getDefinition().getProducedId();

            addResource(producedId, producedCount);

            // Reset start time
            long overflowMills = progressMills % durationMills;
            long backtrackMills = nowMills - overflowMills;
            //long secondsComponent = backtrackMills / 1000;
            //int nanoComponent = (int)(backtrackMills % 1000 * 10000);
            Timestamp newStartTime = Timestamp.now();// new Timestamp(secondsComponent, nanoComponent);
            instance.setLastStart(newStartTime);
        }
        instance.updateMutableProgress();
    }

    private void sortConverters(GameDataWrapper gameDataWrapper) {
        // Prioritize converters based on how many resources are available to convert.
        converters.sort((lhs, rhs) -> {
            String[] lhsConsumed = lhs.getDefinition().getConsumedIds();
            String[] rhsConsumed = rhs.getDefinition().getConsumedIds();

            long lhsTotalConsumable = countOfResourcesInInventory(lhsConsumed);
            long rhsTotalConsumable = countOfResourcesInInventory(rhsConsumed);

            return Long.compare(rhsTotalConsumable, lhsTotalConsumable);
        });
    }

    private void updateFactoryInstance(ConverterInstance instance, long nowMills, GameDataWrapper gameDataWrapper) {
        long lastStartMills = timeStampToMills(instance.getLastStart());
        long progressMills = nowMills - lastStartMills;
        long durationMills = instance.getDefinition().getDurationMills();

        long completions = progressMills / durationMills;
        if (completions > 0) {
            String[] consumedIds = instance.getDefinition().getConsumedIds();
            String producedId = instance.getDefinition().getProducedId();

            // Convert 1:1 the lowest cost consumedId for the producedId
            LiveData<Long> getCount = instance.getCountLive();
            long maxConvertedCount = (getCount.getValue() == null ? 1L : getCount.getValue()) * completions;
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

                addResource(producedId, 1L);
            }

            // Reset start time
            long overflowMills = progressMills % durationMills;
            long backtrackMills = nowMills - overflowMills;
            //long secondsComponent = backtrackMills / 1000;
            //int nanoComponent = (int)(backtrackMills % 1000 * 10000);
            Timestamp newStartTime = Timestamp.now();// new Timestamp(secondsComponent, nanoComponent);
            instance.setLastStart(newStartTime);
        }
        instance.updateMutableProgress();
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
        long lastStartMills = timeStampToMills(instance.getLastStart());
        long progressMills = nowMills - lastStartMills;
        long durationMills = instance.getDefinition().getDurationMills();

        long completions = progressMills / durationMills;
        if (completions > 0) {
            String[] consumedIds = instance.getDefinition().getConsumedIds();
            float valueMultiplier = instance.getDefinition().getValueMultiplier();

            // Convert the highest cost consumedId to the multiplied Gold value.
            LiveData<Long> getCount = instance.getCountLive();
            long maxConvertedCount = (getCount.getValue() == null ? 1L : getCount.getValue()) * completions;
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
            long overflowMills = progressMills % durationMills;
            long backtrackMills = nowMills - overflowMills;
            //long secondsComponent = backtrackMills / 1000;
            //int nanoComponent = (int)(backtrackMills % 1000 * 10000);
            Timestamp newStartTime = Timestamp.now();// new Timestamp(secondsComponent, nanoComponent);
            instance.setLastStart(newStartTime);
        }
        instance.updateMutableProgress();
    }

    private Long countOfResourceInInventory(String resourceId) {
        return inventory.getOrDefault(resourceId, 0L);
    }

    private Long countOfResourcesInInventory(String[] resourceIds) {
        Long total = 0L;
        for (String resourceId : resourceIds) {
            total += countOfResourceInInventory(resourceId);
        }
        return total;
    }

    public static long timeStampToMills(Timestamp timestamp) {
        return timestamp.getSeconds() * 1000 + Math.round(timestamp.getNanoseconds() / 0.000001d);
    }

    public static String findLowestCostOwnedResourceInList(String[] resourceIds, GameDataWrapper gameDataWrapper, Map<String, Long> inventory) {
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

    public static String findHighestCostOwnedResourceInList(String[] resourceIds, GameDataWrapper gameDataWrapper, Map<String, Long> inventory) {
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