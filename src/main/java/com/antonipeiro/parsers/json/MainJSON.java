package com.antonipeiro.parsers;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.util.*;

public class Main {

    public static void main(String[] args) {
        Main main = new Main();

        System.out.println("5. LISTADO DE ALUMNOS");
        try {
            Alumno[] alumnos = main.parsearAlumnos("alumnos.json");
            if (alumnos != null) {
                for (Alumno a : alumnos) {
                    System.out.println("Alumno: " + a.getNombre() + " | Fecha de nacimiento: " + a.getFechaNacimiento());
                }

                System.out.println("6. OBJETOS Y ESTADÍSTICAS");
                System.out.println("asignatura mas alta de alumno");
                for (Alumno a : alumnos) {
                    if (a.getNotas() != null && a.getNotas().length > 0) {
                        Nota mejorNota = a.getNotas()[0];
                        for (Nota n : a.getNotas()) {
                            if (n.getNota() > mejorNota.getNota()) {
                                mejorNota = n;
                            }
                        }
                        System.out.println("- " + a.getNombre() + ": " + mejorNota.getAsignatura() + " (" + mejorNota.getNota() + ")");
                    }
                }

                Alumno mejorAlumno = null;
                double maxMedia = -1;
                for (Alumno a : alumnos) {
                    if (a.getNotas() != null && a.getNotas().length > 0) {
                        double suma = 0;
                        for (Nota n : a.getNotas()) {
                            suma += n.getNota();
                        }
                        double media = suma / a.getNotas().length;
                        if (media > maxMedia) {
                            maxMedia = media;
                            mejorAlumno = a;
                        }
                    }
                }

                if (mejorAlumno != null) {
                    System.out.println("alumno con nota media mas alta: " + mejorAlumno.getNombre() + " (Media: " + String.format(Locale.US, "%.2f", maxMedia) + ")");
                }
            }
        } catch (IOException e) {
            System.err.println("Error procesando alumnos.json: " + e.getMessage());
        }

        System.out.println("7. CONSULTA DE INVENTARIO");
        try {
            Producto[] inventario = main.parsearInventario("inventario.json");
            if (inventario != null) {
                Scanner scanner = new Scanner(System.in);
                System.out.print("introduce el numero del producto: ");
                String busqueda = scanner.nextLine().trim();

                Producto encontrado = null;
                for (Producto p : inventario) {
                    if (p.getNombre().equalsIgnoreCase(busqueda)) {
                        encontrado = p;
                        break;
                    }
                }

                if (encontrado != null) {
                    System.out.println("Ubicación del producto '" + encontrado.getNombre() + "': " + encontrado.getUbicacion());
                } else {
                    System.out.println("Producto no encontrado en el inventario.");
                }
            }
        } catch (IOException e) {
            System.err.println("Error procesando inventario.json: " + e.getMessage());
        }

        System.out.println("\n=== 8. PELÍCULAS ===");
        try {
            Pelicula[] peliculas = main.parsearPeliculas("peliculas.json");
            if (peliculas != null) {
                Map<Pelicula, Double> mediasPeliculas = new HashMap<>();
                for (Pelicula p : peliculas) {
                    double rtNormalizado = p.getRottenTomatoes() / 10.0;
                    double mediaFinal = (p.getImdb() + rtNormalizado) / 2.0;
                    mediasPeliculas.put(p, mediaFinal);
                }

                Arrays.sort(peliculas, (p1, p2) -> Double.compare(mediasPeliculas.get(p2), mediasPeliculas.get(p1)));

                System.out.println("a) Películas ordenadas por puntuación media (descendente):");
                for (Pelicula p : peliculas) {
                    System.out.println("- " + p.getTitulo() + " | Nota Media: " + String.format(Locale.US, "%.2f", mediasPeliculas.get(p)));
                }

                Set<String> generosDistintos = new TreeSet<>();
                for (Pelicula p : peliculas) {
                    if (p.getGeneros() != null) {
                        Collections.addAll(generosDistintos, p.getGeneros());
                    }
                }

                System.out.println("\nb) Géneros distintos ordenados alfabéticamente:");
                for (String genero : generosDistintos) {
                    System.out.println("- " + genero);
                }
            }
        } catch (IOException e) {
            System.err.println("Error procesando peliculas.json: " + e.getMessage());
        }
    }

    public Alumno[] parsearAlumnos(String fichero) throws IOException {
        Alumno[] alumnos = null;
        try (InputStream is = getClass().getResourceAsStream("/" + fichero)) {
            if (is == null) return null;
            JSONTokener tokener = new JSONTokener(is);
            JSONArray jsonArray = new JSONArray(tokener);
            alumnos = new Alumno[jsonArray.length()];
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObjectAlumno = jsonArray.getJSONObject(i);
                String id = jsonObjectAlumno.getString("id");
                String nombre = jsonObjectAlumno.getString("nombre");
                boolean matriculado = jsonObjectAlumno.getBoolean("matriculado");
                String fechaNacimientoStr = jsonObjectAlumno.getString("fechaNacimiento");
                LocalDate fechaNacimiento = LocalDate.parse(fechaNacimientoStr);
                JSONArray jsonArrayNotas = jsonObjectAlumno.getJSONArray("notas");
                Nota[] notas = new Nota[jsonArrayNotas.length()];
                for (int j = 0; j < jsonArrayNotas.length(); j++) {
                    JSONObject jsonObjectNota = jsonArrayNotas.getJSONObject(j);
                    String asignatura = jsonObjectNota.getString("asignatura");
                    double nota = jsonObjectNota.getDouble("nota");
                    notas[j] = new Nota(asignatura, nota);
                }
                alumnos[i] = new Alumno(id, nombre, matriculado, fechaNacimiento, notas);
            }
        }
        return alumnos;
    }

    public Producto[] parsearInventario(String fichero) throws IOException {
        Producto[] productos = null;
        try (InputStream is = getClass().getResourceAsStream("/" + fichero)) {
            if (is == null) return null;
            JSONTokener tokener = new JSONTokener(is);
            JSONArray jsonArray = new JSONArray(tokener);
            productos = new Producto[jsonArray.length()];
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject obj = jsonArray.getJSONObject(i);
                String nombre = obj.getString("nombre");
                String ubicacion = obj.getString("ubicacion");
                productos[i] = new Producto(nombre, ubicacion);
            }
        }
        return productos;
    }

    public Pelicula[] parsearPeliculas(String fichero) throws IOException {
        Pelicula[] peliculas = null;
        try (InputStream is = getClass().getResourceAsStream("/" + fichero)) {
            if (is == null) return null;
            JSONTokener tokener = new JSONTokener(is);
            JSONArray jsonArray = new JSONArray(tokener);
            peliculas = new Pelicula[jsonArray.length()];
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject obj = jsonArray.getJSONObject(i);
                String titulo = obj.getString("titulo");
                double imdb = obj.getDouble("imdb");
                double rt = obj.getDouble("rt");
                JSONArray jsonGeneros = obj.getJSONArray("generos");
                String[] generos = new String[jsonGeneros.length()];
                for (int j = 0; j < jsonGeneros.length(); j++) {
                    generos[j] = jsonGeneros.getString(j);
                }
                peliculas[i] = new Pelicula(titulo, generos, imdb, rt);
            }
        }
        return peliculas;
    }
}