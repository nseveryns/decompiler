package net.nseveryns.decompiler.gui;

import javax.swing.AbstractAction;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.KeyStroke;
import javax.swing.WindowConstants;

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

    public DecompilerWindow() {
        projects = new CopyOnWriteArrayList<>();
        this.setLayout(new BorderLayout());
        DecompilerMenu menu = new DecompilerMenu();
        menu.addMenuItem(new SettingsMenuItem());
        this.setJMenuBar(menu);

        this.code = new RSyntaxTextArea();
        this.code.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_JAVA);
        this.code.setEditable(false);
        this.sidebar = new ProjectSidebar();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        JScrollPane scrollPane = new JScrollPane(sidebar);
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

    private void addProject(File file) {
        String extension = FilenameUtils.getExtension(file.getName());
        switch (extension) {
            case "java":
                addProjects(Transformers.JAVA.getInstance().createProject(file));
                break;
            case "jar":
                addProjects(Transformers.JAR.getInstance().createProject(file));
                break;
            default:
                break;
        }
    }

    private void decompileFile(File file, String ending) {
        switch (ending) {
            case "java":
                Transformers.JAVA.getInstance().decompile(file, code::setText);
                break;
            case "class":
                Transformers.CLASS.getInstance().decompile(file, code::setText);
                break;
            case "jar":
                addProject(file);
                break;
            default:
                Transformers.DEFAULT.getInstance().decompile(file, code::setText);
        }
    }

    private void addProjects(Project project) {
        for (Map.Entry<String, File> entry : project.getFiles().entrySet()) {
            JButton field = new JButton(entry.getKey());
            field.addActionListener(e -> {
                DecompilerWindow.this.decompileFile(entry.getValue(), FilenameUtils.getExtension(entry.getKey()));
                code.setEditable(project.isEditable());
                code.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_S,
                                Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()),
                        "actionMapKey");
                code.getActionMap().put("actionMapKey", new AbstractAction() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        System.out.println("Saving.");
                        String text = code.getText();
                        try {
                            FileUtils.writeStringToFile(entry.getValue(), text);
                        } catch (IOException e1) {
                            e1.printStackTrace();
                        }
                    }
                });
            });
            this.sidebar.add(field);
        }
    }
}
