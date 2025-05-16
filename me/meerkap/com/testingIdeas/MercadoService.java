package me.meerkap.com.testingIdeas;

import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class MercadoService {
    private final TestingIdeas plugin;
    private final Mercado mercado;
    private final Map<UUID, PaginatedInventoryManager> sesiones = new HashMap<>();

    public MercadoService(TestingIdeas plugin, Mercado mercado) {
        this.plugin = plugin;
        this.mercado = mercado;
    }

    // Abre una sesión del mercado para un jugador en una página específica
    public void abrirMercado(Player p, int pagina) {
        PaginatedInventoryManager mgr = sesiones.computeIfAbsent(
                p.getUniqueId(), id -> new PaginatedInventoryManager(plugin, mercado, 45)
        );
        mgr.setPage(pagina);
        mgr.open(p);
    }

    // Cierra la sesión del jugador
    public void cerrarSesion(UUID id) {
        sesiones.remove(id);
    }

    // Notifica a todos los viewers que hubo un cambio (para refrescar sus inventarios)
    public void notificarCambio(ItemEnVenta mod) {
        sesiones.values().forEach(PaginatedInventoryManager::refresh);
    }
}
