package com.jackdaw.chatwithnpc.data;

import com.jackdaw.chatwithnpc.auxiliary.yaml.YamlMethods;
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
public class DataManager implements YamlMethods, StorageInterface {

    private final Logger logger;
    private final File theFile;

    private final NPCEntity npc;

    public DataManager(File workingDirectory, Logger logger, NPCEntity npc) {
        this.logger = logger;
        this.npc = npc;
        this.theFile = new File(workingDirectory, "npc" + npc.getName() + ".yml");
    }

    @Override
    public void sync() {
        if (!theFile.exists()) {
            return;
        }
        try {
            HashMap data = YamlUtils.readFile(theFile);
            // 读取储存在文件中的数据，然后将其赋值给npc
            npc.setCareer((String) data.get("careers"));
            npc.setLocalGroup((String) data.get("localGroup"));
            npc.setBasicPrompt((String) data.get("basicPrompt"));
            npc.setLastMessageTime((long) data.get("lastMessageTime"));
            // 在data中读取存在history中的数据，其中key为时间，value为消息
            HashMap messageRecord = (HashMap) data.get("history");
            for (Object key : messageRecord.keySet()) {
                npc.addMessageRecord((long) key, (String) messageRecord.get(key));
            }
        } catch (FileNotFoundException e) {
            logger.error("Can't open the data file.");
        }
    }

    @Override
    public void write() {
        try {
            if (!theFile.exists()) {
                if (!theFile.createNewFile()) {
                    return;
                }
            }
            HashMap data = new HashMap();
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
            YamlUtils.writeFile(theFile, data);
        } catch (IOException e) {
            this.logger.error("Can't write the data file.");
        }
    }

    @Override
    public void delete() {
        if (!theFile.exists()) {
            return;
        }
        if (!theFile.delete()) {
            this.logger.error("Can't delete the data file.");
        }
    }
}
