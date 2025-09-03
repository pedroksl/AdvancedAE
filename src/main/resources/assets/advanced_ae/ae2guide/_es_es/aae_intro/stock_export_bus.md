---
navigation:
  parent: aae_intro/aae_intro-index.md
  title: Bus de exportación de existencias
  icon: advanced_ae:stock_export_bus_part
categories:
  - advanced items
item_ids:
  - advanced_ae:stock_export_bus_part
---

# Bus de exportación de existencias

<GameScene zoom="8" background="transparent">
  <ImportStructure src="../structure/cable_stock_export_bus.snbt"></ImportStructure>
</GameScene>

El <ItemLink id="advanced_ae:stock_export_bus_part" /> se puede configurar para exportar una cantidad exacta de los stacks filtrados.
Lleva un registro de la cantidad actualmente presente en el inventario de destino y no inserta por encima de ese número. Para configurarlo,
abre la interfaz de usuario, arrastra el objeto deseado a la ranura de filtro y, usando un clic central, puedes configurar la cantidad.
Ten en cuenta que no regulará la salida, lo que significa que no extraerá objetos/fluidos adicionales del inventario si superan la cantidad configurada.
