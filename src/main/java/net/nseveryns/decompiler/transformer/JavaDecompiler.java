package net.nseveryns.decompiler.transformer;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.function.Consumer;

import com.google.common.collect.ImmutableMap;
import net.nseveryns.decompiler.Project;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;

public class JavaDecompiler implements Transformer {
    @Override
    public Project createProject(File file) {
        return new Project(file.getName(), ImmutableMap.of(file.getName(), file), true);
    }

    @Override
    public void decompile(File file, Consumer<String> consumer) {
        try {
            consumer.accept(FileUtils.readFileToString(file));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean acceptFile(File file) {
        return FilenameUtils.getExtension(file.getName()).equals("java");
    }
}
