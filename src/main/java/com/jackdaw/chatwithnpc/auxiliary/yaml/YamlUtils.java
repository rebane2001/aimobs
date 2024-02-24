package com.jackdaw.chatwithnpc.auxiliary.yaml;

import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.DumperOptions.FlowStyle;
import org.yaml.snakeyaml.Yaml;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

public final class YamlUtils {
    public YamlUtils() {
    }

    public static HashMap readFile(File file) throws FileNotFoundException {
        Yaml yaml = new Yaml();
        Map ret;
        try {
            ret = yaml.load(new FileReader(file));
        } catch (FileNotFoundException e) {
            ret = null;
            }
            if (ret == null)
                ret = new HashMap();
            return new HashMap(ret);
    }

    public static void writeFile(File file, Object object) throws IOException {
        DumperOptions options = new DumperOptions();
        options.setIndent(2);
        options.setPrettyFlow(true);
        options.setDefaultFlowStyle(FlowStyle.BLOCK);
        Yaml yaml = new Yaml(options);
        yaml.dump(object, new FileWriter(file));
    }
}
