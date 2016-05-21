package net.nseveryns.decompiler;

import com.google.common.collect.MapMaker;

import java.io.File;
import java.util.Map;

/**
 * @author nseveryns
 */
public class Project {
    private final String name;
    private final Map<String, File> files;

    public Project(String name, Map<String, File> files) {
        this.name = name;
        this.files = new MapMaker().weakValues().makeMap();
        this.files.putAll(files);
    }

    public String getName() {
        return name;
    }

    public Map<String, File> getFiles() {
        return files;
    }
}
