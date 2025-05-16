package me.meerkap.com.testingIdeas;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class InventoryBuilder {
    private final InventoryType type;
    private int size;
    private String title;
    private InventoryHolder holder;
    private final Map<Integer, ItemStack> items = new HashMap<>();
    private boolean cancelAll;
    private final Map<Integer, Consumer<InventoryClickEvent>> clickSlots = new HashMap<>();
    private Consumer<InventoryClickEvent> clickAny;
    private Consumer<InventoryCloseEvent> onClose;
    private Consumer<InventoryDragEvent> onDrag;

    private InventoryBuilder(InventoryType type) {
        this.type = type;
        this.size = type.getDefaultSize();
        this.title = type.getDefaultTitle();
    }

    public static InventoryBuilder of(InventoryType type) {
        return new InventoryBuilder(type);
    }

    public InventoryBuilder size(int s) {
        this.size = s;
        return this;
    }

    public InventoryBuilder title(String t) {
        this.title = t;
        return this;
    }

    public InventoryBuilder holder(InventoryHolder h) {
        this.holder = h;
        return this;
    }

    public InventoryBuilder item(int slot, ItemStack item) {
        items.put(slot, item);
        return this;
    }

    public InventoryBuilder cancelAllClicks() {
        this.cancelAll = true;
        return this;
    }

    public InventoryBuilder onClick(int slot, Consumer<InventoryClickEvent> cb) {
        clickSlots.put(slot, cb);
        return this;
    }

    public InventoryBuilder onClickAny(Consumer<InventoryClickEvent> cb) {
        this.clickAny = cb;
        return this;
    }

    public InventoryBuilder onClose(Consumer<InventoryCloseEvent> cb) {
        this.onClose = cb;
        return this;
    }

    public InventoryBuilder onDrag(Consumer<InventoryDragEvent> cb) {
        this.onDrag = cb;
        return this;
    }

    public Inventory build(JavaPlugin plugin) {
        Inventory inv = Bukkit.createInventory(holder, size, title);
        items.forEach(inv::setItem);

        // Registrar listener si hay necesidad de manejar eventos
        if (cancelAll || !clickSlots.isEmpty() || clickAny != null || onClose != null || onDrag != null) {
            Bukkit.getPluginManager().registerEvents(new Internal(inv), plugin);
        }

        return inv;
    }

    // Clase interna para manejar eventos sobre el inventario
    private class Internal implements Listener {
        private final Inventory inv;

        Internal(Inventory inv) {
            this.inv = inv;
        }

        @EventHandler
        public void onClick(InventoryClickEvent e) {
            if (e.getInventory().equals(inv)) {
                if (cancelAll) e.setCancelled(true);
                int slot = e.getRawSlot();
                clickSlots.getOrDefault(slot, ev -> {
                }).accept(e);
                if (clickAny != null) clickAny.accept(e);
            }
        }

        @EventHandler
        public void onClose(InventoryCloseEvent e) {
            if (e.getInventory().equals(inv) && onClose != null) onClose.accept(e);
        }

        @EventHandler
        public void onDrag(InventoryDragEvent e) {
            if (e.getInventory().equals(inv)) {
                if (cancelAll) e.setCancelled(true);
                if (onDrag != null) onDrag.accept(e);
            }
        }
    }
}
