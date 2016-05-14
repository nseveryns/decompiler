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
public class DecompilerWindow extends JFrame implements View {
    private final List<Project> projects;
    private final ProjectSidebar sidebar;
    private final JPanel code;

    public DecompilerWindow() {
        projects = new CopyOnWriteArrayList<>();
        this.setLayout(new BorderLayout());
        DecompilerMenu menu = new DecompilerMenu();
        menu.addMenuItem(new SettingsMenuItem());
        this.setJMenuBar(menu);

        this.code = new JPanel();
        this.sidebar = new ProjectSidebar(projects);
        JSplitPane pane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, sidebar, code);
        pane.setDropTarget(new DropTarget() {
            @Override
            public synchronized void drop(DropTargetDropEvent event) {
                event.acceptDrop(DnDConstants.ACTION_LINK);
                try {
                    Object obj = event.getTransferable().getTransferData(DataFlavor.javaFileListFlavor);
                    if (!(obj instanceof List)) {
                        return;
                    }
                    //noinspection unchecked
                    List<File> droppedFiles = (List<File>) obj;
                    droppedFiles.forEach(DecompilerWindow.this::decompileFile);
                } catch (UnsupportedFlavorException | IOException e) {
                    e.printStackTrace();
                }
            }
        });
        this.add(pane);
        this.setSize(500, 500);
        this.setAlwaysOnTop(true);
        this.setResizable(true);
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
