package net.nseveryns.decompiler.gui;

import javax.swing.JPanel;

import java.awt.Color;
import java.util.List;

import net.nseveryns.decompiler.Project;

/**
 * @author nseveryns
 */
public class ProjectSidebar extends JPanel {
    private final List<Project> projects;

    public ProjectSidebar(List<Project> projects) {
        this.projects = projects;
        this.setBackground(Color.LIGHT_GRAY);
    }
}
