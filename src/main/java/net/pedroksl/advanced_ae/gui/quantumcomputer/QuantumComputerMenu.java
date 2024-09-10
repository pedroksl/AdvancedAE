package net.pedroksl.advanced_ae.gui.quantumcomputer;

import java.util.*;

import org.jetbrains.annotations.Nullable;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;
import net.pedroksl.advanced_ae.common.cluster.AdvCraftingCPU;
import net.pedroksl.advanced_ae.common.definitions.AAEMenus;
import net.pedroksl.advanced_ae.common.entities.AdvCraftingBlockEntity;

import appeng.api.config.CpuSelectionMode;
import appeng.api.networking.crafting.ICraftingCPU;
import appeng.api.stacks.GenericStack;
import appeng.menu.guisync.GuiSync;
import appeng.menu.guisync.PacketWritable;
import appeng.menu.me.crafting.CraftingCPUMenu;
import appeng.menu.me.crafting.CraftingStatusMenu;

public class QuantumComputerMenu extends CraftingCPUMenu {

    private static final String ACTION_SELECT_CPU = "selectCpu";

    private WeakHashMap<ICraftingCPU, Integer> cpuSerialMap = new WeakHashMap<>();

    private int nextCpuSerial = 1;

    private List<AdvCraftingCPU> lastCpuSet = List.of();

    private int lastUpdate = 0;

    @GuiSync(8)
    public CraftingStatusMenu.CraftingCpuList cpuList = EMPTY_CPU_LIST;

    // This is server-side
    @Nullable
    private ICraftingCPU selectedCpu = null;

    @GuiSync(9)
    private int selectedCpuSerial = -1;

    private final AdvCraftingBlockEntity host;

    public QuantumComputerMenu(int id, Inventory ip, AdvCraftingBlockEntity te) {
        this(AAEMenus.QUANTUM_COMPUTER, id, ip, te);
    }

    public QuantumComputerMenu(MenuType<?> menuType, int id, Inventory ip, AdvCraftingBlockEntity te) {
        super(menuType, id, ip, te);
        this.cpuList = EMPTY_CPU_LIST;
        this.selectedCpu = null;
        this.selectedCpuSerial = -1;
        this.host = te;
        this.registerClientAction("selectCpu", Integer.class, this::selectCpu);
    }

    private static final CraftingStatusMenu.CraftingCpuList EMPTY_CPU_LIST =
            new CraftingStatusMenu.CraftingCpuList(Collections.emptyList());

    private static final Comparator<CraftingStatusMenu.CraftingCpuListEntry> CPU_COMPARATOR = Comparator.comparing(
                    (CraftingStatusMenu.CraftingCpuListEntry e) -> e.name() == null)
            .thenComparing(e -> e.name() != null ? e.name().getString() : "")
            .thenComparingInt(CraftingStatusMenu.CraftingCpuListEntry::serial);

    @Override
    protected void setCPU(ICraftingCPU c) {
        super.setCPU(c);
        this.selectedCpuSerial = getOrAssignCpuSerial(c);
    }

    @Override
    public void broadcastChanges() {
        if (this.host.getGridNode() == null) {
            super.broadcastChanges();
            return;
        }

        if (isServerSide() && this.host.getCluster() != null) {
            List<AdvCraftingCPU> newCpuSet = this.host.getCluster().getActiveCPUs();
            newCpuSet.add(this.host.getCluster().getRemainingCapacityCPU());
            if (!lastCpuSet.equals(newCpuSet)
                    // Always try to update once every second to show job progress
                    || ++lastUpdate >= 20) {
                lastCpuSet = newCpuSet;
                cpuList = createCpuList();
            }
        } else {
            lastUpdate = 20;
            if (!lastCpuSet.isEmpty()) {
                cpuList = EMPTY_CPU_LIST;
                lastCpuSet = List.of();
            }
        }

        // Clear selection if CPU is no longer in list
        if (selectedCpuSerial != -1) {
            if (cpuList.cpus().stream().noneMatch(c -> c.serial() == selectedCpuSerial)) {
                selectCpu(-1);
            }
        }

        // Select a suitable CPU if none is selected
        if (selectedCpuSerial == -1) {
            // Try busy CPUs first
            for (var cpu : cpuList.cpus()) {
                if (cpu.currentJob() != null) {
                    selectCpu(cpu.serial());
                    break;
                }
            }
            // If we couldn't find a busy one, just select the first
            if (selectedCpuSerial == -1 && !cpuList.cpus().isEmpty()) {
                selectCpu(cpuList.cpus().get(0).serial());
            }
        }

        super.broadcastChanges();
    }

    private CraftingStatusMenu.CraftingCpuList createCpuList() {
        var entries = new ArrayList<CraftingStatusMenu.CraftingCpuListEntry>(lastCpuSet.size());
        for (var cpu : lastCpuSet) {
            var serial = getOrAssignCpuSerial(cpu);
            var status = cpu.getJobStatus();
            entries.add(new CraftingStatusMenu.CraftingCpuListEntry(
                    serial,
                    cpu.getAvailableStorage(),
                    cpu.getCoProcessors(),
                    cpu.getName(),
                    cpu.getSelectionMode(),
                    status != null ? status.crafting() : null,
                    status != null ? status.totalItems() : 0,
                    status != null ? status.progress() : 0,
                    status != null ? status.elapsedTimeNanos() : 0));
        }
        entries.sort(CPU_COMPARATOR);
        return new CraftingStatusMenu.CraftingCpuList(entries);
    }

    private int getOrAssignCpuSerial(ICraftingCPU cpu) {
        if (this.cpuSerialMap == null) {
            this.cpuSerialMap = new WeakHashMap<>();
        }
        return cpuSerialMap.computeIfAbsent(cpu, ignored -> nextCpuSerial++);
    }

    @Override
    public boolean allowConfiguration() {
        return false;
    }

    public void selectCpu(int serial) {
        if (isClientSide()) {
            selectedCpuSerial = serial;
            sendClientAction(ACTION_SELECT_CPU, serial);
        } else {
            ICraftingCPU newSelectedCpu = null;
            if (serial != -1) {
                for (var cpu : lastCpuSet) {
                    if (cpuSerialMap.getOrDefault(cpu, -1) == serial) {
                        newSelectedCpu = cpu;
                        break;
                    }
                }
            }

            if (newSelectedCpu != selectedCpu) {
                setCPU(newSelectedCpu);
            }
        }
    }

    public int getSelectedCpuSerial() {
        return selectedCpuSerial;
    }

    public record CraftingCpuList(List<CraftingStatusMenu.CraftingCpuListEntry> cpus) implements PacketWritable {
        public CraftingCpuList(RegistryFriendlyByteBuf data) {
            this(readFromPacket(data));
        }

        private static List<CraftingStatusMenu.CraftingCpuListEntry> readFromPacket(RegistryFriendlyByteBuf data) {
            var count = data.readInt();
            var result = new ArrayList<CraftingStatusMenu.CraftingCpuListEntry>(count);
            for (int i = 0; i < count; i++) {
                result.add(CraftingStatusMenu.CraftingCpuListEntry.readFromPacket(data));
            }
            return result;
        }

        @Override
        public void writeToPacket(RegistryFriendlyByteBuf data) {
            data.writeInt(cpus.size());
            for (var entry : cpus) {
                entry.writeToPacket(data);
            }
        }
    }

    public record CraftingCpuListEntry(
            int serial,
            long storage,
            int coProcessors,
            Component name,
            CpuSelectionMode mode,
            GenericStack currentJob,
            long totalItems,
            long progress,
            long elapsedTimeNanos) {
        public static CraftingStatusMenu.CraftingCpuListEntry readFromPacket(RegistryFriendlyByteBuf data) {
            return new CraftingStatusMenu.CraftingCpuListEntry(
                    data.readInt(),
                    data.readLong(),
                    data.readInt(),
                    data.readBoolean() ? ComponentSerialization.TRUSTED_STREAM_CODEC.decode(data) : null,
                    data.readEnum(CpuSelectionMode.class),
                    GenericStack.readBuffer(data),
                    data.readVarLong(),
                    data.readVarLong(),
                    data.readVarLong());
        }

        public void writeToPacket(RegistryFriendlyByteBuf data) {
            data.writeInt(serial);
            data.writeLong(storage);
            data.writeInt(coProcessors);
            data.writeBoolean(name != null);
            if (name != null) {
                ComponentSerialization.TRUSTED_STREAM_CODEC.encode(data, name);
            }
            data.writeEnum(mode);
            GenericStack.writeBuffer(currentJob, data);
            data.writeVarLong(totalItems);
            data.writeVarLong(progress);
            data.writeVarLong(elapsedTimeNanos);
        }
    }
}
