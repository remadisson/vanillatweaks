package de.remadisson.vanillatweaks.file;

import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

public class FileAPI {

    private final String filename;
    private final String path;

    private final File file;
    private YamlConfiguration config;
    private LoadRoutine loadRoutine = null;

    public FileAPI(String filename, String path) {
        this.filename = filename;
        this.path = path;

        File folder = new File(path);

        if (!folder.exists()) {
            folder.mkdir();
        }

        this.file = new File(path, filename);

        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        this.config = YamlConfiguration.loadConfiguration(file);
    }

    public String getFilename() {
        return filename;
    }

    public String getPath() {
        return path;
    }

    public File getFile() {
        return file;
    }

    public YamlConfiguration getConfig() {
        return config;
    }

    public void save() {
        try {
            this.config.save(file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void reload() {
        this.config = YamlConfiguration.loadConfiguration(file);
        doLoadRoutine();
    }

    public void setLoadRoutine(LoadRoutine loadRoutine) {
        if (this.loadRoutine != null) {
            throw new RuntimeException("LoadRoutine is already defined");
        }
        this.loadRoutine = loadRoutine;
    }

    private void doLoadRoutine() {
        if (this.loadRoutine == null) {
            return;
        }

        loadRoutine.executeLoadRoutine(this);
    }

}
