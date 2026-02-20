# Animal.java - Modelo de Datos Completo

```java
package com.arcadigital.model;

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
        // Constructor vacío es útil para librerías de serialización y para crear objetos paso a paso.
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
```

## Cambios principales:

- **Nuevo campo `medicacion`**: TEXT - Almacena los medicamentos del animal
- **Nuevo campo `castrado`**: BOOLEAN - Indica si el animal ha sido castrado/esterilizado
- **Getters y Setters**: `getMedicacion()`, `setMedicacion()`, `isCastrado()`, `setCastrado()`
- **toJson()**: Actualizado para incluir ambos campos en la serialización JSON
