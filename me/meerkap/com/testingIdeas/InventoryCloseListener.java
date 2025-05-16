package me.meerkap.com.testingIdeas;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;

public class InventoryCloseListener implements Listener {
    private final MercadoService service;

    public InventoryCloseListener(MercadoService service) {
        this.service = service;
    }

    @EventHandler
    public void onClose(InventoryCloseEvent e) {

        Bukkit.broadcastMessage("Inventario cerrado");
        service.cerrarSesion(e.getPlayer().getUniqueId());
    }
}
