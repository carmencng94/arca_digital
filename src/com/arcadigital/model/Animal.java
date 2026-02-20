package com.arcadigital.model;

// Clase que representa un animal en el sistema.
// Uso este objeto para pasar datos entre la base de datos, el
// servidor y el frontend. Si no existiera, tendría que manejar
// filas de SQL o JSON crudo todo el tiempo, lo cual sería un dolor.
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Base64;

public class Animal {

    private int id;
    private String nombre;
    private String especie;
    private String raza;
    private int edad;
    private String descripcion;
    private String medicacion;
    private boolean castrado;
    private String estado;
    private boolean urgente;
    private String fotoUrl;
    private Timestamp fechaIngreso;

    public Animal() {
        // Constructor vacío me permite instanciar el objeto y luego
        // llamar a los setters uno por uno. Lo necesito también cuando
        // convierto desde JSON con alguna biblioteca automática.
    }

    // Getters y Setters...
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getEspecie() { return especie; }
    public void setEspecie(String especie) { this.especie = especie; }
    public String getRaza() { return raza; }
    public void setRaza(String raza) { this.raza = raza; }
    public int getEdad() { return edad; }
    public void setEdad(int edad) { this.edad = edad; }
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    public String getMedicacion() { return medicacion; }
    public void setMedicacion(String medicacion) { this.medicacion = medicacion; }
    public boolean isCastrado() { return castrado; }
    public void setCastrado(boolean castrado) { this.castrado = castrado; }
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
    public boolean isUrgente() { return urgente; }
    public void setUrgente(boolean urgente) { this.urgente = urgente; }
    public String getFotoUrl() { return fotoUrl; }
    public void setFotoUrl(String fotoUrl) { this.fotoUrl = fotoUrl; }
    public Timestamp getFechaIngreso() { return fechaIngreso; }
    public void setFechaIngreso(Timestamp fechaIngreso) { this.fechaIngreso = fechaIngreso; }

    /**
     * Devuelve la fecha de ingreso formateada como "día/mes/año".
     * @return Fecha formateada o un string vacío si es nula.
     */
    public String getFechaIngresoFormateada() {
        // Devuelvo la fecha en formato legible por humanos.
        // Si no pongo este método, el frontend tendría que manejar
        // la conversión de Timestamp a texto, lo cual está fuera de su
        // responsabilidad.
        if (fechaIngreso == null) {
            return "N/A";
        }
        return new SimpleDateFormat("dd/MM/yyyy").format(fechaIngreso);
    }

    /**
     * Escapa un String para que sea un valor JSON válido.
     * @param value El string a escapar.
     * @return El string entre comillas y con caracteres especiales escapados.
     */
    private String escapeJson(String value) {
        // Este método me asegura que cualquier comilla o caracter raro
        // dentro de un campo no rompa el JSON cuando llamo a toJson().
        // Si lo quitara, un nombre como "O'Connor" podría generar
        // un JSON inválido y la app fallaría al parsearlo.
        if (value == null) {
            return "null";
        }
        return "\"" + value.replace("\\", "\\\\")
                           .replace("\"", "\\\"")
                           .replace("\b", "\\b")
                           .replace("\f", "\\f")
                           .replace("\n", "\\n")
                           .replace("\r", "\\r")
                           .replace("\t", "\\t") + "\"";
    }

    /**
     * Convierte el objeto a una cadena de texto en formato JSON de forma manual.
     * @return Un String con la representación JSON del objeto.
     */
    public String toJson() {
        // Construyo una cadena JSON a mano usando escapeJson para cada campo.
        // La uso en el API cuando devuelvo animales; si no tuviera este método
        // tendría que repetir esta construcción en otro lugar o añadir una
        // dependencia de librería.
        return new StringBuilder()
            .append("{")
            .append("\"id\": ").append(id).append(",")
            .append("\"nombre\": ").append(escapeJson(nombre)).append(",")
            .append("\"especie\": ").append(escapeJson(especie)).append(",")
            .append("\"raza\": ").append(escapeJson(raza)).append(",")
            .append("\"edad\": ").append(edad).append(",")
            .append("\"descripcion\": ").append(escapeJson(descripcion)).append(",")
            .append("\"medicacion\": ").append(escapeJson(medicacion)).append(",")
            .append("\"castrado\": ").append(castrado).append(",")
            .append("\"estado\": ").append(escapeJson(estado)).append(",")
            .append("\"urgente\": ").append(urgente).append(",")
            .append("\"fotoUrl\": ").append(escapeJson(fotoUrl)).append(",")
            .append("\"fechaIngreso\": ").append(escapeJson(getFechaIngresoFormateada()))
            .append("}")
            .toString();
    }
}
