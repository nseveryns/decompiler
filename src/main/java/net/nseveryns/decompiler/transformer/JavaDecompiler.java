package net.nseveryns.decompiler.transformer;

import com.google.common.collect.ImmutableMap;
import net.nseveryns.decompiler.Project;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.io.IOException;
import java.util.function.Consumer;

/**
 * This will create a project and decompile a java file. Java files are in plain text so it is safe to assume
 * that editing will be enabled and that it can be read by just reading the file to a string. If java files
 * change or any extensibility is added, this will be changed.
 */
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
