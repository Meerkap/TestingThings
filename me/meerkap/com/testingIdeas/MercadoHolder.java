package me.meerkap.com.testingIdeas;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

import java.util.UUID;

public class MercadoHolder implements InventoryHolder {

    private final UUID propietario;
    private final String tipoMercado;

    public MercadoHolder(UUID propietario, String tipoMercado) {
        this.propietario = propietario;
        this.tipoMercado = tipoMercado;
    }

    public UUID getPropietario() {
        return propietario;
    }

    public String getTipoMercado() {
        return tipoMercado;
    }

    @Override
    public Inventory getInventory() {
        return null;
    }


}
