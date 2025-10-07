---
navigation:
  parent: aae_intro/aae_intro-index.md
  title: Bus de E/S avanzado
  icon: advanced_ae:advanced_io_bus_part
categories:
  - advanced items
item_ids:
  - advanced_ae:advanced_io_bus_part
---

# Bus de E/S avanzado

<GameScene zoom="8" background="transparent">
  <ImportStructure src="../structure/cable_advanced_io_bus.snbt"></ImportStructure>
</GameScene>

El <ItemLink id="advanced_ae:advanced_io_bus_part"/> es una herramienta muy poderosa para interactuar con inventarios externos.
Se crea al fusionar un <ItemLink id="advanced_ae:import_export_bus_part"/> y un <ItemLink id="advanced_ae:stock_export_bus_part"/>.
Heredará las funciones de ambos objetos. Además, la velocidad base del <ItemLink id="advanced_ae:advanced_io_bus_part"/> es 8 veces
más alta que la velocidad base de un <ItemLink id="ae2:export_bus"/>. Tomará un tiempo alcanzar la velocidad máxima, pero será
increíblemente rápido cuando esté completamente mejorado.

## Exportando

El <ItemLink id="advanced_ae:advanced_io_bus_part"/> exportará según su filtro, hasta una cantidad fija y se detendrá allí.
En el lado izquierdo de la interfaz, también hay una configuración que permite al usuario elegir regular el stock de objetos.

## Importando

El <ItemLink id="advanced_ae:advanced_io_bus_part"/> también importará cualquier cosa que no esté filtrada para ser exportada.
Las operaciones de importación y exportación se cuentan por separado, por lo que el bus no se quedará atascado haciendo una u otra.
Cuando el bus está configurado en modo regular, priorizará la importación de cualquier cosa que supere la cantidad establecida.
Si queda alguna operación, importará lo que no esté filtrado.

<RecipeFor id="advanced_ae:advanced_io_bus_part"/>