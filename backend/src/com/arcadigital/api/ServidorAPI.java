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

public class ServidorAPI {

    // método para escribir logs en consola y en archivo
    public static void log(String mensaje) {
        System.out.println(mensaje);
        try (java.io.FileWriter fw = new java.io.FileWriter("server.log", true)) {
            fw.write(mensaje + System.lineSeparator());
        } catch (IOException e) {
            // ignorar
        }
    }

    public static void main(String[] args) throws IOException {
        //   configuro el puerto del servidor en 8080
        int puerto = 8080;
        //   creo el servidor HTTP que escuchará en ese puerto
        HttpServer server = HttpServer.create(new InetSocketAddress(puerto), 0);
        
        log(" Servidor Arca Digital iniciado");
        log(" Escuchando en: http://localhost:" + puerto);

        // ============================================
        //   CONFIGURO LA RUTA PRINCIPAL "/"
        // Cuando el usuario abre http://localhost:8080/
        // le sirvo el dashboard (index.html)
        // ============================================
        // ============================================
//   CONFIGURO LA RUTA PRINCIPAL "/"
// ============================================
server.createContext("/", new HttpHandler() {
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String path = exchange.getRequestURI().getPath();
        
        // 1. Rutas exactas para la web principal
        if (path.equals("/") || path.equals("/index.html")) {
            serveFile(exchange, "Frontend/index.html", "text/html");
        }
        else if (path.equals("/app.js")) {
            serveFile(exchange, "Frontend/app.js", "application/javascript");
        }
        else if (path.equals("/styles.css")) {
            serveFile(exchange, "Frontend/styles.css", "text/css");
        }
        //  NUEVO: Lógica para servir cualquier imagen de la carpeta img
        else if (path.startsWith("/img/")) {
            // Ejemplo: Piden "/img/gato.jpg" -> Buscamos en "Frontend/img/gato.jpg"
            String filePath = "Frontend" + path;
            
            // Determinamos el tipo de imagen
            String mimeType = "image/jpeg"; // Por defecto jpg
            if (path.endsWith(".png")) mimeType = "image/png";
            else if (path.endsWith(".gif")) mimeType = "image/gif";
            else if (path.endsWith(".svg")) mimeType = "image/svg+xml";
            else if (path.endsWith(".webp")) mimeType = "image/webp";
            
            serveFile(exchange, filePath, mimeType);
        }
        // Fin de lo nuevo
        else {
            String error = "404 - Pagina no encontrada: " + path;
            exchange.sendResponseHeaders(404, error.getBytes().length);
            exchange.getResponseBody().write(error.getBytes());
            exchange.getResponseBody().close();
        }
    }
});
        // ============================================
        //   CONFIGURO LA RUTA DE LA API "/api/animales"
        // Cuando el usuario (o el JavaScript) pide datos,
        //   voy a la base de datos y devuelvo JSON
        // ============================================
        server.createContext("/api/animales", new HttpHandler() {
            @Override
            public void handle(HttpExchange exchange) throws IOException {
                //   configuro las cabeceras para que el navegador entienda que es JSON
                exchange.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");
                //   permito que cualquier origen acceda (CORS - para que el frontend pueda llamar)
                exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
                
                //   verifico el método HTTP
                String method = exchange.getRequestMethod();
                if ("GET".equals(method)) {
                    //   leo todos los animales y devuelvo JSON
                    AnimalDAO dao = new AnimalDAO();
                    List<Animal> lista = dao.listarTodos();
                    StringBuilder jsonBuilder = new StringBuilder();
                    jsonBuilder.append("[");
                    for (int i = 0; i < lista.size(); i++) {
                        jsonBuilder.append(lista.get(i).toJson());
                        if (i < lista.size() - 1) jsonBuilder.append(",");
                    }
                    jsonBuilder.append("]");
                    byte[] bytes = jsonBuilder.toString().getBytes(StandardCharsets.UTF_8);
                    exchange.sendResponseHeaders(200, bytes.length);
                    OutputStream os = exchange.getResponseBody();
                    os.write(bytes);
                    os.close();
                } else if ("POST".equals(method)) {
                    // recibo un JSON con los datos del nuevo animal (puede contener fotoBase64)
                    String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
                    try {
                        // parseo muy básico del JSON sin librerías externas
                        java.util.Map<String,String> map = new java.util.HashMap<>();
                        // quitamos llaves exteriores
                        String payload = body.trim();
                        if (payload.startsWith("{")) payload = payload.substring(1);
                        if (payload.endsWith("}")) payload = payload.substring(0, payload.length() - 1);
                        // separamos por comas que no estén dentro de comillas
                        String[] pairs = payload.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)");
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
                        int id = Integer.parseInt(map.getOrDefault("id", "0"));
                        String nombre = map.get("nombre");
                        String especie = map.get("especie");
                        String raza = map.get("raza");
                        int edad = Integer.parseInt(map.getOrDefault("edad", "0"));
                        String descripcion = map.get("descripcion");
                        String estado = map.get("estado");
                        boolean urgente = Boolean.parseBoolean(map.getOrDefault("urgente", "false"));
                        String fotoUrl = map.get("fotoUrl");
                        String fotoBase64 = map.get("fotoBase64");
                        byte[] fotoBytes = null;
                        if (fotoBase64 != null && !"null".equals(fotoBase64)) {
                            fotoBytes = java.util.Base64.getDecoder().decode(fotoBase64);
                        }
                        Animal nuevo = new Animal(id, nombre, especie, raza, edad, descripcion, estado, urgente, fotoUrl, fotoBytes, null);
                        AnimalDAO dao = new AnimalDAO();
                        boolean ok = dao.insertar(nuevo);
                        String resp = ok ? "{\"status\":\"ok\"}" : "{\"error\":\"no se pudo insertar\"}";
                        byte[] bytes = resp.getBytes(StandardCharsets.UTF_8);
                        exchange.sendResponseHeaders(ok ? 200 : 500, bytes.length);
                        exchange.getResponseBody().write(bytes);
                        exchange.getResponseBody().close();
                    } catch (Exception ex) {
                        String resp = "{\"error\":\"" + ex.getMessage() + "\"}";
                        exchange.sendResponseHeaders(400, resp.getBytes().length);
                        exchange.getResponseBody().write(resp.getBytes());
                        exchange.getResponseBody().close();
                    }
                } else if ("PUT".equals(method)) {
                    // Actualizar campos de un animal existente (ej. fotoUrl)
                    String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
                    // log para depuración
                    log("[PUT /api/animales] body=" + body);
                    try {
                        // parseo simple para id y fotoUrl
                        String idStr = getJsonValue(body, "id");
                        String fotoUrl = getJsonValue(body, "fotoUrl");
                        log("Parsed id=" + idStr + " fotoUrl=" + fotoUrl);
                        if (idStr == null) throw new Exception("Falta campo id");
                        int id = Integer.parseInt(idStr);
                        AnimalDAO dao = new AnimalDAO();
                        boolean ok = dao.actualizarFotoUrl(id, fotoUrl);
                        String resp = ok ? "{\"status\":\"ok\"}" : "{\"error\":\"no se pudo actualizar\"}";
                        byte[] bytes = resp.getBytes(StandardCharsets.UTF_8);
                        exchange.sendResponseHeaders(ok ? 200 : 500, bytes.length);
                        exchange.getResponseBody().write(bytes);
                        exchange.getResponseBody().close();
                    } catch (Exception ex) {
                        String resp = "{\"error\": \"" + ex.getMessage() + "\"}";
                        exchange.sendResponseHeaders(400, resp.getBytes().length);
                        exchange.getResponseBody().write(resp.getBytes());
                        exchange.getResponseBody().close();
                    }
                }
            }
        });

        // ============================================
// RUTA PARA SUBIR IMÁGENES (/api/upload)
// ============================================
server.createContext("/api/upload", new HttpHandler() {
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        // Configurar cabeceras CORS
        exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
        exchange.getResponseHeaders().set("Access-Control-Allow-Methods", "POST, OPTIONS");
        exchange.getResponseHeaders().set("Access-Control-Allow-Headers", "Content-Type, X-Filename"); // Permitimos nuestra cabecera personalizada

        if ("OPTIONS".equals(exchange.getRequestMethod())) {
            exchange.sendResponseHeaders(204, -1);
            return;
        }

        if ("POST".equals(exchange.getRequestMethod())) {
            try {
                // 1. Obtener el nombre del archivo de la cabecera personalizada
                List<String> filenameHeader = exchange.getRequestHeaders().get("X-Filename");
                if (filenameHeader == null || filenameHeader.isEmpty()) {
                    throw new Exception("Falta la cabecera X-Filename");
                }
                String filename = filenameHeader.get(0);
                
                // Evitar nombres duplicados o peligrosos (limpieza básica)
                filename = System.currentTimeMillis() + "_" + filename.replaceAll("[^a-zA-Z0-9.-]", "_");

                // 2. Definir dónde guardar (Carpeta Frontend/img)
                // Nota: Asegúrate de que esta carpeta exista
                File uploadsDir = new File("Frontend/img");
                if (!uploadsDir.exists()) uploadsDir.mkdirs();
                
                File targetFile = new File(uploadsDir, filename);

                // 3. Leer los bytes que envía el navegador y guardarlos en el archivo
                Files.copy(exchange.getRequestBody(), targetFile.toPath(), java.nio.file.StandardCopyOption.REPLACE_EXISTING);

                // 4. Responder con la ruta relativa para que la base de datos la use
                String jsonResponse = "{\"url\": \"/img/" + filename + "\"}";
                byte[] responseBytes = jsonResponse.getBytes(StandardCharsets.UTF_8);
                
                exchange.getResponseHeaders().set("Content-Type", "application/json");
                exchange.sendResponseHeaders(200, responseBytes.length);
                OutputStream os = exchange.getResponseBody();
                os.write(responseBytes);
                os.close();

                System.out.println("📸 Imagen guardada: " + targetFile.getAbsolutePath());

            } catch (Exception e) {
                String error = "{\"error\": \"" + e.getMessage() + "\"}";
                exchange.sendResponseHeaders(500, error.getBytes().length);
                exchange.getResponseBody().write(error.getBytes());
                exchange.getResponseBody().close();
                e.printStackTrace();
            }
        }
    }
});
        server.createContext("/api/login", new HttpHandler() {
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
                    try {
                        String username = getJsonValue(body, "username");
                        String password = getJsonValue(body, "password");

                        if (("admin".equals(username) && "admin123".equals(password)) ||
                            ("voluntario".equals(username) && "voluntario123".equals(password))) {
                            String resp = "{\"status\":\"ok\", \"user\": {\"username\": \"" + username + "\", \"role\": \"" + username + "\"}}";
                            byte[] bytes = resp.getBytes(StandardCharsets.UTF_8);
                            exchange.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");
                            exchange.sendResponseHeaders(200, bytes.length);
                            exchange.getResponseBody().write(bytes);
                        } else {
                            throw new Exception("Usuario o contraseña incorrectos");
                        }
                    } catch (Exception ex) {
                        String resp = "{\"error\":\"" + ex.getMessage() + "\"}";
                        exchange.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");
                        exchange.sendResponseHeaders(401, resp.getBytes().length);
                        exchange.getResponseBody().write(resp.getBytes());
                    } finally {
                        exchange.getResponseBody().close();
                    }
                }
            }
        });

        //   configuro que el servidor NO use un executor especial
        // (esto es un detalle técnico que significa: usa el thread actual)
        server.setExecutor(null); 
        //   inicio el servidor y dejo que escuche peticiones
        server.start();
    }

    // ============================================
    //   CREO UN MÉTODO AUXILIAR PARA SERVIR ARCHIVOS
    // Este método toma un archivo y lo envía al navegador
    // con el tipo de contenido correcto (HTML, CSS, JavaScript)
    // ============================================
    private static void serveFile(HttpExchange exchange, String filePath, String contentType) throws IOException {
        try {
            //   creo un objeto File apuntando al archivo que se solicita
            File file = new File(filePath);
            
            //   verifico si el archivo existe
            if (!file.exists()) {
                // Si no existe,   lanzo una excepción
                throw new FileNotFoundException();
            }
            
            //   leo TODOS los bytes del archivo en memoria
            byte[] fileContent = Files.readAllBytes(file.toPath());
            
            //   configuro la cabecera diciendo qué tipo de archivo es
            // (por ejemplo: "text/html" para HTML, "application/javascript" para JS)
            exchange.getResponseHeaders().set("Content-Type", contentType);
            
            //   envío un código 200 (OK) y digo cuántos bytes contiene
            exchange.sendResponseHeaders(200, fileContent.length);
            
            //   obtengo el canal de salida para escribir
            OutputStream os = exchange.getResponseBody();
            
            //   escribo todos los bytes del archivo
            os.write(fileContent);
            
            //   cierro la conexión (esto le dice al navegador que terminé)
            os.close();
            
        } catch (FileNotFoundException e) {
            //   capturo el error si el archivo no existe
            String error = "404 - Archivo no encontrado: " + filePath;
            //   envío un código 404 de error
            exchange.sendResponseHeaders(404, error.getBytes().length);
            //   escribo el mensaje de error
            exchange.getResponseBody().write(error.getBytes());
            //   cierro la conexión
            exchange.getResponseBody().close();
        }
    }
    /**
     * M�todo auxiliar muy simple para extraer el valor de una propiedad JSON plana.
     * No es un parser completo, s�lo funciona con el formato que nosotros generamos.
     */
    private static String getJsonValue(String json, String key) {
        // Parse a flat JSON object looking for a field named key.
        // This is not a full JSON parser, just enough for our use case.
        String quoted = "\"" + key + "\"";
        int idx = json.indexOf(quoted);
        if (idx == -1) return null;
        int colon = json.indexOf(':', idx + quoted.length());
        if (colon == -1) return null;
        int start = colon + 1;
        // skip whitespace
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
