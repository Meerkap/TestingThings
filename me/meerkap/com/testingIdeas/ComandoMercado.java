package me.meerkap.com.testingIdeas;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public class ComandoMercado implements CommandExecutor {

    private final MercadoService mercadoService;
    private final TestingIdeas plugin;

    public ComandoMercado(MercadoService mercadoService, TestingIdeas plugin) {
        this.mercadoService = mercadoService;
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("§cEste comando solo puede usarse en el juego.");
            return true;
        }
        Player jugador = (Player) sender;

        // Debug stock
        if (args.length==1 && args[0].equalsIgnoreCase("stock")) {
            agregarItemsDePrueba(jugador.getUniqueId());
            return true;
        }

        // Determinar propietario del mercado a abrir
        UUID propietario = jugador.getUniqueId();
        int pagina = 0;

        if (args.length >= 1) {
            // ¿es un jugador online?
            Player target = Bukkit.getPlayerExact(args[0]);
            if (target != null) {
                propietario = target.getUniqueId();
                // ¿hay un segundo arg para página?
                if (args.length >= 2) {
                    pagina = parsePagina(args[1], jugador);
                    if (pagina < 0) return true;
                }
            } else {
                // si no existía como jugador, pruebo a parsear página
                pagina = parsePagina(args[0], jugador);
                if (pagina < 0) return true;
            }
        }

        mercadoService.abrirMercado(jugador, propietario, pagina);
        return true;
    }

    private int parsePagina(String s, Player jugador) {
        try {
            int p = Integer.parseInt(s) - 1;
            return Math.max(p, 0);
        } catch (NumberFormatException e) {
            jugador.sendMessage("§cLa página debe ser un número válido.");
            return -1;
        }
    }

    private void agregarItemsDePrueba(UUID uniqueId) {
        org.bukkit.Material[] materiales = org.bukkit.Material.values();
        java.util.Random rand = new java.util.Random();

        for (int i = 0; i < 300; i++) { // 100 ítems para probar varias páginas
            org.bukkit.inventory.ItemStack item = new org.bukkit.inventory.ItemStack(materiales[i % materiales.length], rand.nextInt(5) + 1);// vendedor ficticio
            double precio = (rand.nextDouble() * 100.0);
            plugin.getService().getMercado(uniqueId).agregar(new ItemEnVenta(item, uniqueId, precio));
        }
    }

}
