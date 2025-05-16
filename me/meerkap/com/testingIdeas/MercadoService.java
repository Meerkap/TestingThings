package me.meerkap.com.testingIdeas;

import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class MercadoService {
    private final TestingIdeas plugin;
    // Mercados por propietario
    private final Map<UUID, Mercado> mercados = new HashMap<>();
    // Sesiones abiertas: cada viewer tiene su PaginatedInventoryManager
    private final Map<UUID, PaginatedInventoryManager> sesiones = new HashMap<>();

    public MercadoService(TestingIdeas plugin) {
        this.plugin = plugin;
    }

    // Obtiene o crea el Mercado de éste propietario
    public Mercado getMercado(UUID propietario) {
        return mercados.computeIfAbsent(propietario, id -> new Mercado());
    }

    // Abre una sesión del mercado 'propietario' para el jugador 'p'
    public void abrirMercado(Player p, UUID propietario, int pagina) {
        Mercado mercadoPropio = getMercado(propietario);
        PaginatedInventoryManager mgr = sesiones.computeIfAbsent(
                p.getUniqueId(),
                id -> new PaginatedInventoryManager(plugin, mercadoPropio, 45, propietario)
        );
        mgr.setPage(pagina);
        mgr.open(p);
    }

    public void cerrarSesion(UUID viewerId) {
        sesiones.remove(viewerId);
    }

    // Notifica sólo a las sesiones cuyo manager sea de ese mercado
    public void notificarCambio(UUID propietario, ItemEnVenta mod) {
        sesiones.values().stream()
                .filter(mgr -> mgr.getPropietario().equals(propietario))
                .forEach(mgr -> mgr.refresh());
    }
}

