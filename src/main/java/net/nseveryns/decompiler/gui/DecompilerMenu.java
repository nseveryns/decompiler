package net.nseveryns.decompiler.gui;

import javax.swing.*;

public class DecompilerMenu extends JMenuBar {

    /**
     * Add an item to the menu bar.
     */
    public void addMenuItem(MenuItem item) {
        this.add(generateJMenu(item));
    }

    /**
     * Generate a menu from an item
     *
     * @param item the item to create an menu from
     * @return a {@link JMenuItem} instance.
     */
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
