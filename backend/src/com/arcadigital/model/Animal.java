package com.arcadigital.model;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;

/**
 * Clase Animal
 * Representa la entidad 'Animal' dentro del sistema Arca Digital.
 * Esta clase sirve como molde para crear objetos que contienen la información
 * de cada animal que gestionará la protectora.
 */
public class Animal {

    // ATRIBUTOS (Campos)
    // Se declaran 'private' para proteger los datos (Encapsulamiento).
    // Solo se puede acceder a ellos mediante los métodos get y set.
    private int id;
    private String nombre;
    private String especie;
    private String raza;
    private int edad;
    private String descripcion;
    private String estado;
    private boolean urgente;
    private String fotoUrl;
    private byte[] foto;
    private Timestamp fechaIngreso;

    /**
     * Constructor Vacío
     * Es necesario para que ciertas librerías o herramientas puedan instanciar
     * la clase sin pasar datos iniciales.
     */
    public Animal() {
    }

    /**
     * Constructor Completo
     * Permite crear un objeto Animal con todos sus datos iniciales.
     */
    public Animal(int id, String nombre, String especie, String raza, int edad, String descripcion, String estado, boolean urgente, String fotoUrl, Timestamp fechaIngreso) {
        this.id = id;
        this.nombre = nombre;
        this.especie = especie;
        this.raza = raza;
        this.edad = edad;
        this.descripcion = descripcion;
        this.estado = estado;
        this.urgente = urgente;
        this.fotoUrl = fotoUrl;
        this.fechaIngreso = fechaIngreso;
    }

    /**
     * Constructor que incluye foto en bytes.
     */
    public Animal(int id, String nombre, String especie, String raza, int edad, String descripcion, String estado, boolean urgente, String fotoUrl, byte[] foto, Timestamp fechaIngreso) {
        this.id = id;
        this.nombre = nombre;
        this.especie = especie;
        this.raza = raza;
        this.edad = edad;
        this.descripcion = descripcion;
        this.estado = estado;
        this.urgente = urgente;
        this.fotoUrl = fotoUrl;
        this.foto = foto;
        this.fechaIngreso = fechaIngreso;
    }

    // MÉTODOS GETTERS Y SETTERS

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getEspecie() {
        return especie;
    }

    public void setEspecie(String especie) {
        this.especie = especie;
    }

    public String getRaza() {
        return raza;
    }

    public void setRaza(String raza) {
        this.raza = raza;
    }

    public int getEdad() {
        return edad;
    }

    public void setEdad(int edad) {
        this.edad = edad;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public boolean isUrgente() {
        return urgente;
    }

    public void setUrgente(boolean urgente) {
        this.urgente = urgente;
    }

    public String getFotoUrl() {
        return fotoUrl;
    }

    public void setFotoUrl(String fotoUrl) {
        this.fotoUrl = fotoUrl;
    }

    public byte[] getFoto() {
        return foto;
    }

    public void setFoto(byte[] foto) {
        this.foto = foto;
    }

    public Timestamp getFechaIngreso() {
        return fechaIngreso;
    }

    public void setFechaIngreso(Timestamp fechaIngreso) {
        this.fechaIngreso = fechaIngreso;
    }
    
    // --- Métodos de utilidad ---
    
    /**
     * Devuelve la fecha de ingreso formateada como "día/mes/año".
     * @return Fecha formateada o un string vacío si es nula.
     */
    public String getFechaIngresoFormateada() {
        if (fechaIngreso == null) {
            return "";
        }
        // Convierte el Timestamp a un formato legible
        return new SimpleDateFormat("dd/MM/yyyy").format(fechaIngreso);
    }

    /**
     * Método toString
     * Convierte el objeto a una cadena de texto con formato profesional.
     */
    @Override
    public String toString() {
        return "FICHA DE ANIMAL\n" +
               "----------------------------------\n" +
               "ID:         " + id + "\n" +
               "Nombre:     " + nombre + "\n" +
               "Especie:    " + especie + "\n" +
               "Raza:       " + raza + "\n" +
               "Edad:       " + edad + " años\n" +
               "Estado:     " + estado + "\n" +
               "Urgente:    " + (urgente ? "Sí" : "No") + "\n" +
               "Ingreso:    " + getFechaIngresoFormateada() + "\n" +
               "Descripción:" + descripcion + "\n" +
               "Foto:       " + fotoUrl + "\n" +
               "----------------------------------";
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
        // Reemplaza caracteres especiales para que el JSON sea válido
        return "\"" + value.replace("\\", "\\\\")
                           .replace("\"", "\\\"")
                           .replace("\b", "\\b")
                           .replace("\f", "\\f")
                           .replace("\n", "\\n")
                           .replace("\r", "\\r")
                           .replace("\t", "\\t") + "\"";
    }

    /**
     * Convierte el objeto a una cadena de texto en formato JSON.
     * @return Un String con la representación JSON del objeto.
     */
            public String toJson() {
        // Construimos el JSON paso a paso para poder incluir la imagen en Base64
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        sb.append("\"id\": ").append(id).append(",");
        sb.append("\"nombre\": ").append(escapeJson(nombre)).append(",");
        sb.append("\"especie\": ").append(escapeJson(especie)).append(",");
        sb.append("\"raza\": ").append(escapeJson(raza)).append(",");
        sb.append("\"edad\": ").append(edad).append(",");
        sb.append("\"descripcion\": ").append(escapeJson(descripcion)).append(",");
        sb.append("\"estado\": ").append(escapeJson(estado)).append(",");
        sb.append("\"urgente\": ").append(urgente).append(",");
        sb.append("\"fotoUrl\": ").append(escapeJson(fotoUrl)).append(",");
        sb.append("\"fotoBase64\": ")
          .append(foto != null ? escapeJson(java.util.Base64.getEncoder().encodeToString(foto)) : "null").append(",");
        sb.append("\"fechaIngreso\": ").append(escapeJson(getFechaIngresoFormateada()));
        sb.append("}");
        return sb.toString();
    }
}


