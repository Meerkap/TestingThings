package me.meerkap.com.testingIdeas;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class Mercado {

    private final List<ItemEnVenta> lista = new ArrayList<>();

    // Método sincronizado para obtener copia de todos los items en venta
    public synchronized List<ItemEnVenta> getTodos() {
        return new ArrayList<>(lista);
    }

    // Agrega un item al mercado
    public synchronized void agregar(ItemEnVenta item) {
        lista.add(item);
    }

    // Intenta comprar un item (eliminarlo de la lista si está disponible)
    public synchronized Optional<ItemEnVenta> comprar(ItemEnVenta i) {
        boolean removed = lista.remove(i);
        return removed ? Optional.of(i) : Optional.empty();
    }

    // Elimina un item del mercado (sin retornar nada)
    public synchronized void eliminar(ItemEnVenta item) {
        lista.remove(item);
    }

}