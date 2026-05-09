# Create: Smart Logistics

A Create addon that adds smart logistics blocks.

## Features

### Horizontal Item Pusher
A directional item pusher that pushes items in the direction it faces.

**Key features:**
- **Redstone controlled**: Does not work when redstone signal is applied
- **Filter support**: Uses Create's filter system to control which items can pass through
- **Smart pulling**: Automatically pulls items from the back side (hoppers, inventories)
- **Push functionality**: Pushes held items in the facing direction
- **Spit out**: If there's no inventory in front, items are ejected as entities

**How it works:**
1. Filter items using Create filter items placed in the GUI
2. If the target inventory is full or doesn't exist, items are ejected as entities

## Required Mods

| Mod | Version |
|-----|---------|
| **NeoForge** | 21.1+ |
| **Create** | 6.0+ |
| **Minecraft** | 1.21.1 |

## Installation

1. Install NeoForge 21.1 for Minecraft 1.21.1
2. Install Create 6.0 or higher
3. Install this mod
4. Launch the game

## Usage

1. Place the Horizontal Item Pusher block
2. Right-click to open the GUI
3. Place a Create filter in the filter slot to control which items pass through
4. Place hoppers or other inventories on the back side to automatically pull items

## Testing and Bugs

The mod has been tested, but not 100%. Bugs and unexpected behavior are possible. If you find any issues, please report them to the author.

## License

MIT
