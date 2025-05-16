package me.meerkap.com.testingIdeas;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

public class PaginatedInventoryManager {
    private final TestingIdeas plugin;
    private final Mercado mercado;
    private final UUID propietario;
    private final int itemsPerPage;
    private int currentPage = 0;
    private Inventory ventana;
    private Player viewer;

    public PaginatedInventoryManager(TestingIdeas plugin, Mercado mercado, int itemsPerPage, UUID propietario) {
        this.plugin = plugin;
        this.mercado = mercado;
        this.itemsPerPage = itemsPerPage;
        this.propietario = propietario;
    }

    public UUID getPropietario() {
        return propietario;
    }

    public void setPage(int page) {
        this.currentPage = page;
    }

    public void open(Player p) {
        this.viewer = p;
        this.ventana = buildInventory(p);
        p.openInventory(ventana);
    }

    // Crea y construye el inventario para la página actual
    private Inventory buildInventory(Player p) {
        String nombreProp = Bukkit.getOfflinePlayer(propietario).getName();
        InventoryBuilder b = InventoryBuilder.of(InventoryType.CHEST)
                .size(54)
                .title("Mercado de " + nombreProp)
                .holder(new MercadoHolder(propietario, "personal"))
                .cancelAllClicks();

        List<ItemEnVenta> todos = mercado.getTodos();
        int start = currentPage * itemsPerPage;
        for (int i = 0; i < itemsPerPage && start + i < todos.size(); i++) {
            ItemEnVenta iv = todos.get(start + i);
            b.item(i, crearItemVisual(iv))
                    .onClick(i, e -> {
                        if (iv.getVendedor().equals(p.getUniqueId())) {
                            // Retirar tu propio ítem
                            mercado.comprar(iv).ifPresent(item -> {
                                p.getInventory().addItem(item.getItem());
                                p.sendMessage("§aHas retirado tu ítem del mercado.");
                                open(p);
                                plugin.getService().notificarCambio(propietario, item);
                            });
                        } else {
                            // Abrir confirmación, pasando callback
                            abrirMenuConfirmacion(p, iv, precio -> {
                                mercado.comprar(iv).ifPresent(item2 -> {
                                    p.getInventory().addItem(item2.getItem());
                                    p.sendMessage("§a¡Has comprado el ítem por §e" + precio + "§a!");
                                    plugin.getService().notificarCambio(propietario, item2);
                                    p.closeInventory();
                                });
                            });
                        }
                    });
        }

        // Flecha anterior
        if (currentPage > 0) {
            ItemStack prev = new ItemStack(Material.ARROW);
            ItemMeta pm = prev.getItemMeta();
            pm.setDisplayName("§aPágina anterior");
            prev.setItemMeta(pm);
            b.item(45, prev).onClick(45, e -> {
                setPage(currentPage - 1);
                open(p);
            });
        }

        // Flecha siguiente
        if ((currentPage + 1) * itemsPerPage < todos.size()) {
            ItemStack next = new ItemStack(Material.ARROW);
            ItemMeta nm = next.getItemMeta();
            nm.setDisplayName("§aPágina siguiente");
            next.setItemMeta(nm);
            b.item(53, next).onClick(53, e -> {
                setPage(currentPage + 1);
                open(p);
            });
        }

        return b.build(plugin);
    }

    // -- abrirMenuConfirmacion con callback --
    private void abrirMenuConfirmacion(Player jugador, ItemEnVenta iv, Consumer<Double> onConfirm) {
        InventoryBuilder builder = InventoryBuilder.of(InventoryType.CHEST)
                .size(27)
                .title("Confirmar Compra")
                .cancelAllClicks();

        // Ítem en el centro
        builder.item(13, crearItemVisual(iv));

        double precio = iv.getPrecioUnitario();

        // Botón Confirmar
        ItemStack confirmar = new ItemStack(Material.LIME_WOOL);
        ItemMeta cm = confirmar.getItemMeta();
        cm.setDisplayName("§aConfirmar Compra (§e" + precio + "§a)");
        confirmar.setItemMeta(cm);
        builder.item(11, confirmar).onClick(11, e -> {
            onConfirm.accept(precio);
        });

        // Botón Cancelar
        ItemStack cancelar = new ItemStack(Material.BARRIER);
        ItemMeta xm = cancelar.getItemMeta();
        xm.setDisplayName("§cCancelar");
        cancelar.setItemMeta(xm);
        builder.item(15, cancelar).onClick(15, e -> jugador.closeInventory());

        jugador.openInventory(builder.build(plugin));
    }

    // Refresca los ítems visibles sin cerrar el inventario
    public void refresh() {
        if (ventana == null || !ventana.getViewers().contains(viewer)) return;
        List<ItemEnVenta> todos = mercado.getTodos();
        int start = currentPage * itemsPerPage;
        for (int i = 0; i < itemsPerPage; i++) {
            if (start + i < todos.size()) {
                ventana.setItem(i, crearItemVisual(todos.get(start + i)));
            } else {
                ventana.setItem(i, null);
            }
        }
    }

    // Métodos auxiliares por implementar: crearItemVisual, abrirMenuConfirmacion

    // Crea un ItemStack visual para mostrar en el inventario
    private ItemStack crearItemVisual(ItemEnVenta itemVenta) {
        // Clonamos el ítem original para no modificar el real
        ItemStack visual = itemVenta.getItem().clone();

        // Modificamos el lore para mostrar el precio (se puede hacer más elaborado)
        ItemMeta meta = visual.getItemMeta();
        if (meta != null) {
            List<String> lore = meta.hasLore() ? meta.getLore() : new ArrayList<>();
            lore.add("§7Precio: §e" + itemVenta.getPrecioUnitario());
            meta.setLore(lore);
            visual.setItemMeta(meta);
        }
        return visual;
    }

    // Abre un menú de confirmación para comprar un ítem
    private void abrirMenuConfirmacion(Player jugador, ItemStack item, double precio, JavaPlugin plugin) {
        // Creamos un inventario simple de 3 filas
        InventoryBuilder builder = InventoryBuilder.of(InventoryType.CHEST)
                .size(27)
                .title("Confirmar Compra")
                .cancelAllClicks();

        // Slot central con la información del ítem
        builder.item(13, item);

        // Slot de confirmación (verde)
        ItemStack confirmar = new ItemStack(Material.LIME_WOOL);
        ItemMeta confirmarMeta = confirmar.getItemMeta();
        confirmarMeta.setDisplayName("§aConfirmar Compra");
        confirmar.setItemMeta(confirmarMeta);
        builder.item(11, confirmar).onClick(11, e -> {
            jugador.sendMessage("§a¡Has comprado el ítem por §e" + precio + "§a!");
            jugador.closeInventory();
            // Aquí iría la lógica real de compra: quitar dinero, dar ítem, etc.
        });

        // Slot de cancelar (rojo)
        ItemStack cancelar = new ItemStack(Material.BARRIER);
        ItemMeta cancelarMeta = cancelar.getItemMeta();
        cancelarMeta.setDisplayName("§cCancelar");
        cancelar.setItemMeta(cancelarMeta);
        builder.item(15, cancelar).onClick(15, e -> jugador.closeInventory());

        // Abrimos el inventario
        jugador.openInventory(builder.build(plugin));
    }

}

