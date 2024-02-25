package com.jackdaw.chatwithnpc.data;

import com.jackdaw.chatwithnpc.ChatWithNPCMod;
import com.jackdaw.chatwithnpc.auxiliary.yaml.YamlUtils;
import com.jackdaw.chatwithnpc.npc.NPCEntity;
import org.slf4j.Logger;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;

/**
 * A serializer used to read or write the data from the files.
 * <p>
 * This data is related to the NPC's chat content.
 *
 * <p>Read or Write the data file with some information, each file just record one relative information.</p>
 */
public class NPCDataManager implements DataManager {

    private static final Logger logger = ChatWithNPCMod.LOGGER;
    private final File theFile;

    private final NPCEntity npc;

    public NPCDataManager(NPCEntity npc) {
        this.npc = npc;
        this.theFile = new File(ChatWithNPCMod.workingDirectory.toFile(), "npc/" + npc.getName() + ".yml");
    }

    @Override
    public boolean isExist() {
        return theFile.exists();
    }

    @Override
    public void sync() {
        if (!isExist()) {
            logger.error("[chat-with-npc] The data file doesn't exist.");
            return;
        }
        try {
            HashMap data = YamlUtils.readFile(theFile);
            // 读取储存在文件中的数据，然后将其赋值给npc
            npc.setCareer((String) data.get("careers"));
            npc.setLocalGroup((String) data.get("localGroup"));
            npc.setBasicPrompt((String) data.get("basicPrompt"));
            npc.updateLastMessageTime((long) data.get("lastMessageTime"));
            // 在data中读取存在history中的数据，其中key为时间，value为消息
            HashMap messageRecord = (HashMap) data.get("history");
            for (Object key : messageRecord.keySet()) {
                npc.addMessageRecord((long) key, (String) messageRecord.get(key));
            }
        } catch (FileNotFoundException e) {
            logger.error("[chat-with-npc] Can't open the data file.");
        }
    }

    @Override
    public void save() {
        try {
            if (!isExist()) {
                if (!theFile.createNewFile()) {
                    logger.error("[chat-with-npc] Can't create the data file.");
                    return;
                }
            }
            HashMap<String, Object> data = new HashMap<>();
            // 将npc的数据写入data中
            data.put("name", npc.getName());
            data.put("uuid", npc.getUuid().toString());
            data.put("careers", npc.getCareer());
            data.put("localGroup", npc.getLocalGroup());
            data.put("basicPrompt", npc.getBasicPrompt());
            data.put("lastMessageTime", npc.getLastMessageTime());
            // 将npc的消息记录写入data中
            HashMap<Long, String> messageRecord = new HashMap<>();
            for (long key : npc.getMessageRecord().keySet()) {
                messageRecord.put(key, npc.getMessageRecord().get(key));
            }
            data.put("history", messageRecord);
            YamlUtils.writeFile(theFile, data);
        } catch (IOException e) {
            logger.error("[chat-with-npc] Can't write the data file.");
        }
    }

    @Override
    public void delete() {
        if (!isExist()) {
            logger.warn("[chat-with-npc] The data file doesn't exist.");
            return;
        }
        if (!theFile.delete()) {
            logger.error("[chat-with-npc] Can't delete the data file.");
        }
    }
}
