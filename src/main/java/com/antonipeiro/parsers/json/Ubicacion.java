package com.antonipeiro.parsers.json;

public class Ubicacion {
    private int pasillo;
    private String estante;

    public Ubicacion(int pasillo, String estante) {
        this.pasillo = pasillo;
        this.estante = estante;
    }

    public int getPasillo() { return pasillo; }
    public String getEstante() { return estante; }

    public String toString() {
        return "Pasillo " + pasillo + ", Estante " + estante;
    }
}