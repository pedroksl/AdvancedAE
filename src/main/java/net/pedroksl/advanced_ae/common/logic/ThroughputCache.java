package net.pedroksl.advanced_ae.common.logic;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import net.pedroksl.advanced_ae.common.definitions.AAEConfig;

import appeng.hooks.ticking.TickHandler;

public class ThroughputCache {

    private final int MAX_SIZE = AAEConfig.instance().getThroughputMonitorCacheSize();
    private final LinkedList<CacheEntry> cache = new LinkedList<>();

    public int size() {
        return cache.size();
    }

    public void push(long amount, long timestamp) {
        if (timestamp == 0) return;
        if (!cache.isEmpty() && cache.getFirst().timestamp == timestamp) return;

        cache.addFirst(new CacheEntry(amount, timestamp));
        if (cache.size() > MAX_SIZE) {
            cache.removeLast();
        }
    }

    public void clear() {
        cache.clear();
    }

    public long averagePerTick(int timeLimit_s) {
        long now = TickHandler.instance().getCurrentTick();
        long tLimit = now - timeLimit_s * 20L;

        long lastAmount = -1;
        long lastTimestamp = -1;
        List<Double> averages = new ArrayList<>();
        for (var entry : cache) {
            if (entry.timestamp < tLimit) break;

            if (lastTimestamp != -1) {
                var timestampDelta = lastTimestamp - entry.timestamp;
                var amountDelta = lastAmount - entry.amount;
                averages.add(amountDelta / (double) timestampDelta);
            }

            lastAmount = entry.amount;
            lastTimestamp = entry.timestamp;
        }

        double average = 0;
        int size = averages.size();
        for (var avg : averages) {
            average += avg / (double) size;
        }

        return Math.round(average);
    }

    private record CacheEntry(long amount, long timestamp) {}
}
