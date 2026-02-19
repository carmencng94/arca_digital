package com.arcadigital;
import com.arcadigital.database.AnimalDAO;
import com.arcadigital.model.Animal;
import java.util.List;

public class Main {

    public static void main(String[] args) {
        System.out.println("--------------------------------------");
        System.out.println(" INICIANDO ARCA DIGITAL (BACKEND)");
        System.out.println("--------------------------------------");

        // 1. Creamos al trabajador (el DAO)
        AnimalDAO dao = new AnimalDAO();

        // 2. Le pedimos que nos traiga la lista de la base de datos
        System.out.println("Consultando base de datos...");
        List<Animal> lista = dao.listarTodos();

        // 3. Imprimimos los resultados
        if (lista.isEmpty()) {
            System.out.println("No hay animales en la base de datos o falló la conexión.");
        } else {
            System.out.println("¡ÉXITO! Se han encontrado " + lista.size() + " animales:");
            for (Animal a : lista) {
                // El nuevo método toString() en Animal se encarga del formato detallado.
                System.out.println(a);
            }
        }
    }
    /**
     * Convierte este animal en formato JSON manualmente.
     * Ejemplo: {"id": "JVM-1", "nombre": "Rex", ...}
     * Esto evita tener que descargar librerías externas complejas como Gson.
     
    public String toJson() {
        return String.format(
            "{\"id\": \"%s\", \"nombre\": \"%s\", \"especie\": \"%s\", \"raza\": \"%s\", \"edad\": \"%s\", \"genero\": \"%s\", \"estado\": \"%s\", \"descripcion\": \"%s\", \"imagenUrl\": \"%s\", \"fechaRegistro\": \"%s\"}",
            id, nombre, especie, raza, edad, genero, estado, 
            // Limpiamos saltos de línea en la descripción para no romper el JSON
            descripcion != null ? descripcion.replace("\n", " ") : "", 
            imagenUrl, fechaRegistro
        );
    }*/
}