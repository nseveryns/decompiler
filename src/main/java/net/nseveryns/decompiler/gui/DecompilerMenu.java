package net.nseveryns.decompiler.gui;

import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

public class DecompilerMenu extends JMenuBar {

    public void addMenuItem(MenuItem item) {
        this.add(generateJMenu(item));
    }

    private JMenuItem generateJMenu(MenuItem item) {
        JMenu menu = new JMenu(item.getTitle());
        menu.addActionListener(e -> {
            item.toggle();
            item.getConsumer().accept(item.isToggled());
        });
        for (MenuItem menuItem : item.getItems()) {
            menu.add(generateJMenu(menuItem));
        }
        return menu;
    }

}
