package net.nseveryns.decompiler.transformer.dissambler;

import java.io.File;
import java.util.function.Consumer;

import net.nseveryns.decompiler.Project;
import net.nseveryns.decompiler.transformer.Transformer;
import org.apache.commons.io.FilenameUtils;

public class JavacDissasembler implements Transformer {
    @Override
    public Project createProject(File file) {
        return null;
    }

    @Override
    public void decompile(File file, Consumer<String> consumer) {

    }

    @Override
    public boolean acceptFile(File file) {
        return FilenameUtils.getExtension(file.getName()).equals("class");
    }
}
