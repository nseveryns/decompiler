package net.nseveryns.decompiler.gui;

import com.google.common.collect.ImmutableList;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

/**
 * Represents a menu item. This is for use in settings menus.
 */
public class MenuItem {
    private final List<MenuItem> items;
    private final Consumer<Boolean> consumer;
    private final AtomicBoolean toggled;
    private final String title;

    public MenuItem(String title, Consumer<Boolean> consumer, MenuItem... items) {
        this(title, Arrays.asList(items), consumer);
    }

    public MenuItem(String title, List<MenuItem> items, Consumer<Boolean> consumer) {
        this.title = title;
        this.consumer = consumer;
        this.items = ImmutableList.copyOf(items);
        this.toggled = new AtomicBoolean(false);
    }

    public List<MenuItem> getItems() {
        return items;
    }

    public Consumer<Boolean> getConsumer() {
        return consumer;
    }

    public boolean isToggled() {
        return toggled.get();
    }

    public String getTitle() {
        return title;
    }

    public void toggle() {
        this.toggled.set(!this.toggled.get());
    }
}
