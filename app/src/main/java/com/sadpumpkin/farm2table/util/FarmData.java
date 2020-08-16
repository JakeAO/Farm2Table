package com.sadpumpkin.farm2table.util;

import com.google.firebase.Timestamp;
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
                Map.of(),
                0f
        );
    }

    private Long coins = 1000L;
    private ArrayList<ProducerInstance> producers = new ArrayList<>();
    private ArrayList<ConverterInstance> converters = new ArrayList<>();
    private ArrayList<ConsumerInstance> consumers = new ArrayList<>();
    private HashMap<String, Long> inventory = new HashMap<>();
    private float prestige = 0f;

    public Long getCoins() {
        return coins;
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

    public HashMap<String, Long> getInventory() {
        return inventory;
    }

    public float getPrestige() {
        return prestige;
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
                instance.setCount(instance.getCount() + 1);
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

    public void addResouces(List<String> resourcesIds) {
        for (String resourceId : resourcesIds) {
            inventory.put(resourceId, inventory.getOrDefault(resourceId, 0L) + 1L);
        }
    }

    public FarmData(){

    }

    public FarmData(Long coins, List<ProducerInstance> producers, List<ConverterInstance> converters, List<ConsumerInstance> consumers, Map<String, Long> inventory, float prestige) {
        this.coins = coins;
        this.producers = new ArrayList<>(producers);
        this.converters = new ArrayList<>(converters);
        this.consumers = new ArrayList<>(consumers);
        this.inventory = new HashMap<>(inventory);
        this.prestige = prestige;
    }
}
