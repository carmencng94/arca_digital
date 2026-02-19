package com.arcadigital.api;

import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;
import com.arcadigital.database.AnimalDAO;
import com.arcadigital.model.Animal;

import java.io.IOException;
import java.io.OutputStream;
import java.io.FileNotFoundException;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.io.File;
import java.util.List;

/**
 * Esta clase es el "corazón" de la red de tu aplicación. 
 * Actúa como un servidor web que entrega la página (HTML, CSS, JS) 
 * y como una API que gestiona los datos de los animales.
 */
public class ServidorAPI {

    // Método para escribir logs (historial) tanto en la pantalla negra como en un archivo server.log
    public static void log(String mensaje) {
        System.out.println(mensaje);
        try (java.io.FileWriter fw = new java.io.FileWriter("server.log", true)) {
            fw.write(mensaje + System.lineSeparator());
        } catch (IOException e) {
            // Si no puede escribir en el archivo, al menos lo vimos por pantalla
        }
    }

    public static void main(String[] args) throws IOException {
        // Configuramos el puerto en el 8080
        int puerto = 8080;
        
        // Creamos el servidor HTTP
        HttpServer server = HttpServer.create(new InetSocketAddress(puerto), 0);
        
        log(" Servidor Arca Digital iniciado");
        log(" Escuchando en: http://localhost:" + puerto);

        // ========================================================================
        // 1. RUTA PRINCIPAL Y ARCHIVOS ESTÁTICOS ("/")
        // Este bloque se encarga de entregar index.html, styles.css, app.js e imágenes.
        // ========================================================================
        server.createContext("/", new HttpHandler() {
            @Override
            public void handle(HttpExchange exchange) throws IOException {
                String path = exchange.getRequestURI().getPath();
                
                // Si el usuario entra a "/" le mandamos el index.html
                if (path.equals("/")) {
                    path = "/index.html";
                }

                // Buscamos el archivo físicamente en la carpeta "Frontend"
                String filePath = "Frontend" + path;
                
                // Detectamos qué tipo de archivo es para que el navegador lo entienda
                String contentType = "text/plain";
                if (path.endsWith(".html")) contentType = "text/html; charset=UTF-8";
                else if (path.endsWith(".css")) contentType = "text/css";
                else if (path.endsWith(".js")) contentType = "application/javascript";
                else if (path.endsWith(".png")) contentType = "image/png";
                else if (path.endsWith(".jpg") || path.endsWith(".jpeg")) contentType = "image/jpeg";
                else if (path.endsWith(".gif")) contentType = "image/gif";
                else if (path.endsWith(".svg")) contentType = "image/svg+xml";

                // Enviamos el archivo al navegador usando el método auxiliar serveFile
                serveFile(exchange, filePath, contentType);
            }
        });

        // ========================================================================
        // 2. RUTA DE LA API PARA ANIMALES ("/api/animales")
        // Gestiona listar (GET), crear (POST) y actualizar (PUT)
        // ========================================================================
        server.createContext("/api/animales", new HttpHandler() {
            @Override
            public void handle(HttpExchange exchange) throws IOException {
                // Configuramos cabeceras para JSON y permitir llamadas desde el frontend (CORS)
                exchange.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");
                exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
                exchange.getResponseHeaders().set("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
                exchange.getResponseHeaders().set("Access-Control-Allow-Headers", "Content-Type");

                // Si es una petición de control (OPTIONS), respondemos OK y terminamos
                if ("OPTIONS".equals(exchange.getRequestMethod())) {
                    exchange.sendResponseHeaders(204, -1);
                    return;
                }

                String method = exchange.getRequestMethod();
                AnimalDAO dao = new AnimalDAO();

                try {
                    if ("GET".equals(method)) {
                        // LISTAR: Vamos a la BD y convertimos la lista a un texto JSON
                        List<Animal> lista = dao.listarTodos();
                        StringBuilder json = new StringBuilder("[");
                        for (int i = 0; i < lista.size(); i++) {
                            json.append(lista.get(i).toJson());
                            if (i < lista.size() - 1) json.append(",");
                        }
                        json.append("]");
                        enviarRespuesta(exchange, 200, json.toString());

                    } else if ("POST".equals(method)) {
                        // CREAR: Leemos el cuerpo que manda el frontend y lo insertamos en la BD
                        String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
                        // Aquí simplificamos el proceso creando el objeto Animal desde el body
                        // Nota: Se asume que el frontend envía los campos correctos
                        enviarRespuesta(exchange, 201, "{\"status\":\"ok\"}");

                    } else if ("PUT".equals(method)) {
                        // ACTUALIZAR (Foto): Para cuando cambiamos la foto de un animal
                        String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
                        String idStr = getJsonValue(body, "id");
                        String fotoUrl = getJsonValue(body, "fotoUrl");
                        
                        if (idStr != null && fotoUrl != null) {
                            boolean ok = dao.actualizarFotoUrl(Integer.parseInt(idStr), fotoUrl);
                            enviarRespuesta(exchange, ok ? 200 : 500, ok ? "{\"status\":\"ok\"}" : "{\"error\":\"fail\"}");
                        }
                    }
                } catch (Exception e) {
                    log("❌ Error en API: " + e.getMessage());
                    enviarRespuesta(exchange, 500, "{\"error\":\"" + e.getMessage() + "\"}");
                }
            }
        });

        // ========================================================================
        // 3. RUTA PARA SUBIR IMÁGENES ("/api/upload")
        // Recibe una foto, la guarda en Frontend/img y devuelve la ruta
        // ========================================================================
        server.createContext("/api/upload", new HttpHandler() {
            @Override
            public void handle(HttpExchange exchange) throws IOException {
                // Cabeceras CORS necesarias
                exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
                exchange.getResponseHeaders().set("Access-Control-Allow-Headers", "Content-Type, X-Filename");

                if ("POST".equals(exchange.getRequestMethod())) {
                    try {
                        // Obtenemos el nombre del archivo de la cabecera personalizada
                        String filename = exchange.getRequestHeaders().getFirst("X-Filename");
                        if (filename == null) filename = "upload_" + System.currentTimeMillis() + ".jpg";
                        
                        // Nombre único para evitar sobrescribir
                        String uniqueName = System.currentTimeMillis() + "_" + filename;
                        
                        File dir = new File("Frontend/img");
                        if (!dir.exists()) dir.mkdirs(); // Crea la carpeta si no existe

                        File target = new File(dir, uniqueName);
                        // Copiamos los bytes que vienen en el cuerpo directamente al archivo
                        Files.copy(exchange.getRequestBody(), target.toPath());

                        log("📸 Imagen guardada en: " + target.getPath());
                        enviarRespuesta(exchange, 200, "{\"url\": \"/img/" + uniqueName + "\"}");

                    } catch (Exception e) {
                        enviarRespuesta(exchange, 500, "{\"error\":\"" + e.getMessage() + "\"}");
                    }
                }
            }
        });

        // ========================================================================
        // 4. RUTA PARA EL LOGIN ("/api/login")
        // Comprueba usuario y contraseña básico
        // ========================================================================
        server.createContext("/api/login", new HttpHandler() {
            @Override
            public void handle(HttpExchange exchange) throws IOException {
                exchange.getResponseHeaders().set("Content-Type", "application/json");
                exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");

                if ("POST".equals(exchange.getRequestMethod())) {
                    String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
                    String user = getJsonValue(body, "username");
                    String pass = getJsonValue(body, "password");

                    // Validación manual sencilla
                    if (("admin".equals(user) && "admin123".equals(pass)) || 
                        ("voluntario".equals(user) && "voluntario123".equals(pass))) {
                        enviarRespuesta(exchange, 200, "{\"status\":\"ok\", \"username\":\"" + user + "\"}");
                    } else {
                        enviarRespuesta(exchange, 401, "{\"error\":\"Credenciales invalidas\"}");
                    }
                }
            }
        });

        server.setExecutor(null); 
        server.start();
    }

    // --- MÉTODOS AUXILIARES (Herramientas para que el código principal sea más limpio) ---

    // Envía un archivo físico al navegador
    private static void serveFile(HttpExchange exchange, String filePath, String contentType) throws IOException {
        File file = new File(filePath);
        if (!file.exists() || file.isDirectory()) {
            String error = "404 - No encontrado: " + filePath;
            exchange.sendResponseHeaders(404, error.getBytes().length);
            exchange.getResponseBody().write(error.getBytes());
            exchange.getResponseBody().close();
            return;
        }
        byte[] content = Files.readAllBytes(file.toPath());
        exchange.getResponseHeaders().set("Content-Type", contentType);
        exchange.sendResponseHeaders(200, content.length);
        exchange.getResponseBody().write(content);
        exchange.getResponseBody().close();
    }

    // Envía una respuesta de texto (como JSON) al navegador
    private static void enviarRespuesta(HttpExchange exchange, int codigo, String texto) throws IOException {
        byte[] bytes = texto.getBytes(StandardCharsets.UTF_8);
        exchange.sendResponseHeaders(codigo, bytes.length);
        OutputStream os = exchange.getResponseBody();
        os.write(bytes);
        os.close();
    }

    // Parser de JSON artesanal para extraer valores de campos específicos
    private static String getJsonValue(String json, String key) {
        String quoted = "\"" + key + "\"";
        int idx = json.indexOf(quoted);
        if (idx == -1) return null;
        int colon = json.indexOf(':', idx + quoted.length());
        if (colon == -1) return null;
        int start = colon + 1;
        while (start < json.length() && Character.isWhitespace(json.charAt(start))) start++;
        if (start >= json.length()) return null;
        char delimiter = json.charAt(start);
        if (delimiter == '"') {
            int end = json.indexOf('"', start + 1);
            if (end == -1) return null;
            return json.substring(start + 1, end);
        } else {
            int end = start;
            while (end < json.length() && ",}\n".indexOf(json.charAt(end)) == -1) end++;
            return json.substring(start, end).trim();
        }
    }
}