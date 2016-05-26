package net.nseveryns.decompiler;

import com.google.common.collect.MapMaker;

import java.io.File;
import java.util.Map;

/**
 * An over-glorified object representation of a list of files. This will hold data if the files
 * are able to be edited and any other data that might be accumulated.
 *
 * @author nseveryns
 */
public class Project {
    private final String name;
    private final Map<String, File> files;
    private boolean editable;

    public Project(String name, Map<String, File> files, boolean editable) {
        this(name, files);
        this.editable = editable;
    }

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

    public boolean isEditable() {
        return editable;
    }

    public void setEditable(boolean editable) {
        this.editable = editable;
    }
}
