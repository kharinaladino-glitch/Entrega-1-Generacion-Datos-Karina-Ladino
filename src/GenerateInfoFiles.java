import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;

/**
 * Genera archivos planos de prueba para el proyecto.
 * Los archivos contienen productos, vendedores y ventas.
 */
public class GenerateInfoFiles {

    static Random random = new Random();

    static String[] nombres = {
        "Ana", "Carlos", "Laura", "Juan", "Maria",
        "Andres", "Paula", "Diego", "Sofia", "Felipe"
    };

    static String[] apellidos = {
        "Gomez", "Lopez", "Rodriguez", "Martinez", "Torres",
        "Ramirez", "Castro", "Moreno", "Vargas", "Hernandez"
    };

    static String[] nombresProductos = {
        "Teclado", "Mouse", "Monitor", "Audifonos", "Camara",
        "Memoria USB", "Parlante", "Impresora", "Router", "Cargador"
    };

    static long[] idsVendedores;
    static String[] nombresVendedores;
    static int cantidadProductos;

    /**
     * Ejecuta la generacion de los archivos de prueba.
     */
    public static void main(String[] args) {
        try {
            createProductsFile(10);
            createSalesManInfoFile(5);

            for (int i = 0; i < idsVendedores.length; i++) {
                int cantidadVentas = 5 + random.nextInt(6);
                createSalesMenFile(cantidadVentas, nombresVendedores[i], idsVendedores[i]);
            }

            System.out.println("Archivos generados correctamente.");
        } catch (IOException e) {
            System.out.println("Ocurrio un error al generar los archivos.");
            System.out.println(e.getMessage());
        }
    }

    /**
     * Crea un archivo de ventas para un vendedor.
     *
     * @param randomSalesCount cantidad de ventas a generar
     * @param name nombre del vendedor
     * @param id numero de documento del vendedor
     */
    public static void createSalesMenFile(int randomSalesCount, String name, long id)
            throws IOException {

        String nombreArchivo = "ventas_" + id + "_" + name.replace(" ", "_") + ".txt";
        BufferedWriter archivo = new BufferedWriter(new FileWriter(nombreArchivo));

        archivo.write("CC;" + id);
        archivo.newLine();

        for (int i = 0; i < randomSalesCount; i++) {
            int idProducto = 1 + random.nextInt(cantidadProductos);
            int cantidadVendida = 1 + random.nextInt(10);

            archivo.write("P" + idProducto + ";" + cantidadVendida + ";");
            archivo.newLine();
        }

        archivo.close();
    }

    /**
     * Crea el archivo con la informacion de los productos.
     *
     * @param productsCount cantidad de productos a generar
     */
    public static void createProductsFile(int productsCount) throws IOException {
        cantidadProductos = productsCount;

        BufferedWriter archivo = new BufferedWriter(new FileWriter("productos.txt"));

        for (int i = 1; i <= productsCount; i++) {
            String nombreProducto;

            if (i <= nombresProductos.length) {
                nombreProducto = nombresProductos[i - 1];
            } else {
                nombreProducto = "Producto " + i;
            }

            int precio = 10000 + random.nextInt(190001);
            archivo.write("P" + i + ";" + nombreProducto + ";" + precio);
            archivo.newLine();
        }

        archivo.close();
    }

    /**
     * Crea el archivo con la informacion de los vendedores.
     *
     * @param salesmanCount cantidad de vendedores a generar
     */
    public static void createSalesManInfoFile(int salesmanCount) throws IOException {
        idsVendedores = new long[salesmanCount];
        nombresVendedores = new String[salesmanCount];

        BufferedWriter archivo = new BufferedWriter(new FileWriter("vendedores.txt"));

        for (int i = 0; i < salesmanCount; i++) {
            long id = 1000000000L + random.nextInt(900000000);
            String nombre = nombres[random.nextInt(nombres.length)];
            String apellido = apellidos[random.nextInt(apellidos.length)];

            idsVendedores[i] = id;
            nombresVendedores[i] = nombre + " " + apellido;

            archivo.write("CC;" + id + ";" + nombre + ";" + apellido);
            archivo.newLine();
        }

        archivo.close();
    }
}
