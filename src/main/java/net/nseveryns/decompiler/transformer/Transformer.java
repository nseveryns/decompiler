package net.nseveryns.decompiler.transformer;

import java.io.File;
import java.util.function.Consumer;

import net.nseveryns.decompiler.Project;

public interface Transformer {

    Project createProject(File file);

    void decompile(File file, Consumer<String> consumer);

    boolean acceptFile(File file);

}
