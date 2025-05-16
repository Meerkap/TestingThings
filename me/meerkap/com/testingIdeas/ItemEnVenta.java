package me.meerkap.com.testingIdeas;

import org.bukkit.inventory.ItemStack;

import java.util.UUID;

public class ItemEnVenta {

    private final ItemStack item;
    private final UUID vendedor;
    private final double precioUnitario;

    public ItemEnVenta(ItemStack i, UUID v, double p) {
        this.item = i;
        this.vendedor = v;
        this.precioUnitario = p;
    }

    public ItemStack getItem() {
        return item;
    }

    public UUID getVendedor() {
        return vendedor;
    }

    public double getPrecioUnitario() {
        return precioUnitario;
    }

}

