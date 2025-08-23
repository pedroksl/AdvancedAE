- Reworked throughput monitor logic to include history caching for added precision
- Fixed serialization issue with quantum computers
- Major fixes and changes for the Quantum Crafter:
  - It no longer accepts patterns that use items that lose durability in the crafting process.
    - This was done to avoid exploits like infinite MA essence crafting with basic infusion crystals.
    - There will be an indication on the crafter's screen when a pattern is not supported.
  - For patterns that change NBT/Component data to create the outputs, substitutions are now disabled.
    - This means exact inputs will be required for the craft.
    - This prevents using the quantum crafter to duplicate items by generating otherwise invalid outputs.
  - Fixed replacing a pattern directly not correctly updating the related crafting job
- Updated Directional Output Screen to be more cross mod compatible

Contrinbutions:
- Added support for language localization for the Quantum Armor Config Screen @StarskyXIII
- Fixed missing pixel in Reaction Chamber interface @GuavaDealer
- Fixed copy/paste typo in mod configs @nyf-d