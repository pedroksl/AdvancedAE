---
navigation:
  parent: aae_intro/aae_intro-index.md
  title: Monitor de rendimiento ME
  icon: advanced_ae:throughput_monitor
categories:
  - advanced items
item_ids:
  - advanced_ae:throughput_monitor
  - advanced_ae:throughput_monitor_configurator
---

# Monitor de rendimiento ME

<GameScene zoom="8" background="transparent">
<ImportStructure src="../structure/throughput_monitors.snbt"></ImportStructure>
<IsometricCamera yaw="195" pitch="30" />
</GameScene>

Los <ItemLink id="advanced_ae:throughput_monitor" /> son un subtipo de monitor. Proporcionan las mismas funcionalidades que un <ItemLink id="ae2:storage_monitor" />,
con la adición de un medidor de rendimiento. Hará un seguimiento de un solo tipo de objeto/fluido y monitoreará los cambios en su
cantidad, mostrando la cantidad por segundo al usuario.

*No* requiere un canal.

## Combinaciones de teclas

*   Haz clic derecho con un objeto o haz doble clic derecho con un contenedor de fluidos para configurar el monitor en ese objeto/fluido.
*   Haz clic derecho con la mano vacía para borrar el monitor.
*   Haz shift + clic derecho con la mano vacía para bloquear el monitor.

## Configurador del monitor de rendimiento

<ItemImage id="advanced_ae:throughput_monitor_configurator" scale="4"></ItemImage>

El <ItemLink id="advanced_ae:throughput_monitor_configurator" /> es una herramienta que se puede usar para cambiar los datos presentados. Haciendo clic derecho en un monitor
con uno en la mano, se alternará entre tres opciones:

* Objetos por tic
* Objetos por segundo
* Objetos por minuto

Nota: ¡Puede tomar algún tiempo antes de que las lecturas se estabilicen al cambiar los modos, así que no confíes en los valores iniciales!