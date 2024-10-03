---
navigation:
  parent: aae_intro/aae_intro-index.md
  title: ME Throughput Monitor
  icon: advanced_ae:throughput_monitor
categories:
  - advanced items
item_ids:
  - advanced_ae:throughput_monitor
---

# ME Throughput Monitor

<GameScene zoom="8" background="transparent">
<ImportStructure src="../structure/throughput_monitors.snbt"></ImportStructure>
<IsometricCamera yaw="195" pitch="30" />
</GameScene>

Throughput Monitors are a subtype of monitor. They provide the same functionalities a <ItemLink id="ae2:storage_monitor" />
does, with the addition of a throughput meter. I will keep track of a single item/fluid type and monitor changed to its
quantity, displaying the amount per second to the user.

It does *not* require a channel.

## Keybinds

*   Right-click with an item or double-right-click with a fluid container to set the monitor to that item/fluid.
*   Right-click with an empty hand to clear the monitor.
*   Shift-right-click with an empty hand to lock the monitor.