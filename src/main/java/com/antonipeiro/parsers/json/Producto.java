package com.antonipeiro.parsers.json;

public class Producto {
    private String id;
    private String nombre;
    private int stock;
    private double precio;
    private String[] tags;
    private Ubicacion ubicacion;

    public Producto(String id, String nombre, int stock, double precio, String[] tags, Ubicacion ubicacion) {
        this.id = id;
        this.nombre = nombre;
        this.stock = stock;
        this.precio = precio;
        this.tags = tags;
        this.ubicacion = ubicacion;
    }

    public String getId() { return id; }
    public String getNombre() { return nombre; }
    public int getStock() { return stock; }
    public double getPrecio() { return precio; }
    public String[] getTags() { return tags; }
    public Ubicacion getUbicacion() { return ubicacion; }
}