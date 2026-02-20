package com.arcadigital.database;

import com.arcadigital.model.Animal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * Esta clase es el DAO (Objeto de Acceso a Datos). 
 * Su única misión es hablar con la base de datos MariaDB para 
 * leer, guardar o borrar animales.
 */
public class AnimalDAO {

    /**
     * Este método sirve para traer todos los animales que tenemos guardados.
     * Devuelve una Lista de objetos Animal.
     */
    public List<Animal> listarTodos() {
        // Creamos una lista vacía para ir guardando los animales que encontremos.
        List<Animal> animales = new ArrayList<>();
        
        // Esta es la orden que le enviamos a la base de datos.
        // Le pedimos todo (*) de la tabla animales, ordenado por fecha.
        String sql = "SELECT * FROM animales ORDER BY fecha_ingreso DESC";

        // Usamos try-with-resources. Esto abre la conexión y se asegura de cerrarla
        // sola al terminar, para no dejar la "puerta abierta" y gastar memoria.
        try (Connection conn = ConexionDB.conectar();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            // El ResultSet (rs) es como un puntero que va fila por fila.
            // Mientras haya una fila siguiente (rs.next), seguimos trabajando.
            while (rs.next()) {
                // Creamos un "molde" vacío de un animal.
                Animal animal = new Animal();
                
                // Sacamos los datos de las columnas de la tabla y los guardamos en el objeto.
                animal.setId(rs.getInt("id"));
                animal.setNombre(rs.getString("nombre"));
                animal.setEspecie(rs.getString("especie"));
                animal.setRaza(rs.getString("raza"));
                animal.setEdad(rs.getInt("edad"));
                animal.setDescripcion(rs.getString("descripcion"));
                animal.setMedicacion(rs.getString("medicacion"));
                animal.setCastrado(rs.getBoolean("castrado"));
                animal.setEstado(rs.getString("estado"));
                animal.setUrgente(rs.getBoolean("urgente"));
                animal.setFotoUrl(rs.getString("foto_url"));
                animal.setFechaIngreso(rs.getTimestamp("fecha_ingreso"));
                
                // Una vez que el animal tiene todos sus datos, lo metemos en nuestra lista.
                animales.add(animal);
            }
        } catch (SQLException e) {
            // Si algo sale mal (ej: la base de datos está apagada), avisamos del error.
            System.err.println("Error al listar los animales: " + e.getMessage());
        }

        // ¡IMPORTANTE! Aquí es donde estaba el error. El return debe ir 
        // después de cerrar el bloque try-catch, pero antes de cerrar el método.
        return animales;
    }

    /**
     * Este método sirve para guardar un animal nuevo que enviamos desde el formulario.
     */
    public Animal insertar(Animal animal) {
        // Preparamos la orden de insertar. Usamos signos de interrogación (?) por seguridad.
        // Esto evita que hackers puedan enviarnos código malicioso por los campos de texto.
        String sql = "INSERT INTO animales (nombre, especie, raza, edad, descripcion, medicacion, castrado, estado, urgente, foto_url) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = ConexionDB.conectar();
             // RETURN_GENERATED_KEYS sirve para que la base de datos nos diga qué ID le puso al animal.
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            // Sustituimos los signos de interrogación por los datos reales del animal.
            stmt.setString(1, animal.getNombre());
            stmt.setString(2, animal.getEspecie());
            stmt.setString(3, animal.getRaza());
            stmt.setInt(4, animal.getEdad());
            stmt.setString(5, animal.getDescripcion());
            stmt.setString(6, animal.getMedicacion());
            stmt.setBoolean(7, animal.isCastrado());
            stmt.setString(8, animal.getEstado());
            stmt.setBoolean(9, animal.isUrgente());
            // Si el animal no tiene foto, le ponemos una por defecto.
            stmt.setString(10, animal.getFotoUrl() != null ? animal.getFotoUrl() : "/img/rex.png");

            // Ejecutamos la orden. executeUpdate devuelve cuántas filas se crearon.
            int filasAfectadas = stmt.executeUpdate();

            if (filasAfectadas > 0) {
                // Si se guardó bien, le pedimos a la base de datos el ID autogenerado.
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        // Le ponemos ese ID a nuestro objeto animal.
                        animal.setId(generatedKeys.getInt(1));
                        return animal; 
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al insertar el animal: " + e.getMessage());
        }
        return null;
    }

    /**
     * Este método sirve para cambiar la foto de un animal que ya existe.
     */
    public boolean actualizarFotoUrl(int id, String nuevaRuta) {
        String sql = "UPDATE animales SET foto_url = ? WHERE id = ?";
        try (Connection conn = ConexionDB.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, nuevaRuta);
            stmt.setInt(2, id);

            int filasAfectadas = stmt.executeUpdate();
            // Si filasAfectadas es mayor a 0, significa que se encontró al animal y se cambió su foto.
            return filasAfectadas > 0;
        } catch (SQLException e) {
            System.err.println("Error al actualizar la foto: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Este método sirve para borrar un animal permanentemente de la lista.
     */
    public boolean eliminar(int id) {
        String sql = "DELETE FROM animales WHERE id = ?";
        try (Connection conn = ConexionDB.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            int filasAfectadas = stmt.executeUpdate();
            return filasAfectadas > 0;
            
        } catch (SQLException e) {
            System.err.println("Error al eliminar el animal: " + e.getMessage());
            return false;
        }
    }
}