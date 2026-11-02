package com.antonipeiro.parsers.xml;

public class Pedido {
    private String id;
    private String nombreCliente;
    private String fecha;
    private ItemPedido[] items;
    private double totalDeclarado;

    public Pedido(String id, String nombreCliente, String fecha, ItemPedido[] items, double totalDeclarado) {
        this.id = id;
        this.nombreCliente = nombreCliente;
        this.fecha = fecha;
        this.items = items;
        this.totalDeclarado = totalDeclarado;
    }

    public String getId() { return id; }
    public String getNombreCliente() { return nombreCliente; }
    public String getFecha() { return fecha; }
    public ItemPedido[] getItems() { return items; }
    public double getTotalDeclarado() { return totalDeclarado; }
}