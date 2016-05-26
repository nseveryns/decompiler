package net.nseveryns.decompiler.gui;

import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;

import javax.swing.*;

import java.awt.BorderLayout;
import java.awt.Toolkit;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import net.nseveryns.decompiler.Project;
import net.nseveryns.decompiler.transformer.Transformers;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.SyntaxConstants;
import org.fife.ui.rtextarea.RTextScrollPane;

/**
 * @author nseveryns
 */
public class DecompilerWindow extends JFrame {
    private final List<Project> projects;
    private final ProjectSidebar sidebar;
    private final RSyntaxTextArea code;
    private final JScrollPane scrollPane;
    private final ListeningExecutorService executor;

    public DecompilerWindow() {
        this.executor = MoreExecutors.listeningDecorator(Executors.newSingleThreadExecutor());
        this.projects = new CopyOnWriteArrayList<>();
        this.setLayout(new BorderLayout());
        DecompilerMenu menu = new DecompilerMenu();
        menu.addMenuItem(new SettingsMenuItem());
        this.setJMenuBar(menu);

        this.code = new RSyntaxTextArea();
        this.code.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_JAVA);
        this.code.setEditable(false);
        this.sidebar = new ProjectSidebar();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        this.scrollPane = new JScrollPane(sidebar);
        JSplitPane pane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, scrollPane, new RTextScrollPane(code));
        pane.setResizeWeight(.2);
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
                    droppedFiles.forEach(DecompilerWindow.this::addProject);
                } catch (UnsupportedFlavorException | IOException e) {
                    e.printStackTrace();
                }
            }
        });
        this.add(pane);
        this.setSize(500, 500);
        this.setResizable(true);
        this.setVisible(true);
        this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    }

    /**
     * Create a project from a file that has been drag and dropped.
     *
     * @param file the file that will be decompiled.
     */
    private void addProject(File file) {
        String extension = FilenameUtils.getExtension(file.getName());
        switch (extension) {
            case "java":
                this.addProjects(Transformers.JAVA.getInstance().createProject(file));
                sidebar.updateUI();
                break;
            case "class":
                this.addProjects(Transformers.CLASS.getInstance().createProject(file));
                sidebar.updateUI();
                break;
            case "jar":
                executor.submit(() -> this.addProjects(Transformers.JAR.getInstance().createProject(file)))
                        .addListener(sidebar::updateUI, MoreExecutors.directExecutor());
                break;
            default:
                break;
        }
    }

    /**
     * Decompile to the file using the transformer associated to the file type.
     * This will use the Transformers enum to access the transformer instance.
     *
     * @param file the file that will be decompiled.
     * @param ending the file type ending.
     *               This is used if the file is actually a temporary file with an incorrect ending
     */
    private void decompileFile(File file, String ending) {
        code.setText("Decompiling... please wait.");
        switch (ending) {
            case "java":
                executor.execute(() -> Transformers.JAVA.getInstance().decompile(file, code::setText));
                break;
            case "class":
                executor.execute(() -> Transformers.CLASS.getInstance().decompile(file, code::setText));
                break;
            case "jar":
                code.setText("");
                addProject(file);
                break;
            default:
                executor.execute(() -> Transformers.DEFAULT.getInstance().decompile(file, code::setText));
        }
    }

    /**
     * This will take the project, create buttons for all of the files and then add listeners to them.
     * The listeners will listen for a click and when it happens, it will decompile and display it in the text box.
     * Additionally it will add a save function for editable files.
     * At the very end it will update the UI and display the buttons.
     *
     * @param project the project that will be added to the screen.
     */
    private void addProjects(Project project) {
        for (Map.Entry<String, File> entry : project.getFiles().entrySet()) {
            JButton field = new JButton(entry.getKey());
            field.addActionListener(e -> {
                DecompilerWindow.this.decompileFile(entry.getValue(), FilenameUtils.getExtension(entry.getKey()));
                code.setEditable(project.isEditable());
                if (project.isEditable()) {
                    code.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_S,
                                    Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()),
                            "actionMapKey");
                    code.getActionMap().put("actionMapKey", new AbstractAction() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            executor.execute(() -> {
                                String text = code.getText();
                                try {
                                    FileUtils.writeStringToFile(entry.getValue(), text);
                                } catch (IOException e1) {
                                    e1.printStackTrace();
                                }
                            });
                        }
                    });
                }
            });
            this.sidebar.add(field);
        }
    }
}
