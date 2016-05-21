package net.nseveryns.decompiler;

import com.google.common.collect.MapMaker;

import java.io.File;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * @author nseveryns
 */
public class Project {
    private final String name;
    private final Map<String, File> files;

    public Project(String name, List<File> files) {
        this.name = name;
        this.files = new MapMaker().weakValues().makeMap();
        for (File file : files) {
            this.files.put(file.getName(), file);
        }
    }

    public String getName() {
        return name;
    }

    public Collection<File> getFiles() {
        return files.values();
    }
}
