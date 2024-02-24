# Chat-with-NPC
Chat-with-NPC allow players to interact and talk to specific entities under specific prompts (for use in RPG maps).

This mode is a fork of [AIMobs](https://github.com/rebane2001/aimobs) by [rebane2001](https://github.com/rebane2001) and [Eianex](https://github.com/Eianex).

AIMobs is a mod that lets you chat with Minecraft mobs and other entities by creating prompts and using the OpenAI API. This mod has the following changes:

- The mod is now a server-side mod, so it can be used in multiplayer servers.
- ...... (to be continued)

## Requirements
- Minecraft 1.19.*
- Fabric
- Fabric API

## 开发结构（临时章节）

### 软件包结构

1. api
   - 包含与OpenAI API交互的所有代码。
   - 类：`ApiManager`, `ApiRequest`, `ApiResponse`
2. data
   - 用于存储和管理数据，如NPC状态和环境信息。
   - 类/接口：`DataManager`, `StorageInterface`
   - 子包：`memory`（处理NPC的记忆）
3. npc
   - 与NPC相关的核心功能。
   - 类：`NpcEntity`, `NpcManager`
   - 接口：`NpcInteractionHandler`
4. event
   - 用于处理游戏事件和触发相应的行为。
   - 类：`EventHandler`, `EventProcessor`
5. environment
   - 管理全局和本地环境设置。
   - 类：`EnvironmentManager`, `GlobalEnvironment`, `LocalEnvironment`
6. interaction
   - 处理与玩家交互的用户界面。
   - 类：`Interaction`
7. auxiliary
   - 通用工具和助手类。
   - 子包: `configuration`, `command`

### 类和接口
- ApiManager
  - 负责与OpenAI API的交互。
- ApiRequest/ApiResponse
  - 封装API请求和响应。
- DataManager/StorageInterface
  - 管理数据存储，提供数据访问的接口。
- NpcEntity
  - 表示一个NPC实体，包含状态和行为。
- NpcManager
  - 管理NPC实体的创建和更新。
- NpcInteractionHandler
  - 定义与NPC交互的方法。
- EventHandler/EventProcessor
  - 处理游戏事件，如玩家与NPC的交互。
- EnvironmentManager
  - 管理全局和本地环境设置。
- GlobalEnvironment/LocalEnvironment
  - 存储和管理特定的环境信息。
- InteractionUI
  - 管理玩家与NPC交互的用户界面。
- Logger/ConfigHelper
  - 提供日志记录和配置管理的功能。

### 抽象类
- AbstractNpc
  - 为NPC提供一个共同的基类，定义了一些通用的属性和方法。

### 注意事项
- 模块化：确保每个类都有明确的职责，避免过大的类和过多的职责。
- 接口与实现分离：使用接口定义行为，然后提供具体的实现类。
- 扩展性：设计时考虑未来可能的扩展，使得添加新功能或修改现有功能更容易。
- 测试：为关键的功能编写单元测试，确保代码的可靠性。

---

**The mod is still being modified.** The information below is from the original README.md file of AIMobs.

---

## Usage
After installing the mod, grab your OpenAI API key from [here](https://beta.openai.com/account/api-keys), and set it with the `/aimobs setkey <key>` command.

You should now be able to **talk to mobs by shift+clicking** on them!

## Commands
- `/aimobs` - View configuration status
- `/aimobs help` - View commands help
- `/aimobs enable/disable` - Enable/disable the mod
- `/aimobs setkey <key>` - Set OpenAI API key
- `/aimobs setmodel <model>` - Set AI model
- `/aimobs settemp <temperature>` - Set model temperature

## Notes
This project was initially made in 1.12 as a client Forge mod, then ported to 1.19 PaperMC as a server plugin, then ported to Fabric 1.19. Because of this, the code can be a little messy and weird. A couple hardcoded limits are 512 as the max token length and 4096 as the max prompt length (longer prompts will get the beginning cut off), these could be made configurable in the future.

Some plans for the future:  
- Support for the Forge modloader.
- Support for other AI APIs.

An unofficial community-made fork is available with support for Ukranian and Español at [Eianex/aimobs](https://github.com/Eianex/aimobs/releases).

The icon used is the **🧠** emoji from [Twemoji](https://twemoji.twitter.com/) (CC BY 4.0)
