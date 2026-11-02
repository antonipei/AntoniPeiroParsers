package com.antonipeiro.parsers;

public class Libro {
    private String isbn;
    private String titulo;
    private String autor;
    private int anio;
    private String[] generos;
    private boolean disponible;

    public Libro(String isbn, String titulo, String autor, int anio, String[] generos, boolean disponible) {
        this.isbn = isbn;
        this.titulo = titulo;
        this.autor = autor;
        this.anio = anio;
        this.generos = generos;
        this.disponible = disponible;
    }

    public String getIsbn() { return isbn; }
    public String getTitulo() { return titulo; }
    public String getAutor() { return autor; }
    public int getAnio() { return anio; }
    public String[] getGeneros() { return generos; }
    public boolean isDisponible() { return disponible; }
}