package net.nseveryns.decompiler.transformer;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.function.Consumer;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import net.nseveryns.decompiler.Project;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;

public class JarDecompiler implements Transformer {
    @Override
    public Project createProject(File file) {
        try {
            JarFile jarFile = new JarFile(file);
            Enumeration<JarEntry> entries = jarFile.entries();
            List<File> files = new ArrayList<>();
            while (entries.hasMoreElements()) {
                JarEntry entry = entries.nextElement();
                InputStream stream = jarFile.getInputStream(entry);
                File cachedFile = new File("./cache/" + file.getName());
                if (cachedFile.exists()) {
                    cachedFile.mkdir();
                }
                FileUtils.copyInputStreamToFile(stream, cachedFile);
                files.add(cachedFile);
            }
            return new Project(file.getName(), files);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void decompile(File file, Consumer<String> consumer) {
        try {
            JarFile jarFile = new JarFile(file);
            Enumeration<JarEntry> entries = jarFile.entries();
            StringBuilder builder = new StringBuilder();
            while (entries.hasMoreElements()) {
                JarEntry entry = entries.nextElement();
                builder.append(entry.getName());
                builder.append("\n");
            }
            consumer.accept(builder.toString());
            return;
        } catch (IOException e) {
            e.printStackTrace();
        }
        consumer.accept("Invalid file.");
    }

    @Override
    public boolean acceptFile(File file) {
        return FilenameUtils.getExtension(file.getName()).equals("jar");
    }
}
