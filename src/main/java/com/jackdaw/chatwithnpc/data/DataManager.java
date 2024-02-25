package com.jackdaw.chatwithnpc.data;

import com.jackdaw.chatwithnpc.ChatWithNPCMod;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * A serializer used to read or write the information from the Yaml files.
 *
 * <p>Read or Write the data file with some information, each file just record one relative information.</p>
 */
public interface DataManager {
    /**
     * Check if the file exists.
     * @return true if the file exists, otherwise false.
     */
    boolean isExist();

    /**
     * Read the user data file.
     */
    void sync();


    /**
     * Write the file as the class record.
     */
    void save();

    /**
     * Delete the record.
     */
    void delete();

    /**
     * Create the directory.
     */
    static void mkdir(String childPathName) {
        Path workingDirectory = ChatWithNPCMod.workingDirectory.resolve(childPathName);
        if (!Files.exists(workingDirectory)) {
            try {
                Files.createDirectories(workingDirectory);
            } catch (IOException e) {
                ChatWithNPCMod.LOGGER.error("[chat-with-npc] Failed to create the npc directory");
                ChatWithNPCMod.LOGGER.error(e.getMessage());
                throw new RuntimeException(e);
            }
        }
    }
}
