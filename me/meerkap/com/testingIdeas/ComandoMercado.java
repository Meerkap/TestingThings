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
        // Solo jugadores pueden usar este comando
        if (!(sender instanceof Player)) {
            sender.sendMessage("§cEste comando solo puede usarse en el juego.");
            return true;
        }

        Player jugador = (Player) sender;

        if(args.length > 0) {
            if(args[0].equalsIgnoreCase("stock")) {
                Bukkit.broadcastMessage("añadimos stock");
                agregarItemsDePrueba(jugador.getUniqueId());
                return true;
            }
        }

        // Página inicial por defecto
        int pagina = 0;

        // Si el jugador pone un número, lo usamos como página
        if (args.length > 0) {
            try {
                pagina = Integer.parseInt(args[0]) - 1; // Convertir de 1-based a 0-based
                if (pagina < 0) pagina = 0;
            } catch (NumberFormatException e) {
                jugador.sendMessage("§cLa página debe ser un número.");
                return true;
            }
        }

        // Abrir mercado
        mercadoService.abrirMercado(jugador, pagina);
        return true;
    }


    private void agregarItemsDePrueba(UUID uniqueId) {
        org.bukkit.Material[] materiales = org.bukkit.Material.values();
        java.util.Random rand = new java.util.Random();

        for (int i = 0; i < 300; i++) { // 100 ítems para probar varias páginas
            org.bukkit.inventory.ItemStack item = new org.bukkit.inventory.ItemStack(materiales[i % materiales.length], rand.nextInt(5) + 1);// vendedor ficticio
            double precio = (rand.nextDouble() * 100.0);
            plugin.getMercado().agregar(new ItemEnVenta(item, uniqueId, precio));
        }
    }

}
