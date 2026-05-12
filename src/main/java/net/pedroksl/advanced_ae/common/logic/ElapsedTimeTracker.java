package net.pedroksl.advanced_ae.common.logic;

import it.unimi.dsi.fastutil.objects.Reference2LongMap;
import it.unimi.dsi.fastutil.objects.Reference2LongOpenHashMap;

import net.minecraft.util.Mth;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

import appeng.api.stacks.AEKeyType;
import appeng.api.stacks.AEKeyTypes;

public class ElapsedTimeTracker {
    private static final String NBT_ELAPSED_TIME = "elapsedTime";
    private static final String NBT_STARTED_WORK = "startedWork";
    private static final String NBT_COMPLETED_WORK = "completedWork";

    private long lastTime = System.nanoTime();
    private long elapsedTime = 0;

    private final Reference2LongMap<AEKeyType> startedWorkByType =
            new Reference2LongOpenHashMap<>(AEKeyTypes.getAll().size());
    private final Reference2LongMap<AEKeyType> completedWorkByType =
            new Reference2LongOpenHashMap<>(AEKeyTypes.getAll().size());

    public ElapsedTimeTracker() {}

    public ElapsedTimeTracker(ValueInput input) {
        this.elapsedTime = input.getLongOr(NBT_ELAPSED_TIME, 0);
        readLongByTypeMap(input.childOrEmpty(NBT_STARTED_WORK), startedWorkByType);
        readLongByTypeMap(input.childOrEmpty(NBT_COMPLETED_WORK), completedWorkByType);
    }

    public void writeToNBT(ValueOutput output) {
        output.putLong(NBT_ELAPSED_TIME, elapsedTime);
        writeLongByTypeMap(startedWorkByType, output.child(NBT_STARTED_WORK));
        writeLongByTypeMap(completedWorkByType, output.child(NBT_COMPLETED_WORK));
    }

    private static void readLongByTypeMap(ValueInput input, Reference2LongMap<AEKeyType> output) {
        for (var keyType : AEKeyTypes.getAll()) {
            output.put(keyType, input.getLongOr(keyType.getId().toString(), 0));
        }
    }

    private static void writeLongByTypeMap(Reference2LongMap<AEKeyType> input, ValueOutput output) {
        for (var entry : input.reference2LongEntrySet()) {
            output.putLong(entry.getKey().getId().toString(), entry.getLongValue());
        }
    }

    private void updateTime() {
        long currentTime = System.nanoTime();
        this.elapsedTime = this.elapsedTime + (currentTime - this.lastTime);
        this.lastTime = currentTime;
    }

    void decrementItems(long itemDiff, AEKeyType keyType) {
        updateTime();
        completedWorkByType.merge(keyType, itemDiff, this::saturatedSum);
    }

    private long saturatedSum(long a, long b) {
        var result = a + b;
        return result < 0 ? Long.MAX_VALUE : result;
    }

    void addMaxItems(long itemDiff, AEKeyType keyType) {
        updateTime();
        startedWorkByType.merge(keyType, itemDiff, this::saturatedSum);
    }

    public long getElapsedTime() {
        boolean allDone = true;
        for (var keyType : AEKeyTypes.getAll()) {
            if (completedWorkByType.getLong(keyType) < startedWorkByType.getLong(keyType)) {
                allDone = false;
                break;
            }
        }

        if (!allDone) {
            return this.elapsedTime + (System.nanoTime() - this.lastTime);
        } else {
            return this.elapsedTime;
        }
    }

    public float getProgress() {
        double startedUnits = 0;
        double completedUnits = 0;
        for (var keyType : AEKeyTypes.getAll()) {
            var startedForType = startedWorkByType.getLong(keyType);
            var completedForType = completedWorkByType.getLong(keyType);
            startedUnits += startedForType / (double) keyType.getAmountPerUnit();
            completedUnits += completedForType / (double) keyType.getAmountPerUnit();
        }

        return Mth.clamp((float) (completedUnits / startedUnits), 0, 1);
    }

    public long getRemainingItemCount() {
        return (int) (Integer.MAX_VALUE - (double) getProgress() * Integer.MAX_VALUE);
    }

    public long getStartItemCount() {
        return Integer.MAX_VALUE;
    }
}
