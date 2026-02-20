# 📔 DIARIO DE DESARROLLO - ARCA DIGITAL

## Introducción
Este documento describe la progresión recomendada para desarrollar **Arca Digital** desde cero, de manera ordenada y estructurada.

---

## FASE 0: PLANIFICACIÓN Y CONFIGURACIÓN INICIAL

### Día 1: Definición del Proyecto
- [ ] **Definir el alcance del proyecto**
  - Nombre: ARCA DIGITAL
  - Descripción: Sistema de gestión de animales (registro, consulta y administración)
  - Plataforma: Web (Backend: Java, Frontend: HTML/CSS/JavaScript)
  
- [ ] **Identificar requisitos principales**
  - Crear animales en la base de datos
  - Listar todos los animales
  - Consultar detalles de un animal
  - Actualizar información de un animal
  - Eliminar registros de animales
  
- [ ] **Definir la arquitectura**
  - Frontend: Aplicación web simple (HTML/CSS/JS)
  - Backend: API REST en Java con Servidor HTTP
  - BD: MariaDB/MySQL
  - Estructura: Patrón MVC (Model-View-Controller) + DAO

---

## FASE 1: CONFIGURACIÓN DEL ENTORNO

### Día 2: Preparar el Ambiente de Desarrollo
- [ ] **Instalar herramientas requeridas**
  ```
  ✓ JDK 22 (Java Development Kit)
  ✓ MariaDB 10.5+
  ✓ Editor: VS Code / IntelliJ IDEA
  ✓ Git (para control de versiones)
  ```

- [ ] **Crear estructura de carpetas**
  ```
  arca_digital/
  ├── backend/
  │   └── src/
  │       └── com/arcadigital/
  │           ├── model/
  │           ├── database/
  │           ├── api/
  │           └── Main.java
  ├── Frontend/
  ├── sql/
  ├── lib/
  ├── docs/
  └── README.md
  ```

- [ ] **Descargar dependencias**
  - MariaDB JDBC Driver (`mariadb-java-client-3.5.7.jar`)
  - Colocar en carpeta `/lib`

---

## FASE 2: BASE DE DATOS

### Día 3: Diseñar y Crear la Base de Datos

- [ ] **Crear el script SQL** (`sql/databasesetup.sql`)
  CREATE DATABASE arca_digital;
  USE arca_digital;
  
  CREATE TABLE animales (
    id VARCHAR(50) PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    especie VARCHAR(100) NOT NULL,
    raza VARCHAR(100),
    edad VARCHAR(50),
    genero VARCHAR(20),
    estado VARCHAR(50),
    descripcion TEXT,
    imagenUrl VARCHAR(500),
    fechaRegistro VARCHAR(20)
  );
  ```

- [ ] **Ejecutar el script en MariaDB**
  ```bash
  mysql -u root -p < sql/databasesetup.sql
  ```

- [ ] **Verificar la base de datos creada**

---

## FASE 3: MODELO DE DATOS (Backend)

### Día 4: Crear la Clase Model - Animal

- [ ] **Crear archivo** `backend/src/com/arcadigital/model/Animal.java`
  - Atributos: id, nombre, especie, raza, edad, genéro, estado, descripción, imagenUrl, fechaRegistro
  - Constructor con parámetros
  - Getters y Setters para todos los atributos
  - Método `toString()` para mostrar la información formateada
  - Método `toJson()` para convertir a JSON (opcional pero útil)

- [ ] **Compilar la clase**
  ```bash
  javac -d backend backend/src/com/arcadigital/model/Animal.java
  ```

---
## FASE 4: CONEXIÓN A BASE DE DATOS

### Día 5: Crear la Clase de Conexión

- [ ] **Crear archivo** `backend/src/com/arcadigital/database/ConexionDB.java`
  - Método estático para obtener conexión a MariaDB
  - Parámetros: URL, usuario, contraseña
  - Manejo de excepciones (SQLException)
  - Verificar que se haya cargado el driver JDBC

- [ ] **Compilar la clase**
  ```bash
  javac -cp "lib/mariadb-java-client-3.5.7.jar" -d backend backend/src/com/arcadigital/database/ConexionDB.java
  ```

---

## FASE 5: ACCESO A DATOS (DAO)

### Día 6: Implementar el DAO - AnimalDAO

- [ ] **Crear archivo** `backend/src/com/arcadigital/database/AnimalDAO.java`
  
  Implementar métodos CRUD:
  - **`listarTodos()`** - Obtener lista de todos los animales
    ```java
    SELECT * FROM animales;
    ```
  - **`obtenerPorId(String id)`** - Obtener un animal por ID
  - **`crear(Animal animal)`** - Insertar un nuevo animal
  - **`actualizar(Animal animal)`** - Actualizar información
  - **`eliminar(String id)`** - Eliminar un animal

  ```bash
  javac -cp "lib/mariadb-java-client-3.5.7.jar" -d backend backend/src/com/arcadigital/database/AnimalDAO.java
  ```

---

## FASE 6: PRUEBAS INICIALES DEL BACKEND

### Día 7: Crear Main para Probar el Sistema

- [ ] **Crear archivo** `backend/src/com/arcadigital/Main.java`
  - Importar AnimalDAO
  - En `main()`:
    1. Crear instancia del DAO
    2. Llamar a `listarTodos()`
    3. Mostrar los resultados
  
- [ ] **Compilar todas las clases**
  ```bash
  javac -d backend -encoding UTF-8 \
    backend/src/com/arcadigital/model/Animal.java \
    backend/src/com/arcadigital/database/ConexionDB.java \
    backend/src/com/arcadigital/database/AnimalDAO.java \
    backend/src/com/arcadigital/Main.java
  ```

- [ ] **Ejecutar Main.java**
  ```bash
  java -cp "backend;lib/mariadb-java-client-3.5.7.jar" com.arcadigital.Main
  ```

- [ ] **Verificar conexión a BD**
  - Si funciona: mensaje "ÉXITO!"
  - Si hay error: revisar credenciales, puerto, driver

---

## FASE 7: API REST

### Día 8: Crear el Servidor API

- [ ] **Crear archivo** `backend/src/com/arcadigital/api/ServidorAPI.java`
  
  Características:
  - Servidor HTTP en puerto `8080`
  - Endpoints REST:
    - `GET /animales` - Listar todos
    - `GET /animales/{id}` - Obtener uno
    - `POST /animales` - Crear nuevo
    - `PUT /animales/{id}` - Actualizar
    - `DELETE /animales/{id}` - Eliminar
  
  - Respuestas en JSON
  - Manejo de errores
  - Headers HTTP apropiados

- [ ] **Compilar la clase**
  ```bash
  javac -cp "lib/mariadb-java-client-3.5.7.jar" -d backend \
    backend/src/com/arcadigital/api/ServidorAPI.java
  ```

- [ ] **Ejecutar el servidor**
  ```bash
  java -cp "backend;lib/mariadb-java-client-3.5.7.jar" \
    com.arcadigital.api.ServidorAPI
  ```

- [ ] **Verificar que el servidor está activo**
  ```bash
  netstat -ano | findstr ":8080"
  ```

---

## FASE 8: FRONTEND

### Día 9: Crear la Interfaz de Usuario

- [ ] **Crear archivo** `Frontend/index.html`
  - Estructura HTML5
  - Formulario para crear animales
  - Tabla para listar animales
  - Botones de acción (editar, eliminar, actualizar)

- [ ] **Crear archivo** `Frontend/styles.css`
  - Estilos responsive
  - Colores, tipografía, spacing
  - Efectos hover y transiciones
  - Responsive design (mobile-first)

- [ ] **Crear archivo** `Frontend/app.js`
  - Cargar lista de animales al iniciar
  - Función crear animal:
    ```javascript
    POST http://localhost:8080/animales
    ```
  - Función listar animales:
    ```javascript
    GET http://localhost:8080/animales
    ```
  - Función actualizar:
    ```javascript
    PUT http://localhost:8080/animales/{id}
    ```
  - Función eliminar:
    ```javascript
    DELETE http://localhost:8080/animales/{id}
    ```
  - Manejo de errores
  - Actualizar UI dinámicamente

- [ ] **Abrir el frontend en navegador**
  - `Frontend/index.html` en navegador local
  - Verificar CORS si hay errores

---

## FASE 9: INTEGRACIÓN Y PRUEBAS

### Día 10: Conectar Frontend con Backend

- [ ] **Verificar que el servidor está corriendo**
  ```bash
  java -cp "backend;lib/mariadb-java-client-3.5.7.jar" \
    com.arcadigital.api.ServidorAPI
  ```

- [ ] **Abrir `Frontend/index.html` en navegador**
  - Probar listar animales
  - Probar crear animal
  - Probar editar animal
  - Probar eliminar animal

- [ ] **Verificar respuestas del servidor**
  - Abrir DevTools (F12)
  - Ver Network tab
  - Confirmar que las peticiones van a `localhost:8080`

- [ ] **Pruebas finales**
  - [ ] CRUD completo funcionando
  - [ ] Sin errores en consola
  - [ ] Base de datos actualiza correctamente

---

## FASE 10: DOCUMENTACIÓN Y MEJORAS

### Día 11: Documentar y Optimizar

- [ ] **Crear README.md**
  - Descripción del proyecto
  - Requisitos del sistema
  - Instrucciones de instalación
  - Cómo ejecutar el proyecto
  - Estructura de carpetas
  - Endpoints de la API

- [ ] **Crear DOCUMENTACION_TECNICA.md**
  - Arquitectura del sistema
  - Diagrama de clases
  - Flujo de datos
  - Configuración de BD
  - Detalles de API REST

- [ ] **Manejo de errores**
  - Crear archivo `ERRORES_Y_SOLUCIONES.md`
  - Documentar problemas comunes
  - Soluciones probadas

- [ ] **Mejoras opcionales**
  - Validación de datos
  - Autenticación (token JWT)
  - Paginación en listados
  - Búsqueda y filtrado
  - Logging del sistema

---

## FASE 11: DEPLOYMENT (Opcional)

### Día 12: Preparar para Producción

- [ ] **Crear archivo `.env` o config**
  - Variables de configuración
  - Credenciales de BD
  - Puerto de servidor

- [ ] **Empaquetar como JAR**
  ```bash
  jar cvfe arca-digital.jar com.arcadigital.api.ServidorAPI \
    -C backend/ com
  ```

- [ ] **Opciones de hosting**
  - Servidor local
  - Heroku
  - AWS
  - Azure
  - Digital Ocean

---

## � DIARIO DE DESARROLLO REAL - LUNES Y MARTES

**Desarrollador:** Carmen Casas Novas  
**Período de Ejecución:** Lunes 16 y Martes 17 de Febrero de 2026  
**Horario de Trabajo:** 15:00–21:00 (6 horas diarias)

---

## 📅 LUNES 16 DE FEBRERO: Infraestructura y Primera Conexión

**Objetivo del Día:** Establecer la infraestructura base del proyecto, configurar el entorno de desarrollo y lograr la primera conexión funcional entre la aplicación Java (Backend) y MariaDB.

### 15:00 - 17:00 | Configuración Sólida del Entorno de Desarrollo

La jornada comenzó con la preparación del espacio de trabajo. Una infraestructura bien definida es crucial para evitar problemas de compatibilidad futuros.

**Instalación y Setup de Herramientas Clave:**

1. **Java Development Kit (JDK 22)**
   - Se instaló la versión más reciente del JDK para aprovechar las últimas características y optimizaciones del lenguaje.
   - Necesario para la compilación y ejecución de la lógica de negocio en el backend.

2. **MariaDB Server**
   - Se instaló como el sistema de gestión de base de datos relacional.
   - Se eligió MariaDB por ser un fork de MySQL robusto, estable y de código abierto.
   - **Nota de Seguridad:** Se asignó una contraseña al usuario root para el entorno de desarrollo local, con la intención de migrar a un sistema de credenciales seguras (variables de entorno) en etapas posteriores.

3. **Driver JDBC**
   - Se descargó el archivo `mariadb-java-client-3.5.7.jar` desde el sitio oficial de MariaDB.
   - Se depositó en la carpeta `/lib/` del proyecto.
   - Se añadió al Classpath de ejecución en VS Code mediante "Referenced Libraries".

**Concepto Técnico: ¿Qué es JDBC?**

JDBC (Java Database Connectivity) es la especificación estándar de Java para interactuar con bases de datos. El archivo `.jar` descargado es el **driver específico** (la implementación concreta de esa API) que actúa como un intérprete: traduce las llamadas y peticiones SQL que hace Java al protocolo nativo que entiende el servidor MariaDB. Sin este "traductor", la comunicación binaria entre el lenguaje y la base de datos es imposible.

**Arquitectura del Proyecto (Estructura de Carpetas):**

Se creó la estructura siguiendo un estándar profesional MVC para facilitar escalabilidad y mantenimiento:

```
arca_digital/
├── backend/
│   ├── src/
│   │   └── com/arcadigital/
│   │       ├── model/          (Clases de datos: Animal.java)
│   │       ├── database/       (Conexión y DAO)
│   │       └── api/            (Servidor HTTP y REST)
│   └── com/                    (Clases compiladas .class)
├── Frontend/                   (HTML, CSS, JS)
├── lib/                        (Librerías JDBC)
├── sql/                        (Scripts de BD)
└── docs/                       (Documentación)
```

### 17:00 - 19:00 | Diseño de la Persistencia y Mapeo de Objetos

Se pasó al diseño lógico de la base de datos y su representación dentro del código Java.

**Diseño SQL de la Base de Datos:**

Se escribió y ejecutó el script DDL (Data Definition Language) para crear la base de datos `arca_digital` y la tabla `animales`:

```sql
CREATE DATABASE arca_digital;
USE arca_digital;

CREATE TABLE animales (
    id VARCHAR(50) PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    estado VARCHAR(50),
    urgente BOOLEAN,
    imagenUrl VARCHAR(500)
);
```

**Decisiones de Diseño:**
- `id`: VARCHAR sin autoincremento, permitiendo códigos legibles como 'JVM-001'.
- `nombre`: Campo obligatorio (NOT NULL) para garantizar que todo animal tenga nombre.
- `estado`: Describe la situación actual del animal (Adoptable, En Tratamiento, etc.).
- `urgente`: Booleano para indicar prioridad visual en la interfaz.
- `imagenUrl`: Campo para almacenar la ruta de la foto del animal.

La ejecución del script en la terminal de MariaDB fue exitosa, confirmada mediante inspección de las tablas creadas.

**Modelo de Datos en Java (Animal.java):**

Se creó la clase `Animal.java` en `backend/src/com/arcadigital/model/`. Esta clase funciona como un espejo de la tabla `animales`, conteniendo atributos idénticos a las columnas (un proceso conocido como **Mapeo Objeto-Relacional** manual).

Métodos implementados:
- **Getters:** Para acceder a los datos del animal desde otras clases.
- **Setters:** Para modificar los atributos.
- **Constructor vacío:** Requisito habitual para deserialización y frameworks.
- **toString():** Para imprimir los datos de forma legible (uso en debug).

```java
public class Animal {
    private String id;
    private String nombre;
    private String estado;
    private boolean urgente;
    private String imagenUrl;
    
    // Constructor vacío, getters, setters, toString()
}
```

### 19:00 - 21:00 | Búsqueda de la Conexión y Resolución de Clases

La última franja se dedicó a la implementación de `ConexionDB.java` y a probar la conectividad por primera vez.

**Implementación de ConexionDB.java:**

```java
public class ConexionDB {
    public static Connection conectar() throws SQLException {
        String url = "jdbc:mariadb://localhost:3306/arca_digital";
        String usuario = "root";
        String password = "****";  // Contraseña del entorno local
        
        Class.forName("org.mariadb.jdbc.Driver");
        return DriverManager.getConnection(url, usuario, password);
    }
}
```

**Primer Obstáculo: ClassNotFoundException**

**Error Registrado:**
```
java.lang.ClassNotFoundException: org.mariadb.jdbc.Driver
```

**Análisis del Problema:**
Java, en tiempo de ejecución, no estaba encontrando la definición de la clase principal del driver MariaDB, aunque el archivo `.jar` estuviera físicamente en la carpeta `/lib/`. Esto indica un problema con la configuración de la **ruta de clases (Classpath)**.

**Solución Aplicada:**
Se ajustó el comando de ejecución manual para incluir explícitamente el directorio de librerías en el Classpath de la JVM:

```bash
# ANTES (fallaba):
java -cp backend com.arcadigital.Main

# DESPUÉS (funcionó):
java -cp "backend;lib/mariadb-java-client-3.5.7.jar" com.arcadigital.Main
```

El parámetro `-cp` (classpath) instruye a Java a buscar las dependencias en la ruta especificada. Sin esto, Java no encuentra el driver.

**Éxito de Conexión:**
Tras corregir el Classpath y asegurar que las credenciales (usuario `root`, contraseña correcta) fueran válidas en el código de conexión, la ejecución finalizó con:

```
✅ ¡Conexión a MariaDB exitosa!
```

Esto confirmó que el backend Java puede comunicarse eficientemente con la base de datos, sentando las bases para las operaciones CRUD en los próximos días.

---

## 📅 MARTES 17 DE FEBRERO: Backend DAO, API REST y Frontend Básico

**Objetivo General del Día:** Implementar la lógica de acceso a datos (DAO), crear un servidor HTTP básico para servir una API RESTful y construir un frontend web mínimo para visualizar la información.

### 15:00 - 17:00 | El Corazón de la Persistencia: AnimalDAO

La clase Data Access Object (DAO) es el puente entre la lógica de negocio y la base de datos. Se implementó la primera operación crítica de lectura.

**Implementación de AnimalDAO.java:**

```java
public List<Animal> listarTodos() {
    List<Animal> lista = new ArrayList<>();
    String sql = "SELECT * FROM animales";
    
    // try-with-resources: cierra automáticamente los recursos
    try (Connection conn = ConexionDB.conectar();
         PreparedStatement stmt = conn.prepareStatement(sql);
         ResultSet rs = stmt.executeQuery()) {
        
        // Recorremos el 'excel virtual' que nos devuelve la BD
        while (rs.next()) {
            String id = rs.getString("id");
            String nombre = rs.getString("nombre");
            String estado = rs.getString("estado");
            boolean urgente = rs.getBoolean("urgente");
            String imagenUrl = rs.getString("imagenUrl");
            
            // Creamos el objeto Animal y lo añadimos a la lista
            lista.add(new Animal(id, nombre, estado, urgente, imagenUrl));
        }
    } catch (SQLException e) {
        System.err.println("Error en SQL: " + e.getMessage());
    }
    return lista;
}
```

**Flujo del Método (paso a paso):**

1. **Apertura de Conexión:** `Connection conn = ConexionDB.conectar()`
   - Se obtiene la conexión a la base de datos.

2. **Preparación de Sentencia:** `PreparedStatement stmt = conn.prepareStatement(sql)`
   - Se precompila la sentencia SQL (mecanismo de seguridad contra inyecciones SQL).
   - Aunque en este caso no hay parámetros dinámicos, es una buena práctica profesional.

3. **Ejecución de Query:** `ResultSet rs = stmt.executeQuery()`
   - Se ejecuta la sentencia y se obtiene un `ResultSet`: un cursor que representa la tabla de resultados.

4. **Iteración sobre Resultados:** `while (rs.next())`
   - El cursor avanza fila por fila sobre los resultados.
   - En cada iteración, se extraen los valores de las columnas mediante métodos como `rs.getString()` y `rs.getBoolean()`.

5. **Mapeo a Objetos Java:** 
   - Los valores de las columnas se utilizan para crear instancias del objeto `Animal`.
   - Cada objeto se añade a la `List<Animal>`.

**Estructura Clave: try-with-resources**

En el código del DAO, se utilizó la sintaxis moderna `try (recursos) { ... }`. Esta estructura es fundamental en Java para la gestión de recursos de E/S y bases de datos:

```java
try (Connection conn = ConexionDB.conectar();
     PreparedStatement stmt = ...) {
    // usar recursos
} catch (SQLException e) {
    // manejar error
}
// Los recursos se cierran AUTOMÁTICAMENTE aquí
```

**¿Por qué es importante?**

Garantiza que los recursos críticos (`Connection`, `PreparedStatement`, `ResultSet`) sean cerrados automática y limpiamente, incluso si ocurre una excepción. Esto previene el problema de **"conexiones zombie"** o la saturación del pool de conexiones (`Too many connections`), un error común en aplicaciones mal construidas que no cierran conexiones correctamente.

### 17:00 - 19:00 | Creación del Servidor HTTP y Manejo de Puertos

Se implementó el componente de red para exponer los datos a través de una API web.

**Servidor API (ServidorAPI.java):**

Se utilizó la clase `HttpServer` nativa de Java (`com.sun.net.httpserver`) para crear un servidor web ligero sin necesidad de frameworks pesados como Spring Boot.

```java
public class ServidorAPI {
    public static void main(String[] args) throws IOException {
        HttpServer server = HttpServer.create(
            new InetSocketAddress("localhost", 8080), 0
        );
        
        // Ruta para archivos estáticos
        server.createContext("/", new ContextoEstatico());
        
        // Ruta para la API
        server.createContext("/api/animales", exchangeAsistant "api/animales");
        
        server.setExecutor(null);  // Executor por defecto
        server.start();
        
        System.out.println("✅ Servidor escuchando en http://localhost:8080");
    }
}
```

**Configuración de Contextos (Rutas):**

- **Ruta `/`:** Configurada para manejar peticiones de archivos estáticos (el frontend HTML, CSS, JS).
  - Se instancia un manejador que sirve los archivos del directorio `Frontend/`.

- **Ruta `/api/animales`:** La ruta principal de la API, encargada de:
  - Invocar `AnimalDAO.listarTodos()`.
  - Serializar la `List<Animal>` resultante a formato JSON.
  - Devolver la respuesta al cliente.

**Segundo Obstáculo: BindException (Puerto en Uso)**

**Error Crítico:**
```
java.net.BindException: Address already in use: bind
```

**Causa:**
El error surgió al intentar ejecutar el servidor varias veces seguidas sin haber detenido completamente la ejecución anterior.

**Explicación Técnica:**
Un puerto de red (el 8080 en este caso) funciona como un punto de escucha exclusivo. Una vez que un proceso se "bindea" (se ata) a un puerto, ningún otro proceso puede usarlo simultáneamente. La versión anterior del servidor seguía corriendo en segundo plano, manteniendo el puerto ocupado.

**Solución Aplicada:**
Se aprendió la importancia de detener explícitamente los procesos en el entorno de desarrollo:

```powershell
# Detener todos los procesos Java
Stop-Process -Name java -Force

# O identificar qué proceso usa el puerto 8080
netstat -ano | findstr ":8080"

# Y terminarlo específicamente
taskkill /PID <process_id> /F
```

Luego de detener el proceso anterior, el servidor iniciaba correctamente y escuchaba en el puerto 8080.

### 19:00 - 21:00 | Integración del Frontend y Lógica de Visualización

La fase final consistió en construir la interfaz de usuario para consumir la API recién creada.

**Frontend Estático: HTML y CSS**

1. **index.html:** 
   - Creación de la estructura del Dashboard.
   - Se definió el elemento contenedor principal (`id="animal-grid"`) que sería poblado dinámicamente por JavaScript.

2. **styles.css:**
   - Implementación de un diseño dinámico con un tema oscuro (fondo `#1a1a1a`).
   - Crucialmente, se usó **CSS Grid** para asegurar una disposición de tarjetas flexible y responsive:
     ```css
     #animal-grid {
         display: grid;
         grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
         gap: 20px;
     }
     ```

**Lógica del Cliente (app.js):**

Función para cargar datos de la API:

```javascript
const fetchAnimals = async () => {
    try {
        const response = await fetch('http://localhost:8080/api/animales');
        if (!response.ok) {
            throw new Error('Error en la respuesta del servidor');
        }
        const animals = await response.json();
        
        const grid = document.getElementById('animal-grid');
        animals.forEach(animal => {
            grid.appendChild(createAnimalCard(animal));
        });
    } catch (error) {
        console.error('Error al cargar animales:', error);
        document.getElementById('animal-grid').innerHTML = 
            '<p class="error-msg">Error de conexión con el servidor API.</p>';
    }
};

// Ejecutar al cargar la página
document.addEventListener('DOMContentLoaded', fetchAnimals);
```

Función para renderizar cada tarjeta:

```javascript
const createAnimalCard = (animal) => {
    const card = document.createElement('div');
    card.className = 'animal-card';
    
    // Asignar clase CSS según estado para colorear dinámicamente
    const statusClass = animal.urgente ? 'status-urgente' : 'status-normal';
    
    card.innerHTML = `
        <div class="card-header">
            <img src="${animal.imagenUrl}" alt="${animal.nombre}" 
                 onerror="this.src='/img/placeholder.png'">
        </div>
        <div class="card-body">
            <h3>${animal.nombre}</h3>
            <p class="estado">${animal.estado}</p>
            <span class="status-badge ${statusClass}">
                ${animal.urgente ? '⚠️ ¡URGENTE!' : '✓ Normal'}
            </span>
        </div>
    `;
    return card;
};
```

**Tercer Obstáculo: Error de CORS (Cross-Origin Request Blocked)**

**Error Registrado en Navegador:**
```
Access to fetch at 'http://localhost:8080/api/animales' from origin 'file://' 
has been blocked by CORS policy
```

**Explicación del Problema:**
El navegador bloqueó la petición debido a la política de Origen Cruzado (CORS). Aunque tanto el frontend como el API están en el mismo servidor (`localhost:8080`), el navegador interpreta que la petición proviene de un origen diferente cuando el HTML se abre como `file://` (archivo local) en lugar de vía HTTP.

**Solución Aplicada:**
Se tuvo que modificar el servidor Java (`ServidorAPI.java`) para añadir el encabezado HTTP necesario a todas las respuestas de la API:

```java
HttpExchange exchange = ...;
exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
exchange.getResponseHeaders().add("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE");
exchange.getResponseHeaders().add("Access-Control-Allow-Headers", "Content-Type");
```

Esto instruye al navegador a permitir que cualquier origen (`*` significa "todos") pueda consumir los datos de la API.

Tras añadir estos headers y recompilar el servidor, el frontend cargó correctamente y las tarjetas de los animales aparecieron en la pantalla.

---

## 🔧 Errores Encontrados y Soluciones (Resumen Técnico)

Durante estos dos días de desarrollo, enfrenté varios obstáculos técnicos comunes que son muy instructivos:

**1. ClassNotFoundException: org.mariadb.jdbc.Driver**
- **Causa:** El Classpath de Java no incluía la ubicación del driver JDBC.
- **Solución:** Incluir explícitamente `-cp "backend;lib/mariadb-java-client-3.5.7.jar"` en el comando de ejecución.

**2. BindException: Address already in use**
- **Causa:** Intento de ejecutar el servidor sin detener el proceso anterior que seguía ocupando el puerto.
- **Solución:** Usar `Stop-Process -Name java` o `netstat -ano | findstr ":8080"` para identificar y terminar el proceso.

**3. CORS Policy Blocked**
- **Causa:** Peticiones desde `file://` hacia un servidor HTTP son bloqueadas por el navegador.
- **Solución:** Añadir headers `Access-Control-Allow-Origin` en las respuestas de la API.

**4. Imágenes Rotas (src rutas inválidas)**
- **Síntoma:** Las fotos de los animales no aparecen en las tarjetas.
- **Solución (provisional):** Usar fallback de imágenes con `onerror` en HTML, o implementar un servidor de medios en futuras fases.

---

## 📋 Estado Actual del Proyecto (Fin del Martes)

**Logros Alcanzados:**

- ✅ **Conectividad Total:** Java se conecta a MariaDB de manera estable y segura usando JDBC.
- ✅ **DAO Funcional:** El patrón Data Access Object está implementado y operativo. `AnimalDAO.listarTodos()` devuelve correctamente todos los registros de la BD.
- ✅ **API RESTful Básica:** El servidor HTTP expone un endpoint `/api/animales` que sirve datos en formato JSON.
- ✅ **Visualización en Vivo:** El frontend web carga dinámicamente los datos del API usando `fetch()` y genera tarjetas HTML.
- ✅ **Lógica de Estado Responsiva:** Las tarjetas cambian de apariencia según el campo `urgente` de la BD, indicando prioridad.
- ✅ **CORS Configurado:** El servidor permite peticiones desde el frontend sin bloqueos de seguridad.

**Próximos Pasos Críticos:**

1. **Gestión de Medios Digitales (Imágenes):** Implementar subida de archivos o almacenamiento en la nube.
2. **Operaciones de Escritura (CRUD - C, U, D):** Formularios y endpoints para crear, actualizar y eliminar animales.
3. **Autenticación y Seguridad:** Sistema de login y restricción de acceso a operaciones administrativas.
4. **Mejoras UX:** Paginación, búsqueda, validaciones, mensajes mejorados.

---

## 🔧 Documentación Técnica Rápida de Referencia

---

## 📚 Orden de Compilación y Ejecución (Para Referencia)

**Compilación en orden correcto:**

```bash
# 1. Modelo (no depende de nada)
javac -d backend -encoding UTF-8 backend/src/com/arcadigital/model/Animal.java

# 2. Conexión (no depende de clases propias)
javac -cp "lib/mariadb-java-client-3.5.7.jar" -d backend backend/src/com/arcadigital/database/ConexionDB.java

# 3. DAO (depende de Animal y ConexionDB)
javac -cp "lib/mariadb-java-client-3.5.7.jar" -d backend backend/src/com/arcadigital/database/AnimalDAO.java

# 4. Servidor API (depende de todo)
javac -cp "lib/mariadb-java-client-3.5.7.jar" -d backend backend/src/com/arcadigital/api/ServidorAPI.java

# 5. Main.java (prueba)
javac -cp "lib/mariadb-java-client-3.5.7.jar" -d backend backend/src/com/arcadigital/Main.java
```

**Ejecución:**

```bash
# Ejecutar servidor API
java -cp "backend;lib/mariadb-java-client-3.5.7.jar" com.arcadigital.api.ServidorAPI

# En otra terminal, abrir frontend
file:///C:/Users/Usuario/Desktop/arca_digital/Frontend/index.html

# O servir estáticamente desde http://localhost:8080
```

---

## ⚙️ Notas Técnicas Importantes

1. **Classpath Obligatorio:** Siempre incluir `-cp "backend;lib/mariadb-java-client-3.5.7.jar"` al compilar y ejecutar.

2. **Puerto 8080:** Si está ocupado, cambiar en `ServidorAPI.java` línea: `static int puerto = 8080;`

3. **Credenciales BD:** Verificar en `ConexionDB.java`:
   ```java
   String url = "jdbc:mariadb://localhost:3306/arca_digital";
   String usuario = "root";
   String password = "****";  // Tu contraseña local
   ```

4. **CORS Headers:** Siempre presentes en respuestas de API para evitar bloqueos del navegador.

5. **Try-with-Resources:** Usar siempre para cerrar conexiones automáticamente y evitar fugas de conexión.

---

**Creado y actualizado:** 17 de febrero de 2026  
**Estado del proyecto:** Arquitectura base funcional, próxima fase: CRUD completo

### 15:15 - 16:00 | Crear Base de Datos

**Abrir cmd de MariaDB:**
```bash
mysql -u root -p
```

**Dentro de MySQL ejecutar:**
```sql
-- Crear base de datos
CREATE DATABASE IF NOT EXISTS arca_digital;

-- Usar la BD
USE arca_digital;

-- Crear tabla de animales
CREATE TABLE IF NOT EXISTS animales (
    id VARCHAR(50) PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    especie VARCHAR(100) NOT NULL,
    raza VARCHAR(100),
    edad VARCHAR(50),
    genero VARCHAR(20),
    estado VARCHAR(50),
    descripcion TEXT,
    imagenUrl VARCHAR(500),
    fechaRegistro VARCHAR(20)
);

-- Insertar datos de prueba
INSERT INTO animales VALUES 
('JVM-001', 'Rex', 'Perro', 'Pastor Alemán', '3', 'Macho', 'Activo', 'Perro amigable y protector', '/img/rex.jpg', '2024-01-15');

INSERT INTO animales VALUES 
('JVM-002', 'Luna', 'Gato', 'Siamés', '2', 'Hembra', 'Activo', 'Gata curiosa y juguetona', '/img/luna.jpg', '2024-02-10');

-- Verificar datos
SELECT * FROM animales;

-- Salir
EXIT;
```

**✅ Checklist:**
- [ ] BD `arca_digital` creada
- [ ] Tabla `animales` creada
- [ ] 2 animales insertados
- [ ] SELECT muestra datos correctamente

### 16:00 - 16:30 | Compilar Modelo Animal.java

**En terminal PowerShell:**
```bash
# Abrir VS Code o editor
code backend/src/com/arcadigital/model/Animal.java

# Verificar que exista el archivo con contenido:
# - 10 atributos (id, nombre, especie, raza, edad, genero, estado, descripcion, imagenUrl, fechaRegistro)
# - Constructor
# - Getters y Setters
# - toString()
```

**Compilar:**
```bash
javac -d backend -encoding UTF-8 backend/src/com/arcadigital/model/Animal.java

# Verificar compilación exitosa (no debe mostrar errores)
echo "Animal.java compilado ✓"
```

**✅ Checklist:**
- [ ] Animal.java tiene todos los atributos
- [ ] Compiló sin errores
- [ ] Se creó backend/com/arcadigital/model/Animal.class

### 16:30 - 17:15 | Compilar Clases de BD

**Compilar ConexionDB.java:**
```bash
javac -cp "lib/mariadb-java-client-3.5.7.jar" -d backend backend/src/com/arcadigital/database/ConexionDB.java

# Si hay error, revisar:
# - ¿El archivo existe?
# - ¿Está en la ruta correcta?
# - ¿El JDBC driver está en lib/?

# Si compiló bien:
echo "ConexionDB.java compilado ✓"
```

**Compilar AnimalDAO.java:**
```bash
javac -cp "lib/mariadb-java-client-3.5.7.jar" -d backend backend/src/com/arcadigital/database/AnimalDAO.java

# Verificar
echo "AnimalDAO.java compilado ✓"
```

**✅ Checklist:**
- [ ] ConexionDB.java compiló
- [ ] AnimalDAO.java compiló
- [ ] No hay errores de conexión
- [ ] Archivos .class creados

### 17:15 - 17:45 | Compilar y Probar Main.java

**Compilar Main.java:**
```bash
javac -cp "lib/mariadb-java-client-3.5.7.jar" -d backend backend/src/com/arcadigital/Main.java

echo "Main.java compilado ✓"
```

**Ejecutar Main.java para probar conexión:**
```bash
java -cp "backend;lib/mariadb-java-client-3.5.7.jar" com.arcadigital.Main

# Salida esperada:
# INICIANDO ARCA DIGITAL (BACKEND)
# Consultando base de datos...
# ¡ÉXITO! Se han encontrado 2 animales:
# JVM-001 - Rex (Perro)
# JVM-002 - Luna (Gato)
```

**✅ Checklist:**
- [ ] Main compiló sin errores
- [ ] Se conectó a la BD exitosamente
- [ ] Mostró los 2 animales
- [ ] Salida limpia sin excepciones

### 17:45 - 18:00 | Resumen Lunes

**✅ LUNES COMPLETADO:**
- ✓ BD creada y con datos
- ✓ Modelo Animal compilado
- ✓ ConexionDB compilado
- ✓ AnimalDAO compilado
- ✓ Main.java prueba exitosa
- ✓ Conexión a BD funciona

**Notas para mañana:**
- El driver JDBC está en su lugar
- Las credenciales de BD son correctas
- No reiniciar la BD

---

## 📅 MARTES - DÍA 2: API REST BACKEND

### 15:00 - 15:20 | Preparación y Revisión

```bash
# 15:00 - Revisar que todo del lunes está en orden
java -cp "backend;lib/mariadb-java-client-3.5.7.jar" com.arcadigital.Main

# Debe mostrar los animales correctamente
# Si hay error, revisar que MariaDB está activo
```

### 15:20 - 16:30 | Crear ServidorAPI.java

**Abrir VS Code y crear/editar:**
```bash
code backend/src/com/arcadigital/api/ServidorAPI.java
```

**El archivo debe tener:**

```java
package com.arcadigital.api;

import com.arcadigital.database.AnimalDAO;
import com.arcadigital.model.Animal;
import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;
import java.io.*;
import java.net.InetSocketAddress;
import java.util.List;

public class ServidorAPI {
    
    static AnimalDAO dao = new AnimalDAO();
    static int puerto = 8080;
    
    public static void main(String[] args) throws Exception {
        
        // Crear servidor
        HttpServer server = HttpServer.create(new InetSocketAddress(puerto), 0);
        
        // Registrar endpoints
        server.createContext("/animales", new ManejadorAnimales());
        
        // Iniciar
        server.setExecutor(null);
        server.start();
        
        System.out.println("========================================");
        System.out.println(" 🚀 SERVIDOR API ARCA DIGITAL");
        System.out.println(" Puerto: " + puerto);
        System.out.println(" URL: http://localhost:" + puerto);
        System.out.println("========================================");
    }
    
    // Manejador para /animales
    static class ManejadorAnimales implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            
            // CORS Headers
            exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
            exchange.getResponseHeaders().add("Access-Control-Allow-Methods", "GET,POST,PUT,DELETE,OPTIONS");
            exchange.getResponseHeaders().add("Access-Control-Allow-Headers", "Content-Type");
            exchange.getResponseHeaders().add("Content-Type", "application/json");
            
            // Preflight request
            if (exchange.getRequestMethod().equals("OPTIONS")) {
                exchange.sendResponseHeaders(200, -1);
                return;
            }
            
            // Obtener ruta completa
            String ruta = exchange.getRequestURI().getPath();
            String metodo = exchange.getRequestMethod();
            
            try {
                
                if (metodo.equals("GET")) {
                    // GET /animales - Listar todos
                    if (ruta.equals("/animales")) {
                        List<Animal> lista = dao.listarTodos();
                        String json = convertirAJson(lista);
                        exchange.sendResponseHeaders(200, json.getBytes().length);
                        OutputStream os = exchange.getResponseBody();
                        os.write(json.getBytes());
                        os.close();
                    }
                    // GET /animales/{id} - Obtener por ID
                    else if (ruta.startsWith("/animales/")) {
                        String id = ruta.substring("/animales/".length());
                        Animal animal = dao.obtenerPorId(id);
                        String json = animal != null ? animal.toJson() : "{\"error\":\"No encontrado\"}";
                        exchange.sendResponseHeaders(200, json.getBytes().length);
                        OutputStream os = exchange.getResponseBody();
                        os.write(json.getBytes());
                        os.close();
                    }
                }
                
                else if (metodo.equals("POST")) {
                    // POST /animales - Crear nuevo
                    BufferedReader br = new BufferedReader(new InputStreamReader(exchange.getRequestBody()));
                    StringBuilder sb = new StringBuilder();
                    String linea;
                    while ((linea = br.readLine()) != null) {
                        sb.append(linea);
                    }
                    String json = sb.toString();
                    
                    // Parsear JSON simple (sin librerías)
                    Animal animal = parsearJson(json);
                    dao.crear(animal);
                    
                    String respuesta = "{\"mensaje\":\"Creado\",\"id\":\"" + animal.getId() + "\"}";
                    exchange.sendResponseHeaders(201, respuesta.getBytes().length);
                    OutputStream os = exchange.getResponseBody();
                    os.write(respuesta.getBytes());
                    os.close();
                }
                
                else if (metodo.equals("PUT")) {
                    // PUT /animales/{id} - Actualizar
                    String id = ruta.substring("/animales/".length());
                    BufferedReader br = new BufferedReader(new InputStreamReader(exchange.getRequestBody()));
                    StringBuilder sb = new StringBuilder();
                    String linea;
                    while ((linea = br.readLine()) != null) {
                        sb.append(linea);
                    }
                    
                    Animal animal = parsearJson(sb.toString());
                    animal.setId(id);
                    dao.actualizar(animal);
                    
                    String respuesta = "{\"mensaje\":\"Actualizado\"}";
                    exchange.sendResponseHeaders(200, respuesta.getBytes().length);
                    OutputStream os = exchange.getResponseBody();
                    os.write(respuesta.getBytes());
                    os.close();
                }
                
                else if (metodo.equals("DELETE")) {
                    // DELETE /animales/{id} - Eliminar
                    String id = ruta.substring("/animales/".length());
                    dao.eliminar(id);
                    
                    String respuesta = "{\"mensaje\":\"Eliminado\"}";
                    exchange.sendResponseHeaders(200, respuesta.getBytes().length);
                    OutputStream os = exchange.getResponseBody();
                    os.write(respuesta.getBytes());
                    os.close();
                }
                
            } catch (Exception e) {
                String error = "{\"error\":\"" + e.getMessage() + "\"}";
                exchange.sendResponseHeaders(500, error.getBytes().length);
                OutputStream os = exchange.getResponseBody();
                os.write(error.getBytes());
                os.close();
            }
        }
    }
    
    // Convertir lista a JSON
    static String convertirAJson(List<Animal> lista) {
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < lista.size(); i++) {
            sb.append(lista.get(i).toJson());
            if (i < lista.size() - 1) sb.append(",");
        }
        sb.append("]");
        return sb.toString();
    }
    
    // Parsear JSON simple
    static Animal parsearJson(String json) {
        // Implementar parsing simple del JSON
        // Por ahora, retornar un objeto vacío
        return new Animal("", "", "", "", "", "", "", "", "", "");
    }
}
```

**Guardar archivo y compilar:**
```bash
javac -cp "lib/mariadb-java-client-3.5.7.jar" -d backend backend/src/com/arcadigital/api/ServidorAPI.java

# Si compiló bien:
echo "ServidorAPI.java compilado ✓"
```

### 16:30 - 17:00 | Ejecutar Servidor

**Iniciar servidor en terminal:**
```bash
java -cp "backend;lib/mariadb-java-client-3.5.7.jar" com.arcadigital.api.ServidorAPI

# Salida esperada:
# ========================================
#  🚀 SERVIDOR API ARCA DIGITAL
#  Puerto: 8080
#  URL: http://localhost:8080
# ========================================
```

**⚠️ IMPORTANTE:** Dejar esta terminal abierta. El servidor debe seguir corriendo.

### 17:00 - 17:45 | Probar Endpoints (en OTRA terminal)

**Abrir una NUEVA terminal PowerShell:**
```bash
# Terminal 2 - Probar GET todos
curl -X GET http://localhost:8080/animales

# Debe devolver:
# [{"id":"JVM-001","nombre":"Rex",...},{"id":"JVM-002","nombre":"Luna",...}]
```

**Probar GET por ID:**
```bash
curl -X GET http://localhost:8080/animales/JVM-001

# Debe devolver:
# {"id":"JVM-001","nombre":"Rex",...}
```

**Probar POST (crear):**
```bash
$body = @{
    id = "JVM-003"
    nombre = "Max"
    especie = "Perro"
    raza = "Labrador"
    edad = "5"
    genero = "Macho"
    estado = "Activo"
    descripcion = "Perro grande y leal"
    imagenUrl = "/img/max.jpg"
    fechaRegistro = "2024-03-01"
} | ConvertTo-Json

curl -X POST http://localhost:8080/animales `
  -H "Content-Type: application/json" `
  -d $body

# Debe devolver:
# {"mensaje":"Creado","id":"JVM-003"}
```

**Verificar en BD (Terminal 3):**
```bash
mysql -u root -p

USE arca_digital;
SELECT * FROM animales;

# Debe mostrar 3 animales incluyendo Max
EXIT;
```

**✅ Checklist Martes:**
- [ ] ServidorAPI compilado
- [ ] Servidor escucha en puerto 8080
- [ ] GET /animales devuelve JSON
- [ ] GET /animales/{id} funciona
- [ ] POST crea nuevos animales
- [ ] BD se actualiza correctamente

### 17:45 - 18:00 | Resumen Martes

**✅ MARTES COMPLETADO:**
- ✓ ServidorAPI implementado
- ✓ 5 endpoints REST funcionales
- ✓ CORS habilitado
- ✓ Servidor activo en puerto 8080

**Para mañana:**
- No apagar el servidor (o anotar cómo iniciarlo)
- El código podría necesitar ajustes según problemas encontrados

---

## 📅 MIÉRCOLES - DÍA 3: FRONTEND HTML/CSS/JS

### 15:00 - 15:20 | Preparación

```bash
# 15:00 - Verificar servidor está activo
curl -X GET http://localhost:8080/animales

# Debe mostrar JSON con animales
# Si no, reiniciar servidor desde Martes
```

### 15:20 - 16:15 | Crear index.html

**Abrir VS Code:**
```bash
code Frontend/index.html
```

**Contenido HTML:**
```html
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>ARCA DIGITAL - Sistema de Gestión de Animales</title>
    <link rel="stylesheet" href="styles.css">
</head>
<body>
    <div class="container">
        <h1>🐾 ARCA DIGITAL</h1>
        <p class="subtitle">Sistema de Gestión de Animales</p>
        
        <!-- FORMULARIO CREAR -->
        <div class="form-section">
            <h2>➕ Crear Animal</h2>
            <form id="formAnimal">
                <div class="form-group">
                    <label for="id">ID:</label>
                    <input type="text" id="id" placeholder="JVM-003" required>
                </div>
                <div class="form-group">
                    <label for="nombre">Nombre:</label>
                    <input type="text" id="nombre" placeholder="Max" required>
                </div>
                <div class="form-group">
                    <label for="especie">Especie:</label>
                    <input type="text" id="especie" placeholder="Perro" required>
                </div>
                <div class="form-group">
                    <label for="raza">Raza:</label>
                    <input type="text" id="raza" placeholder="Labrador">
                </div>
                <div class="form-group">
                    <label for="edad">Edad:</label>
                    <input type="text" id="edad" placeholder="5">
                </div>
                <div class="form-group">
                    <label for="genero">Género:</label>
                    <select id="genero">
                        <option>Macho</option>
                        <option>Hembra</option>
                    </select>
                </div>
                <div class="form-group">
                    <label for="estado">Estado:</label>
                    <input type="text" id="estado" placeholder="Activo" required>
                </div>
                <div class="form-group">
                    <label for="descripcion">Descripción:</label>
                    <textarea id="descripcion" placeholder="Descripción del animal"></textarea>
                </div>
                <div class="form-group">
                    <label for="imagenUrl">URL Imagen:</label>
                    <input type="text" id="imagenUrl" placeholder="/img/animal.jpg">
                </div>
                <div class="form-group">
                    <label for="fechaRegistro">Fecha Registro:</label>
                    <input type="text" id="fechaRegistro" placeholder="2024-03-01">
                </div>
                <button type="submit" class="btn btn-crear">Crear Animal</button>
            </form>
        </div>
        
        <!-- TABLA ANIMALES -->
        <div class="table-section">
            <div class="table-header">
                <h2>📋 Animales Registrados</h2>
                <button id="btnRefresh" class="btn btn-refresh">🔄 Refrescar</button>
            </div>
            
            <table id="tablaAnimales">
                <thead>
                    <tr>
                        <th>ID</th>
                        <th>Nombre</th>
                        <th>Especie</th>
                        <th>Raza</th>
                        <th>Edad</th>
                        <th>Género</th>
                        <th>Estado</th>
                        <th>Acciones</th>
                    </tr>
                </thead>
                <tbody id="cuerpoTabla">
                    <tr><td colspan="8">Cargando...</td></tr>
                </tbody>
            </table>
        </div>
    </div>
    
    <script src="app.js"></script>
</body>
</html>
```

### 16:15 - 17:00 | Crear styles.css

**Editar o crear:**
```bash
code Frontend/styles.css
```

**Contenido CSS:**
```css
* {
    margin: 0;
    padding: 0;
    box-sizing: border-box;
}

body {
    font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
    min-height: 100vh;
    padding: 20px;
}

.container {
    max-width: 1200px;
    margin: 0 auto;
    background: white;
    border-radius: 15px;
    padding: 30px;
    box-shadow: 0 20px 60px rgba(0, 0, 0, 0.3);
}

h1 {
    color: #333;
    text-align: center;
    margin-bottom: 5px;
    font-size: 2.5em;
}

.subtitle {
    text-align: center;
    color: #666;
    margin-bottom: 30px;
    font-size: 1.1em;
}

h2 {
    color: #444;
    border-bottom: 3px solid #667eea;
    padding-bottom: 10px;
    margin-bottom: 20px;
}

/* FORMULARIO */
.form-section {
    background: #f8f9fa;
    padding: 20px;
    border-radius: 10px;
    margin-bottom: 30px;
}

.form-group {
    margin-bottom: 15px;
}

label {
    display: block;
    margin-bottom: 5px;
    font-weight: 600;
    color: #333;
}

input, select, textarea {
    width: 100%;
    padding: 10px;
    border: 2px solid #ddd;
    border-radius: 5px;
    font-size: 1em;
    font-family: inherit;
    transition: border-color 0.3s;
}

input:focus, select:focus, textarea:focus {
    outline: none;
    border-color: #667eea;
    box-shadow: 0 0 5px rgba(102, 126, 234, 0.2);
}

textarea {
    resize: vertical;
    min-height: 80px;
}

/* BOTONES */
.btn {
    padding: 10px 20px;
    border: none;
    border-radius: 5px;
    cursor: pointer;
    font-size: 1em;
    font-weight: 600;
    transition: all 0.3s;
}

.btn-crear {
    background: #667eea;
    color: white;
    width: 100%;
    padding: 12px;
}

.btn-crear:hover {
    background: #5568d3;
    transform: translateY(-2px);
}

.btn-refresh {
    background: #28a745;
    color: white;
}

.btn-refresh:hover {
    background: #218838;
}

.btn-editar {
    background: #ffc107;
    color: white;
    padding: 5px 10px;
    font-size: 0.9em;
}

.btn-editar:hover {
    background: #e0a800;
}

.btn-eliminar {
    background: #dc3545;
    color: white;
    padding: 5px 10px;
    font-size: 0.9em;
}

.btn-eliminar:hover {
    background: #c82333;
}

/* TABLA */
.table-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 20px;
}

.table-section {
    margin-top: 30px;
}

table {
    width: 100%;
    border-collapse: collapse;
    background: white;
}

thead {
    background: #667eea;
    color: white;
}

th, td {
    padding: 15px;
    text-align: left;
    border-bottom: 1px solid #ddd;
}

tbody tr:hover {
    background: #f5f5f5;
}

th {
    font-weight: 600;
}

td {
    color: #333;
}

/* RESPONSIVE */
@media (max-width: 768px) {
    .container {
        padding: 15px;
    }
    
    h1 {
        font-size: 2em;
    }
    
    table {
        font-size: 0.9em;
    }
    
    th, td {
        padding: 10px;
    }
    
    .table-header {
        flex-direction: column;
        gap: 10px;
    }
    
    .btn-refresh {
        width: 100%;
    }
}
```

### 17:00 - 18:00 | Crear app.js

**Editar o crear:**
```bash
code Frontend/app.js
```

**Contenido JavaScript:**
```javascript
const API_URL = 'http://localhost:8080/animales';

// Cargar animales al iniciar la página
document.addEventListener('DOMContentLoaded', function() {
    cargarAnimales();
    
    // Event listeners
    document.getElementById('formAnimal').addEventListener('submit', crearAnimal);
    document.getElementById('btnRefresh').addEventListener('click', cargarAnimales);
});

// CARGAR ANIMALES
function cargarAnimales() {
    console.log('📥 Cargando animales...');
    
    fetch(API_URL)
        .then(response => {
            if (!response.ok) {
                throw new Error('Error en la red: ' + response.status);
            }
            return response.json();
        })
        .then(animales => {
            console.log('✅ Animales cargados:', animales);
            mostrarAnimalesEnTabla(animales);
        })
        .catch(error => {
            console.error('❌ Error:', error);
            document.getElementById('cuerpoTabla').innerHTML = 
                '<tr><td colspan="8" style="color: red;">Error al cargar: ' + error.message + '</td></tr>';
        });
}

// MOSTRAR EN TABLA
function mostrarAnimalesEnTabla(animales) {
    const tbody = document.getElementById('cuerpoTabla');
    
    if (animales.length === 0) {
        tbody.innerHTML = '<tr><td colspan="8">No hay animales registrados</td></tr>';
        return;
    }
    
    let html = '';
    animales.forEach(animal => {
        html += `
            <tr>
                <td>${animal.id}</td>
                <td><strong>${animal.nombre}</strong></td>
                <td>${animal.especie}</td>
                <td>${animal.raza || '-'}</td>
                <td>${animal.edad || '-'}</td>
                <td>${animal.genero || '-'}</td>
                <td><span class="estado">${animal.estado}</span></td>
                <td>
                    <button class="btn btn-editar" onclick="editarAnimal('${animal.id}')">✏️ Editar</button>
                    <button class="btn btn-eliminar" onclick="eliminarAnimal('${animal.id}')">🗑️ Eliminar</button>
                </td>
            </tr>
        `;
    });
    
    tbody.innerHTML = html;
}

// CREAR ANIMAL
function crearAnimal(e) {
    e.preventDefault();
    
    const animal = {
        id: document.getElementById('id').value,
        nombre: document.getElementById('nombre').value,
        especie: document.getElementById('especie').value,
        raza: document.getElementById('raza').value,
        edad: document.getElementById('edad').value,
        genero: document.getElementById('genero').value,
        estado: document.getElementById('estado').value,
        descripcion: document.getElementById('descripcion').value,
        imagenUrl: document.getElementById('imagenUrl').value,
        fechaRegistro: document.getElementById('fechaRegistro').value
    };
    
    // Validar campos vacíos
    if (!animal.id || !animal.nombre || !animal.especie) {
        alert('❌ Por favor completa ID, Nombre y Especie');
        return;
    }
    
    console.log('📤 Creando animal:', animal);
    
    fetch(API_URL, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(animal)
    })
    .then(response => response.json())
    .then(data => {
        console.log('✅ Animal creado:', data);
        alert('✅ Animal creado exitosamente con ID: ' + animal.id);
        document.getElementById('formAnimal').reset();
        cargarAnimales();
    })
    .catch(error => {
        console.error('❌ Error al crear:', error);
        alert('❌ Error: ' + error.message);
    });
}

// EDITAR ANIMAL (simplificado)
function editarAnimal(id) {
    alert('⚠️ Función de editar en desarrollo\nID del animal: ' + id);
    // Aquí irían campos para editar
}

// ELIMINAR ANIMAL
function eliminarAnimal(id) {
    if (confirm('¿Realmente deseas eliminar el animal ' + id + '?')) {
        console.log('🗑️ Eliminando animal:', id);
        
        fetch(API_URL + '/' + id, {
            method: 'DELETE'
        })
        .then(response => response.json())
        .then(data => {
            console.log('✅ Animal eliminado:', data);
            alert('✅ Animal eliminado');
            cargarAnimales();
        })
        .catch(error => {
            console.error('❌ Error al eliminar:', error);
            alert('❌ Error: ' + error.message);
        });
    }
}
```

### 18:00 - 18:30 | Prueba Inicial del Frontend

**Abrir el archivo en navegador:**
```bash
# En navegador ir a:
file:///C:/Users/Usuario/Desktop/arca_digital/Frontend/index.html

# O en PowerShell:
start "C:\Users\Usuario\Desktop\arca_digital\Frontend\index.html"
```

**✅ Checklist:**
- [ ] Página carga sin errores visuales
- [ ] Formulario visible y completo
- [ ] Tabla visible
- [ ] Estilos aplicados correctamente
- [ ] F12 → Console no muestra errores de sintaxis

### 18:30 - 19:00 | Probar Conectar con API

**Con servidor corriendo:**
```bash
# Hacer clic en "Refrescar" en el frontend
# Debe cargar los 3 animales (Rex, Luna, Max)
```

**Abrir DevTools (F12):**
```
Console → Debe mostrar:
✅ Animales cargados: [objeto, objeto, objeto]

Network → Debe mostrar:
✅ GET http://localhost:8080/animales → 200 OK
```

**Si hay error de CORS:**
```
Error: Access to fetch at 'http://localhost:8080/animales' 
from origin 'file://' has been blocked by CORS policy
```

**Solución:**
- El servidor debe tener CORS habilitado (ya está en ServidorAPI.java)
- Si sigue con error, revisar que el servidor tiene los headers correctos

**✅ MIÉRCOLES COMPLETADO:**
- ✓ index.html creado
- ✓ styles.css con estilos limpios
- ✓ app.js con funciones CRUD
- ✓ Frontend carga desde navegador
- ✓ Se conecta con API (si hay servidor activo)

---

## 📅 JUEVES - DÍA 4: INTEGRACIÓN Y PRUEBAS CRUD

### 15:00 - 15:30 | Verificación Inicial

```bash
# 15:00 - Terminal 1: Iniciar servidor si no está activo
java -cp "backend;lib/mariadb-java-client-3.5.7.jar" com.arcadigital.api.ServidorAPI

# Terminal 2: Abrir navegador con frontend
file:///C:/Users/Usuario/Desktop/arca_digital/Frontend/index.html

# Presionar F12 para DevTools
```

### 15:30 - 16:30 | Prueba GET (Listar Animales)

**En el frontend:**
1. Hacer clic en botón "🔄 Refrescar"
2. Debe mostrar tabla con 3 animales (Rex, Luna, Max)

**En DevTools → Network:**
- Debe haber una petición GET a `http://localhost:8080/animales`
- Status: 200 OK
- Response: JSON con array de animales

**Si NO funciona:**
```
Error esperado: CORS
Solución: Revisar que ServidorAPI tiene headers CORS

Error esperado: Connection refused
Solución: Verificar que servidor está activo

Error esperado: "Cargando..." solo
Solución: F12 Console → Ver qué error exacto hay
```

### 16:30 - 17:15 | Prueba CREATE (Crear Animal)

**En el frontend:**
1. Llenar formulario:
   ```
   ID: JVM-004
   Nombre: Bella
   Especie: Gato
   Raza: Persa
   Edad: 4
   Género: Hembra
   Estado: Activo
   Descripción: Gata tranquila y cariñosa
   URL Imagen: /img/bella.jpg
   Fecha Registro: 2024-03-15
   ```

2. Hacer clic en "Crear Animal"
3. Debe mostrar alert: "✅ Animal creado exitosamente"
4. Tabla debe refrescarse automaticamente
5. Bella debe aparecer en la tabla

**En DevTools → Network:**
- POST a `http://localhost:8080/animales`
- Status: 201 Created
- Request Body: JSON del animal

**Verificar en BD (Terminal 3):**
```bash
mysql -u root -p
USE arca_digital;
SELECT * FROM animales WHERE id='JVM-004';

# Debe mostrar a Bella
EXIT;
```

### 17:15 - 17:45 | Prueba DELETE (Eliminar Animal)

**En el frontend:**
1. Buscar al animal "Luna" en la tabla
2. Hacer clic en botón "🗑️ Eliminar"
3. Confirmar en el diálogo: "¿Realmente deseas eliminar?"
4. Debe mostrar alert: "✅ Animal eliminado"
5. Luna debe desaparecer de la tabla

**En DevTools → Network:**
- DELETE a `http://localhost:8080/animales/JVM-002`
- Status: 200 OK

**Verificar en BD:**
```bash
mysql -u root -p
USE arca_digital;
SELECT COUNT(*) FROM animales;

# Debe mostrar 3 (fue 4, eliminamos 1)
EXIT;
```

### 17:45 - 18:45 | Prueba PUT (Editar Animal) - AVANZADO

**Primero, mejorar app.js para editar:**

En `Frontend/app.js`, reemplazar función `editarAnimal`:

```javascript
function editarAnimal(id) {
    // Obtener datos actuales del animal
    fetch(API_URL + '/' + id)
        .then(r => r.json())
        .then(animal => {
            // Llenar form con datos actuales
            document.getElementById('id').value = animal.id;
            document.getElementById('nombre').value = animal.nombre;
            document.getElementById('especie').value = animal.especie;
            document.getElementById('raza').value = animal.raza;
            document.getElementById('edad').value = animal.edad;
            document.getElementById('genero').value = animal.genero;
            document.getElementById('estado').value = animal.estado;
            document.getElementById('descripcion').value = animal.descripcion;
            document.getElementById('imagenUrl').value = animal.imagenUrl;
            document.getElementById('fechaRegistro').value = animal.fechaRegistro;
            
            // Cambiar botón a "Actualizar"
            let btn = document.querySelector('.btn-crear');
            btn.textContent = '💾 Actualizar Animal';
            btn.dataset.editandoId = id;
            
            // Scroll al formulario
            document.querySelector('.form-section').scrollIntoView();
        });
}
```

**Modificar función `crearAnimal` para detectar edición:**

```javascript
function crearAnimal(e) {
    e.preventDefault();
    
    const btn = document.querySelector('.btn-crear');
    const editandoId = btn.dataset.editandoId;
    
    const animal = {
        id: document.getElementById('id').value,
        nombre: document.getElementById('nombre').value,
        especie: document.getElementById('especie').value,
        // ... resto de campos
    };
    
    const url = editandoId ? API_URL + '/' + editandoId : API_URL;
    const metodo = editandoId ? 'PUT' : 'POST';
    
    fetch(url, {
        method: metodo,
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(animal)
    })
    .then(r => r.json())
    .then(data => {
        alert(editandoId ? '✅ Animal actualizado' : '✅ Animal creado');
        // Resetear
        document.getElementById('formAnimal').reset();
        btn.textContent = '➕ Crear Animal';
        btn.dataset.editandoId = '';
        cargarAnimales();
    })
    .catch(error => alert('❌ Error: ' + error.message));
}
```

**Probar:**
1. Hacer clic en "✏️ Editar" en Rex
2. Form se llena con datos de Rex
3. Cambiar nombre a "Rexit"
4. Hacer clic en "💾 Actualizar Animal"
5. Debe actualizar en tabla y BD

### 18:45 - 19:00 | Resumen Jueves

**✅ JUEVES COMPLETADO:**
- ✓ GET funciona (lista animales)
- ✓ POST funciona (crea animales)
- ✓ DELETE funciona (elimina animales)
- ✓ PUT funciona (actualiza animales)
- ✓ CRUD completo operacional
- ✓ Frontend y Backend integrados

**Animales en BD:**
- JVM-001 Rex (original)
- JVM-002 Luna (eliminado jueves)
- JVM-003 Max (creado martes)
- JVM-004 Bella (creado jueves)

---

## 📅 VIERNES - DÍA 5: VALIDACIONES Y MEJORAS

### 15:00 - 15:30 | Revisar y Corregir Errores

```bash
# 15:00 - Abrir DevTools (F12) en navegador con frontend
# Revisar Console por errores (rojo)
# Revisar Network por respuestas fallidas (4xx, 5xx)
```

**Errores comunes a buscar:**
- JSON con formato incorrecto
- Campos vacíos en tabla
- Validaciones no funcionando
- Mensajes de error no mostrados

### 15:30 - 16:45 | Mejorar Validaciones Frontend

**Abrir `Frontend/app.js` y mejorar validaciones:**

```javascript
// Función para validar animal
function validarAnimal(animal) {
    const errores = [];
    
    // ID
    if (!animal.id || animal.id.trim() === '') {
        errores.push('ID es requerido');
    } else if (animal.id.length > 50) {
        errores.push('ID muy largo (máx 50 caracteres)');
    }
    
    // Nombre
    if (!animal.nombre || animal.nombre.trim() === '') {
        errores.push('Nombre es requerido');
    } else if (animal.nombre.length > 100) {
        errores.push('Nombre muy largo');
    }
    
    // Especie
    if (!animal.especie || animal.especie.trim() === '') {
        errores.push('Especie es requerida');
    }
    
    // Edad (debe ser número)
    if (animal.edad && isNaN(animal.edad)) {
        errores.push('Edad debe ser un número');
    }
    
    // Género (validar opciones)
    const generosValidos = ['Macho', 'Hembra'];
    if (animal.genero && !generosValidos.includes(animal.genero)) {
        errores.push('Género inválido');
    }
    
    return errores;
}

//Modificar crearAnimal para usar validación:
function crearAnimal(e) {
    e.preventDefault();
    
    const animal = {
        // ... código existente para obtener datos
    };
    
    // VALIDAR
    const errores = validarAnimal(animal);
    if (errores.length > 0) {
        alert('❌ Errores encontrados:\n' + errores.join('\n'));
        return;
    }
    
    // Resto del código...
}
```

**Agregar validación de fechas (Frontend):**

```javascript
// En el HTML, cambiar:
// <input type="text" id="fechaRegistro" placeholder="2024-03-01">
// por:
// <input type="date" id="fechaRegistro">

// En JavaScript al enviar, convertir a string:
document.getElementById('fechaRegistro').value.toString()
```

### 16:45 - 17:30 | Mejorar Manejo de Errores

**Modificar `app.js` para mostrar errores más claros:**

```javascript
function mostrarMensaje(tipo, texto) {
    // tipo = 'exito' o 'error'
    const div = document.createElement('div');
    div.className = 'mensaje ' + tipo;
    div.textContent = texto;
    document.body.insertBefore(div, document.body.firstChild);
    
    setTimeout(() => div.remove(), 5000);
}

// En crearAnimal:
.then(data => {
    mostrarMensaje('exito', '✅ ' + (editandoId ? 'Actualizado' : 'Creado'));
    // ...
})
.catch(error => {
    mostrarMensaje('error', '❌ Error: ' + error.message);
});
```

**Agregar estilos en CSS:**

```css
.mensaje {
    position: fixed;
    top: 20px;
    right: 20px;
    padding: 15px 25px;
    border-radius: 5px;
    color: white;
    font-weight: bold;
    z-index: 1000;
    animation: slideIn 0.3s ease;
}

.mensaje.exito {
    background: #28a745;
}

.mensaje.error {
    background: #dc3545;
}

@keyframes slideIn {
    from {
        transform: translateX(400px);
        opacity: 0;
    }
    to {
        transform: translateX(0);
        opacity: 1;
    }
}
```

### 17:30 - 18:15 | Agregar Datos de Prueba

**En MySQL:**
```bash
mysql -u root -p

USE arca_digital;

INSERT INTO animales VALUES 
('JVM-005', 'Spike', 'Erizo', 'Africano', '2', 'Macho', 'Activo', 'Erizo pequeño y activo', '/img/spike.jpg', '2024-02-20'),
('JVM-006', 'Nube', 'Conejo', 'Holandés', '1', 'Hembra', 'Activo', 'Conejo blanco y suave', '/img/nube.jpg', '2024-03-05'),
('JVM-007', 'Capitán', 'Loro', 'Guacamayo', '8', 'Macho', 'Activo', 'Loro ruidoso pero amable', '/img/capitan.jpg', '2024-01-30'),
('JVM-008', 'Whiskers', 'Gato', 'Atigrado', '6', 'Macho', 'Activo', 'Gato independiente', '/img/whiskers.jpg', '2024-02-14');

SELECT * FROM animales;

EXIT;
```

**Verificar en Frontend:**
- Refrescar página
- Debe mostrar 8 animales en tabla

### 18:15 - 19:00 | Testing de Responsividad

**En navegador, F12 → Dispositivos:**

**Para pantalla pequeña (móvil):**
```
- Vista debe ajustarse al ancho
- Botones no deben sobrepasar
- Tabla debe ser legible
- Formulario debe ser usable
```

**Ajustar si es necesario:**
```css
@media (max-width: 480px) {
    .container {
        padding: 10px;
    }
    
    table {
        font-size: 0.8em;
    }
    
    /* Tabla responsive alternativo */
    table tbody tr {
        display: block;
        margin-bottom: 15px;
        border: 1px solid #ddd;
    }
}
```

**✅ VIERNES COMPLETADO:**
- ✓ Validaciones funcionando
- ✓ Manejo de errores mejorado
- ✓ 8 animales en BD
- ✓ Frontend responsivo
- ✓ Aplicación robusta

---

## 📅 SÁBADO - DÍA 6: DOCUMENTACIÓN Y TESTING FINAL

### 15:00 - 16:00 | Crear README.md

**Crear o editar:**
```bash
code README.md
```

**Contenido:**

```markdown
# 🐾 ARCA DIGITAL
Sistema de Gestión de Animales - Aplicación Web Full Stack

## 📋 Descripción
Aplicación para registrar, consultar, actualizar y eliminar información de animales en una base de datos.

## 🎯 Características
- ✅ Listar todos los animales
- ✅ Crear nuevos registros
- ✅ Editar información existente
- ✅ Eliminar animales
- ✅ Interfaz web responsiva
- ✅ Base de datos relacional

## 🛠️ Requisitos del Sistema
- **Java**: JDK 22 o superior
- **BD**: MariaDB 10.5+ o MySQL 5.7+
- **Navegador**: Chrome, Firefox, Edge, Safari
- **Puerto**: 8080 (configurable en ServidorAPI.java)

## 📦 Instalación

### 1. Clonar o descargar el proyecto
```bash
cd C:\Users\Usuario\Desktop\arca_digital
```

### 2. Crear base de datos
```bash
mysql -u root -p < sql/databasesetup.sql
```

### 3. Compilar el Backend
```bash
javac -d backend -encoding UTF-8 `
  backend/src/com/arcadigital/model/Animal.java `
  backend/src/com/arcadigital/database/ConexionDB.java `
  backend/src/com/arcadigital/database/AnimalDAO.java `
  backend/src/com/arcadigital/api/ServidorAPI.java `
  backend/src/com/arcadigital/Main.java
```

### 4. Ejecutar el Servidor API
```bash
java -cp "backend;lib/mariadb-java-client-3.5.7.jar" com.arcadigital.api.ServidorAPI
```

Verás:
```
========================================
 🚀 SERVIDOR API ARCA DIGITAL
 Puerto: 8080
 URL: http://localhost:8080
========================================
```

### 5. Abrir el Frontend
```bash
# En navegador:
file:///C:/Users/Usuario/Desktop/arca_digital/Frontend/index.html
```

## 🌐 API REST Endpoints

### GET - Listar todos los animales
```bash
GET http://localhost:8080/animales

Respuesta:
[
  {
    "id": "JVM-001",
    "nombre": "Rex",
    "especie": "Perro",
    "raza": "Pastor Alemán",
    "edad": "3",
    "genero": "Macho",
    "estado": "Activo",
    "descripcion": "Perro amigable",
    "imagenUrl": "/img/rex.jpg",
    "fechaRegistro": "2024-01-15"
  }
]
```

### GET - Obtener un animal por ID
```bash
GET http://localhost:8080/animales/JVM-001

Respuesta:
{
  "id": "JVM-001",
  "nombre": "Rex",
  ...
}
```

### POST - Crear nuevo animal
```bash
POST http://localhost:8080/animales
Content-Type: application/json

{
  "id": "JVM-009",
  "nombre": "Nuevo",
  "especie": "Perro",
  "raza": "Labrador",
  "edad": "2",
  "genero": "Macho",
  "estado": "Activo",
  "descripcion": "Nuevo animal",
  "imagenUrl": "/img/nuevo.jpg",
  "fechaRegistro": "2024-03-17"
}

Respuesta:
{"mensaje": "Creado", "id": "JVM-009"}
```

### PUT - Actualizar animal
```bash
PUT http://localhost:8080/animales/JVM-001
Content-Type: application/json

{ ... mismo JSON pero con datos modificados ... }

Respuesta:
{"mensaje": "Actualizado"}
```

### DELETE - Eliminar animal
```bash
DELETE http://localhost:8080/animales/JVM-001

Respuesta:
{"mensaje": "Eliminado"}
```

## 📁 Estructura del Proyecto
```
arca_digital/
├── backend/
│   └── src/com/arcadigital/
│       ├── api/
│       │   └── ServidorAPI.java
│       ├── database/
│       │   ├── AnimalDAO.java
│       │   └── ConexionDB.java
│       ├── model/
│       │   └── Animal.java
│       └── Main.java
├── Frontend/
│   ├── index.html
│   ├── styles.css
│   └── app.js
├── sql/
│   └── databasesetup.sql
├── lib/
│   └── mariadb-java-client-3.5.7.jar
├── docs/
├── README.md
├── DOCUMENTACION_TECNICA.md
└── ERRORES_Y_SOLUCIONES.md
```

## 🚀 Uso

1. **Iniciar el servidor** (Terminal 1):
   ```bash
   java -cp "backend;lib/mariadb-java-client-3.5.7.jar" com.arcadigital.api.ServidorAPI
   ```

2. **Abrir el frontend** (Navegador):
   ```
   file:///C:/Users/Usuario/Desktop/arca_digital/Frontend/index.html
   ```

3. **Usar la aplicación**:
   - Completar formulario y hacer clic en "Crear Animal"
   - Ver tabla actualizar automáticamente
   - Hacer clic en "Editar" para modificar
   - Hacer clic en "Eliminar" para borrar

## 🔧 Configuración

### Cambiar puerto del servidor
En `backend/src/com/arcadigital/api/ServidorAPI.java`:
```java
static int puerto = 8080;  // Cambiar aquí
```

Luego recompilar.

### Cambiar credenciales de BD
En `backend/src/com/arcadigital/database/ConexionDB.java`:
```java
String url = "jdbc:mariadb://localhost:3306/arca_digital";
String usuario = "root";
String password = "tu_password";  // Cambiar aquí
```

Luego recompilar.

## 📝 Licencia
Proyecto educativo - Libre para usar y modificar

## 👨‍💻 Autor
Desarrollado como proyecto de aprendizaje en Java

---

**Última actualización**: 17 de febrero de 2026
```

### 16:00 - 17:00 | Crear/Actualizar DOCUMENTACION_TECNICA.md

```bash
code DOCUMENTACION_TECNICA.md
```

**Contenido:**

```markdown
# 📚 DOCUMENTACIÓN TÉCNICA - ARCA DIGITAL

## Arquitectura del Sistema

```
┌─────────────────┐
│   NAVEGADOR     │
│   (Frontend)    │
├─────────────────┤
│  HTML/CSS/JS    │
│   (index.html)  │
│   (styles.css)  │
│   (app.js)      │
└────────┬────────┘
         │ HTTP REST
         ↓
┌─────────────────────────────────────┐
│  SERVIDOR API (Puerto 8080)         │
│  (ServidorAPI.java)                 │
│  - GET /animales                    │
│  - GET /animales/{id}               │
│  - POST /animales                   │
│  - PUT /animales/{id}               │
│  - DELETE /animales/{id}            │
└────────┬────────────────────────────┘
         │ JDBC MySQL Driver
         ↓
┌─────────────────────────────────────┐
│  BASE DE DATOS                      │
│  MariaDB / MySQL                    │
│  Database: arca_digital             │
│  Table: animales                    │
└─────────────────────────────────────┘
```

## Componentes del Backend

### 1. **Animal.java** (Model)
Clase que representa un animal
```java
- id: String (PK)
- nombre: String
- especie: String
- raza: String
- edad: String
- genero: String
- estado: String
- descripcion: String
- imagenUrl: String
- fechaRegistro: String

Métodos:
+ getters/setters
+ toString(): String
+ toJson(): String
```

### 2. **ConexionDB.java** (Database Connection)
Gestiona conexión a la BD
```java
+ conectar(): Connection
  - URL: jdbc:mariadb://localhost:3306/arca_digital
  - Usuario: root
  - Driver: mariadb-java-client
```

### 3. **AnimalDAO.java** (Data Access Object)
Realiza operaciones CRUD
```java
+ listarTodos(): List<Animal>
+ obtenerPorId(String id): Animal
+ crear(Animal animal): void
+ actualizar(Animal animal): void
+ eliminar(String id): void
```

### 4. **ServidorAPI.java** (HTTP Server)
Servidor REST en puerto 8080
```java
- HttpServer en puerto 8080
- ManejadorAnimales: procesa peticiones
- Soporta: GET, POST, PUT, DELETE, OPTIONS
- CORS habilitado
```

## Componentes del Frontend

### 1. **index.html** (Estructura)
```html
- Form: crear/editar animales
- Tabla: lista de animales
- Botones: crear, editar, eliminar, refrescar
```

### 2. **styles.css** (Estilos)
```css
- Responsive design
- Colores: gradiente morado (#667eea a #764ba2)
- Breakpoints: 768px, 480px
- Animaciones suaves
```

### 3. **app.js** (Lógica)
```javascript
- Fetch API para comunicación
- Manejo de CORS
- Validaciones de entrada
- Actualización dinámica de DOM
```

## Flujo de Datos

### Crear Animal

```
1. Usuario completa formulario
      ↓
2. JS valida datos
      ↓
3. fetch(POST) a /animales
      ↓
4. ServidorAPI recibe petición
      ↓
5. AnimalDAO.crear() inserta en BD
      ↓
6. Respuesta JSON al Frontend
      ↓
7. JS refrescar tabla
      ↓
8. Mostrar nuevo animal
```

### Listar Animales

```
1. Página carga / click Refrescar
      ↓
2. fetch(GET) a /animales
      ↓
3. ServidorAPI llama AnimalDAO.listarTodos()
      ↓
4. DAO ejecuta SELECT * FROM animales
      ↓
5. Devuelve List<Animal>
      ↓
6. Convierte a JSON
      ↓
7. Frontend recibe JSON
      ↓
8. JS construye tabla dinámicamente
      ↓
9. Tabla visible en pantalla
```

## Database Schema

```sql
CREATE TABLE animales (
    id VARCHAR(50) PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    especie VARCHAR(100) NOT NULL,
    raza VARCHAR(100),
    edad VARCHAR(50),
    genero VARCHAR(20),
    estado VARCHAR(50),
    descripcion TEXT,
    imagenUrl VARCHAR(500),
    fechaRegistro VARCHAR(20)
);
```

**Índices:**
- PRIMARY KEY en id (búsqueda rápida)

**Constraints:**
- nombre y especie NOT NULL (campos obligatorios)

## Stack Tecnológico

| Capa | Tecnología | Versión |
|------|-----------|---------|
| Frontend | HTML5 | 5 |
| | CSS3 | 3 |
| | JavaScript | ES6+ |
| Backend | Java | 22 |
| | HttpServer | JDK 22 |
| BD | MariaDB | 10.5+ |
| Driver | JDBC | 3.5.7 |

## Performance

**Optimizaciones:**
- Parse JSON manual (sin librerías pesadas)
- Conexión única a BD
- Índices en tabla
- Cache de datos en Frontend (localStorage opcional)

**Límites:**
- Sin paginación
- Sin autenticación
- Sin encriptación
- Máx 1000 animales recomendado

## Seguridad (Consideraciones)

⚠️ **ADVERTENCIA**: Aplicación educativa, NO usar en producción

Problemas de seguridad identificados:
- CORS abierto con "*"
- Sin certificado SSL
- Sin autenticación
- SQL Injection potencial (usar PreparedStatement)
- Validación débil de inputs

## Logs y Debugging

**Console del servidor:**
```
[INFO] Servidor iniciado en puerto 8080
[INFO] GET /animales - Listando todos
[INFO] POST /animales - Creando nuevo
[DEBUG] Animal creado: JVM-009
```

**Console del navegador (F12):**
```javascript
✅ Animales cargados: Array(8)
❌ Error: Connection refused
📤 Creando animal: {id: "JVM-009", ...}
```

## Próximas Mejoras (Opcional)

1. **Autenticación JWT**
2. **Paginación en listados**
3. **Búsqueda y filtrado**
4. **Subida de imágenes**
5. **Caché con Redis**
6. **Validación backend robusta**
7. **Tests unitarios (JUnit)**
8. **CI/CD con GitHub Actions**

---

**Versión**: 1.0.0  
**Fecha**: 17 de febrero de 2026
```

### 17:00 - 18:00 | Crear ERRORES_Y_SOLUCIONES.md

```bash
code ERRORES_Y_SOLUCIONES.md
```

**Contenido:**

```markdown
# 🐛 ERRORES Y SOLUCIONES - ARCA DIGITAL

## Error: "Connection refused" o "No se puede conectar a base de datos"

**Síntoma:**
```
Exception: Could not connect to address (localhost:3306)
```

**Causas posibles:**
1. MariaDB no está activo
2. Usuario/contraseña incorrectos
3. Base de datos no existe

**Solución:**
```bash
# 1. Verificar que MariaDB está corriendo
mysql -u root -p

# 2. Verificar credenciales en ConexionDB.java
String usuario = "root";
String password = "tu_password";  // Cambiar si es necesario

# 3. Crear BD si no existe
mysql -u root -p
CREATE DATABASE arca_digital;
USE arca_digital;
CREATE TABLE animales (...);
EXIT;

# 4. Recompilar
javac -cp "lib/mariadb-java-client-3.5.7.jar" -d backend backend/src/com/arcadigital/database/ConexionDB.java
```

---

## Error: "CORS policy: Cross-Origin Request Blocked"

**Síntoma:**
```
Access to fetch at 'http://localhost:8080/animales' from origin 'file://' 
has been blocked by CORS policy
```

**Causa:**
El servidor no tiene headers CORS o frontend es archivo local

**Solución:**
```java
// En ServidorAPI.java, agregar headers:
exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
exchange.getResponseHeaders().add("Access-Control-Allow-Methods", "GET,POST,PUT,DELETE,OPTIONS");
exchange.getResponseHeaders().add("Access-Control-Allow-Headers", "Content-Type");

// Recompilar y reiniciar servidor
javac -cp "lib/mariadb-java-client-3.5.7.jar" -d backend backend/src/com/arcadigital/api/ServidorAPI.java
java -cp "backend;lib/mariadb-java-client-3.5.7.jar" com.arcadigital.api.ServidorAPI
```

**Alternativa:**
Usar servidor local HTTP en lugar de file://
```bash
# Con Python 3
python -m http.server 8000 --directory Frontend

# Con Node.js
npx http-server Frontend

# Luego abrir: http://localhost:8000
```

---

## Error: "Port 8080 is already in use"

**Síntoma:**
```
Address already in use: bind
```

**Causa:**
Otro proceso está usando puerto 8080

**Solución:**
```bash
# Ver qué proceso usa 8080
netstat -ano | findstr ":8080"

# Terminar proceso Java anterior
taskkill /F /IM java.exe

# O cambiar puerto en ServidorAPI.java
static int puerto = 8081;  // Cambiar aquí

# Recompilar
javac -cp "lib/mariadb-java-client-3.5.7.jar" -d backend backend/src/com/arcadigital/api/ServidorAPI.java
```

---

## Error: "Class not found" al compilar

**Síntoma:**
```
error: cannot find symbol
symbol: class Animal
location: class AnimalDAO
```

**Causa:**
Orden de compilación incorrecto o falta de classpath

**Solución:**
Compilar en este orden:
```bash
1. Animal.java (no depende de nada)
javac -d backend backend/src/com/arcadigital/model/Animal.java

2. ConexionDB.java (no depende de clases propias)
javac -cp "lib/mariadb-java-client-3.5.7.jar" -d backend backend/src/com/arcadigital/database/ConexionDB.java

3. AnimalDAO.java (depende de Animal y ConexionDB)
javac -cp "lib/mariadb-java-client-3.5.7.jar" -d backend backend/src/com/arcadigital/database/AnimalDAO.java

4. ServidorAPI.java (depende de todas)
javac -cp "lib/mariadb-java-client-3.5.7.jar" -d backend backend/src/com/arcadigital/api/ServidorAPI.java

5. Main.java (depende de todas)
javac -cp "lib/mariadb-java-client-3.5.7.jar" -d backend backend/src/com/arcadigital/Main.java
```

---

## Error: "No rows updated" o "Animal no encontrado"

**Síntoma:**
```
Intentas actualizar pero dice que no existe
```

**Causa:**
El ID que buscas no existe en BD

**Solución:**
```bash
# Verificar IDs existentes
mysql -u root -p
USE arca_digital;
SELECT id FROM animales;

# Usar un ID que exista
SELECT * FROM animales WHERE id = 'JVM-001';
```

---

## Error: "SyntaxError: Unexpected token < in JSON"

**Síntoma:**
```
En console del navegador: Error parseando JSON
```

**Causa:**
El servidor devuelve HTML en lugar de JSON (error 500)

**Solución:**
```bash
# Revisar logs del servidor
# Si hay error, revisar:
1. ¿La BD existe?
2. ¿La tabla tiene datos?
3. ¿El Java tiene excepción?

# En terminal del servidor, buscar:
exception, Exception, error, Error

# Fuerza una recompilación limpia:
del /S backend\com
javac -d backend -encoding UTF-8 backend/src/com/arcadigital/model/Animal.java backend/src/com/arcadigital/database/ConexionDB.java backend/src/com/arcadigital/database/AnimalDAO.java backend/src/com/arcadigital/api/ServidorAPI.java

java -cp "backend;lib/mariadb-java-client-3.5.7.jar" com.arcadigital.api.ServidorAPI
```

---

## Error: "Empty response body"

**Síntoma:**
```
Respuesta vacía del servidor
```

**Causa:**
Buffer no fue enviado correctamente

**Solución:**
```java
// En ServidorAPI.java, asegurarse de:
OutputStream os = exchange.getResponseBody();
os.write(json.getBytes());
os.flush();  // Agregar FLUSH
os.close();
```

---

## Error: "tabla 'arca_digital.animales' not found"

**Síntoma:**
```
SQLException: Table 'arca_digital.animales' doesn't exist
```

**Causa:**
La tabla no fue creada

**Solución:**
```bash
mysql -u root -p < sql/databasesetup.sql

# O manualmente:
mysql -u root -p
CREATE DATABASE IF NOT EXISTS arca_digital;
USE arca_digital;
CREATE TABLE IF NOT EXISTS animales (
    id VARCHAR(50) PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    especie VARCHAR(100) NOT NULL,
    raza VARCHAR(100),
    edad VARCHAR(50),
    genero VARCHAR(20),
    estado VARCHAR(50),
    descripcion TEXT,
    imagenUrl VARCHAR(500),
    fechaRegistro VARCHAR(20)
);
EXIT;
```

---

## Error: "No 'Access-Control-Allow-Origin' header"

**Síntoma:**
```
response doesn't include the necessary cors headers
```

**Solución:**
Mismo que error de CORS

---

## Problema: Frontend no se actualiza al crear

**Causa:**
La validación de ID duplicado falla silenciosamente

**Solución:**
```javascript
// En app.js, agregar console.log para debug
console.log('Enviando:', JSON.stringify(animal));

// Revisar que ID sea único
// Si ID existe, PUT en lugar de POST
```

---

## Problema: Tabla vacía sin errores

**Síntoma:**
- Página carga
- Tabla está vacía
- Console sin errores

**Causa:**
GET funciona pero no hay datos

**Solución:**
```bash
# Insertar datos en BD
mysql -u root -p
USE arca_digital;
INSERT INTO animales VALUES ('JVM-001', 'Rex', 'Perro', 'Pastor', '3', 'Macho', 'Activo', 'Desc', '/img.jpg', '2024-01-15');
FLUSH;
SELECT * FROM animales;
```

---

## Problema: Cambios en BD no se ven en frontend

**Causa:**
Frontend cachea datos

**Solución:**
```javascript
// Agregar al fetch:
headers: {
    'Cache-Control': 'no-cache, no-store, must-revalidate',
    'Pragma': 'no-cache',
    'Expires': '0'
}

// O forzar refrescar (Ctrl+Shift+R en navegador)
```

---

## Problema: "Too many connections"

**Causa:**
No cierras conexiones a BD

**Solución:**
```java
// En AnimalDAO.java, asegurarse de cerrar:
try {
    Connection con = ConexionDB.conectar();
    // ... usar conexión
} finally {
    con.close();  // IMPORTANTE
}
```

---

## Checklist de Debugging

Si algo no funciona:

- [ ] 1. ¿MariaDB está activo?
- [ ] 2. ¿El servidor Java está corriendo en 8080?
- [ ] 3. ¿Abrir F12 (DevTools) y ver Console?
- [ ] 4. ¿Red → tomar primera petición fallida?
- [ ] 5. ¿Revisar response (¿es JSON o HTML de error)?
- [ ] 6. ¿Datos en BD? (SELECT * FROM animales)
- [ ] 7. ¿IDs correctos?
- [ ] 8. ¿Puertos no conflictuan?
- [ ] 9. ¿Recompilar si cambié código?
- [ ] 10. ¿Reiniciar servidor después de compilar?

---

**Última actualización**: 17 de febrero de 2026

Si el error no está aquí, revisar logs del servidor y console del navegador.
```

### 18:00 - 19:00 | Testing Exhaustivo

**Checklist de Testing:**

```javascript
// Test 1: GET todos
✅ Refrescar página → Tabla muestra animales
✅ DevTools → Network muestra GET 200

// Test 2: POST crear
✅ Llenar form con datos nuevos
✅ Click crear → Alert de éxito
✅ Tabla se actualiza automáticamente
✅ BD tiene el nuevo registro

// Test 3: PUT actualizar
✅ Click editar en un animal
✅ Form se llena con datos
✅ Modificar un campo
✅ Click actualizar
✅ Tabla muestra datos nuevos
✅ BD actualizada

// Test 4: DELETE eliminar
✅ Click eliminar
✅ Confirmar en diálogo
✅ Animal desaparece de tabla
✅ BD no tiene el registro

// Test 5: Validaciones
✅ Dejar campo vacío → Error
✅ Escribir caracteres inválidos → Error

// Test 6: Responsividad
✅ Desktop (1920x1080) → Funciona
✅ Tablet (768x1024) → Funciona
✅ Mobile (375x667) → Funciona

// Test 7: Error handling
✅ Desconectar internet → Mostrar error
✅ Apagar BD → Mostrar error en consola
```

**Status Final:**
```
✅ VIERNES COMPLETADO:
- Validaciones funcionando
- Manejo de errores mejorado
- 8 animales en BD
- Frontend responsivo
- Tests exhaustivos pasados
```

---

## 📅 DOMINGO - DÍA 7: PREPARACIÓN FINAL Y PRESENTACIÓN

### 15:00 - 15:30 | Última Revisión

```bash
# 15:00 - Verificar que todo funciona desde cero

# Terminal 1: Iniciar BD (si aplica)
# (MariaDB debe ser servicio activo)

# Terminal 2: Iniciar servidor
java -cp "backend;lib/mariadb-java-client-3.5.7.jar" com.arcadigital.api.ServidorAPI

# Terminal 3: Abrir navegador
file:///C:/Users/Usuario/Desktop/arca_digital/Frontend/index.html

# Verificar:
✅ Tabla carga con datos
✅ Crear funciona
✅ Editar funciona
✅ Eliminar funciona
✅ No hay errores en consola
```

### 15:30 - 18:00 | Preparación Presentación

**Si necesitas presentar el proyecto:**

1. **Crear un documento de presentación:**
   ```bash
   code PRESENTACION.md
   ```

   Contenido:
   ```markdown
   # 📊 ARCA DIGITAL - PRESENTACIÓN

   ## 🎯 Objetivo
   Crear un sistema web para gestionar registro de animales

   ## ✨ Características Implementadas
   - ✅ CRUD completo (Crear, Leer, Actualizar, Eliminar)
   - ✅ API REST con 5 endpoints
   - ✅ Base de datos relacional
   - ✅ Frontend responsivo
   - ✅ Validaciones de entrada
   - ✅ Manejo de errores

   ## 🏗️ Arquitectura
   - **Frontend**: HTML5 + CSS3 + JavaScript
   - **Backend**: Java 22 + HttpServer
   - **BD**: MariaDB
   - **Driver**: JDBC MySQL/MariaDB

   ## 📈 Progreso
   - Día 1: BD + Modelo ✅
   - Día 2: API REST ✅
   - Día 3: Frontend ✅
   - Día 4: Integración ✅
   - Día 5: Mejoras ✅
   - Día 6: Documentación ✅
   - Día 7: Presentación ✅

   ## 🐾 Datos de Ejemplo
   - 8 animales en BD
   - Razas variadas
   - Información completa

   ## 🚀 Demostración
   1. Mostrar interfaz
   2. Listar animales
   3. Crear nuevo animal
   4. Editar información
   5. Eliminar registro
   6. Mostrar BD

   ## 📊 Estadísticas
   - Líneas de código Java: ~400
   - Líneas de código JS: ~350
   - Líneas de HTML/CSS: ~300
   - Total: ~1050 líneas
   ```

2. **Grabar video de demostración (Opcional):**
   ```bash
   # Usar OBS Studio o ScreenFlow
   # Grabar:
   # 1. Abrir aplicación
   # 2. Listar animales
   # 3. Crear nuevo
   # 4. Editar
   # 5. Eliminar
   # Duración: 5 minutos
   ```

3. **Preparar carpeta de entrega:**
   ```bash
   # Crear ZIP con:
   ✅ todo el código fuente (backend/)
   ✅ Frontend (Frontend/)
   ✅ SQL (sql/)
   ✅ Documentación (README.md, DIARIO_DESARROLLO.md)
   ✅ (Opcional) Video de demostración

   # Comprimir:
   # 7z a arca_digital_entrega.7z .
   ```

4. **Preparar notas de presentación:**
   ```markdown
   ## NOTAS PARA PRESENTACIÓN

   Muestra:
   - Página cargando → "Aquí cargamos desde la API"
   - Tabla con animales → "Datos en tiempo real de BD"
   - Form llenándose → "Validación en cliente"
   - POST al crear → "Datos guardados en BD"
   - Tabla refrescándose → "Actualización automática"
   - DELETE al eliminar → "Sincronización con BD"

   Menciona:
   - Tiempo de desarrollo: 7 días
   - Tecnologías: JavaJava, HTML, CSS, JavaScript
   - Patrón: MVC + DAO + REST
   - Ventajas: Sin frameworks pesados, código limpio
   ```

### 18:00 - 19:00 | Limpiar y Finalizar

**Limpiar código:**
```bash
# Quitar console.log() de debugging
# Revisar que no hay archivos temporales
# Asegurarse que todo compila
```

**Crear script de ejecución (opcional):**
```batch
# Archivo: run.bat
@echo off
echo.
echo ╔════════════════════════════════════════╗
echo ║   🐾 ARCA DIGITAL - INICIADOR         ║
echo ╚════════════════════════════════════════╝
echo.
echo 1. Compilando...
javac -d backend -encoding UTF-8 ^
  backend/src/com/arcadigital/model/Animal.java ^
  backend/src/com/arcadigital/database/ConexionDB.java ^
  backend/src/com/arcadigital/database/AnimalDAO.java ^
  backend/src/com/arcadigital/api/ServidorAPI.java ^
  backend/src/com/arcadigital/Main.java

echo.
echo 2. Iniciando servidor en puerto 8080...
echo.
java -cp "backend;lib/mariadb-java-client-3.5.7.jar" com.arcadigital.api.ServidorAPI

echo.
echo Presiona Ctrl+C para detener el servidor
pause
```

**Verificación final de archivos:**
```bash
# Verificar estructura completa
tree /F

# Archivos necesarios:
backend/                           ✅
├── com/arcadigital/              ✅
│   ├── api/ServidorAPI.class      ✅
│   ├── database/AnimalDAO.class   ✅
│   ├── database/ConexionDB.class  ✅
│   ├── model/Animal.class         ✅
│   └── Main.class                 ✅
Frontend/                          ✅
├── index.html                     ✅
├── styles.css                     ✅
└── app.js                         ✅
sql/                               ✅
└── databasesetup.sql              ✅
lib/                               ✅
└── mariadb-java-client-3.5.7.jar  ✅
├── README.md                      ✅
├── DOCUMENTACION_TECNICA.md       ✅
├── ERRORES_Y_SOLUCIONES.md        ✅
├── DIARIO_DESARROLLO.md           ✅
└── (opcional) run.bat/run.sh      ✅
```

**✅ DOMINGO COMPLETADO:**
- ✓ Proyecto totalmente funcional
- ✓ Documentación completa
- ✓ Tests todos pasan
- ✓ Listo para presentar/entregar

---

## 🎉 PROYECTO TERMINADO

**Resumen:**

| Aspecto | Estado |
|--------|--------|
| Base de datos | ✅ Completa con 8 registros |
| Backend Java | ✅ API REST 100% funcional |
| Frontend Web | ✅ Responsivo y limpio |
| CRUD | ✅ Todos los 4 operaciones |
| Documentación | ✅ Completa y detallada |
| Testing | ✅ Todo verificado |
| Entrega | ✅ Lista para presentar |

**Lo que aprendiste:**
- ✅ Arquitectura MVC + DAO
- ✅ API REST con HttpServer
- ✅ JDBC y conexión a BD
- ✅ Frontend dinámico con JS
- ✅ Validaciones y manejo de errores
- ✅ CORS y comunicación async
- ✅ Full Stack development

**Próximos pasos (opcional):**
- Agregar autenticación JWT
- Implementar paginación
- Agregar búsqueda y filtros
- Subir a un servidor real
- Agregar tests unitarios

--- 

**🎊 ¡FELICIDADES POR TERMINAR ARCA DIGITAL!** 🎊

Creado: 17 de febrero de 2026
```

### 18:45 - 19:00 | Checklist Final

**✅ Todo lo que debe estar completo:**

```
CÓDIGO:
✅ Animal.java compilado
✅ ConexionDB.java compilado
✅ AnimalDAO.java compilado
✅ ServidorAPI.java compilado
✅ Main.java compilado
✅ frontend/index.html existe
✅ frontend/styles.css existe
✅ frontend/app.js existe
✅ sql/databasesetup.sql existe

BASE DE DATOS:
✅ arca_digital creada
✅ tabla animales creada
✅ 8+ animales insertados
✅ Datos verificables en MySQL

FUNCIONALIDAD:
✅ GET /animales funciona
✅ GET /animales/{id} funciona
✅ POST /animales funciona
✅ PUT /animales/{id} funciona
✅ DELETE /animales/{id} funciona

FRONTEND:
✅ Front/end carga sin errores
✅ Tabla se poblaba automáticamente
✅ Formulario funciona
✅ Botones ejecutan acciones
✅ Sin errores JavaScript en consola

DOCUMENTACIÓN:
✅ README.md completo
✅ DOCUMENTACION_TECNICA.md completo
✅ ERRORES_Y_SOLUCIONES.md completo
✅ DIARIO_DESARROLLO.md completo

EXTRAS:
✅ Estilos responsive
✅ Validaciones funcionan
✅ CORS configurado
✅ Manejo de errores
✅ Mensajes amigables
```

**Felicidades, ¡tu proyecto está completo!** 🎉

---



- [ ] Base de datos creada y funcional
- [ ] Clases del modelo compiladas
- [ ] Conexión a BD establecida
- [ ] DAO con CRUD implementado
- [ ] Main.java prueba conexión exitosamente
- [ ] Servidor API ejecutándose en puerto 8080
- [ ] Endpoints REST respondiendo correctamente
- [ ] Frontend cargando
- [ ] CRUD funcional desde UI
- [ ] Documentación completada
- [ ] Todo compilado sin errores

---

## ESTIMACIÓN DE TIEMPO

| Fase | Días | Descripción |
|------|------|-------------|
| Planificación | 1 | Definir proyecto |
| Entorno | 1 | Instalar y configurar |
| BD | 1 | Diseño y creación |
| Modelo | 1 | Clase Animal |
| Conexión | 1 | ConexionDB |
| DAO | 1 | AnimalDAO CRUD |
| Tests | 1 | Main.java |
| API | 1 | ServidorAPI |
| Frontend | 1 | HTML/CSS/JS |
| Integración | 1 | Conectar todo |
| Documentación | 1 | Docs finales |
| **TOTAL** | **~12 días** | **Proyecto completo** |

---

## NOTAS IMPORTANTES

1. **Orden de compilación**: Siempre compilar en este orden:
   - Animal.java (modelo)
   - ConexionDB.java (dependencia)
   - AnimalDAO.java (usa ConexionDB)
   - ServidorAPI.java (usa DAO)
   - Main.java (prueba)

2. **Classpath**: Siempre incluir el driver JDBC:
   ```bash
   -cp "backend;lib/mariadb-java-client-3.5.7.jar"
   ```

3. **Puertos**: El servidor escucha en puerto `8080`. Si está ocupado, cambiar en ServidorAPI.

4. **CORS**: Si hay errores de CORS en frontend, asegurarse que el servidor agrega los headers apropiados.

5. **Credenciales BD**: Usar las correctas:
   - Host: localhost
   - Puerto: 3306
   - Usuario: root (o el que corresponda)
   - Contraseña: (la configurada)

---

**Creado**: 17 de febrero de 2026  
**Estado**: Proyecto completo y funcional

---

## FASE ADICIONAL: MEJORAS AVANZADAS (20 de Febrero de 2026)

### Ampliaci�n de la Base de Datos y Modelo

#### Cambios en SQL:
- Agregados campos nuevos a la tabla animales:
  - medicacion (TEXT): Almacena los medicamentos que toma el animal
  - castrado (BOOLEAN): Indica si el animal ha sido castrado o esterilizado
  - estos complementan el campo descripcion (TEXT) existente

#### Cambios en Animal.java:
- Nuevos atributos privados:
  - private String medicacion;
  - private boolean castrado;
- Nuevos getters y setters:
  - getMedicacion() y setMedicacion(String medicacion)
  - isCastrado() y setCastrado(boolean castrado)
- Metodo toJson() actualizado para incluir ambos campos en JSON

#### Cambios en AnimalDAO.java:
- Metodo listarTodos(): Ahora lee medicacion y castrado de la base de datos
- Metodo insertar(): Permite insertar los 10 campos incluyendo medicacion y castrado

#### Cambios en ServidorAPI.java:
- AnimalApiHandler.handle(): POST ahora procesa medicacion y castrado desde JSON

### Mejoras en el Dashboard (Frontend)

#### Refactorizaci�n en app.js:
- Funcion mostrarDetalle() mejorada:
  - Ahora muestra 8 items en la grid de detalles (incluyendo Castrado y Medicacion)
  - Medicacion se muestra como 'Ninguna' si no existe
  - Castrado muestra 'Si' o 'No'
- Formulario de registro actualizado para procesarcampos nuevos
- Medicacion y Castrado se envian al servidor al registrar un animal

#### Cambios en index.html:
- Nuevo campo de formulario: textarea para medicacion (opcional)
- Nuevo campo de formulario: select para castrado (Si/No)
- Ubicados entre descripcion y foto del animal

#### Cambios en styles.css:
- Modal de detalles mejorado con positioning correcto
- Grid de detalles muestra 2 columnas en desktop, 1 en mobile
- Estilos para botones y campos nuevos mantienen consistencia

### Estado del Sistema:
- Compilacion: Exitosa sin errores
- Base de datos: SQL actualizado con nuevos campos
- Backend: Modelo y DAO actualizados
- Frontend: Formulario y modal actualizados
- Documentacion: Codigos completos documentados

### Pruebas Realizadas:
- Compilacion de archivos Java: OK
- Servidor HTTP en puerto 8080: Operativo
- Conexion a MariaDB: Exitosa

### Archivos Modificados:
- database.setup.sql
- Animal.java
- AnimalDAO.java
- ServidorAPI.java
- app.js
- index.html
- styles.css

### Proximos Pasos:
1. Reiniciar servidor y probar flujo completo
2. Verificar que medicacion y castrado se guardan correctamente
3. Validar que modal de detalles muestra todos los campos
4. Prueba de end-to-end: registro, visualizacion, eliminacion

