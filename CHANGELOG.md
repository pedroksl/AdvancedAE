* New Features:
    - You can now color your Quantum Armor!
        - This feature can be accessed from the style config button located in the top right corner of the quantum armor config screen.
        - You can select a color from the color picker or use a hex code
        - You can apply colors individually or different colors for each armor piece
        - The inventory slots for the armor pieces were moved to the other side of the screen to prevent tooltips from covering the screen's content
    - Added support for language localization for the Quantum Armor Config Screen @StarskyXIII
    - Reworked throughput monitor logic to include history caching for added precision

* Major fixes and changes for the Quantum Crafter:
    - It no longer accepts patterns that use items that lose durability in the crafting process.
        - This was done to avoid exploits like infinite MA essence crafting with basic infusion crystals.
        - There will be an indication on the crafter's screen when a pattern is not supported.
    - For patterns that change NBT/Component data to create the outputs, substitutions are now disabled.
        - This means exact inputs will be required for the craft.
        - This prevents using the quantum crafter to duplicate items by generating otherwise invalid outputs.
    - Fixed replacing a pattern directly not correctly updating the related crafting job

* Bug Fixes:
    - Fixed some quantum armor screen widgets disappearing when resizing the window
    - Fixed quantum armor screen sliders not working correctly when being dragged
    - Fixed serialization issue with quantum computers
    - Updated Directional Output Screen to be more cross mod compatible

* Crash Fixes:
    - Fixed a bug where the mod would crash a server instance on load