package com.antonipeiro.parsers.xml;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class MainXML {

    public static void main(String[] args) {
        MainXML main = new MainXML();

        System.out.println("=== 1. LISTADO SIMPLE DE EMPLEADOS ===");
        try {
            Empleado[] empleados = main.parsearEmpleados("empleados.xml");
            if (empleados != null) {
                for (Empleado e : empleados) {
                    System.out.println("ID: " + e.getId() + " | Nombre: " + e.getNombre() + " | Salario: " + e.getSalario() + " EUR");
                }

                System.out.println("\n=== 2. OBJETOS Y ESTADÍSTICAS POR DEPARTAMENTO ===");
                Map<String, List<Double>> salariosPorDep = new HashMap<>();
                for (Empleado e : empleados) {
                    salariosPorDep.computeIfAbsent(e.getDepartamento(), k -> new ArrayList<>()).add(e.getSalario());
                }

                System.out.printf("%-15s | %-15s | %-15s%n", "Departamento", "Nº empleados", "Salario medio");
                System.out.println("---------------------------------------------------------");
                for (Map.Entry<String, List<Double>> entry : salariosPorDep.entrySet()) {
                    String dep = entry.getKey();
                    List<Double> salarios = entry.getValue();
                    int numEmpleados = salarios.size();
                    double media = salarios.stream().mapToDouble(Double::doubleValue).average().orElse(0.0);
                    System.out.printf("%-15s | %-15d | %-15.2f%n", dep, numEmpleados, media);
                }
            }
        } catch (Exception e) {
            System.err.println("Error procesando empleados.xml: " + e.getMessage());
        }

        System.out.println("\n=== 3. BIBLIOTECA. CONSULTAS Y FILTRADO ===");
        try {
            Libro[] libros = main.parsearBiblioteca("biblioteca.xml");
            if (libros != null) {
                System.out.println("a) El título de todos los libros:");
                for (Libro l : libros) {
                    System.out.println("- " + l.getTitulo() + " (Autor: " + l.getAutor() + ")");
                }

                Map<String, Integer> conteoGeneros = new HashMap<>();
                for (Libro l : libros) {
                    if (l.getGeneros() != null) {
                        for (String genero : l.getGeneros()) {
                            conteoGeneros.put(genero, conteoGeneros.getOrDefault(genero, 0) + 1);
                        }
                    }
                }

                System.out.println("\nb) Contar cuántos libros hay de cada género:");
                for (Map.Entry<String, Integer> entry : conteoGeneros.entrySet()) {
                    System.out.println("- " + entry.getKey() + ": " + entry.getValue() + " libro(s)");
                }
            }
        } catch (Exception e) {
            System.err.println("Error procesando biblioteca.xml: " + e.getMessage());
        }

        System.out.println("\n=== 4. PEDIDOS. CONSULTA, VALIDACIÓN Y TOTALES ===");
        try {
            Pedido[] pedidos = main.parsearPedidos("pedidos.xml");
            if (pedidos != null) {
                Scanner scanner = new Scanner(System.in);
                System.out.print("Introduce el ID del pedido a consultar (ej. P-1001): ");
                String idBuscado = scanner.nextLine().trim();

                Pedido pedidoEncontrado = null;
                for (Pedido p : pedidos) {
                    if (p.getId().equalsIgnoreCase(idBuscado)) {
                        pedidoEncontrado = p;
                        break;
                    }
                }

                if (pedidoEncontrado != null) {
                    System.out.println("\na) Items del pedido " + pedidoEncontrado.getId() + " (Cliente: " + pedidoEncontrado.getNombreCliente() + "):");
                    for (ItemPedido item : pedidoEncontrado.getItems()) {
                        System.out.println("  - [" + item.getSku() + "] " + item.getDescripcion() +
                                " | Cantidad: " + item.getCantidad() +
                                " | Precio Unitario: " + item.getPrecioUnitario() + " EUR");
                    }

                    double sumaCalculada = 0.0;
                    for (ItemPedido item : pedidoEncontrado.getItems()) {
                        sumaCalculada += (item.getCantidad() * item.getPrecioUnitario());
                    }

                    System.out.println("\nb) Validación del Total del Pedido:");
                    System.out.println("  - Total calculado [Σ(cantidad × precioUnitario)]: " + String.format(Locale.US, "%.2f", sumaCalculada) + " EUR");
                    System.out.println("  - Total que figura en el XML: " + String.format(Locale.US, "%.2f", pedidoEncontrado.getTotalDeclarado()) + " EUR");

                    if (Math.abs(sumaCalculada - pedidoEncontrado.getTotalDeclarado()) < 0.01) {
                        System.out.println("  -> ¡Resultado: El total coincide correctamente con la suma de los items!");
                    } else {
                        System.out.println("  -> ¡Resultado: ATENCIÓN, el total difiere del cálculo de los items!");
                    }
                } else {
                    System.out.println("No se ha encontrado ningún pedido con el ID: " + idBuscado);
                }
            }
        } catch (Exception e) {
            System.err.println("Error procesando pedidos.xml: " + e.getMessage());
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

    public Empleado[] parsearEmpleados(String fichero) throws Exception {
        Empleado[] empleados = null;
        try (InputStream is = abrirFichero(fichero)) {
            if (is == null) return null;
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc = db.parse(is);
            doc.getDocumentElement().normalize();

            NodeList nodeList = doc.getElementsByTagName("empleado");
            empleados = new Empleado[nodeList.getLength()];

            for (int i = 0; i < nodeList.getLength(); i++) {
                Element elem = (Element) nodeList.item(i);
                String id = elem.getAttribute("id");
                String nombre = elem.getElementsByTagName("nombre").item(0).getTextContent();
                String departamento = elem.getElementsByTagName("departamento").item(0).getTextContent();
                double salario = Double.parseDouble(elem.getElementsByTagName("salario").item(0).getTextContent());

                empleados[i] = new Empleado(id, nombre, departamento, salario);
            }
        }
        return empleados;
    }

    public Libro[] parsearBiblioteca(String fichero) throws Exception {
        Libro[] libros = null;
        try (InputStream is = abrirFichero(fichero)) {
            if (is == null) return null;
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc = db.parse(is);
            doc.getDocumentElement().normalize();

            NodeList nodeList = doc.getElementsByTagName("libro");
            libros = new Libro[nodeList.getLength()];

            for (int i = 0; i < nodeList.getLength(); i++) {
                Element elem = (Element) nodeList.item(i);
                String isbn = elem.getAttribute("isbn");
                String titulo = elem.getElementsByTagName("titulo").item(0).getTextContent();

                Element autorElem = (Element) elem.getElementsByTagName("autor").item(0);
                String autor = autorElem.getElementsByTagName("nombre").item(0).getTextContent();

                int anio = Integer.parseInt(elem.getElementsByTagName("anio").item(0).getTextContent());
                boolean disponible = Boolean.parseBoolean(elem.getElementsByTagName("disponible").item(0).getTextContent());

                NodeList generoNodes = elem.getElementsByTagName("genero");
                String[] generos = new String[generoNodes.getLength()];
                for (int j = 0; j < generoNodes.getLength(); j++) {
                    generos[j] = generoNodes.item(j).getTextContent();
                }

                libros[i] = new Libro(isbn, titulo, autor, anio, generos, disponible);
            }
        }
        return libros;
    }

    public Pedido[] parsearPedidos(String fichero) throws Exception {
        Pedido[] pedidos = null;
        try (InputStream is = abrirFichero(fichero)) {
            if (is == null) return null;
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc = db.parse(is);
            doc.getDocumentElement().normalize();

            NodeList pedidoNodes = doc.getElementsByTagName("pedido");
            pedidos = new Pedido[pedidoNodes.getLength()];

            for (int i = 0; i < pedidoNodes.getLength(); i++) {
                Element pedidoElem = (Element) pedidoNodes.item(i);
                String id = pedidoElem.getAttribute("id");

                Element clienteElem = (Element) pedidoElem.getElementsByTagName("cliente").item(0);
                String nombreCliente = clienteElem.getElementsByTagName("nombre").item(0).getTextContent();

                String fecha = pedidoElem.getElementsByTagName("fecha").item(0).getTextContent();
                double totalDeclarado = Double.parseDouble(pedidoElem.getElementsByTagName("total").item(0).getTextContent());

                NodeList itemNodes = pedidoElem.getElementsByTagName("item");
                ItemPedido[] items = new ItemPedido[itemNodes.getLength()];

                for (int j = 0; j < itemNodes.getLength(); j++) {
                    Element itemElem = (Element) itemNodes.item(j);
                    String sku = itemElem.getAttribute("sku");
                    String descripcion = itemElem.getElementsByTagName("descripcion").item(0).getTextContent();
                    int cantidad = Integer.parseInt(itemElem.getElementsByTagName("cantidad").item(0).getTextContent());
                    double precioUnitario = Double.parseDouble(itemElem.getElementsByTagName("precioUnitario").item(0).getTextContent());

                    items[j] = new ItemPedido(sku, descripcion, cantidad, precioUnitario);
                }

                pedidos[i] = new Pedido(id, nombreCliente, fecha, items, totalDeclarado);
            }
        }
        return pedidos;
    }
}