# Chat-with-NPC
Chat-with-NPC allow players to interact and talk to specific entities under specific prompts (for use in RPG maps).

This mode is a fork of [AIMobs](https://github.com/rebane2001/aimobs) by [rebane2001](https://github.com/rebane2001) and [Eianex](https://github.com/Eianex).

AIMobs is a mod that lets you chat with Minecraft mobs and other entities by creating prompts and using the OpenAI API. This mod has the following changes:

- The mod is now a server-side mod, so it can be used in multiplayer servers.
- ...... (to be continued)

### Requirements
- Minecraft 1.19.*
- Fabric
- Fabric API

**The mod is still being modified.** The information below is from the original README.md file of AIMobs.

---

### Usage
After installing the mod, grab your OpenAI API key from [here](https://beta.openai.com/account/api-keys), and set it with the `/aimobs setkey <key>` command.

You should now be able to **talk to mobs by shift+clicking** on them!

### Commands
- `/aimobs` - View configuration status
- `/aimobs help` - View commands help
- `/aimobs enable/disable` - Enable/disable the mod
- `/aimobs setkey <key>` - Set OpenAI API key
- `/aimobs setmodel <model>` - Set AI model
- `/aimobs settemp <temperature>` - Set model temperature

### Notes
This project was initially made in 1.12 as a client Forge mod, then ported to 1.19 PaperMC as a server plugin, then ported to Fabric 1.19. Because of this, the code can be a little messy and weird. A couple hardcoded limits are 512 as the max token length and 4096 as the max prompt length (longer prompts will get the beginning cut off), these could be made configurable in the future.

Some plans for the future:  
- Support for the Forge modloader.
- Support for other AI APIs.

An unofficial community-made fork is available with support for Ukranian and Español at [Eianex/aimobs](https://github.com/Eianex/aimobs/releases).

The icon used is the **🧠** emoji from [Twemoji](https://twemoji.twitter.com/) (CC BY 4.0)
