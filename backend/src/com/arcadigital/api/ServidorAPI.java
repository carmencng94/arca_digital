package com.arcadigital.api;

import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;
import com.arcadigital.database.AnimalDAO;
import com.arcadigital.model.Animal;

import java.io.IOException;
import java.io.OutputStream;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.stream.Collectors;

/**
 * ServidorAPI principal de Arca Digital.
 * Construido con el servidor HTTP nativo de Java, sin frameworks externos.
 * Gestiona el servicio de archivos estáticos y los endpoints de la API.
 */
public class ServidorAPI {

    public static void main(String[] args) throws IOException {
        int puerto = 8080;
        HttpServer server = HttpServer.create(new InetSocketAddress(puerto), 0);

        System.out.println("Servidor Arca Digital iniciado en http://localhost:" + puerto);

        // === MANEJADOR DE ARCHIVOS ESTÁTICOS ===
        // Sirve los archivos principales del frontend (HTML, CSS, JS) y las imágenes.
        server.createContext("/", new StaticFileHandler());
        
        // === MANEJADOR DE LA API DE ANIMALES ===
        // Gestiona las operaciones CRUD para los animales.
        server.createContext("/api/animales", new AnimalApiHandler());

        // === MANEJADOR PARA SUBIDA DE ARCHIVOS ===
        server.createContext("/api/upload", new UploadHandler());
        
        // === MANEJADOR PARA EL LOGIN (SIMULADO) ===
        server.createContext("/api/login", new LoginHandler());

        server.setExecutor(null); // Usar el gestor de hilos por defecto.
        server.start();
    }

    /**
     * Handler para servir archivos estáticos del Frontend.
     */
    static class StaticFileHandler implements HttpHandler {
        private static final String PROJECT_ROOT = System.getProperty("user.dir");
        
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String path = exchange.getRequestURI().getPath();
            System.out.println("[StaticFileHandler] Petición recibida para: " + path);
            if (path.equals("/")) path = "/index.html"; // Redirigir la raíz a index.html

            String filePath = PROJECT_ROOT + File.separator + "Frontend" + path;
            System.out.println("[StaticFileHandler] Intentando servir archivo: " + filePath);
            File file = new File(filePath);

            if (file.exists() && !file.isDirectory()) {
                System.out.println("[StaticFileHandler] Archivo encontrado. Sirviendo...");
                // Determinar el tipo MIME basado en la extensión del archivo.
                String mimeType = "application/octet-stream"; // Tipo por defecto.
                if (path.endsWith(".html")) mimeType = "text/html; charset=UTF-8";
                else if (path.endsWith(".css")) mimeType = "text/css; charset=UTF-8";
                else if (path.endsWith(".js")) mimeType = "application/javascript; charset=UTF-8";
                else if (path.endsWith(".png")) mimeType = "image/png";
                else if (path.endsWith(".jpg") || path.endsWith(".jpeg")) mimeType = "image/jpeg";
                else if (path.endsWith(".gif")) mimeType = "image/gif";
                
                exchange.getResponseHeaders().set("Content-Type", mimeType);
                exchange.sendResponseHeaders(200, file.length());
                try (OutputStream os = exchange.getResponseBody()) {
                    Files.copy(file.toPath(), os);
                }
            } else {
                System.out.println("[StaticFileHandler] ADVERTENCIA: Archivo no encontrado. Devolviendo 404.");
                // Si el archivo no se encuentra, enviar un error 404.
                String response = "404 Not Found";
                exchange.sendResponseHeaders(404, response.getBytes().length);
                try (OutputStream os = exchange.getResponseBody()) {
                    os.write(response.getBytes());
                }
            }
        }
    }

    /**
     * Handler para la API REST de Animales (/api/animales).
     */
    static class AnimalApiHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            // Configurar cabeceras para CORS, permitiendo el acceso desde cualquier origen.
            exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
            exchange.getResponseHeaders().set("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
            exchange.getResponseHeaders().set("Access-Control-Allow-Headers", "Content-Type");

            // El navegador envía una petición OPTIONS (pre-flight) para verificar permisos CORS.
            if ("OPTIONS".equals(exchange.getRequestMethod())) {
                exchange.sendResponseHeaders(204, -1); // 204 No Content
                return;
            }

            String method = exchange.getRequestMethod();
            AnimalDAO dao = new AnimalDAO();
            
            try {
                if ("GET".equals(method)) {
                    // --- OBTENER TODOS LOS ANIMALES ---
                    List<Animal> lista = dao.listarTodos();
                    // Construir el JSON manualmente.
                    String json = lista.stream().map(Animal::toJson).collect(Collectors.joining(",", "[", "]"));
                    sendResponse(exchange, 200, json);

                } else if ("POST".equals(method)) {
                    // --- CREAR UN NUEVO ANIMAL ---
                    String requestBody = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
                    Map<String, String> params = parseJsonBody(requestBody);
                    
                    Animal nuevo = new Animal();
                    nuevo.setNombre(params.get("nombre"));
                    nuevo.setEspecie(params.get("especie"));
                    nuevo.setRaza(params.get("raza"));
                    nuevo.setEdad(Integer.parseInt(params.getOrDefault("edad", "0")));
                    nuevo.setDescripcion(params.get("descripcion"));
                    nuevo.setEstado(params.get("estado"));
                    nuevo.setUrgente(Boolean.parseBoolean(params.getOrDefault("urgente", "false")));
                    
                    Animal insertado = dao.insertar(nuevo);
                    if (insertado != null) {
                        sendResponse(exchange, 201, insertado.toJson()); // 201 Created
                    } else {
                        throw new IOException("No se pudo crear el animal en la base de datos.");
                    }
                
                } else if ("PUT".equals(method)) {
                    // --- ACTUALIZAR FOTO DE UN ANIMAL ---
                    String requestBody = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
                    Map<String, String> params = parseJsonBody(requestBody);
                    
                    int id = Integer.parseInt(params.get("id"));
                    String fotoUrl = params.get("fotoUrl");
                    
                    if (dao.actualizarFotoUrl(id, fotoUrl)) {
                        sendResponse(exchange, 200, "{\"status\":\"ok\", \"message\":\"Foto actualizada\"}");
                    } else {
                        throw new IOException("No se encontró el animal con ID " + id + " para actualizar.");
                    }

                } else if ("DELETE".equals(method)) {
                    // --- ELIMINAR UN ANIMAL ---
                    String path = exchange.getRequestURI().getPath();
                    int id = Integer.parseInt(path.substring(path.lastIndexOf('/') + 1));
                    
                    if (dao.eliminar(id)) {
                        sendResponse(exchange, 200, "{\"status\":\"ok\", \"message\":\"Animal eliminado\"}");
                    } else {
                        throw new IOException("No se pudo eliminar el animal con ID " + id);
                    }
                } else {
                    sendResponse(exchange, 405, "{\"error\":\"Método no permitido\"}"); // 405 Method Not Allowed
                }
            } catch (Exception e) {
                sendResponse(exchange, 500, "{\"error\":\"Error interno del servidor: " + e.getMessage() + "\"}");
            }
        }
    }
    
    /**
     * Handler para subir imágenes.
     */
    static class UploadHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
            exchange.getResponseHeaders().set("Access-Control-Allow-Methods", "POST, OPTIONS");
            exchange.getResponseHeaders().set("Access-Control-Allow-Headers", "Content-Type, X-Filename");

            if ("OPTIONS".equals(exchange.getRequestMethod())) {
                exchange.sendResponseHeaders(204, -1);
                return;
            }

            if ("POST".equals(exchange.getRequestMethod())) {
                try {
                    String filename = exchange.getRequestHeaders().getFirst("X-Filename");
                    if (filename == null) throw new IOException("Cabecera X-Filename es requerida.");

                    // Crear un nombre de archivo único para evitar sobreescrituras.
                    String uniqueFilename = System.currentTimeMillis() + "_" + filename.replaceAll("[^a-zA-Z0-9.-]", "_");
                    File targetFile = new File("Frontend/img", uniqueFilename);

                    // Guardar el cuerpo de la petición (el archivo) en el disco.
                    Files.copy(exchange.getRequestBody(), targetFile.toPath());
                    
                    // Devolver la ruta relativa del archivo guardado.
                    String responseJson = "{\"fotoUrl\": \"/img/" + uniqueFilename + "\"}";
                    sendResponse(exchange, 201, responseJson);

                } catch (Exception e) {
                    sendResponse(exchange, 500, "{\"error\":\"Fallo al subir el archivo: " + e.getMessage() + "\"}");
                }
            }
        }
    }
    
    /**
     * Handler para el login (simulado).
     */
    static class LoginHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
             // Configurar cabeceras CORS
            exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
            exchange.getResponseHeaders().set("Access-Control-Allow-Methods", "POST, OPTIONS");
            exchange.getResponseHeaders().set("Access-Control-Allow-Headers", "Content-Type");

            if ("OPTIONS".equals(exchange.getRequestMethod())) {
                exchange.sendResponseHeaders(204, -1);
                return;
            }
            if ("POST".equals(exchange.getRequestMethod())) {
                String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
                Map<String, String> params = parseJsonBody(body);
                String username = params.get("username");
                String password = params.get("password");

                if (("admin".equals(username) && "admin123".equals(password)) ||
                    ("voluntario".equals(username) && "voluntario123".equals(password))) {
                    String resp = "{\"status\":\"ok\", \"user\": {\"username\": \"" + username + "\"}}";
                    sendResponse(exchange, 200, resp);
                } else {
                    sendResponse(exchange, 401, "{\"error\":\"Usuario o contraseña incorrectos\"}");
                }
            }
        }
    }

    // --- MÉTODOS DE UTILIDAD ---

    /**
     * Envía una respuesta HTTP con un código de estado y un cuerpo en formato JSON.
     */
    private static void sendResponse(HttpExchange exchange, int statusCode, String jsonResponse) throws IOException {
        exchange.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");
        byte[] responseBytes = jsonResponse.getBytes(StandardCharsets.UTF_8);
        exchange.sendResponseHeaders(statusCode, responseBytes.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(responseBytes);
        }
    }

    /**
     * Parsea un cuerpo de petición JSON simple en un mapa de clave-valor.
     * Limitado a JSON de un solo nivel y sin objetos anidados.
     */
    private static Map<String, String> parseJsonBody(String body) {
        Map<String, String> map = new HashMap<>();
        String trimmedBody = body.trim();
        if (trimmedBody.startsWith("{") && trimmedBody.endsWith("}")) {
            String content = trimmedBody.substring(1, trimmedBody.length() - 1);
            String[] pairs = content.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)"); // Regex para split por comas fuera de comillas
            for (String pair : pairs) {
                String[] kv = pair.split(":", 2);
                if (kv.length == 2) {
                    String key = kv[0].trim().replaceAll("^\"|\"$", "");
                    String value = kv[1].trim();
                    if (value.startsWith("\"") && value.endsWith("\"")) {
                         value = value.substring(1, value.length() - 1);
                    }
                    map.put(key, value);
                }
            }
        }
        return map;
    }
}
