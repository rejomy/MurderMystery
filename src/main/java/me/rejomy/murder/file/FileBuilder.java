package me.rejomy.murder.file;

import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

public class FileBuilder {
    public static void create(File file) {
        try {
            file.createNewFile();
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    public static void save(File file, YamlConfiguration config) {
        try {
            config.save(file);
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }
}
