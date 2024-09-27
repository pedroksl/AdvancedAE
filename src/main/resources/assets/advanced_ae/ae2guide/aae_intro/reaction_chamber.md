---
navigation:
  parent: aae_intro/aae_intro-index.md
  title: Reaction Chamber
  icon: advanced_ae:reaction_chamber
categories:
  - advanced devices
item_ids:
  - advanced_ae:reaction_chamber
---

# Reaction Chamber

<BlockImage id="advanced_ae:reaction_chamber" scale="4"></BlockImage>

The reaction chamber is capable of accelerating chemical reactions by using a catalyst fluid and a great amount of
power. In doing so, reactions that naturally occur can be forced to happen inside the chamber, most of the time with
more efficient results, due to the controlled environment.

## Powering the Chamber

The reaction chamber can be quite power hungry when sped up. This accelerates the rate at which the total power cost is
consumed, increasing the required power per tick. To be able to power it properly, there are a few options.

### Full Block Pattern Provider

A full block pattern provider is capable of connecting the Reaction Chamber directly to the AE2 grid, allowing power to
be extracted from store energy inside it. If the total power stored in the grid is lower than the required power per
tick, no progress will be made and no power consumed. To remedy this, it is recommended that the grid is connected to a
few <ItemLink id="ae2:dense_energy_cell" />s.

### External Power

External power will work with the reaction chamber, but it still needs a connection to the grid. Therefore, to be able
to properly utilize external power, at least a directly connected cable is required. Alternatively, a cable part
Pattern Provider will also provide the grid connection (without powering it).

### Induction Cards (Extra Mod Required: Applied Flux)

Induction Cards can be inserted in cable part Pattern Providers or directional Pattern Providers, allowing them to
export power stored in Energy Cells. As long as it is setup properly, it should fill the energy storage of the Reaction
Chamber, allowing it to work. Do note, however, that Induction Cards, as well as some other AE2 components, has a
ramp-up timer, starting slow and increasing speed with time. This means that the crafting speed won't be maxed at first
due to lack of stored power, but as the crafting continues, the Induction Card should be able to provide enough power.