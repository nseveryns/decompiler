package net.nseveryns.decompiler.gui;

import net.nseveryns.decompiler.Project;
import org.apache.commons.io.FilenameUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDropEvent;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @author nseveryns
 */
public class DecompilerWindow extends JSplitPane implements View {
    private final List<Project> projects;

    public DecompilerWindow() {
        super(JSplitPane.HORIZONTAL_SPLIT, new JScrollPane(), new JTextArea("Drag files here."));
        this.setOneTouchExpandable(true);
        this.setDividerLocation(150);
        projects = new CopyOnWriteArrayList<>();
        this.setLayout(new BorderLayout());
        this.setDropTarget(new DropTarget() {
            @Override
            public synchronized void drop(DropTargetDropEvent event) {
                event.acceptDrop(DnDConstants.ACTION_COPY);
                try {
                    Object obj = event.getTransferable().getTransferData(DataFlavor.javaFileListFlavor);
                    if (!(obj instanceof List)) {
                        return;
                    }
                    //noinspection unchecked
                    List<File> droppedFiles = (List<File>) obj;

                    for (File file : droppedFiles) {
                        decompileFile(file);
                    }
                } catch (UnsupportedFlavorException | IOException e) {
                    e.printStackTrace();
                }

            }
        });
        this.setSize(500, 500);
        this.setVisible(true);
    }

    private void decompileFile(File file) {
        String extension = FilenameUtils.getExtension(file.getName());
        switch (extension) {
            case "jar":
                System.out.println("Decompiling!");
                this.projects.add(new Project());
                break;
            default:
                break;
        }
    }

    private boolean hasOpenProject() {
        return !projects.isEmpty();
    }

    @Override
    public void update() {
        if (hasOpenProject()) {
            this.add(new JScrollPane());
        }
        this.add(new TextArea("Drag and drop files here to decompile."));
    }
}
