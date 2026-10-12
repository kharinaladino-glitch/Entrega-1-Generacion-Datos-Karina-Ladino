# Generacion y clasificacion de datos - Entrega 3

Proyecto individual. Continuacion de las entregas 1 y 2 de Conceptos Fundamentales de Programacion.

## Requisitos
- Java 8
- Eclipse IDE para Java Developers
- No usa librerias externas

## Abrir en Eclipse
1. Descomprimir el ZIP.
2. Eclipse > File > Import > General > Existing Projects into Workspace.
3. Seleccionar la carpeta `Entrega_3_Final` y finalizar.
4. Revisar Project > Properties > Java Compiler: compliance level 1.8.
5. Ejecutar primero `GenerateInfoFiles.java` como Java Application.
6. Ejecutar despues `main.java` como Java Application.
7. Actualizar (Refresh) el proyecto y abrir `reportes/reporte_vendedores.csv` y `reportes/reporte_productos.csv`.

IMPORTANTE: ejecutar desde el directorio raiz del proyecto de Eclipse, ya que las rutas relativas son `datos` y `reportes`.

## Estructura
- `src/GenerateInfoFiles.java`: crea productos, vendedores y archivos de ventas.
- `src/main.java`: valida y procesa las ventas y escribe los reportes CSV.
- `datos/`: archivos de prueba generados automaticamente.
- `reportes/`: resultados generados por `main`.
- `conslusion.txt`: aprendizajes, aplicacion profesional y dificultades, con el nombre textual solicitado en la guia.
- `PRUEBAS.txt`: resultados de pruebas efectuadas antes de entregar.

## Reportes
- `reporte_vendedores.csv`: nombre;valor recaudado, ordenado de mayor a menor.
- `reporte_productos.csv`: nombre;precio, ordenado por cantidad total vendida de mayor a menor. La cantidad se utiliza para ordenar pero se conserva el formato de columnas solicitado por la guia.

## Validaciones y mejora opcional
Se detectan registros incompletos, precios o cantidades invalidas, productos y vendedores desconocidos, documentos duplicados y archivos vacios. Se permite procesar mas de un archivo por vendedor. Los valores monetarios se calculan con BigDecimal.

## Repositorio
Subir la carpeta del proyecto a un repositorio accesible de GitHub o Bitbucket y compartir su URL al entregar. Esta copia local no publica automaticamente los archivos.
