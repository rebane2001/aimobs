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

## Usage
After installing the mod, grab your OpenAI API key from [here](https://beta.openai.com/account/api-keys), and set it with the `/chat-with-npc setkey <key>` command.

You should now be able to **talk to mobs by shift+clicking** on them!

## Commands
- `/chat-with-npc` - View configuration status
- `/chat-with-npc help` - View commands help
- `/chat-with-npc enable/disable` - Enable/disable the mod
- `/chat-with-npc setkey <key>` - Set OpenAI API key
- `/chat-with-npc setmodel <model>` - Set AI model
- `/chat-with-npc settemp <temperature>` - Set model temperature

## Notes
This project was initially made in 1.12 as a client Forge mod, then ported to 1.19 PaperMC as a server plugin, then ported to Fabric 1.19. Because of this, the code can be a little messy and weird. A couple hardcoded limits are 512 as the max token length and 4096 as the max prompt length (longer prompts will get the beginning cut off), these could be made configurable in the future.

Some plans for the future:  
- Support for the Forge modloader.
- Support for other AI APIs.

An unofficial community-made fork is available with support for Ukranian and Español at [Eianex/aimobs](https://github.com/Eianex/aimobs/releases).

The icon used is the **🧠** emoji from [Twemoji](https://twemoji.twitter.com/) (CC BY 4.0)

## 开发结构（临时章节）

### 软件包结构

1. api
    - 包含与OpenAI API交互的所有代码。
    - 类：`RequestHandler`
2. data
    - 用于存储和管理数据，如NPC状态和环境信息。
    - 类：`NPCDataManager`, `EnvironmentDataManager`
    - 接口：`DataManager`
3. npc
    - 与NPC相关的核心功能。
    - 类：`NPCEntityManager`, `VillagerNPCEntity`, `LivingNPCEntity`
    - 抽象类：`NPCEntity`
    - 枚举类：`Actions`
    - 接口：`NPCHandler`
4. event
    - 用于管理通话事件和处理程序。
    - 类：`ConversationManager`, `ConversationHandler`
5. environment
    - 管理全局和本地环境设置。
    - 类：`EnvironmentManager`, `GlobalEnvironment`, `LocalEnvironment`
    - 接口：`Environment`
6. auxiliary
    - 通用工具和助手类。
    - 子包: `configuration`, `command`， `prompt`， `yaml`

### 类和接口
- RequestHandler
    - 负责与OpenAI API的交互，封装API请求和响应。
- DataManager
    - 管理数据存储，提供数据访问的接口。
- NpcManager
    - 管理NPC实体的创建和更新。
- NpcEntity
    - 表示一个NPC实体，包含状态和行为。
- ConversationManager
    - 管理与NPC交谈事件的创建和更新。
- ConversationHandler
    - 用于处理与NPC交谈的单一事件。
- EnvironmentManager
    - 管理全局和本地环境设置的创建和更新。
- GlobalEnvironment/LocalEnvironment
    - 存储和管理特定的环境信息。

### 抽象类
- NPCEntity
    - 为NPC提供一个共同的基类，定义了一些通用的属性和方法。

### 注意事项
- 模块化：确保每个类都有明确的职责，避免过大的类和过多的职责。
- 接口与实现分离：使用接口定义行为，然后提供具体的实现类。
- 扩展性：设计时考虑未来可能的扩展，使得添加新功能或修改现有功能更容易。
- 测试：为关键的功能编写单元测试，确保代码的可靠性。
