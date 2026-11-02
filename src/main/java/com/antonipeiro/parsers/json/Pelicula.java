package com.antonipeiro.parsers;

import java.util.List;

public class Pelicula {
    private String titulo;
    private String[] generos;
    private double imdb;

    public Pelicula(String titulo, String[] generos, double imdb, double rottenTomatoes) {
        this.titulo = titulo;
        this.generos = generos;
        this.imdb = imdb;
        this.rottenTomatoes = rottenTomatoes;
    }

    private double rottenTomatoes;

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String[] getGeneros() {
        return generos;
    }

    public void setGeneros(String[] generos) {
        this.generos = generos;
    }

    public double getImdb() {
        return imdb;
    }

    public void setImdb(double imdb) {
        this.imdb = imdb;
    }

    public double getRottenTomatoes() {
        return rottenTomatoes;
    }

    public void setRottenTomatoes(double rottenTomatoes) {
        this.rottenTomatoes = rottenTomatoes;
    }
}
