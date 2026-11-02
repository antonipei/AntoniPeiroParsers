package com.antonipeiro.parsers.json;

public class Pelicula {
    private String id;
    private String titulo;
    private String director;
    private int estreno;
    private int duracionMin;
    private String[] generos;
    private double imdb;
    private double rottenTomatoes;

    public Pelicula(String id, String titulo, String director, int estreno, int duracionMin, String[] generos, double imdb, double rottenTomatoes) {
        this.id = id;
        this.titulo = titulo;
        this.director = director;
        this.estreno = estreno;
        this.duracionMin = duracionMin;
        this.generos = generos;
        this.imdb = imdb;
        this.rottenTomatoes = rottenTomatoes;
    }

    public String getId() { return id; }
    public String getTitulo() { return titulo; }
    public String getDirector() { return director; }
    public int getEstreno() { return estreno; }
    public int getDuracionMin() { return duracionMin; }
    public String[] getGeneros() { return generos; }
    public double getImdb() { return imdb; }
    public double getRottenTomatoes() { return rottenTomatoes; }
}