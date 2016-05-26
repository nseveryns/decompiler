package net.nseveryns.decompiler.transformer;

import com.google.common.collect.ImmutableMap;
import net.nseveryns.decompiler.Project;
import org.apache.commons.io.FileUtils;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.function.Consumer;

/**
 * Decompiles any file type that is not a java, class, or jar file.
 * <p>
 * This will prompt the user if they want to open it in the default application on their computer.
 *
 * @author nseveryns
 */
public class DefaultDecompiler implements Transformer {
    @Override
    public Project createProject(File file) {
        return new Project(file.getName(), ImmutableMap.of(file.getName(), file));
    }

    /**
     * Prompts the user if they want to use the default editor or not.
     */
    @Override
    public void decompile(File file, Consumer<String> consumer) {
        JOptionPane pane = new JOptionPane("Do you want to open this with the default editor for this file type?",
                JOptionPane.QUESTION_MESSAGE,
                JOptionPane.YES_NO_OPTION);
        pane.setVisible(true);
        int reply = JOptionPane.showConfirmDialog(null,
                "Do you want to open this with the default editor for this file type?",
                "Please confirm",
                JOptionPane.YES_NO_OPTION);
        if (reply == JOptionPane.YES_OPTION) {
            try {
                Desktop.getDesktop().open(file);
                return;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            consumer.accept(FileUtils.readFileToString(file));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean acceptFile(File file) {
        return false;
    }
}
