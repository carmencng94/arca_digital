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
}
