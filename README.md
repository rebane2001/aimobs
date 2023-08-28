# AIMobs-Voice
AIMobs-Voice is a mod that lets you chat or talk (hold R key) with Minecraft mobs and other entities by creating prompts and using the OpenAI API. It's forked from [AIMobs](https://github.com/rebane2001/aimobs) for text only interactions.

### Requirements
- Minecraft 1.19.4
- Fabric
- Fabric API 0.86.1.19.4
- (Prism Launcher [here](https://prismlauncher.org))
- (Java 17 [here](https://adoptium.net/en-GB/temurin/releases/?version=17))

### Installation
1. [YouTube Tutorial] (https://www.youtube.com/watch?v=EKCaTp5a8ZU)
2. You won't find aimobs-voice via download mods in Prism Launcher yet. Instead please download the .jar here from the release page and add it manually to Prism Launcher -> Mods -> Add file.

### Usage
After installing the mod, grab your OpenAI API key from [here](https://beta.openai.com/account/api-keys), and set it with the `/aimobs setkey <key>` command. Also in order to let the mobs talk to you: First enable the Cloud Text-to-Speech API - [here](https://console.cloud.google.com/apis/library/texttospeech.googleapis.com) and then create your API key credential [here](https://console.cloud.google.com/apis/credentials). Once you have it, set it with the `/aimobs setvoicekey <voicekey>` command

You should now be able to **talk to mobs by shift+clicking** on them!

### Commands
- `/aimobs` - View configuration status
- `/aimobs help` - View commands help
- `/aimobs enable/disable` - Enable/disable the mod
- `/aimobs setkey <key>` - Set OpenAI API key
- `/aimobs setvoicekey <voicekey>` - Set Google Text-To-Speech API key
- `/aimobs setmodel <model>` - Set AI model
- `/aimobs settemp <temperature>` - Set model temperature

### Notes
This project was initially made in 1.12 as a client Forge mod, then ported to 1.19 PaperMC as a server plugin, then ported to Fabric 1.19. Because of this, the code can be a little messy and weird. A couple hardcoded limits are 512 as the max token length and 4096 as the max prompt length (longer prompts will get the beginning cut off), these could be made configurable in the future.

## 🛣️ Roadmap:
- [ ] Let mobs approach player
- [ ] Stream GPT output to TTS
- [ ] Replace random voices by mob specific ones
- [ ] Cut memory within model token limit to prevent overflow
- [ ] Support for the Forge modloader.
- [ ] Support for other AI APIs.


The icon used is the **🧠** emoji from [Twemoji](https://twemoji.twitter.com/) (CC BY 4.0)
