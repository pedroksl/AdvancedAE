---
navigation:
  parent: aae_intro/aae_intro-index.md
  title: Cámara de reacción
  icon: advanced_ae:reaction_chamber
categories:
  - advanced devices
item_ids:
  - advanced_ae:reaction_chamber
---

# Cámara de reacción

<BlockImage id="advanced_ae:reaction_chamber" scale="4"></BlockImage>

La <ItemLink id="advanced_ae:reaction_chamber" /> es capaz de acelerar reacciones químicas utilizando un fluido catalizador y una gran cantidad de
energía. Al hacerlo, las reacciones que ocurren de forma natural pueden ser forzadas a ocurrir dentro de la cámara, la mayoría de las veces con
resultados más eficientes, debido al entorno controlado.

## Alimentando la cámara

La cámara de reacción puede consumir mucha energía cuando se acelera. El uso de <ItemLink id="ae2:acceleration_card" />s aumenta la velocidad a la que
se consume el costo total de energía, aumentando la energía requerida por tic. Al usar energía directamente a través del sistema ME,
la cámara intentará extraer la cantidad requerida para un tic de procesamiento y si esto falla, es posible que experimente
parpadeo, cuando la energía del sistema se enciende y apaga. Para remediar este efecto, el sistema AE debe estar equipado
con búferes de energía en forma de <ItemLink id="ae2:dense_energy_cell" />s. La cámara también es capaz de extraer
energía directamente de las celdas de energía (de Applied Flux) si están disponibles en las unidades, para recargarse y fabricar
sin consumir de los búferes de energía ME. Reducir la cantidad de tarjetas de aceleración también es una opción si no hay
más generación de energía disponible en este momento. Para poder alimentarlo correctamente, hay algunas opciones.
* Nota: Los proveedores de patrones o las interfaces en forma de pieza (adjuntas a un cable) no proporcionan una conexión de red. Para poder
alimentar la cámara con esos, también necesitará conectar un cable fluix directamente a ella.

### Proveedor de patrones de bloque completo

Un proveedor de patrones de bloque completo es capaz de conectar la cámara de reacción directamente a la red AE2, lo que permite que la energía
se extraiga cuando sea necesario y se elimine la necesaria para el búfer interno de la cámara. Si la energía total almacenada en la red
es menor que la energía requerida por tic, el progreso se ralentizará y se mostrará una advertencia en la pantalla.
Para este método, se recomienda que la red esté conectada a algunas <ItemLink id="ae2:dense_energy_cell" />s.

### Energía externa

Una forma alternativa de alimentar la cámara de reacción es mediante el uso de energía externa. Cualquier fuente que pueda suministrar
energía debería ser suficiente para llenar sus búferes y comenzar a procesar. Si la energía proporcionada se recibe a una
tasa insuficiente, se mostrará una advertencia en la pantalla de la cámara.

### Tarjetas de inducción (mod adicional requerido: Applied Flux)

Las tarjetas de inducción se pueden insertar en proveedores de patrones de piezas de cable o en proveedores de patrones direccionales, lo que les permite
exportar energía almacenada en celdas de energía. Siempre que esté configurado correctamente, debería llenar el almacenamiento de energía de la cámara de
reacción, permitiéndole funcionar. Sin embargo, tenga en cuenta que las tarjetas de inducción, así como algunos otros componentes de AE2, tienen un
temporizador de aceleración, comenzando lento y aumentando la velocidad con el tiempo. Esto significa que la velocidad de fabricación no estará al máximo al principio
debido a la falta de energía almacenada, pero a medida que continúa la fabricación, la tarjeta de inducción debería poder proporcionar suficiente energía.
