import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Procesa los archivos de ventas y construye los reportes finales.
 * Se ejecuta despues de GenerateInfoFiles, sin solicitar datos al usuario.
 * El nombre de la clase se mantiene como "main" por exigencia de la guia.
 */
public class main {
    private static final Path DATOS = Paths.get("datos");
    private static final Path REPORTES = Paths.get("reportes");

    /** Punto de entrada al procesamiento de datos y generacion de CSV. */
    public static void main(String[] args) {
        try {
            Map<Integer, Producto> productos = leerProductos();
            Map<String, Vendedor> vendedores = leerVendedores();
            int archivos = procesarVentas(productos, vendedores);
            Files.createDirectories(REPORTES);
            generarReporteVendedores(vendedores);
            generarReporteProductos(productos);
            System.out.println("Proceso exitoso. Archivos de ventas leidos: " + archivos
                    + ". Reportes creados en 'reportes'.");
        } catch (IOException | IllegalArgumentException | ArithmeticException e) {
            System.err.println("No se generaron los reportes: " + e.getMessage());
            System.exit(1);
        }
    }

    private static Map<Integer, Producto> leerProductos() throws IOException {
        Map<Integer, Producto> productos = new HashMap<Integer, Producto>();
        Path ruta = DATOS.resolve("productos.txt");
        try (BufferedReader reader = Files.newBufferedReader(ruta, StandardCharsets.UTF_8)) {
            String linea;
            int numero = 0;
            while ((linea = reader.readLine()) != null) {
                numero++;
                if (linea.trim().isEmpty()) throw error(ruta, numero, "Linea vacia");
                String[] campos = linea.split(";", -1);
                if (campos.length != 3 || campos[1].trim().isEmpty()) throw error(ruta, numero, "Formato de producto invalido");
                int id = enteroPositivo(campos[0], ruta, numero, "ID de producto");
                BigDecimal precio = precioValido(campos[2], ruta, numero);
                if (productos.put(id, new Producto(id, campos[1].trim(), precio)) != null)
                    throw error(ruta, numero, "ID de producto duplicado: " + id);
            }
        }
        if (productos.isEmpty()) throw new IOException("El archivo de productos esta vacio.");
        return productos;
    }

    private static Map<String, Vendedor> leerVendedores() throws IOException {
        Map<String, Vendedor> vendedores = new HashMap<String, Vendedor>();
        Path ruta = DATOS.resolve("vendedores.txt");
        try (BufferedReader reader = Files.newBufferedReader(ruta, StandardCharsets.UTF_8)) {
            String linea;
            int numero = 0;
            while ((linea = reader.readLine()) != null) {
                numero++;
                String[] campos = linea.split(";", -1);
                if (campos.length != 4 || campos[0].trim().isEmpty() || campos[1].trim().isEmpty()
                        || campos[2].trim().isEmpty() || campos[3].trim().isEmpty())
                    throw error(ruta, numero, "Formato de vendedor invalido");
                String clave = campos[0].trim() + ";" + campos[1].trim();
                Vendedor vendedor = new Vendedor(campos[2].trim() + " " + campos[3].trim());
                if (vendedores.put(clave, vendedor) != null)
                    throw error(ruta, numero, "Documento de vendedor duplicado: " + clave);
            }
        }
        if (vendedores.isEmpty()) throw new IOException("El archivo de vendedores esta vacio.");
        return vendedores;
    }

    /** Permite acumular varios archivos del mismo vendedor. */
    private static int procesarVentas(Map<Integer, Producto> productos, Map<String, Vendedor> vendedores)
            throws IOException {
        int cantidadArchivos = 0;
        List<Path> rutas = new ArrayList<Path>();
        try (DirectoryStream<Path> archivos = Files.newDirectoryStream(DATOS, "ventas_*.txt")) {
            for (Path archivo : archivos) if (Files.isRegularFile(archivo)) rutas.add(archivo);
        }
        Collections.sort(rutas);
        if (rutas.isEmpty()) throw new IOException("No se encontraron archivos de ventas en datos.");
        for (Path ruta : rutas) {
            procesarArchivoVenta(ruta, productos, vendedores);
            cantidadArchivos++;
        }
        return cantidadArchivos;
    }

    private static void procesarArchivoVenta(Path ruta, Map<Integer, Producto> productos,
                                              Map<String, Vendedor> vendedores) throws IOException {
        try (BufferedReader reader = Files.newBufferedReader(ruta, StandardCharsets.UTF_8)) {
            String encabezado = reader.readLine();
            if (encabezado == null) throw error(ruta, 1, "Archivo de ventas vacio");
            String[] identificacion = encabezado.split(";", -1);
            if (identificacion.length != 2 || identificacion[0].trim().isEmpty() || identificacion[1].trim().isEmpty())
                throw error(ruta, 1, "Encabezado de vendedor invalido");
            String clave = identificacion[0].trim() + ";" + identificacion[1].trim();
            Vendedor vendedor = vendedores.get(clave);
            if (vendedor == null) throw error(ruta, 1, "Vendedor desconocido: " + clave);
            String linea;
            int numero = 1;
            while ((linea = reader.readLine()) != null) {
                numero++;
                String[] campos = linea.split(";", -1);
                // El punto y coma final de cada venta se admite tal como figura en la guia.
                if (!((campos.length == 3 && campos[2].isEmpty()) || campos.length == 2))
                    throw error(ruta, numero, "Formato de venta invalido");
                int id = enteroPositivo(campos[0], ruta, numero, "ID de producto");
                int cantidad = enteroPositivo(campos[1], ruta, numero, "Cantidad");
                Producto producto = productos.get(id);
                if (producto == null) throw error(ruta, numero, "Producto no registrado: " + id);
                producto.cantidadVendida = Math.addExact(producto.cantidadVendida, cantidad);
                vendedor.total = vendedor.total.add(producto.precio.multiply(BigDecimal.valueOf(cantidad)));
            }
        }
    }

    private static void generarReporteVendedores(Map<String, Vendedor> vendedores) throws IOException {
        List<Vendedor> lista = new ArrayList<Vendedor>(vendedores.values());
        Collections.sort(lista, new Comparator<Vendedor>() {
            public int compare(Vendedor a, Vendedor b) {
                int resultado = b.total.compareTo(a.total);
                return resultado != 0 ? resultado : a.nombre.compareTo(b.nombre);
            }
        });
        try (BufferedWriter writer = Files.newBufferedWriter(REPORTES.resolve("reporte_vendedores.csv"), StandardCharsets.UTF_8)) {
            for (Vendedor v : lista) {
                writer.write(v.nombre + ";" + v.total.setScale(2).toPlainString());
                writer.newLine();
            }
        }
    }

    private static void generarReporteProductos(Map<Integer, Producto> productos) throws IOException {
        List<Producto> lista = new ArrayList<Producto>(productos.values());
        Collections.sort(lista, new Comparator<Producto>() {
            public int compare(Producto a, Producto b) {
                int resultado = Integer.compare(b.cantidadVendida, a.cantidadVendida);
                return resultado != 0 ? resultado : Integer.compare(a.id, b.id);
            }
        });
        try (BufferedWriter writer = Files.newBufferedWriter(REPORTES.resolve("reporte_productos.csv"), StandardCharsets.UTF_8)) {
            for (Producto p : lista) {
                if (p.cantidadVendida > 0) {
                    // Se conserva el formato exacto pedido: nombre;precio.
                    writer.write(p.nombre + ";" + p.precio.setScale(2).toPlainString());
                    writer.newLine();
                }
            }
        }
    }

    private static int enteroPositivo(String valor, Path ruta, int linea, String campo) throws IOException {
        try {
            int numero = Integer.parseInt(valor.trim());
            if (numero <= 0) throw new NumberFormatException();
            return numero;
        } catch (NumberFormatException e) {
            throw error(ruta, linea, campo + " debe ser entero positivo: " + valor);
        }
    }

    private static BigDecimal precioValido(String valor, Path ruta, int linea) throws IOException {
        try {
            BigDecimal precio = new BigDecimal(valor.trim());
            if (precio.signum() < 0 || precio.scale() > 2) throw new NumberFormatException();
            return precio.setScale(2);
        } catch (NumberFormatException e) {
            throw error(ruta, linea, "Precio invalido: " + valor);
        }
    }

    private static IOException error(Path ruta, int linea, String detalle) {
        return new IOException(ruta + " (linea " + linea + "): " + detalle);
    }

    private static class Producto {
        final int id;
        final String nombre;
        final BigDecimal precio;
        int cantidadVendida;
        Producto(int id, String nombre, BigDecimal precio) {
            this.id = id; this.nombre = nombre; this.precio = precio;
        }
    }

    private static class Vendedor {
        final String nombre;
        BigDecimal total = BigDecimal.ZERO;
        Vendedor(String nombre) { this.nombre = nombre; }
    }
}
