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
 * DAO (Data Access Object) para la entidad Animal.
 * Esta clase encapsula toda la lógica de acceso a la base de datos para los animales,
 * manteniendo el código SQL aislado del resto de la aplicación.
 */
public class AnimalDAO {

    /**
     * Recupera todos los animales de la base de datos.
     * @return Una lista de objetos Animal. La lista estará vacía si no hay animales o si ocurre un error.
     */
    public List<Animal> listarTodos() {
        List<Animal> animales = new ArrayList<>();
        // Consulta SQL para seleccionar todos los campos de la tabla.
        String sql = "SELECT * FROM animales ORDER BY fecha_ingreso DESC";

        // El bloque try-with-resources asegura que la conexión y los recursos se cierren automáticamente.
        try (Connection conn = ConexionDB.conectar();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            // Itera sobre cada fila del resultado.
            while (rs.next()) {
                // Crea un objeto Animal a partir de los datos de la fila.
                Animal animal = new Animal();
                animal.setId(rs.getInt("id"));
                animal.setNombre(rs.getString("nombre"));
                animal.setEspecie(rs.getString("especie"));
                animal.setRaza(rs.getString("raza"));
                animal.setEdad(rs.getInt("edad"));
                animal.setDescripcion(rs.getString("descripcion"));
                animal.setEstado(rs.getString("estado"));
                animal.setUrgente(rs.getBoolean("urgente"));
                animal.setFotoUrl(rs.getString("foto_url"));
                animal.setFechaIngreso(rs.getTimestamp("fecha_ingreso"));
                
                // Añade el objeto a la lista.
                animales.add(animal);
            }
        } catch (SQLException e) {
            // En un entorno de producción, aquí se registraría el error en un sistema de logging.
            System.err.println("Error al listar los animales: " + e.getMessage());
        }
        return animales;
    }

    /**
     * Inserta un nuevo animal en la base de datos.
     * La foto_url no se incluye, ya que la base de datos le asignará el valor por defecto.
     * @param animal El objeto Animal con los datos a insertar. El ID se ignora ya que es autoincremental.
     * @return El objeto Animal insertado, ahora con el ID generado por la base de datos, o null si falla.
     */
    public Animal insertar(Animal animal) {
        String sql = "INSERT INTO animales (nombre, especie, raza, edad, descripcion, estado, urgente) VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = ConexionDB.conectar();
             // Statement.RETURN_GENERATED_KEYS le pide a la BD que devuelva el ID creado.
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setString(1, animal.getNombre());
            stmt.setString(2, animal.getEspecie());
            stmt.setString(3, animal.getRaza());
            stmt.setInt(4, animal.getEdad());
            stmt.setString(5, animal.getDescripcion());
            stmt.setString(6, animal.getEstado());
            stmt.setBoolean(7, animal.isUrgente());

            int filasAfectadas = stmt.executeUpdate();

            if (filasAfectadas > 0) {
                // Recupera el ID generado.
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        animal.setId(generatedKeys.getInt(1));
                        return animal; // Devuelve el animal con su nuevo ID.
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al insertar el animal: " + e.getMessage());
        }
        return null; // Devuelve null si la inserción falla.
    }

    /**
     * Actualiza la URL de la foto de un animal específico.
     * @param id El ID del animal a actualizar.
     * @param nuevaRuta La nueva ruta relativa de la imagen (ej: /img/12345_foto.jpg).
     * @return true si la actualización fue exitosa, false en caso contrario.
     */
    public boolean actualizarFotoUrl(int id, String nuevaRuta) {
        String sql = "UPDATE animales SET foto_url = ? WHERE id = ?";
        try (Connection conn = ConexionDB.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, nuevaRuta);
            stmt.setInt(2, id);

            int filasAfectadas = stmt.executeUpdate();
            return filasAfectadas > 0; // Si se actualizó al menos una fila, devuelve true.
        } catch (SQLException e) {
            System.err.println("Error al actualizar la foto_url: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Elimina un animal de la base de datos por su ID.
     * @param id El ID del animal a eliminar.
     * @return true si la eliminación fue exitosa, false en caso contrario.
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
