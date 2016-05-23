package net.nseveryns.decompiler.transformer;

import com.google.common.collect.ImmutableMap;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.function.Consumer;

import net.nseveryns.decompiler.Project;
import org.apache.commons.io.FilenameUtils;

public class ClassDecompiler implements Transformer {
    @Override
    public Project createProject(File file) {
        return new Project(file.getName(), ImmutableMap.of(file.getName(), file));
    }

    @Override
    public void decompile(File file, Consumer<String> consumer) {
        try {
            byte[] bytes = Files.readAllBytes(file.toPath());
            consumer.accept(formatHex(bytes));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean acceptFile(File file) {
        return FilenameUtils.getExtension(file.getName()).equals("class");
    }

    private String formatHex(byte[] a) {
        StringBuilder sb = new StringBuilder(a.length * 2);
        for (byte b : a) {
            sb.append(String.format("%02x", b & 0xff));
        }
        return sb.toString();
    }
}
