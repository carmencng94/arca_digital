package com.arcadigital.database;

// Aquí centralizo la configuración y creación de la conexión hacia
// MariaDB. Todos los demás clases llaman a ConexionDB.conectar() en
// vez de repetir estos datos.
// Si cambio la contraseña o la URL solamente modifico este archivo.
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Clase ConexionDB
 * Se encarga de establecer la comunicación técnica entre nuestra aplicación Java
 * y la base de datos MariaDB.
 */
public class ConexionDB {

    // DATOS DE CONEXIÓN
    // Definimos las constantes con la información de acceso.
    // URL: Protocolo (jdbc:mariadb) + Servidor (localhost) + Puerto (3306) + NombreBD (arca_digital)
    private static final String URL = "jdbc:mariadb://localhost:3306/arca_digital";
    private static final String USUARIO = "root";
    private static final String PASSWORD = "1234"; // Contraseña vacía por defecto en XAMPP/WAMP

    /**
     * Método conectar
     * Intenta establecer una conexión con la base de datos.
     * 
     * @return Connection Objeto que representa la conexión activa, o null si falla.
     */
    public static Connection conectar() {
        // Intento abrir la conexión paso a paso y atrapo errores para
        // que el resto de la aplicación no explote.
        Connection conexion = null;

        try {
            // 1. Cargar el Driver (Opcional en versiones nuevas de Java/JDBC, pero recomendado para aprender)
            // Esto le dice a Java: "Prepara la librería de MariaDB para usarse".
            Class.forName("org.mariadb.jdbc.Driver");

            // 2. Establecer la conexión
            // DriverManager es como el portero que usa nuestras credenciales para abrir la puerta.
            conexion = DriverManager.getConnection(URL, USUARIO, PASSWORD);

            System.out.println("¡Conexión a MariaDB exitosa!");

        } catch (ClassNotFoundException e) {
            // Error si no encuentra el archivo .jar en la carpeta lib
            System.err.println("Error: No se encontró el Driver JDBC de MariaDB.");
            e.printStackTrace();
        } catch (SQLException e) {
            // Error si la URL, usuario o contraseña están mal, o si la BD está apagada
            System.err.println("Error: Fallo al conectar con la base de datos.");
            e.printStackTrace();
        }

        // Devolveré null si no se pudo conectar, el código llamante debe
        // comprobarlo.
        return conexion;
    }
}
