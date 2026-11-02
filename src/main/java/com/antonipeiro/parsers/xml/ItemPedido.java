package com.antonipeiro.parsers;

public class ItemPedido {
    private String sku;
    private String descripcion;
    private int cantidad;
    private double precioUnitario;

    public ItemPedido(String sku, String descripcion, int cantidad, double precioUnitario) {
        this.sku = sku;
        this.descripcion = descripcion;
        this.cantidad = cantidad;
        this.precioUnitario = precioUnitario;
    }

    public String getSku() { return sku; }
    public String getDescripcion() { return descripcion; }
    public int getCantidad() { return cantidad; }
    public double getPrecioUnitario() { return precioUnitario; }
}