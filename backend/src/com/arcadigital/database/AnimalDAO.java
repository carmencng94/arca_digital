package com.arcadigital.database;

import com.arcadigital.model.Animal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Clase AnimalDAO (Data Access Object)
 * Esta clase es el "trabajador". Su única función es realizar operaciones
 * de lectura y escritura (CRUD) sobre la tabla 'animales'.
 * Usa la ConexionDB para viajar a la base de datos.
 */
public class AnimalDAO {

    /**
     * Método listarTodos
     * Va a la base de datos, pide todos los animales y los devuelve como una lista de Java.
     * Es lo que usaremos para mostrar el catálogo a Marta.
     * * @return List<Animal> Una lista con todos los animales encontrados.
     */
    public List<Animal> listarTodos() {
        // Preparamos una lista vacía para ir llenándola
        List<Animal> listaAnimales = new ArrayList<>();
        
        // Esta es la orden SQL que queremos enviar
        String sql = "SELECT * FROM animales";

        // Usamos un bloque 'try-with-resources' (los paréntesis después del try).
        // Esto asegura que la conexión se cierre sola al terminar, evitando problemas.
        try (Connection conn = ConexionDB.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            // El ResultSet (rs) es como una hoja de cálculo con los resultados.
            // Recorremos fila por fila con un bucle while.
            while (rs.next()) {
                // 1. Extraemos los datos de la fila actual
                int id = rs.getInt("id");
                String nombre = rs.getString("nombre");
                String especie = rs.getString("especie");
                String raza = rs.getString("raza");
                int edad = rs.getInt("edad");
                String descripcion = rs.getString("descripcion");
                String estado = rs.getString("estado");
                boolean urgente = rs.getBoolean("urgente");
                String fotoUrl = rs.getString("foto_url");
                byte[] fotoBytes = rs.getBytes("foto");
                java.sql.Timestamp fechaIngreso = rs.getTimestamp("fecha_ingreso");

                // 2. Usamos el molde (Animal) para crear el objeto
                Animal animal = new Animal(id, nombre, especie, raza, edad, descripcion, estado, urgente, fotoUrl, fotoBytes, fechaIngreso);

                // 3. Lo añadimos a la lista
                listaAnimales.add(animal);
            }

        } catch (SQLException e) {
            System.err.println("Error al listar animales: " + e.getMessage());
        }

        // Devolvemos la lista llena (o vacía si hubo error)
        return listaAnimales;
    }

    /**
     * Inserta un nuevo animal en la base de datos.
     * Devuelve true si la operación tuvo éxito.
     */
    public boolean insertar(Animal a) {
        String sql = "INSERT INTO animales (id, nombre, especie, raza, edad, descripcion, estado, urgente, foto_url, foto) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = ConexionDB.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, a.getId());
            stmt.setString(2, a.getNombre());
            stmt.setString(3, a.getEspecie());
            stmt.setString(4, a.getRaza());
            stmt.setInt(5, a.getEdad());
            stmt.setString(6, a.getDescripcion());
            stmt.setString(7, a.getEstado());
            stmt.setBoolean(8, a.isUrgente());
            stmt.setString(9, a.getFotoUrl());
            stmt.setBytes(10, a.getFoto());

            int filas = stmt.executeUpdate();
            return filas > 0;
        } catch (SQLException e) {
            System.err.println("Error al insertar animal: " + e.getMessage());
            return false;
        }
    }

    /**
     * Actualiza la columna foto_url de un animal existente por su id.
     * Devuelve true si se actualizó al menos una fila.
     */
    public boolean actualizarFotoUrl(int id, String fotoUrl) {
        String sql = "UPDATE animales SET foto_url = ? WHERE id = ?";
        try (Connection conn = ConexionDB.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, fotoUrl);
            stmt.setInt(2, id);

            int filas = stmt.executeUpdate();
            // registrar en log del servidor si es posible
            // log directly using public helper
            com.arcadigital.api.ServidorAPI.log("[AnimalDAO] actualizarFotoUrl id=" + id + " filas=" + filas);
            return filas > 0;
        } catch (SQLException e) {
            System.err.println("Error al actualizar foto_url: " + e.getMessage());
            return false;
        }
    }
}