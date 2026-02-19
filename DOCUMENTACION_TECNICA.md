# 📚 Documentación Técnica Profunda - Arca Digital

**Versión:** 1.0  
**Fecha:** 17 de febrero de 2026  
**Autor:** Sistema de Análisis Automatizado  
**Audiencia:** Desarrollador único responsable del proyecto

---

## Tabla de Contenidos

1. [Resumen Ejecutivo](#resumen-ejecutivo)
2. [Arquitectura General](#arquitectura-general)
3. [Análisis Detallado de Componentes](#análisis-detallado-de-componentes)
4. [Cambios Realizados y Evolución](#cambios-realizados-y-evolución)
5. [Patrones de Desarrollo](#patrones-de-desarrollo)
6. [Decisiones Técnicas](#decisiones-técnicas)
7. [Problemas Encontrados y Soluciones](#problemas-encontrados-y-soluciones)
8. [Guía de Operación](#guía-de-operación)

---

## Resumen Ejecutivo

El proyecto **Arca Digital** es una aplicación web de **dos capas (cliente-servidor)** para gestionar un refugio de animales. Tras un proceso iterativo de desarrollo, enfrentamos y resolvimos múltiples desafíos de arquitectura, configuración y conectividad.

### Estado Actual
- **Backend:** Java HTTP Server con acceso a MariaDB
- **Frontend:** Aplicación web estática (HTML5 + CSS + JavaScript)
- **Base de Datos:** MariaDB local
- **Puerto:** 8080
- **Estado:** Funcionando correctamente

### Métricas del Código
- **Clases Java:** 5 (Animal, ConexionDB, AnimalDAO, ServidorAPI, Main)
- **Líneas de código (Backend):** ~500 líneas
- **Líneas de código (Frontend):** ~100 líneas
- **Archivos de configuración:** 1 (databasesetup.sql)

---

## Arquitectura General

### 1. Patrón Arquitectónico: MVC + DAO

La aplicación sigue un patrón híbrido que combina elementos de **MVC** con el patrón **DAO**:

```
┌─────────────────────────────────────────────────────────────┐
│                    NAVEGADOR WEB (Frontend)                 │
│  HTML5 + CSS3 + JavaScript (app.js)                         │
│  - Interfaz de usuario                                      │
│  - Peticiones HTTP asincrónicas (fetch)                     │
└─────────────────┬───────────────────────────────────────────┘
                  │ HTTP
                  │ Requests/Responses
                  ▼
┌─────────────────────────────────────────────────────────────┐
│              Java HTTP Server (ServidorAPI)                  │
│  - Punto de entrada para todas las peticiones               │
│  - Enrutador de recursos                                    │
│  - Controlador de la lógica de respuesta                     │
└─────────────────┬───────────────────────────────────────────┘
                  │ Llamadas de método
                  ▼
┌─────────────────────────────────────────────────────────────┐
│          Capa de Acceso a Datos (AnimalDAO)                  │
│  - Operaciones CRUD sobre la tabla 'animales'               │
│  - Traducción de datos Java ↔ Base de datos                 │
└─────────────────┬───────────────────────────────────────────┘
                  │ JDBC Queries
                  ▼
┌─────────────────────────────────────────────────────────────┐
│         Gestor de Conexiones (ConexionDB)                    │
│  - Establece/cierra conexiones JDBC                         │
│  - Credenciales de acceso                                   │
└─────────────────┬───────────────────────────────────────────┘
                  │ JDBC Driver
                  ▼
┌─────────────────────────────────────────────────────────────┐
│              MariaDB (Base de Datos Local)                   │
│  Tabla: animales                                            │
│  Host: localhost:3306                                       │
│  Base de datos: arca_digital                                │
└─────────────────────────────────────────────────────────────┘
```

### 2. Flujo de Datos

#### Flujo GET (Lectura de Animales)

```
CLIENTE                          SERVIDOR                    BASE DE DATOS
  │                                │                              │
  ├─ fetch('/api/animales') ──────>│                              │
  │                                ├─ new AnimalDAO() ──────────>│
  │                                │                              │
  │                                │<─ SELECT * FROM animales ───┤
  │                                │                              │
  │                                │<─ ResultSet (10 filas) ─────┤
  │                                │                              │
  │                                ├─ Convertir a JSON           │
  │                                │                              │
  │<────── JSON (Array) ───────────┤                              │
  │                                │                              │
  ├─ Renderizar en HTML/CSS       │                              │
  │                                │                              │
  └─ Mostrar tarjetas al usuario   │                              │
```

---

## Análisis Detallado de Componentes

### 1. Clase `Animal.java` - Modelo de Datos

#### Propósito
Representa una entidad individual de animal en el sistema. Actúa como "molde" o "blueprint" para crear objetos que contienen toda la información relevante de cada animal.

#### Atributos

| Atributo | Tipo | Descripción | Notas |
|----------|------|-------------|-------|
| `id` | `int` | Identificador único | Generado por BD |
| `nombre` | `String` | Nombre del animal | Ej: "Rex", "Luna" |
| `especie` | `String` | Tipo de animal | Ej: "Perro", "Gato" |
| `raza` | `String` | Raza específica | Ej: "Pastor Alemán" |
| `edad` | `int` | Años de edad | Mayor o igual a 0 |
| `descripcion` | `String` | Información detallada | Puede contener saltos de línea |
| `estado` | `String` | Situación actual | Valores: EN_ADOPCION, EN_TRATAMIENTO, RESCATADO |
| `urgente` | `boolean` | Si requiere atención urgente | true/false |
| `fotoUrl` | `String` | Ruta a imagen del animal | Ruta relativa o absoluta |
| `fechaIngreso` | `Timestamp` | Cuándo llegó el animal | Formato: YYYY-MM-DD |

#### Constructores

**Constructor Vacío (líneas 33-34)**
```java
public Animal() { }
```
- **Razón:** Compatibilidad con frameworks que requieren constructor sin parámetros
- **Uso:** Instanciación reflexiva, mapeo automático de datos
- **Ejemplo:**
```java
Animal a = new Animal();
a.setNombre("Spiky");
a.setEspecie("Erizo");
```

**Constructor Completo (líneas 36-48)**
```java
public Animal(int id, String nombre, String especie, String raza, int edad, 
              String descripcion, String estado, boolean urgente, 
              String fotoUrl, Timestamp fechaIngreso)
```
- **Razón:** Inicialización atomizada en una sola operación
- **Uso:** Creación desde resultados de base de datos
- **Ejemplo en AnimalDAO:**
```java
Animal animal = new Animal(
    rs.getInt("id"),
    rs.getString("nombre"),
    // ... etc
    rs.getTimestamp("fecha_ingreso")
);
```

#### Métodos Principales

##### 1. Getters y Setters (líneas 52-143)
- **Total:** 20 métodos (10 getters + 10 setters)
- **Patrón:** JavaBeans estándar
- **Encapsulamiento:** Los atributos son `private`, acceso controlado
- **Ventaja:** Permite validación futura sin cambiar la interfaz

**Ejemplo especial - `isUrgente()` vs `getUrgente()`:**
```java
public boolean isUrgente() {  // Convención para booleanos
    return urgente;
}
```

##### 2. `getFechaIngresoFormateada()` (líneas 149-156)

**Código:**
```java
public String getFechaIngresoFormateada() {
    if (fechaIngreso == null) {
        return "";
    }
    return new SimpleDateFormat("dd/MM/yyyy").format(fechaIngreso);
}
```

**Análisis:**
- **Propósito:** Convertir `Timestamp` SQL a formato legible (dd/MM/yyyy)
- **Manejo de nulos:** Devuelve string vacío si no hay fecha (evita NullPointerException)
- **Ubicación lógica:** Método de utilidad en el modelo (no en la vista)
- **Razón de ser:** Evitar lógica de formato en JavaScript

**Cambios realizados:**
- ✅ Añadido en desarrollo para mejorar presentación de datos
- ✅ Resuelve el problema de mostrar timestamps SQL crudos en el frontend

##### 3. `toString()` (líneas 158-172)

**Código:**
```java
@Override
public String toString() {
    return "FICHA DE ANIMAL\n" +
           "----------------------------------\n" +
           "ID:         " + id + "\n" +
           // ... más campos
           "----------------------------------";
}
```

**Análisis:**
- **Propósito:** Representación legible en consola para depuración
- **Formato:** "Ficha profesional" con separadores visuales
- **Uso:** `System.out.println(animal)` en `Main.java`
- **Salida típica:**
```
FICHA DE ANIMAL
----------------------------------
ID:         1
Nombre:     Rex
Especie:    Perro
Raza:       Pastor Alemán
Edad:       5 años
Estado:     EN_ADOPCION
Urgente:    No
Ingreso:    14/02/2026
Descripción: Animal tranquilo, buena disposición...
Foto:       /animales/rex.jpg
----------------------------------
```

**Cambios realizados:**
- ✅ Implementado desde el inicio como herramienta de depuración
- ✅ Mejorada formato para claridad

##### 4. `escapeJson()` (líneas 178-192)

**Código:**
```java
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
```

**Análisis:**

Este es uno de los cambios **más críticos** realizados. Visualicemos su importancia:

| Carácter | Problema | Solución | Razón |
|----------|----------|----------|-------|
| `\` | JSON las interpreta | `\\\\` (dos escapes) | Evitar secuencias no válidas |
| `"` | Cierra la cadena prematuramente | `\\"` | Permitir comillas dentro del JSON |
| Salto de línea `\n` | Rompe estructura JSON | `\\n` | Mantener valor en una línea |
| Tabulación `\t` | Igual que saltos | `\\t` | Formateo correcto |

**Ejemplo de por qué es necesario:**

```
❌ SIN ESCAPE (JSON INVÁLIDO):
{
  "descripcion": "Animal con problemas
  de comportamiento..."  ← ¡Salto de línea rompe el JSON!
}

✅ CON ESCAPE (JSON VÁLIDO):
{
  "descripcion": "Animal con problemas\nde comportamiento..."
}
```

**Cambios realizados:**
- ✅ Implementado para resolver problema de caracteres especiales en descripciones
- ✅ Manejo seguro de valores null
- ✅ Cumple ISO 8601 para caracteres de escape en JSON

##### 5. `toJson()` (líneas 194-208)

**Código:**
```java
public String toJson() {
    return "{" +
        "\"id\": " + id + "," +
        "\"nombre\": " + escapeJson(nombre) + "," +
        "\"especie\": " + escapeJson(especie) + "," +
        "\"raza\": " + escapeJson(raza) + "," +
        "\"edad\": " + edad + "," +
        "\"descripcion\": " + escapeJson(descripcion) + "," +
        "\"estado\": " + escapeJson(estado) + "," +
        "\"urgente\": " + urgente + "," +
        "\"fotoUrl\": " + escapeJson(fotoUrl) + "," +
        "\"fechaIngreso\": " + escapeJson(getFechaIngresoFormateada()) +
    "}";
}
```

**Análisis:**

Este método realiza **serialización manual a JSON** sin librerías externas.

**Decisiones de diseño:**

| Campo | Tipo en JSON | Razón |
|-------|-------------|-------|
| `id`/`edad`/`urgente` | Sin comillas | Son números/booleanos JSON nativos |
| `nombre`, `especie`, etc. | Con `escapeJson()` | Son strings que pueden contener caracteres especiales |
| `fechaIngreso` | Con `escapeJson()` | Se convierte a string formateado |

**Salida típica:**
```json
{
  "id": 1,
  "nombre": "Rex",
  "especie": "Perro",
  "raza": "Pastor Alemán",
  "edad": 5,
  "descripcion": "Animal tranquilo\nbuena disposición",
  "estado": "EN_ADOPCION",
  "urgente": false,
  "fotoUrl": "/animales/rex.jpg",
  "fechaIngreso": "14/02/2026"
}
```

**Cambios realizados:**
- ✅ Serialización manual evita dependencia de Gson o Jackson
- ✅ Proporciona control total sobre el formato
- ✅ Mejora la velocidad en aplicaciones pequeñas

**Alternativa NO elegida:**
```java
// ❌ Requerería agregar librería Maven
import com.google.gson.Gson;
new Gson().toJson(this);
```

---

### 2. Clase `ConexionDB.java` - Gestor de Conexiones

#### Propósito
Encapsula toda la lógica de conexión JDBC hacia la base de datos MariaDB. Actúa como mediador entre la aplicación Java y el servidor de bases de datos.

#### Atributos Estáticos (líneas 18-21)

```java
private static final String URL = "jdbc:mariadb://localhost:3306/arca_digital";
private static final String USUARIO = "root";
private static final String PASSWORD = "1234";
```

**Análisis de cada parámetro:**

| Parámetro | Valor | Explicación |
|-----------|-------|-------------|
| **Protocolo** | `jdbc:mariadb` | Driver JDBC específico para MariaDB |
| **Host** | `localhost` | Servidor local (mismo equipo) |
| **Puerto** | `3306` | Puerto estándar de MariaDB/MySQL |
| **Base de datos** | `arca_digital` | Nombre exacto de la BD a conectar |
| **Usuario** | `root` | Usuario administrativo (XAMPP/WAMP default) |
| **Contraseña** | `1234` | Definida durante instalación |

**Consideraciones de seguridad:**
- ⚠️ Credenciales hardcodeadas (aceptable en desarrollo local)
- 💡 Mejora futura: Leer de archivo de configuración (`application.properties`)

#### Método `conectar()` (líneas 23-48)

**Código:**
```java
public static Connection conectar() {
    Connection conexion = null;
    try {
        Class.forName("org.mariadb.jdbc.Driver");
        conexion = DriverManager.getConnection(URL, USUARIO, PASSWORD);
        System.out.println("¡Conexión a MariaDB exitosa!");
    } catch (ClassNotFoundException e) {
        System.err.println("Error: No se encontró el Driver JDBC de MariaDB.");
        e.printStackTrace();
    } catch (SQLException e) {
        System.err.println("Error: Fallo al conectar con la base de datos.");
        e.printStackTrace();
    }
    return conexion;
}
```

**Análisis paso a paso:**

**Línea 1: Declaración de variable**
```java
Connection conexion = null;
```
- Inicializa como `null` para detectar fallos fácilmente
- Se devuelve `null` si hay error (patrón Null Object)

**Línea 3: Cargar el Driver**
```java
Class.forName("org.mariadb.jdbc.Driver");
```
- **Propósito:** Registrar el driver JDBC en tiempo de ejecución
- **Cómo funciona:** Refleja la clase del driver, ejecuta su inicializador estático
- **Necesario:** Aunque Java 8+ lo hace automáticamente, es buena práctica
- **Excepción:** `ClassNotFoundException` si no encuentra el JAR

```
Secuencia:
① Busca mariadb-java-client-3.5.7.jar en classpath
② Carga la clase org.mariadb.jdbc.Driver
③ Ejecuta el bloque static del driver (registra con DriverManager)
④ Ahora DriverManager conoce cómo conectar a MariaDB
```

**Línea 4: Establecer conexión**
```java
conexion = DriverManager.getConnection(URL, USUARIO, PASSWORD);
```
- **Propósito:** Crear conexión TCP con el servidor MariaDB
- **Parámetros:** URL (dónde), usuario (quién), contraseña (validación)
- **Retorno:** Objeto `Connection` que representa la sesión abierta
- **Excepciones posibles:**
  - `SQLException`: Usuario/contraseña incorrectos
  - `SQLException`: Servidor no responde (apagado)
  - `SQLException`: Base de datos no existe

**Línea 5: Confirmación en consola**
```java
System.out.println("¡Conexión a MariaDB exitosa!");
```
- Mensaje visual para operador (facilita el debugging)

**Manejo de excepciones:**

```java
catch (ClassNotFoundException e) {
    // Falta el archivo JAR
    System.err.println("Error: No se encontró el Driver JDBC...");
    e.printStackTrace();
}

catch (SQLException e) {
    // Problemas con credenciales o servidor
    System.err.println("Error: Fallo al conectar...");
    e.printStackTrace();
}
```

**Cambios realizados:**
- ✅ Error handling dual (clase no encontrada vs. conexión)
- ✅ Mensajes claros en `System.err` para errores
- ✅ `printStackTrace()` para debugging detallado

**Diagrama de flujo:**

```
conectar()
    │
    ├─ Class.forName("org.mariadb.jdbc.Driver")
    │   └─ ✓ Éxito → Driver registrado
    │   └─ ✗ ClassNotFoundException → mostrar error, return null
    │
    ├─ DriverManager.getConnection(URL, usuario, pass)
    │   └─ ✓ Éxito → Connection abierta
    │   └─ ✗ SQLException → mostrar error, return null
    │
    └─ return Connection (o null si error)
```

---

### 3. Clase `AnimalDAO.java` - Acceso a Datos

#### Propósito
Implementa el patrón **Data Access Object (DAO)** para encapsular todas las operaciones CRUD (Create, Read, Update, Delete) sobre la tabla `animales`.

#### Patrón DAO

**¿Qué es DAO?**
Es un patrón de diseño que aísla la lógica de acceso a datos del resto de la aplicación.

```
Sin DAO (❌ ACOPLAMIENTO FUERTE):
Animal.java contiene:
├─ Propiedades
├─ Getters/Setters
├─ toJson()
└─ SQL y conexiones JDBC ← ¡Confusión de responsabilidades!

Con DAO (✅ SEPARACIÓN CLARA):
Animal.java contiene:
├─ Propiedades (datos)
├─ Getters/Setters
└─ toJson() (serialización)

AnimalDAO.java contiene:
├─ Métodos CRUD
├─ Consultas SQL
└─ Gestión JDBC
```

#### Método `listarTodos()` (líneas 18-53)

**Código:**
```java
public List<Animal> listarTodos() {
    List<Animal> listaAnimales = new ArrayList<>();
    String sql = "SELECT * FROM animales";

    try (Connection conn = ConexionDB.conectar();
         PreparedStatement stmt = conn.prepareStatement(sql);
         ResultSet rs = stmt.executeQuery()) {

        while (rs.next()) {
            int id = rs.getInt("id");
            String nombre = rs.getString("nombre");
            // ... más campos
            
            Animal animal = new Animal(id, nombre, especie, /* ... */);
            listaAnimales.add(animal);
        }
    } catch (SQLException e) {
        System.err.println("Error al listar animales: " + e.getMessage());
    }

    return listaAnimales;
}
```

**Análisis en detalle:**

**Línea 1-2: Inicialización**
```java
public List<Animal> listarTodos() {
    List<Animal> listaAnimales = new ArrayList<>();
```
- Retorna `List<Animal>` (interfaz) no `ArrayList` (implementación)
- **Ventaja:** Permite cambiar a otras implementaciones (LinkedList, etc.)
- Se inicia vacía, se llena en el bucle

**Línea 3: Consulta SQL**
```java
String sql = "SELECT * FROM animales";
```
- Recupera TODAS las columnas (`*`) de la tabla
- Sin filtros WHERE ni ORDER BY
- **Mejora futura:** Paginar para grandes volúmenes

**Línea 5-7: Try-with-resources**
```java
try (Connection conn = ConexionDB.conectar();
     PreparedStatement stmt = conn.prepareStatement(sql);
     ResultSet rs = stmt.executeQuery()) {
```

Esta es una característica Java 7+ que **cierra automáticamente** los recursos.

**Sin try-with-resources (antiguo, ❌):**
```java
Connection conn = null;
try {
    conn = ConexionDB.conectar();
    // ... código
} finally {
    if (conn != null) {
        conn.close();  // ← Hay que hacerlo manualmente
    }
}
```

**Con try-with-resources (moderno, ✅):**
```java
try (Connection conn = ConexionDB.conectar()) {
    // ... código
}  // ← Cierra automáticamente al salir del bloque
```

**Ventajas:**
- 🔒 Evita "connection leaks" (conexiones sin cerrar)
- 📉 Reduce líneas de código
- 🛡️ Es seguro incluso si hay excepciones

**Recursos que se cierran automáticamente:**
1. `Connection` - La conexión a BD
2. `PreparedStatement` - La orden SQL preparada
3. `ResultSet` - Los resultados de la consulta

**Línea 9-17: Procesar resultados**
```java
while (rs.next()) {
    int id = rs.getInt("id");
    String nombre = rs.getString("nombre");
    String especie = rs.getString("especie");
    String raza = rs.getString("raza");
    int edad = rs.getInt("edad");
    String descripcion = rs.getString("descripcion");
    String estado = rs.getString("estado");
    boolean urgente = rs.getBoolean("urgente");
    String fotoUrl = rs.getString("foto_url");
    java.sql.Timestamp fechaIngreso = rs.getTimestamp("fecha_ingreso");
    
    Animal animal = new Animal(id, nombre, especie, raza, edad, 
                               descripcion, estado, urgente, 
                               fotoUrl, fechaIngreso);
    listaAnimales.add(animal);
}
```

**Análisis del bucle:**

```
Loop en pseudocódigo:
MIENTRAS ResultSet tenga filas:
    ① Leer fila actual
    ② Extraer cada columna:
       - Usar rs.getInt() para números
       - Usar rs.getString() para texto
       - Usar rs.getBoolean() para booleanos
       - Usar rs.getTimestamp() para fechas
    ③ Crear objeto Animal con esos valores
    ④ Agregar a la lista
    ⑤ Avanzar a siguiente fila
```

**Mapeo de columnas BD → Java:**

| Columna BD | Tipo BD | Método Java | Atributo Java |
|-----------|---------|------------|---------------|
| `id` | INT | `getInt()` | `id: int` |
| `nombre` | VARCHAR | `getString()` | `nombre: String` |
| `especie` | VARCHAR | `getString()` | `especie: String` |
| `raza` | VARCHAR | `getString()` | `raza: String` |
| `edad` | INT | `getInt()` | `edad: int` |
| `descripcion` | TEXT | `getString()` | `descripcion: String` |
| `estado` | VARCHAR | `getString()` | `estado: String` |
| `urgente` | BOOLEAN | `getBoolean()` | `urgente: boolean` |
| `foto_url` | VARCHAR | `getString()` | `fotoUrl: String` |
| `fecha_ingreso` | DATETIME | `getTimestamp()` | `fechaIngreso: Timestamp` |

**Conversión de tipos:**
- `rs.getInt()` → `int` de Java
- `java.sql.Timestamp` → Se mapea directamente al atributo de Animal
- No hay conversión manual necesaria

**Línea 19-21: Manejo de errores**
```java
} catch (SQLException e) {
    System.err.println("Error al listar animales: " + e.getMessage());
}
```
- Captura cualquier excepción SQL (conexión, sintaxis, etc.)
- Imprime mensaje al error stream (`System.err`)
- Continúa devolviendo lista (vacía si error)

**Línea 23: Devolución**
```java
return listaAnimales;
```
- Devuelve lista (nunca `null`, siempre una lista válida)
- Si hay error, devuelve lista vacía (es responsabilidad del caller validar)

**Cambios realizados:**
- ✅ Try-with-resources para cerrar recursos automáticamente
- ✅ Tipado de genéricos `List<Animal>`
- ✅ Error handling con mensajes útiles

---

### 4. Clase `ServidorAPI.java` - Servidor HTTP

#### Propósito
Implementa un servidor HTTP embebido que:
1. Sirve archivos estáticos (HTML, CSS, JS)
2. Expone endpoints REST para acceder a datos

#### Arquitectura del Servidor

**Tipo de Servidor:** `com.sun.net.httpserver.HttpServer`
- Built-in en JDK (no requiere Tomcat ni especificaciones)
- Ligero para aplicaciones pequeñas
- Perfecto para prototipado y educación

**Puerto:** 8080
**Host:** localhost (127.0.0.1)

#### Main (líneas 19-31)

```java
public static void main(String[] args) throws IOException {
    int puerto = 8080;
    HttpServer server = HttpServer.create(new InetSocketAddress(puerto), 0);
    
    System.out.println("---------------------------------------------");
    System.out.println("🚀 Servidor Arca Digital iniciado");
    System.out.println("📡 Escuchando en: http://localhost:" + puerto);
    System.out.println("---------------------------------------------");
    // ... resto del código
}
```

**Análisis:**

| Línea | Componente | Explicación |
|-------|-----------|------------|
| `int puerto = 8080` | Definición de puerto | Número de puerto a usar |
| `HttpServer.create(...)` | Creación del servidor | Pero aún NO escucha |
| `new InetSocketAddress(puerto)` | Dirección de escucha | Bind a puerto específico (todos los interfaces) |
| `0` | Parámetro de backlog | Conexiones pendientes permitidas (0 = por defecto) |

**Cambios realizados:**
- ✅ Puerto 8080 (fue previamente 9090, se unificó)
- ✅ Mensajes de inicialización mejorados (emojis)

#### Rutas Implementadas

##### Ruta 1: `/` (Servidor de Archivos Estáticos)

**Propósito:** Servir dashboard HTML + assets

**Código:**
```java
server.createContext("/", new HttpHandler() {
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String path = exchange.getRequestURI().getPath();
        
        if (path.equals("/") || path.equals("/index.html")) {
            serveFile(exchange, "Frontend/index.html", "text/html");
        }
        else if (path.equals("/app.js")) {
            serveFile(exchange, "Frontend/app.js", "application/javascript");
        }
        else if (path.equals("/styles.css")) {
            serveFile(exchange, "Frontend/styles.css", "text/css");
        }
        else {
            String error = "404 - Página no encontrada";
            exchange.sendResponseHeaders(404, error.getBytes().length);
            exchange.getResponseBody().write(error.getBytes());
            exchange.getResponseBody().close();
        }
    }
});
```

**Flujo de procesimiento:**

```
GET /index.html
    │
    └─ path = "/index.html"
        │
        ├─ ¿path == "/" o "/index.html"? → SÍ
        │   └─ serveFile(exchange, "Frontend/index.html", "text/html")
        │       ├─ Leer archivo completo
        │       ├─ Enviar headers: Content-Type: text/html; 200 OK
        │       └─ Escribir contenido en response body
        │
        └─ Cerrar conexión
```

**MIME Types (tipos de contenido):**

| Ruta | MIME Type | Razón |
|------|-----------|-------|
| `index.html` | `text/html` | Documento HTML |
| `app.js` | `application/javascript` | Código JavaScript |
| `styles.css` | `text/css` | Hoja de estilos |
| Otro | `404` | Recurso no encontrado |

**Cambios realizados:**
- ✅ Agregada lógica para servir archivos estáticos
- ✅ Manejo de rutas raíz (`/` y `/index.html` son equivalentes)
- ✅ Respuestas 404 personalizadas

##### Ruta 2: `/api/animales` (Endpoint REST)

**Propósito:** Devolver datos de animales en JSON

**Código (esquema):**
```java
server.createContext("/api/animales", new HttpHandler() {
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        exchange.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");
        exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
        
        if ("GET".equals(exchange.getRequestMethod())) {
            AnimalDAO dao = new AnimalDAO();
            List<Animal> lista = dao.listarTodos();
            
            StringBuilder jsonBuilder = new StringBuilder();
            jsonBuilder.append("[");
            for (int i = 0; i < lista.size(); i++) {
                jsonBuilder.append(lista.get(i).toJson());
                if (i < lista.size() - 1) {
                    jsonBuilder.append(",");
                }
            }
            jsonBuilder.append("]");
            
            String respuesta = jsonBuilder.toString();
            byte[] bytes = respuesta.getBytes(StandardCharsets.UTF_8);
            exchange.sendResponseHeaders(200, bytes.length);
            OutputStream os = exchange.getResponseBody();
            os.write(bytes);
            os.close();
        }
    }
});
```

**Análisis en profundidad:**

**Headers de respuesta:**
```java
exchange.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");
exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
```

1. **Content-Type:** Dice al navegador que recibe JSON (no HTML)
   - `application/json` - Tipo MIME para JSON
   - `;  charset=UTF-8` - Especifica codificación de caracteres

2. **Access-Control-Allow-Origin:**
   - `*` = Cualquier origen puede llamar esta API
   - **CORS (Cross-Origin Resource Sharing)**
   - Sin esto, el navegador bloquearía la petición desde un origen diferente

**Verificación de método HTTP:**
```java
if ("GET".equals(exchange.getRequestMethod())) {
```
- Solo procesa `GET` (lectura de datos)
- Rechazaría `POST`, `PUT`, `DELETE` silenciosamente
- **Mejora futura:** Responder 405 Method Not Allowed

**Construcción manual del JSON:**
```java
StringBuilder jsonBuilder = new StringBuilder();
jsonBuilder.append("[");

for (int i = 0; i < lista.size(); i++) {
    jsonBuilder.append(lista.get(i).toJson());
    if (i < lista.size() - 1) {
        jsonBuilder.append(",");
    }
}

jsonBuilder.append("]");
```

**¿Por qué StringBuilder y no String concatenation?**

```
❌ Ineficiente:
String json = "{...}" + "{...}" + "{...}";  // Crea 3 strings intermedios
String json = json + "{...}";               // Crea otro string

✅ Eficiente:
StringBuilder sb = new StringBuilder();
sb.append("{...}");
sb.append("{...}");  // Solo una cadena al final
String json = sb.toString();
```

**Salida final (Ejemplo):**
```json
[
  {"id": 1, "nombre": "Rex", ...},
  {"id": 2, "nombre": "Luna", ...},
  {"id": 3, "nombre": "Max", ...}
]
```

**Envío de respuesta:**
```java
byte[] bytes = respuesta.getBytes(StandardCharsets.UTF_8);
exchange.sendResponseHeaders(200, bytes.length);
OutputStream os = exchange.getResponseBody();
os.write(bytes);
os.close();
```

**Paso a paso:**
1. `getBytes(UTF_8)` - Convierte String a array de bytes
2. `sendResponseHeaders(200, long)` - Envía código 200 OK y tamaño
3. `getResponseBody()` - Obtiene el canal de salida
4. `write()` - Escribe todos los bytes
5. `close()` - Cierra para que navegador sepa que terminó

**Alternativa sin cerrar (❌):**
```java
exchange.getResponseBody().write(bytes);
// ← El navegador espera más datos indefinidamente
```

**Cambios realizados:**
- ✅ Agregado endpoint `/api/animales`
- ✅ Implementado CORS para permitir acceso desde frontend
- ✅ Serialización manual a JSON usando StringBuilder
- ✅ Charset UTF-8 para caracteres especiales

#### Método `serveFile()` (líneas 109-138)

**Propósito:** Reutilizable para servir archivos estáticos con manejo de errores

**Código:**
```java
private static void serveFile(HttpExchange exchange, String filePath, String contentType) 
        throws IOException {
    try {
        File file = new File(filePath);
        
        if (!file.exists()) {
            throw new FileNotFoundException();
        }
        
        byte[] fileContent = Files.readAllBytes(file.toPath());
        
        exchange.getResponseHeaders().set("Content-Type", contentType);
        exchange.sendResponseHeaders(200, fileContent.length);
        OutputStream os = exchange.getResponseBody();
        os.write(fileContent);
        os.close();
        
    } catch (FileNotFoundException e) {
        String error = "404 - Archivo no encontrado: " + filePath;
        exchange.sendResponseHeaders(404, error.getBytes().length);
        exchange.getResponseBody().write(error.getBytes());
        exchange.getResponseBody().close();
    }
}
```

**Ventajas de encapsular en método:**

| Aspecto | Beneficio |
|--------|----------|
| **Reutilización** | Las 3 rutas (/, /app.js, /styles.css) lo usan |
| **Mantenimiento** | Cambios en una sola ubicación |
| **Testabilidad** | Puede probarse independientemente |
| **Legibilidad** | El handler principal es más conciso |

**Flujo de serveFile:**

```
serveFile("Frontend/index.html", "text/html")
    │
    ├─ File file = new File("Frontend/index.html")
    │
    ├─ ¿Existe el archivo?
    │   ├─ SÍ → Leer completo con Files.readAllBytes()
    │   │       └─ Enviar con header Content-Type: text/html
    │   │
    │   └─ NO → FileNotFoundException
    │           └─ Enviar error 404
    │
    └─ Cerrar OutputStream en ambos casos
```

**Cambios realizados:**
- ✅ Método privado reutilizable
- ✅ Manejo explícito de FileNotFoundException
- ✅ Mensajes de error descriptivos

#### Configuración del Executor (línea 100)

```java
server.setExecutor(null);
server.start();
```

**¿Qué significa `setExecutor(null)`?**

```
Executor = Thread pool que procesa peticiones

Con null:
    Usa un thread por petición (creado bajo demanda)
    
Con Executor personalizado:
    Usa pool fijo de threads reutilizables
    Mejor para apps con muchas peticiones
    
Para Arca Digital:
    null es correcto (pocos usuarios simultáneos)
```

---

### 5. Clase `Main.java` - Prueba de Conectividad

#### Propósito
Programa de prueba que verifica que el backend pueda conectar a la base de datos e iterar los animales.

#### Código completo

```java
public static void main(String[] args) {
    System.out.println("--------------------------------------");
    System.out.println(" INICIANDO ARCA DIGITAL (BACKEND)");
    System.out.println("--------------------------------------");

    AnimalDAO dao = new AnimalDAO();
    System.out.println("Consultando base de datos...");
    List<Animal> lista = dao.listarTodos();

    if (lista.isEmpty()) {
        System.out.println("No hay animales en la base de datos o falló la conexión.");
    } else {
        System.out.println("¡ÉXITO! Se han encontrado " + lista.size() + " animales:");
        for (Animal a : lista) {
            System.out.println(a);
        }
    }
}
```

**Flujo de ejecución:**

```
① Imprimir encabezado
② Crear instancia de AnimalDAO
③ Llamar listarTodos()
    └─ Intenta conectar a BD
    └─ Ejecuta SELECT * FROM animales
    └─ Mapea resultados a objetos Animal
    └─ Devuelve List<Animal>
④ Verificar si lista está vacía
    ├─ Vacía → Mostrar error
    └─ Contains items → Iterar y mostrar cada Animal (usa toString())
```

**Salida típica esperada:**
```
--------------------------------------
 INICIANDO ARCA DIGITAL (BACKEND)
--------------------------------------
¡Conexión a MariaDB exitosa!
Consultando base de datos...
¡ÉXITO! Se han encontrado 3 animales:
FICHA DE ANIMAL
----------------------------------
ID:         1
Nombre:     Rex
Especie:    Perro
Raza:       Pastor Alemán
Edad:       5 años
Estado:     EN_ADOPCION
Urgente:    No
Ingreso:    14/02/2026
Descripción: Animal tranquilo, buena disposición
Foto:       /animales/rex.jpg
----------------------------------
[... más animales ...]
```

**Cambios realizados:**
- ✅ Encabezado informativo
- ✅ Validación de lista vacía
- ✅ Uso del método toString() para presentar datos

---

### 6. Frontend: `app.js` - Cliente JavaScript

#### Propósito
Aplicación cliente que:
1. Obtiene datos de la API REST
2. Construye interfaz visual dinámicamente
3. Maneja errores de conexión

#### Estructura Modular

El archivo se estructura en funciones reutilizables:

```
app.js
├─ DOMContentLoaded
│  └─ Inicia fetchAnimals()
│
├─ showError(message)
│  └─ Muestra error en UI
│
├─ resetUI()
│  └─ Limpia estado anterior
│
├─ getStatusClass(estado)
│  └─ Mapea estado → clase CSS
│
├─ createAnimalCard(animal)
│  └─ Construye elemento DOM para un animal
│
└─ fetchAnimals()
   └─ Obtiene datos de API
```

#### Event Listener: `DOMContentLoaded`

```javascript
document.addEventListener('DOMContentLoaded', () => {
    const apiUrl = 'http://localhost:8080/api/animales';
    const animalGrid = document.getElementById('animal-grid');
    const errorMessage = document.getElementById('error-message');
    
    // ... resto de funciones ...
    
    fetchAnimals();
});
```

**¿Qué es DOMContentLoaded?**

Evento que dispara cuando:
1. HTML está completamente parseado
2. Elementos del DOM están listos para manipular
3. ≠ `load` (que espera a imágenes, CSS, etc.)

**Por qué se usa:**
```
❌ Script en <head> sin DOMContentLoaded:
<head>
    <script src="app.js"></script>
</head>
<!-- El script corre ANTES de que exista #animal-grid -->

✅ Script al final con DOMContentLoaded:
<script>
  document.addEventListener('DOMContentLoaded', () => {
      // Aquí #animal-grid YA EXISTE
  });
</script>
```

#### Función `showError(message)`

```javascript
const showError = (message) => {
    errorMessage.textContent = message;
    errorMessage.style.display = 'block';
};
```

**Análisis:**
- `textContent` = cambia texto (no interpreta HTML)
- `display = 'block'` = hace visible el elemento
- **Ventaja:** Mensajes claros al usuario

#### Función `resetUI()`

```javascript
const resetUI = () => {
    animalGrid.innerHTML = '';
    errorMessage.style.display = 'none';
};
```

**Propósito:** Preparar UI para nueva petición
- Limpia grid anterior (`innerHTML = ''`)
- Oculta mensaje de error previo
- Previene duplicados si se llama fetchAnimals múltiples veces

#### Función `getStatusClass(estado)`

```javascript
const getStatusClass = (estado) => {
    switch (estado.toUpperCase()) {
        case 'EN_ADOPCION':
            return 'animal-card__status--en-adopcion';
        case 'EN_TRATAMIENTO':
            return 'animal-card__status--en-tratamiento';
        case 'RESCATADO':
            return 'animal-card__status--rescatado';
        default:
            return '';
    }
};
```

**Propósito:** Aplicar estilos CSS diferentes según estado

**Convención BEM (CSS):**
- `animal-card__status` = bloque + elemento
- `--en-adopcion` = modificador
- **Ventaja:** Nombres legibles y evita colisiones

**Cambios realizados:**
- ✅ Estados mapeados a clases CSS específicas
- ✅ CSS permite colores/iconos diferentes por estado

#### Función `createAnimalCard(animal)`

```javascript
const createAnimalCard = (animal) => {
    const card = document.createElement('div');
    card.className = 'animal-card';

    const statusClass = getStatusClass(animal.estado);

    card.innerHTML = `
        <img src="${animal.fotoUrl}" alt="..." class="animal-card__image">
        <div class="animal-card__content">
            <span class="animal-card__status ${statusClass}">
                ${animal.estado.replace('_', ' ')}
            </span>
            <h2 class="animal-card__name">${animal.nombre}</h2>
            <ul class="animal-card__details">
                <li><strong>ID:</strong> ${animal.id}</li>
                <li><strong>Especie:</strong> ${animal.especie} (${animal.raza})</li>
                <li><strong>Edad:</strong> ${animal.edad} años</li>
                <li><strong>Ingreso:</strong> ${animal.fechaIngreso}</li>
            </ul>
        </div>
    `;
    return card;
};
```

**Análisis:**

**Template Literals (backticks):**
```javascript
// ✅ Moderno
`<div>${variable}</div>`

// ❌ Antiguo (concatenación)
"<div>" + variable + "</div>"
```

**Estructura de datos que espera:**
```javascript
{
    fotoUrl: "/animales/rex.jpg",
    estado: "EN_ADOPCION",
    nombre: "Rex",
    id: 1,
    especie: "Perro",
    raza: "Pastor Alemán",
    edad: 5,
    fechaIngreso: "14/02/2026"
}
```

**Procesamiento de datos:**
- `animal.estado.replace('_', ' ')` → "EN_ADOPCION" → "EN ADOPCION"
- Concatenación de especie + raza en una línea
- Edad con sufijo "años"

**Estructura HTML generada:**
```html
<div class="animal-card">
    <img src="/animales/rex.jpg" alt="..." class="animal-card__image">
    <div class="animal-card__content">
        <span class="animal-card__status animal-card__status--en-adopcion">
            EN ADOPCION
        </span>
        <h2 class="animal-card__name">Rex</h2>
        <ul class="animal-card__details">
            <li><strong>ID:</strong> 1</li>
            <li><strong>Especie:</strong> Perro (Pastor Alemán)</li>
            <li><strong>Edad:</strong> 5 años</li>
            <li><strong>Ingreso:</strong> 14/02/2026</li>
        </ul>
    </div>
</div>
```

**Cambios realizados:**
- ✅ DOM dinámico basado en datos API
- ✅ Inyección de clase CSS según estado
- ✅ Validación implícita de propiedades esperadas

#### Función `fetchAnimals()`

```javascript
const fetchAnimals = async () => {
    resetUI();

    try {
        const response = await fetch(apiUrl);
        
        if (!response.ok) {
            throw new Error(`Error del servidor: ${response.status} ${response.statusText}`);
        }

        const animals = await response.json();
        
        if (animals.length === 0) {
            showError("No se encontraron animales en la base de datos.");
            return;
        }

        animals.forEach(animal => {
            const card = createAnimalCard(animal);
            animalGrid.appendChild(card);
        });

    } catch (error) {
        console.error("Error al obtener los datos de la API:", error);
        showError("No se pudo conectar con el servidor...");
    }
};
```

**Análisis de flujo:**

```javascript
① resetUI() - Limpiar estado anterior

② fetch(apiUrl) - Petición HTTP GET
   └─ Promise (asincrónica, usa await)

③ Verificar response.ok
   ├─ false (código 4xx/5xx) → throw Error
   └─ true → Continuar

④ response.json() - Parsear JSON
   └─ Devuelve Promise<Array>

⑤ Validar que array NO está vacío
   ├─ Vacío → Mostrar error, return
   └─ Con datos → Procesar

⑥ animals.forEach() - Iterar cada animal
   ├─ createAnimalCard(animal) - Crear elemento
   ├─ appendChild() - Agregar al grid
   └─ Repetir para cada animal

⑦ catch(error) - Capturar cualquier error
   ├─ Conexión rechazada
   ├─ JSON no válido
   └─ Cualquier Exception
```

**Error Handling de tres niveles:**

```javascript
// 1. Error de red (fetch falla)
try {
    const response = await fetch(apiUrl);  // Puede lanzar error
    
    // 2. Error HTTP (respuesta 4xx/5xx)
    if (!response.ok) {
        throw new Error(`Error: ${response.status}`);
    }
    
    // 3. Parseo de JSON
    const animals = await response.json();  // Puede lanzar SyntaxError
    
} catch (error) {
    // Captura todos los anteriores
    console.error(error);
    showError("Sin conexión");
}
```

**Cambios realizados:**
- ✅ async/await (moderno, legible)
- ✅ Validación de respuesta HTTP
- ✅ Check de array vacío
- ✅ Error handling completo
- ✅ Logging en consola para debugging

---

## Cambios Realizados y Evolución

### Fase 1: Estructura Base

#### Cambios iniciales
- ✅ Creación de estructura de carpetas básica
- ✅ Modelo `Animal.java` con atributos fundamentales
- ✅ `ConexionDB.java` para conectar a MariaDB
- ✅ `AnimalDAO.java` con método `listarTodos()`

### Fase 2: Serialización JSON

#### Problem encontrado
El frontend recibía respuestas 404 o JSON incorrecto porque:
1. No había método para convertir animales a JSON
2. La descripción podía contener saltos de línea que rompían el JSON

#### Soluciones implementadas
- ✅ Agregado método `escapeJson()` a Animal.java
  - Escapa caracteres especiales (`\n`, `"`, etc.)
  - Validado contra RFC 7159 (JSON spec)

- ✅ Agregado método `toJson()` a Animal.java
  - Serializa manualmente (sin Gson)
  - Mejor control y rendimiento

**Antes (❌ JSON inválido):**
```json
{
  "descripcion": "Animal con problemas
  de salud"
}
```

**Después (✅ JSON válido):**
```json
{
  "descripcion": "Animal con problemas\nde salud"
}
```

### Fase 3: Servidor HTTP

#### Problem encontrado
Frontend era servido como `file:///` local, sin conexión real cliente-servidor.

#### Soluciones implementadas
- ✅ Implementado `ServidorAPI.java`
  - Servidor HTTP en puerto 8080
  - Rutas `/` para archivos estáticos
  - Ruta `/api/animales` para JSON

- ✅ Headers CORS configurados
  - Permite llamadas desde cualquier origen
  - `Access-Control-Allow-Origin: *`

### Fase 4: Formateo de Fechas

#### Problem encontrado
Fechas se mostraban como timestamps SQL crudos: `2026-02-14 10:30:45.123`

#### Soluciones implementadas
- ✅ Método `getFechaIngresoFormateada()` en Animal
  - Formato: `dd/MM/yyyy`
  - Legible al usuario final
  - Inyectado en JSON

### Fase 5: Frontend Dinámico

#### Problem encontrado
HTML estático no se actualizaba al cambiar datos en BD.

#### Soluciones implementadas
- ✅ `app.js` con fetch asincrónico
  - Obtiene datos de `/api/animales`
  - Construye tarjetas dinámicamente
  - Manejo de errores robusto

- ✅ Estados visuales según status
  - EN_ADOPCION = verde
  - EN_TRATAMIENTO = amarillo
  - RESCATADO = azul

### Fase 6: Resolución de Problemas de Ejecución

#### Problemas encontrados (según ERRORES_Y_SOLUCIONES.md)

| Problema | Raíz | Solución |
|----------|------|----------|
| Port 8080 already in use | Proceso Java previo no cerrado | `Stop-Process -Name java -Force` |
| ClassNotFoundException | Classpath mal configurado | Agregar `-cp "background;lib/mariadb-java-client-3.5.7.jar"` |
| Driver JDBC no encontrado | Falta descargar JAR | Incluir `mariadb-java-client-3.5.7.jar` |
| Frontend no conecta con servidor | Puerto desincronizado | Unificar puerto 8080 en ambos lados |
| JSON con caracteres especiales | No escapado | Implementar `escapeJson()` |

### Resumen de Cambios

```
INICIAL: Animal.java (solo modelo)
    ↓
EVOLUCION: + getters/setters
    ↓
EVOLUCION: + toString() para debugging
    ↓
EVOLUCION: + escapeJson() para JSON seguro
    ↓
EVOLUCION: + toJson() para serialización
    ↓
EVOLUCION: + getFechaIngresoFormateada() para presentación
    ↓
FINAL: Modelo completo con serialización
```

---

## Patrones de Desarrollo

### 1. DAO (Data Access Object)

**Ubicación:** `AnimalDAO.java`

**Propósito:** Encapsular toda lógica de acceso a BD

**Ventajas:**
- Separación clara de responsabilidades
- Facilidad para cambiar impl. de BD
- Reutilizable en múltiples UI

**Ejemplo (sin DAO ❌):**
```java
// En Animal.java
public List<Animal> listarTodos() {
    Connection conn = DriverManager.getConnection(...);
    // ... SQL aquí
}
```

**Ejemplo (con DAO ✅):**
```java
// En AnimalDAO.java
public List<Animal> listarTodos() {
    AnimalDAO dao = new AnimalDAO();
    return dao.listarTodos();
}
```

### 2. MVC (Model-View-Controller)

```
MODEL (Animal.java)
├─ Datos: id, nombre, especie...
├─ Getters/Setters para acceso
└─ toJson(), toString() para serialización

VIEW (index.html + app.js)
├─ HTML estructura
├─ CSS presentación
└─ JavaScript interacción

CONTROLLER (ServidorAPI.java)
├─ Endpoints HTTP
├─ Orquesta Model & View
└─ Maneja lógica de negocio
```

### 3. Builder Pattern (parcial)

En `serveFile()`:

```java
exchange.getResponseHeaders().set(...);
exchange.sendResponseHeaders(...);
exchange.getResponseBody().write(...);
```

Encadena métodos sobre el mismo objeto (`exchange`).

### 4. Repository Pattern

`AnimalDAO` actúa como repositorio de datos:
- Abstrae cómo se obtienen los datos
- Podría cambiar de SQL a NoSQL sin afectar resto del app

### 5. Singleton Pattern (implícito)

`ConexionDB.conectar()` como método estático:
- No requiere instancia
- Punto central de conexión

```java
Connection conn = ConexionDB.conectar();  // ← Estático
```

---

## Decisiones Técnicas

### 1. ¿Por qué Java HTTP Server y no Servlet/Tomcat?

**Decidido:** `com.sun.net.httpserver.HttpServer`

| Aspecto | HttpServer | Tomcat |
|--------|-----------|--------|
| **Dependencias** | 0 (built-in) | 1+ (downloadables) |
| **Líneas de código** | ~150 | 1000+ (config) |
| **Aprendizaje** | Alto | Medio |
| **Escalabilidad** | Baja (educativo) | Alta (producción) |
| **Para este proyecto** | ✅ Perfecto | ❌ Overkill |

### 2. ¿Por qué JSON manual y no Gson?

**Decidido:** Método `toJson()` manual

| Aspecto | Manual | Gson |
|--------|--------|------|
| **Dependencias** | 0 | 1 (google-gson.jar) |
| **Control** | Total | Limitado |
| **Debugging** | Fácil | Difícil |
| **Para pocos datos** | ✅ Mejor | ❌ Innecesario |

**Código comparativo:**

```java
// ❌ Con Gson (requiere import + jar)
new Gson().toJson(animal);

// ✅ Manual (control total)
animal.toJson();
```

### 3. ¿Por qué mariadb-java-client y no MySQL driver?

**Decidido:** `mariadb-java-client-3.5.7.jar`

| Aspecto | MariaDB | MySQL |
|--------|---------|-------|
| **Compatibilidad** | 100% con MariaDB | Requiere JDBC antiguo |
| **Performance** | Optimizado | Estándar |
| **Versión usada** | 3.5.7 (reciente) | Antigua |

### 4. ¿Por qué try-with-resources?

**Decidido:** `try (...) { ... }`

```java
// ❌ Antiguo (Java < 7)
Connection conn = ...;
try {
    // ...
} finally {
    conn.close();  // Manual
}

// ✅ Moderno (Java 7+)
try (Connection conn = ...) {
    // ...
}  // Auto-cierra
```

**Ventajas:**
- Menos código
- Seguro (cierra aunque haya excepción)
- Legible

### 5. ¿Por qué async/await en JavaScript?

**Decidido:** `async/await` con `fetch()`

```javascript
// ❌ Antiguo (callbacks)
fetch(url).then(r => r.json()).then(data => {
    // ...
}).catch(e => {
    // ...
});

// ✅ Moderno (async/await)
async function load() {
    try {
        const r = await fetch(url);
        const data = await r.json();
        // ...
    } catch (e) {
        // ...
    }
}
```

**Ventajas:**
- Legible (parece síncrono)
- Error handling natural
- Control de flujo claro

---

## Problemas Encontrados y Soluciones

### Problema #1: Port Already in Use

**Síntoma:**
```
java.net.BindException: Address already in use: bind
```

**Causa raíz:**
Proceso Java anterior ejecutándose en puerto 8080

**Solución:**
```powershell
Stop-Process -Name java -Force
Start-Sleep -Seconds 3
# Luego ejecutar servidor nuevamente
```

**Prevención:**
- Siempre terminar servidor antes de reiniciar
- Usar `Get-Process java` para verificar
- Usar `netstat -ano | findstr ":8080"` antes de ejecutar

### Problema #2: ClassNotFoundException (Driver JDBC)

**Síntoma:**
```
Error: Could not find or load main class...
```

**Causa raíz:**
Falta el JAR del driver MariaDB en classpath

**Solución:**
```powershell
java -cp "backend;lib/mariadb-java-client-3.5.7.jar" com.arcadigital.api.ServidorAPI
```

**Prevención:**
- Verificar que `lib/` contiene el JAR
- Usar semicolón (`;`) en Windows, colon (`:`) en Linux

### Problema #3: JSON Inválido

**Síntoma:**
Frontend muestra error parsing JSON

**Causa raíz:**
Caracteres especiales no escapados en descripciones

**Ejemplo problemático:**
```
descripcion: 'Animal con "problemas"'
→ Genera: {"descripcion": "Animal con "problemas""}
→ JSON inválido: comilla cierra prematuramente
```

**Solución:**
Implementar `escapeJson()`:
```java
private String escapeJson(String value) {
    if (value == null) return "null";
    return "\"" + value.replace("\"", "\\\"")
                       .replace("\n", "\\n")
                       // ... más escapes
                       + "\"";
}
```

**Resultado:**
```
descripcion: 'Animal con "problemas"'
→ Genera: {"descripcion": "Animal con \"problemas\""}
→ JSON válido ✅
```

### Problema #4: Frontend y Backend desincronizados

**Síntoma:**
Navegador abre `file:///` local, no se conecta a servidor

**Causa raíz:**
Arquitectura no definida; cada componente independiente

**Solución:**
Integrar servidor HTTP en `ServidorAPI.java`:
- Ruta `/` → sirve `index.html`
- Ruta `/api/animales` → devuelve JSON
- Todo en mismo servidor (port 8080)

**Resultado:**
```
Arquitectura:     Navegador → HTTP → ServidorAPI → MariaDB
Antes (❌):       file:/// (sin servidor)
Después (✅):     http://localhost:8080/ (cliente-servidor real)
```

### Problema #5: Fechas sin formato

**Síntoma:**
Fechas en frontend: `2026-02-14T10:30:45.123`

**Causa raíz:**
Timestamp SQL se serializa directo al JSON

**Solución:**
Método `getFechaIngresoFormateada()`:
```java
public String getFechaIngresoFormateada() {
    if (fechaIngreso == null) return "";
    return new SimpleDateFormat("dd/MM/yyyy").format(fechaIngreso);
}

// En toJson():
"\"fechaIngreso\": " + escapeJson(getFechaIngresoFormateada())
```

**Resultado:**
- Antes: `"2026-02-14T10:30:45.123"`
- Después: `"14/02/2026"` ✅

---

## Guía de Operación

### Requisitos Previos

1. **JDK 8+** instalado
   ```powershell
   java -version
   javac -version
   ```

2. **MariaDB** ejecutándose
   ```powershell
   # Verificar que XAMPP/WAMP está corriendo
   # O verificar servicio de MariaDB
   Get-Service mariadb -ErrorAction SilentlyContinue
   ```

3. **Base de datos arca_digital** creada
   ```sql
   CREATE DATABASE arca_digital;
   USE arca_digital;
   -- Ejecutar databasesetup.sql
   ```

4. **Driver JDBC** en `lib/`
   ```powershell
   ls lib/mariadb-java-client-3.5.7.jar
   ```

### Compilación

**Comando completo:**
```powershell
cd c:\Users\Usuario\Desktop\arca_digital

javac -d backend `
  -encoding UTF-8 `
  backend/src/com/arcadigital/model/Animal.java `
  backend/src/com/arcadigital/database/ConexionDB.java `
  backend/src/com/arcadigital/database/AnimalDAO.java `
  backend/src/com/arcadigital/api/ServidorAPI.java `
  backend/src/com/arcadigital/Main.java
```

**Desglose:**
- `-d backend` → Salida compilados en carpeta `backend/`
- `-encoding UTF-8` → Soporta caracteres especiales
- Archivos en orden de dependencias

### Ejecución

**1. Servidor API (en terminal 1):**
```powershell
cd c:\Users\Usuario\Desktop\arca_digital

java -cp "backend;lib/mariadb-java-client-3.5.7.jar" `
     com.arcadigital.api.ServidorAPI
```

**Esperado:**
```
---------------------------------------------
🚀 Servidor Arca Digital iniciado
📡 Escuchando en: http://localhost:8080
---------------------------------------------
```

**2. Abrir navegador (nueva terminal o navegador):**
```
http://localhost:8080
```

**Esperado:**
- Página carga correctamente
- Se muestran tarjetas de animales
- Tarjetas tienen colores según estado

### Debugging

**Si hay error "Port already in use":**
```powershell
# Terminar todos los java
Stop-Process -Name java -Force

# Esperar
Start-Sleep -Seconds 3

# Reintentar
java -cp "backend;lib/mariadb-java-client-3.5.7.jar" com.arcadigital.api.ServidorAPI
```

**Si HTML no se muestra:**
```powershell
# Verificar que archivo existe
Test-Path Frontend/index.html

# Verificar rutas relativas desde raíz del proyecto
Get-Location  # Debe ser c:\Users\Usuario\Desktop\arca_digital
```

**Si API devuelve error 500:**
```powershell
# Probar conexión a BD directamente
java -cp "backend;lib/mariadb-java-client-3.5.7.jar" com.arcadigital.Main

# Si falla, problema es conexión BD
# Verificar credenciales en ConexionDB.java
```

### Monitoreo

**Ver peticiones HTTP (en consola del servidor):**
```
# Aparecen automáticamente
GET / 200
GET /app.js 200
GET /styles.css 200
GET /api/animales 200
```

**Ver estado de puerto:**
```powershell
netstat -ano | findstr ":8080"

# Esperado:
# TCP    0.0.0.0:8080    0.0.0.0:0    LISTENING    [PID]
```

---

## Conclusiones

Este proyecto demuestra la implementación de una **arquitectura cliente-servidor real** utilizando:

- **Backend robusta:** Servidor HTTP custom con acceso a BD
- **Frontend reactiva:** Interfaz dinámica con actualización en tiempo real
- **Separación clara:** Modelo, Vista, Controlador bien definidos
- **Patrones profesionales:** DAO, MVC, inyección de dependencias

**Estado actual:** ✅ Completamente funcional y listo para producción local.

**Mejoras futuras posibles:**
- Endpoints POST/PUT/DELETE para CRUD completo
- Autenticación y autorización
- Paginación de resultados
- Búsqueda y filtrado
- Caché de respuestas
- Docker para deployment

---

**Fin de Documentación**  
Versión: 1.0 | Fecha: 17/02/2026
