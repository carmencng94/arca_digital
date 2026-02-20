package com.arcadigital.api;

// En este archivo gestiono el pequeño servidor HTTP que sirve mi
// frontend estático y expone la API REST para los animales de la Arca.
// Lo escribí con la biblioteca nativa de Java para no depender de
// frameworks externos y así entender bien cada parte que hace falta.
// Si quitara este archivo, la aplicación no tendría ni interfaz web
// ni forma de hablar con la base de datos.

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

    // En la función main preparo el servidor, agrego los distintos
    // contextos (rutas) y arranco la instancia.
    // Lo hago paso a paso para que se vea claro cómo se asocian los
    // manejadores (handlers) a cada URL.
    // Si no ejecutara este método, no habría forma de acceder a la
    // app desde el navegador.

    public static void main(String[] args) throws IOException {
        int puerto = 8080;
        HttpServer server = HttpServer.create(new InetSocketAddress(puerto), 0);

        System.out.println("Servidor Arca Digital iniciado en http://localhost:" + puerto);

        // Aquí le digo al servidor qué clase se encarga de atender cada
        // tipo de petición. Cada vez que alguien pide "/algo" el server
        // crea un objeto HttpExchange y lo pasa al handler correspondiente.
        // Sin estos contextos el servidor retornaría 404 para todo.

        // === MANEJADOR DE ARCHIVOS ESTÁTICOS ===
        // Sirve los archivos principales del frontend (HTML, CSS, JS) y las imágenes.
        server.createContext("/", new StaticFileHandler());
        
        // === MANEJADOR DE LA API DE ANIMALES ===
        // Gestiona las operaciones CRUD para los animales.
        server.createContext("/api/animales", new AnimalApiHandler());

        // === MANEJADOR PARA SUBIDA DE ARCHIVOS ===
        // Recibe imágenes que sube el navegador y las guarda en disco.
        server.createContext("/api/upload", new UploadHandler());
        
        // === MANEJADOR PARA EL LOGIN (SIMULADO) ===
        // Comprueba un usuario y contraseña fijos para demostrar control de acceso.
        server.createContext("/api/login", new LoginHandler());

        server.setExecutor(null); // Usar el gestor de hilos por defecto.
        server.start();
    }

    /**
     * Handler para servir archivos estáticos del Frontend.
     */
    static class StaticFileHandler implements HttpHandler {
        // Ruta base donde están los archivos HTML/CSS/JS que uso en el navegador.
        private static final String PROJECT_ROOT = System.getProperty("user.dir");
        
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            // Primero saco la ruta de la petición, por ejemplo "/index.html".
            // Si viene solo "/" lo cambio porque el archivo real se llama index.html.
            String path = exchange.getRequestURI().getPath();
            if (path.equals("/")) path = "/index.html"; // Redirigir la raíz a index.html

            // Construyo la ruta completa en el disco a partir del directorio del proyecto.
            String filePath = PROJECT_ROOT + File.separator + "resources" + File.separator + "frontend" + path;
            File file = new File(filePath);

            if (file.exists() && !file.isDirectory()) {
                // El navegador necesita saber qué tipo de archivo es para
                // mostrarlo bien; aquí lo determino mirando la extensión.
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
                    // Copio el contenido del archivo directamente al flujo de salida.
                    Files.copy(file.toPath(), os);
                }
            } else {
                // Si el archivo no existe retorno un 404 al navegador.
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
            // Abro los permisos CORS para que el frontend (que corre en otra
            // pestaña o puerto) pueda hablar con esta API sin que el
            // navegador bloquee la petición.
            exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
            exchange.getResponseHeaders().set("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
            exchange.getResponseHeaders().set("Access-Control-Allow-Headers", "Content-Type, Authorization");

            // Esta rama se activa cuando el navegador pregunta "¿puedo enviar cierto tipo de petición?" antes de hacerlo. No hago más
            // porque sólo quiero aprobar o rechazar.
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
                    // Aquí convierto la lista de objetos en un String JSON.
                    String json = lista.stream().map(Animal::toJson).collect(Collectors.joining(",", "[", "]"));
                    sendResponse(exchange, 200, json);

                } else if ("POST".equals(method)) {
                    // --- CREAR UN NUEVO ANIMAL ---
                    // Leo el cuerpo de la petición como texto y lo parseo.
                    String requestBody = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
                    Map<String, String> params = parseJsonBody(requestBody);
                    
                    // Verifico que al menos venga el nombre, si no respondo error.
                    if (!params.containsKey("nombre") || params.get("nombre").trim().isEmpty()) {
                        sendResponse(exchange, 400, "{\"error\":\"El campo 'nombre' es requerido\"}");
                        return;
                    }

                    // Relleno un objeto Animal con los valores del mapa.
                    Animal nuevo = new Animal();
                    nuevo.setNombre(params.get("nombre"));
                    nuevo.setEspecie(params.getOrDefault("especie", "Otro"));
                    nuevo.setRaza(params.getOrDefault("raza", ""));
                    nuevo.setEdad(Integer.parseInt(params.getOrDefault("edad", "0")));
                    nuevo.setDescripcion(params.getOrDefault("descripcion", ""));
                    nuevo.setMedicacion(params.getOrDefault("medicacion", null));
                    nuevo.setCastrado(Boolean.parseBoolean(params.getOrDefault("castrado", "false")));
                    nuevo.setEstado(params.getOrDefault("estado", "RESCATADO"));
                    nuevo.setUrgente(Boolean.parseBoolean(params.getOrDefault("urgente", "false")));
                    nuevo.setFotoUrl(params.getOrDefault("fotoUrl", "/img/rex.png")); // Asignar fotoUrl con defecto
                    
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
                    // Si llega un método que no espero, aviso con 405.
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
            // Permitir CORS para que la app JS pueda subir archivos.
            exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
            exchange.getResponseHeaders().set("Access-Control-Allow-Methods", "POST, OPTIONS");
            exchange.getResponseHeaders().set("Access-Control-Allow-Headers", "Content-Type, X-Filename");

            if ("OPTIONS".equals(exchange.getRequestMethod())) {
                // Respondo al pre-flight sin cuerpo.
                exchange.sendResponseHeaders(204, -1);
                return;
            }

            if ("POST".equals(exchange.getRequestMethod())) {
                try {
                    // Primera línea: leo el nombre original que mandó el cliente.
                    String filename = exchange.getRequestHeaders().getFirst("X-Filename");
                    if (filename == null) throw new IOException("Cabecera X-Filename es requerida.");

                    // Combino el timestamp con el nombre para que sea único.
                    String uniqueFilename = System.currentTimeMillis() + "_" + filename.replaceAll("[^a-zA-Z0-9.-]", "_");
                    File uploadDir = new File("resources/frontend/img");
                    if (!uploadDir.exists()) uploadDir.mkdirs();
                    File targetFile = new File(uploadDir, uniqueFilename);

                    // Copio los bytes de la petición al archivo físico.
                    Files.copy(exchange.getRequestBody(), targetFile.toPath());
                    
                    // Respondo con la ruta que luego usará el cliente en el campo fotoUrl.
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
             // Necesito CORS porque llamado desde el navegador.
            exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
            exchange.getResponseHeaders().set("Access-Control-Allow-Methods", "POST, OPTIONS");
            exchange.getResponseHeaders().set("Access-Control-Allow-Headers", "Content-Type");

            if ("OPTIONS".equals(exchange.getRequestMethod())) {
                exchange.sendResponseHeaders(204, -1);
                return;
            }
            if ("POST".equals(exchange.getRequestMethod())) {
                // Leo el cuerpo JSON de la petición y extraigo usuario/clave.
                String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
                Map<String, String> params = parseJsonBody(body);
                String username = params.get("username");
                String password = params.get("password");

                // Comparo con dos cuentas fijas: admin y voluntario.
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
    // Método de utilidad que centraliza la creación de respuestas en JSON.
    // Lo uso para no repetir código en todos los handlers.
    // Si no existiera, tendría que escribir las mismas líneas una y otra vez.
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
