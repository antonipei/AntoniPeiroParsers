package com.antonipeiro.parsers.json;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.*;

public class MainJSON {

    public static void main(String[] args) {
        MainJSON main = new MainJSON();

        // 5. Listado de alumnos
        System.out.println("=== 5. LISTADO DE ALUMNOS ===");
        try {
            Alumno[] alumnos = main.parsearAlumnos("alumnos.json");
            if (alumnos != null) {
                for (Alumno a : alumnos) {
                    System.out.println("Alumno: " + a.getNombre() + " | Fecha de nacimiento: " + a.getFechaNacimiento());
                }

                // 6. Objetos y estadísticas
                System.out.println("\n=== 6. OBJETOS Y ESTADÍSTICAS ===");
                System.out.println("a) Asignatura con la nota más alta por alumno:");
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
                    System.out.println("\nb) Alumno con la nota media más alta: " + mejorAlumno.getNombre() + " (Media: " + String.format(Locale.US, "%.2f", maxMedia) + ")");
                }
            }
        } catch (Exception e) {
            System.err.println("Error procesando alumnos.json: " + e.getMessage());
        }

        // 7. Consulta de inventario
        System.out.println("\n=== 7. CONSULTA DE INVENTARIO ===");
        try {
            Producto[] inventario = main.parsearInventario("inventario.json");
            if (inventario != null) {
                Scanner scanner = new Scanner(System.in);
                System.out.print("Introduce el nombre del producto a buscar: ");
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
        } catch (Exception e) {
            System.err.println("Error procesando inventario.json: " + e.getMessage());
        }

        // 8. Películas
        System.out.println("\n=== 8. PELÍCULAS ===");
        try {
            Pelicula[] peliculas = main.parsearPeliculas("peliculas.json");
            if (peliculas != null) {
                Map<Pelicula, Double> mediasPeliculas = new HashMap<>();
                for (Pelicula p : peliculas) {
                    double rtNormalizado = p.getRottenTomatoes() / 10.0; // Escala 1-100 a 1-10
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
        } catch (Exception e) {
            System.err.println("Error procesando peliculas.json: " + e.getMessage());
        }
    }

    private InputStream abrirFichero(String fichero) throws IOException {
        InputStream is = getClass().getResourceAsStream("/" + fichero);
        if (is != null) return is;

        Path path = Path.of(fichero);
        if (Files.exists(path)) {
            return Files.newInputStream(path);
        }
        throw new IOException("No se encuentra el fichero: " + fichero);
    }

    public Alumno[] parsearAlumnos(String fichero) throws IOException {
        Alumno[] alumnos = null;
        try (InputStream is = abrirFichero(fichero)) {
            JSONTokener tokener = new JSONTokener(is);
            JSONArray jsonArray = new JSONArray(tokener);
            alumnos = new Alumno[jsonArray.length()];
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObjectAlumno = jsonArray.getJSONObject(i);
                String id = jsonObjectAlumno.optString("id", "");
                String nombre = jsonObjectAlumno.getString("nombre");
                boolean matriculado = jsonObjectAlumno.optBoolean("matriculado", true);
                String fechaNacimientoStr = jsonObjectAlumno.optString("fechaNacimiento", jsonObjectAlumno.optString("fecha_nacimiento", "2000-01-01"));
                LocalDate fechaNacimiento = LocalDate.parse(fechaNacimientoStr);

                JSONArray jsonArrayNotas = jsonObjectAlumno.getJSONArray("notas");
                Nota[] notas = new Nota[jsonArrayNotas.length()];
                for (int j = 0; j < jsonArrayNotas.length(); j++) {
                    JSONObject jsonObjectNota = jsonArrayNotas.getJSONObject(j);
                    String asignatura = jsonObjectNota.getString("asignatura");
                    double nota = jsonObjectNota.optDouble("nota", jsonObjectNota.optDouble("valor", 0.0));
                    notas[j] = new Nota(asignatura, nota);
                }
                alumnos[i] = new Alumno(id, nombre, matriculado, fechaNacimiento, notas);
            }
        }
        return alumnos;
    }

    public Producto[] parsearInventario(String fichero) throws IOException {
        Producto[] productos = null;
        try (InputStream is = abrirFichero(fichero)) {
            JSONTokener tokener = new JSONTokener(is);
            JSONObject rootObj = new JSONObject(tokener); // Raíz es un objeto JSON
            JSONArray jsonArray = rootObj.getJSONArray("productos"); // Obtenemos el array interno

            productos = new Producto[jsonArray.length()];
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject obj = jsonArray.getJSONObject(i);
                String id = obj.getString("id");
                String nombre = obj.getString("nombre");
                int stock = obj.getInt("stock");
                double precio = obj.getDouble("precio");

                JSONArray tagsArr = obj.getJSONArray("tags");
                String[] tags = new String[tagsArr.length()];
                for (int t = 0; t < tagsArr.length(); t++) {
                    tags[t] = tagsArr.getString(t);
                }

                JSONObject ubObj = obj.getJSONObject("ubicacion"); // Objeto anidado de ubicación
                int pasillo = ubObj.getInt("pasillo");
                String estante = ubObj.getString("estante");
                Ubicacion ubicacion = new Ubicacion(pasillo, estante);

                productos[i] = new Producto(id, nombre, stock, precio, tags, ubicacion);
            }
        }
        return productos;
    }

    public Pelicula[] parsearPeliculas(String fichero) throws IOException {
        Pelicula[] peliculas = null;
        try (InputStream is = abrirFichero(fichero)) {
            JSONTokener tokener = new JSONTokener(is);
            JSONObject rootObj = new JSONObject(tokener); // Raíz es un objeto JSON
            JSONArray jsonArray = rootObj.getJSONArray("peliculas"); // Obtenemos el array interno

            peliculas = new Pelicula[jsonArray.length()];
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject obj = jsonArray.getJSONObject(i);
                String id = obj.getString("id");
                String titulo = obj.getString("titulo");
                String director = obj.getString("director");
                int estreno = obj.getInt("estreno");
                int duracionMin = obj.getInt("duracionMin");

                JSONArray jsonGeneros = obj.getJSONArray("generos");
                String[] generos = new String[jsonGeneros.length()];
                for (int j = 0; j < jsonGeneros.length(); j++) {
                    generos[j] = jsonGeneros.getString(j);
                }

                JSONObject puntuacionesObj = obj.getJSONObject("puntuaciones"); // Objeto anidado de puntuaciones
                double imdb = puntuacionesObj.getDouble("imdb");
                double rt = puntuacionesObj.getDouble("rt");

                peliculas[i] = new Pelicula(id, titulo, director, estreno, duracionMin, generos, imdb, rt);
            }
        }
        return peliculas;
    }
}