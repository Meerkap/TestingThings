package me.meerkap.com.testingIdeas;

import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;

public final class TestingIdeas extends JavaPlugin {


    //* Para TESTING
    private MercadoService service;
    private Mercado mercado;


    @Override
    public void onEnable() {
        // Plugin startup logic

        //* Para TESTING
        mercado = new Mercado();
        service = new MercadoService(this, mercado);
        getServer().getPluginManager().registerEvents(new InventoryCloseListener(service), this);


        Objects.requireNonNull(getCommand("mercado")).setExecutor(new ComandoMercado(service, this));
        // Agregar ítems de prueba para verificar paginación

    }



    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public Mercado getMercado() {
        return mercado;
    }

    public MercadoService getService() {
        return service;
    }
}
