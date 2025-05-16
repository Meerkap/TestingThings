package me.meerkap.com.testingIdeas;

import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;

public final class TestingIdeas extends JavaPlugin {


    //* Para TESTING
    private MercadoService service;


    @Override
    public void onEnable() {
        this.service = new MercadoService(this);
        getServer().getPluginManager().registerEvents(new InventoryCloseListener(service), this);
        Objects.requireNonNull(getCommand("mercado"))
                .setExecutor(new ComandoMercado(service, this));
    }



    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public MercadoService getService() {
        return service;
    }
}
