# Arca Digital - Sistema de Gestión de Animales

Proyecto de registro y gestión de animales rescatados con una interfaz web moderna.

##  Estructura de Directorios (Profesional)

```
arca_digital/
├── src/                           # Código fuente Java
│   └── com/arcadigital/
│       ├── Main.java             # Punto de entrada
│       ├── api/
│       │   └── ServidorAPI.java  # Servidor HTTP + Endpoints REST
│       ├── database/
│       │   ├── ConexionDB.java   # Conexión a BD
│       │   └── AnimalDAO.java    # Acceso a datos
│       └── model/
│           └── Animal.java       # Modelo de datos
│
├── resources/                     # Recursos (archivos estáticos)
│   └── frontend/
│       ├── index.html           # Dashboard principal
│       ├── login.html           # Página de login
│       ├── styles.css           # Estilos globales
│       ├── js/                  # JavaScript
│       │   ├── app.js           # Lógica del dashboard
│       │   ├── auth.js          # Autenticación
│       │   └── login.js         # Lógica de login
│       └── img/                 # Imágenes
│
├── lib/                          # Librerías externas
│   └── mariadb-java-client-3.5.7.jar
│
├── out/                          # Archivos compilados (.class)
│
├── sql/                          # Scripts de base de datos
│   └── databasesetup.sql
│
├── docs/                         # Documentación
├── build.sh                      # Script de compilación
└── README.md                     # Este archivo
```

##  Compilación y Ejecución

### Opción 1: Compilación manual
```powershell
cd C:\Users\Usuario\Desktop\arca_digital
javac -d out -cp "lib\mariadb-java-client-3.5.7.jar;src" `
    src\com\arcadigital\model\Animal.java `
    src\com\arcadigital\database\ConexionDB.java `
    src\com\arcadigital\database\AnimalDAO.java `
    src\com\arcadigital\api\ServidorAPI.java `
    src\com\arcadigital\Main.java

java -cp "out;lib\mariadb-java-client-3.5.7.jar" com.arcadigital.api.ServidorAPI
```

### Opción 2: Con script
```powershell
./build.sh
```

##  Configuración

### Base de Datos
- **Host:** localhost
- **Puerto:** 3306
- **Usuario:** root
- **Contraseña:** 1234
- **Base de Datos:** arca_digital

Edita `src/com/arcadigital/database/ConexionDB.java` para cambiar estos valores.

### Servidor HTTP
- **Puerto:** 8080
- **URL:** http://localhost:8080

Edita `src/com/arcadigital/api/ServidorAPI.java` para cambiar el puerto.

##  Credenciales de Prueba

| Usuario | Contraseña |
|---------|-----------|
| admin | admin123 |
| voluntario | voluntario123 |

## 📡 API Endpoints

### Animales
- `GET /api/animales` - Listar todos
- `POST /api/animales` - Crear nuevo
- `PUT /api/animales` - Actualizar foto
- `DELETE /api/animales/{id}` - Eliminar

### Autenticación
- `POST /api/login` - Iniciar sesión

### Archivos
- `POST /api/upload` - Subir imagen

## 🛠️ Tecnologías

- **Backend:** Java (sin frameworks)
- **Frontend:** HTML5, CSS3, JavaScript vanilla
- **Base de Datos:** MariaDB
- **Servidor:** HttpServer nativo de Java

##  Cambios Recientes

- Reorganización de estructura (separación clara src/resources/out)
- Actualización de rutas en ServidorAPI.java
- Estructuración profesional de archivos frontend (carpeta js/)
-  Compilación centralizada en carpeta `out/`

##  Contribución

Para cambios futuros:
1. Modifica archivos en `src/`
2. Compila con el comando anterior
3. Ejecuta el servidor
4. Los archivos estáticos en `resources/frontend/` se sirven automáticamente
