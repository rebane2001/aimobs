package com.jackdaw.chatwithnpc.data;

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
}
