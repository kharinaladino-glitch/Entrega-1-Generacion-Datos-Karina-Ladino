import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

/**
 * Genera los datos de prueba. Continuacion de la clase construida en la Entrega 1.
 * Se ejecuta primero, sin solicitar datos al usuario.
 */
public class GenerateInfoFiles {
    private static final Path DATOS = Paths.get("datos");
    private static final Random AZAR = new Random();
    private static final List<Integer> IDS_PRODUCTOS = new ArrayList<Integer>();
    private static final List<VendedorPrueba> VENDEDORES = new ArrayList<VendedorPrueba>();
    private static final String[] NOMBRES = {"Ana", "Carlos", "Laura", "Daniel", "Sofia", "Miguel", "Valentina", "Andres"};
    private static final String[] APELLIDOS = {"Rodriguez", "Gomez", "Martinez", "Lopez", "Ramirez", "Moreno", "Vargas", "Torres"};
    private static final String[] PRODUCTOS = {"Teclado", "Mouse", "Monitor", "Audifonos", "Camara", "Memoria USB", "Disco SSD", "Cargador", "Parlante", "Router"};

    /** Ejecuta la generacion automatica de productos, vendedores y ventas. */
    public static void main(String[] args) {
        try {
            Files.createDirectories(DATOS);
            limpiarVentasAnteriores();
            createProductsFile(10);
            createSalesManInfoFile(6);
            for (VendedorPrueba vendedor : VENDEDORES) {
                createSalesMenFile(6, vendedor.nombre, vendedor.id);
            }
            System.out.println("Generacion exitosa: 10 productos, 6 vendedores y 6 archivos de ventas en 'datos'.");
        } catch (IOException | IllegalArgumentException e) {
            System.err.println("Error al generar archivos: " + e.getMessage());
            System.exit(1);
        }
    }

    /**
     * Crea ventas pseudoaleatorias asociadas a un vendedor existente.
     * @param randomSalesCount numero de registros de venta (no negativo)
     * @param name nombre del vendedor para el nombre del archivo
     * @param id documento del vendedor
     * @throws IOException si no se puede escribir el archivo
     */
    public static void createSalesMenFile(int randomSalesCount, String name, long id) throws IOException {
        if (randomSalesCount < 0 || name == null || name.trim().isEmpty() || id <= 0 || IDS_PRODUCTOS.isEmpty()) {
            throw new IllegalArgumentException("Parametros de vendedor invalidos o productos no generados.");
        }
        Files.createDirectories(DATOS);
        String nombreSeguro = name.trim().replaceAll("[^a-zA-Z0-9_-]+", "_");
        Path ruta = DATOS.resolve("ventas_" + id + "_" + nombreSeguro + ".txt");
        try (BufferedWriter writer = Files.newBufferedWriter(ruta, StandardCharsets.UTF_8)) {
            writer.write("CC;" + id);
            writer.newLine();
            for (int i = 0; i < randomSalesCount; i++) {
                int idProducto = IDS_PRODUCTOS.get(AZAR.nextInt(IDS_PRODUCTOS.size()));
                int cantidad = 1 + AZAR.nextInt(5);
                writer.write(idProducto + ";" + cantidad + ";");
                writer.newLine();
            }
        }
    }

    /**
     * Genera el catalogo de productos.
     * @param productsCount cantidad positiva de productos
     * @throws IOException si ocurre un error de escritura
     */
    public static void createProductsFile(int productsCount) throws IOException {
        if (productsCount <= 0) throw new IllegalArgumentException("Se necesita al menos un producto.");
        Files.createDirectories(DATOS);
        IDS_PRODUCTOS.clear();
        try (BufferedWriter writer = Files.newBufferedWriter(DATOS.resolve("productos.txt"), StandardCharsets.UTF_8)) {
            for (int i = 0; i < productsCount; i++) {
                int id = 1001 + i;
                IDS_PRODUCTOS.add(id);
                String nombre = PRODUCTOS[i % PRODUCTOS.length] + (i >= PRODUCTOS.length ? " " + (i + 1) : "");
                int precio = 20 + AZAR.nextInt(181);
                writer.write(id + ";" + nombre + ";" + precio + ".00");
                writer.newLine();
            }
        }
    }

    /**
     * Genera el archivo maestro de vendedores con documentos unicos.
     * @param salesmanCount cantidad no negativa de vendedores
     * @throws IOException si ocurre un error de escritura
     */
    public static void createSalesManInfoFile(int salesmanCount) throws IOException {
        if (salesmanCount < 0) throw new IllegalArgumentException("La cantidad no puede ser negativa.");
        Files.createDirectories(DATOS);
        VENDEDORES.clear();
        Set<Long> documentos = new HashSet<Long>();
        try (BufferedWriter writer = Files.newBufferedWriter(DATOS.resolve("vendedores.txt"), StandardCharsets.UTF_8)) {
            for (int i = 0; i < salesmanCount; i++) {
                long id;
                do { id = 10000000L + AZAR.nextInt(90000000); } while (!documentos.add(id));
                String nombre = NOMBRES[i % NOMBRES.length];
                String apellido = APELLIDOS[i % APELLIDOS.length];
                VENDEDORES.add(new VendedorPrueba(id, nombre + " " + apellido));
                writer.write("CC;" + id + ";" + nombre + ";" + apellido);
                writer.newLine();
            }
        }
    }

    private static void limpiarVentasAnteriores() throws IOException {
        try (java.nio.file.DirectoryStream<Path> archivos = Files.newDirectoryStream(DATOS, "ventas_*.txt")) {
            for (Path archivo : archivos) Files.delete(archivo);
        }
    }

    private static class VendedorPrueba {
        final long id;
        final String nombre;
        VendedorPrueba(long id, String nombre) { this.id = id; this.nombre = nombre; }
    }
}
