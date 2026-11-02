package com.antonipeiro.parsers;

import java.time.LocalDate;

public class Alumno {
    private final String id;
    private final String nombre;
    private final boolean matriculado;
    private final LocalDate fechaNacimiento;
    private final Nota[] notas;

    public Alumno(String id, String nombre, boolean matriculado, LocalDate fechaNacimiento, Nota[] notas) {
        this.id = id;
        this.nombre = nombre;
        this.matriculado = matriculado;
        this.fechaNacimiento = fechaNacimiento;
        this.notas = notas;
    }

    public String getId() {
        return id;
    }

    public boolean isMatriculado() {
        return matriculado;
    }

    public String getNombre() {
        return nombre;
    }

    public LocalDate getFechaNacimiento() {
        return fechaNacimiento;
    }

    public Nota[] getNotas() {
        return notas;
    }
}
