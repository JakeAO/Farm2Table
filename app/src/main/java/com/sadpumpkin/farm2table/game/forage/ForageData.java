package com.sadpumpkin.farm2table.game.forage;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.Exclude;
import com.sadpumpkin.farm2table.util.SeedDefinition;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class ForageData {

    private static final int MINIMUM_TYPE_COUNT = 1;
    private static final int MAXIMUM_TYPE_COUNT = 5;

    private static final int MINIMUM_REGROWTH_SECONDS = 60 * 60 * 6; // 6 hr
    private static final int MAXIMUM_REGROWTH_SECONDS = 60 * 30; // 30 min

    private static final int MINIMUM_HARVEST_COUNT = 1;
    private static final int MAXIMUM_HARVEST_COUNT = 5;

    public static ForageData BuildDefault(Collection<SeedDefinition> allSeeds) {
        Random random = new Random();
        double fertility = 0.1d + random.nextDouble() % 0.9d;
        double regrowth = 0.1d + random.nextDouble() % 0.9d;

        int idealResourceCount = MINIMUM_TYPE_COUNT + random.nextInt(MAXIMUM_TYPE_COUNT - MINIMUM_TYPE_COUNT);

        ArrayList<SeedDefinition> seeds = new ArrayList<>(allSeeds);
        Map<String, Double> resourceIds = new HashMap<>(idealResourceCount);
        for (int i = 0; i < idealResourceCount; i++) {
            int index = random.nextInt(seeds.size());
            SeedDefinition resource = seeds.get(index);

            String id = resource.getId();
            double rarity = resource.getRarity();

            double baseRarity = resourceIds.getOrDefault(id, 0d);
            resourceIds.put(id, baseRarity + rarity);
        }

        return new ForageData(
                resourceIds,
                fertility,
                regrowth,
                null);
    }

    private HashMap<String, Double> resourceIds = null;
    private double fertility = 0.5d;
    private double regrowth = 0.5d;
    private Timestamp lastForage = null;

    public HashMap<String, Double> getResourceIds() {
        return resourceIds;
    }

    public void setResourceIds(Map<String, Double> resourceIds) {
        this.resourceIds = new HashMap<>(resourceIds);
    }

    public double getFertility() {
        return fertility;
    }

    public void setFertility(double fertility){
        this.fertility = fertility;
    }

    public double getRegrowth() {
        return regrowth;
    }

    public void setRegrowth(double regrowth) {
        this.regrowth = regrowth;
    }

    public Timestamp getLastForage() {
        return lastForage;
    }

    public void setLastForage(Timestamp timestamp) {
        this.lastForage = timestamp;
    }

    public void setLastForage() {
        lastForage = Timestamp.now();
    }

    @Exclude
    private long getLastForageSeconds() {
        return lastForage != null ? lastForage.getSeconds() : 0L;
    }

    @Exclude
    public double getRegrowthPercent() {
        Timestamp now = Timestamp.now();
        long secondsSinceLastForage = now.getSeconds() - getLastForageSeconds();
        long secondsRequired = getTimeForRegrowth();

        if (secondsSinceLastForage >= secondsRequired) {
            return 1d;
        } else {
            return secondsSinceLastForage / (double) secondsRequired;
        }
    }

    @Exclude
    public long getRegrownInSeconds() {
        Timestamp now = Timestamp.now();
        long secondsSinceLastForage = now.getSeconds() - getLastForageSeconds();
        long secondsRequired = getTimeForRegrowth();

        if (secondsSinceLastForage >= secondsRequired) {
            return 0L;
        } else {
            return secondsRequired - secondsSinceLastForage;
        }
    }

    @Exclude
    public long getTimeForRegrowth() {
        return Math.round(MINIMUM_REGROWTH_SECONDS - regrowth * (MINIMUM_REGROWTH_SECONDS - MAXIMUM_REGROWTH_SECONDS));
    }

    @Exclude
    public long getHarvestCount() {
        return Math.round(MINIMUM_HARVEST_COUNT + fertility * (MAXIMUM_HARVEST_COUNT - MINIMUM_HARVEST_COUNT));
    }

    public List<String> computeForageResults() {
        Random random = new Random();

        double regrowthPercent = getRegrowthPercent();
        long maxHarvest = getHarvestCount();
        long realHarvest = Math.round(Math.ceil(regrowthPercent * maxHarvest));

        double totalRarity = 0d;
        for (double d : resourceIds.values()) {
            totalRarity += d;
        }

        List<String> results = new ArrayList<>();
        for (int i = 0; i < realHarvest; i++) {
            double randVal = Math.abs(random.nextDouble() * 1000) % totalRarity;
            for (Map.Entry<String, Double> entry : resourceIds.entrySet()) {
                randVal -= entry.getValue();
                if (randVal <= 0d) {
                    results.add(entry.getKey());
                    break;
                }
            }
        }

        return results;
    }

    public ForageData() {

    }

    public ForageData(Map<String, Double> resourceIds, double fertility, double regrowth, Timestamp lastForage) {
        this.resourceIds = new HashMap<>(resourceIds);
        this.fertility = fertility;
        this.regrowth = regrowth;
        this.lastForage = lastForage;
    }
}
