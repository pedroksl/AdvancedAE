---
navigation:
  parent: aae_intro/aae_intro-index.md
  title: Computadora cuántica
  icon: advanced_ae:quantum_core
categories:
  - advanced devices
item_ids:
  - advanced_ae:quantum_unit
  - advanced_ae:quantum_core
  - advanced_ae:quantum_structure
  - advanced_ae:quantum_accelerator
  - advanced_ae:quantum_multi_threader
  - advanced_ae:quantum_storage_128
  - advanced_ae:quantum_storage_256
  - advanced_ae:data_entangler
---

# Computadora cuántica

La computadora cuántica es un tipo especial de computadora de fabricación. Es capaz de ejecutar una cantidad ilimitada de
solicitudes de fabricación, siempre que tenga suficiente almacenamiento de fabricación.

<GameScene zoom="2" background="transparent">
  <ImportStructure src="../structure/quantum_computer_multiblock.snbt"></ImportStructure>
</GameScene>

## Núcleo cuántico

<BlockImage id="advanced_ae:quantum_core" p:powered="true" p:formed="true" scale="4"></BlockImage>

El núcleo cuántico es el corazón de la computadora cuántica. Tiene 256M de almacenamiento de fabricación y 8 hilos de
coprocesador por sí mismo. Es el único bloque que puede crear una computadora cuántica formada por sí mismo y proporcionar
todos los beneficios de una computadora cuántica. Sin embargo, si se usa para crear un multibloque, se puede crear una computadora
mucho más poderosa. Cuando se usa como una computadora independiente, la energía debe proporcionarse a través de los lados de arriba o abajo, donde están los conectores.

## Almacenamientos cuánticos

<Row gap="20">
<BlockImage id="advanced_ae:quantum_storage_128" scale="4"></BlockImage>
<BlockImage id="advanced_ae:quantum_storage_256" scale="4"></BlockImage>
</Row>

Estos bloques amplían el almacenamiento de fabricación del núcleo cuántico. Aumentan efectivamente la cantidad de tareas concurrentes
que la computadora cuántica puede ejecutar. Hay dos variaciones, con una capacidad de 128M y 256M.

## Entrelazador de datos cuánticos

<BlockImage id="advanced_ae:data_entangler" scale="4"></BlockImage>

Los entrelazadores de datos son un bloque especial que afecta a todos los bloques de almacenamiento presentes en el multibloque.
Permiten que los bloques de almacenamiento almacenen datos en varias dimensiones, multiplicando efectivamente su almacenamiento por 4.
Solo se puede colocar uno de estos en cada multibloque de computadora cuántica.

## Acelerador cuántico

<BlockImage id="advanced_ae:quantum_accelerator" scale="4"></BlockImage>

Los aceleradores cuánticos agregan 8 coprocesadores al multibloque de la computadora cuántica. Es importante tener en cuenta que todos los
patrones de fabricación que ejecuta la computadora cuántica pueden compartir todos los coprocesadores, por lo que probablemente sea una
buena idea invertir en una gran cantidad de estos.

## Multihilo cuántico

<BlockImage id="advanced_ae:quantum_multi_threader" scale="4"></BlockImage>

De manera similar a los entrelazadores de datos, los multihilos permiten a los aceleradores ejecutar hilos adicionales en dimensiones separadas,
multiplicando su potencia de coprocesamiento por 4. Solo se puede colocar uno de estos en cada multibloque de computadora cuántica.

## Estructura cuántica

<Row gap="20">
<BlockImage id="advanced_ae:quantum_structure" scale="4"></BlockImage>
<BlockImage id="advanced_ae:quantum_structure" p:formed="true" scale="4"></BlockImage>
<BlockImage id="advanced_ae:quantum_structure" p:formed="true" p:powered="true" scale="4"></BlockImage>
</Row>

Estos bloques proporcionan el marco de la computadora cuántica. Se utilizan como un bloque de construcción para la computadora
cuántica y conectan todo junto.

## El multibloque

Para crear una computadora cuántica multibloque, se deben seguir algunas reglas:
- El tamaño máximo es 7x7x7 (dimensiones externas);
- No pueden estar presentes espacios vacíos dentro del multibloque. Pueden llenarse con <ItemLink id="advanced_ae:quantum_unit" /> 
sin beneficios adicionales;
- Exactamente un <ItemLink id="advanced_ae:quantum_core" />;
- Como máximo un <ItemLink id="advanced_ae:data_entangler" />;
- Como máximo un <ItemLink id="advanced_ae:quantum_multi_threader"
- Todas las bloques en la capa exterior deben ser de <ItemLink id="advanced_ae:quantum_structure" />;
- Ningún bloque en el interior puede ser de <ItemLink id="advanced_ae:quantum_structure" />.

## Configs del servidor

Varios valores pueden ajustarse mediante la config del servidor. Tales como:
- Tamaño máximo del multibloque;
- Los coprocesadores en cada acelerador cuántico;
- Cantidad máxima de multihilos cuánticos;
- Valor de multiplicación de hilos de multihilo;
- Cantidad máxima de entrelazadores de datos;
- Valor de multiplicación de almacenamiento del entrelazador de datos;

Los límites para su instancia se pueden verificar utilizando las descripciones del objeto.