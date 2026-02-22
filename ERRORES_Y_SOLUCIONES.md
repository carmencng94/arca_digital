# 📋 Recopilatorio de Errores - Arca Digital

## Resumen General
A lo largo de toda la configuración del proyecto **Arca Digital**, enfrentamos varios errores relacionados con:
- **Estructura de directorios duplicada**
- **Problemas de classpath**
- **Puertos ocupados**
- **Diferencias entre Frontend y Backend**

---

## 🔴 ERROR #1: "Address already in use: bind"

### ¿Cuándo pasó?
Cuando intentabas ejecutar el servidor Java múltiples veces sin terminar el proceso anterior.

### ¿Cuál fue el error exacto?
```
Exception in thread "main" java.net.BindException: Address already in use: bind
        at java.base/sun.nio.ch.Net.bind0(Native Method)
        at java.base/sun.nio.ch.ServerSocketAdaptor.bind(ServerSocketAdaptor.java:89)
        at jdk.httpserver/sun.net.httpserver.ServerImpl.<init>(ServerImpl.java:144)
        at com.arcadigital.api.ServidorAPI.main(ServidorAPI.java:19)
```

### 🔍 Causa raíz
Java intentaba iniciar el servidor en el puerto 8080, pero ese puerto **YA ESTABA EN USO** por:
- Una instancia anterior del servidor que no se cerró correctamente
- Un proceso Java "zombie" que Windows no había limpiado completamente
- Procesos de la extensión VS Code Red Hat Java

### ✅ Solución aplicada
```powershell
# 1. Terminar TODOS los procesos Java
Stop-Process -Name java -Force

# 2. Esperar a que Windows libere completamente los puertos
Start-Sleep -Seconds 3

# 3. Verificar que el puerto está libre
netstat -ano | Select-String ":8080"

# 4. LUEGO ejecutar el servidor
java -cp "backend;lib/mariadb-java-client-3.5.7.jar" com.arcadigital.api.ServidorAPI
```

### 💡 Cómo prevenirlo
- **SIEMPRE termina los procesos Java antes de iniciar nuevamente**
- Usa: `Get-Process java` para verificar qué procesos hay activos
- Usa: `netstat -ano | Select-String ":PUERTO"` para verificar puertos antes de ejecutar
- Nunca abras múltiples instancias sin cerrar la anterior

---

## 🔴 ERROR #2: "Could not find or load main class backend.api.ServidorAPI"

### ¿Cuándo pasó?
Cuando intentabas ejecutar el servidor con classpath incorrecto.

### ¿Cuál fue el error exacto?
```
Error: Could not find or load main class backend.api.ServidorAPI
Caused by: java.lang.ClassNotFoundException: backend.api.ServidorAPI
```

### 🔍 Causa raíz
Tenías **archivos duplicados y confusión en la estructura**:
```
backend/
  ├── api/                                    ❌ DUPLICADA
  │   └── ServidorAPI.java (package backend.api)
  └── src/                                    ✅ CORRECTA
      └── com/arcadigital/api/ServidorAPI.java (package com.arcadigital.api)
```

Compilabas pero ejecutabas apuntando a la clase incorrecta.

### ✅ Solución aplicada
```powershell
# 1. Eliminar la carpeta duplicada
Remove-Item -Recurse backend/api -Force

# 2. Recompilar desde la ubicación correcta
javac -d backend -encoding UTF-8 backend/src/com/arcadigital/model/Animal.java backend/src/com/arcadigital/database/ConexionDB.java backend/src/com/arcadigital/database/AnimalDAO.java backend/src/com/arcadigital/api/ServidorAPI.java backend/src/com/arcadigital/Main.java

# 3. Ejecutar con el classpath correcto
java -cp "backend;lib/mariadb-java-client-3.5.7.jar" com.arcadigital.api.ServidorAPI
```

### 💡 Cómo prevenirlo
- **Mantén una estructura clara y SIN DUPLICADOS**
- Estructura recomendada:
  ```
  backend/
    ├── src/                     (código fuente)
    │   └── com/arcadigital/...
    ├── com/                     (código compilado)
    │   └── arcadigital/...
    └── lib/                     (librerías externas)
  ```
- Si tienes archivos en dos lugares, SIEMPRE usa uno solo
- Borra inmediatamente cualquier duplicado

---

## 🔴 ERROR #3: "Could not find or load main class com.arcadigital.api.ServidorAPI" (Sin driver JDBC)

### ¿Cuándo pasó?
Cuando ejecutabas el servidor pero faltaba incluir el driver JDBC en el classpath.

### 🔍 Causa raíz
Aunque la clase Java existía, **el servidor no podía conectarse a la base de datos** porque no tenía acceso al archivo:
```
lib/mariadb-java-client-3.5.7.jar
```

Sin este JAR, MariaDB no podía comunicarse con Java.

### ✅ Solución aplicada
```powershell
# Incluir el driver JDBC en el classpath usando semicolon (;)
java -cp "backend;lib/mariadb-java-client-3.5.7.jar" com.arcadigital.api.ServidorAPI
```

**Nota:** En Windows usamos semicolon (`;`), en Linux/Mac usamos colon (`:`).

### 💡 Cómo prevenirlo
- **SIEMPRE incluye las librerías en el classpath**
- Estructura correcta:
  ```powershell
  java -cp "ruta_compilado;ruta_librerias/*" NombreClase
  ```
- Verifica que el archivo JAR realmente existe:
  ```powershell
  Get-ChildItem lib/
  ```
- Usa rutas relativas desde el directorio del proyecto

---

## 🔴 ERROR #4: "No se pudo conectar con el servidor" (Error en Frontend)

### ¿Cuándo pasó?
Cuando abrías `index.html` en el navegador pero el frontend no podía conectar con la API.

### 🔍 Causa raíz
El problema tenía DOS causas:

**Causa 1:** Desajuste de puertos
- `ServidorAPI.java` estaba configurado en puerto **9090**
- `app.js` intentaba conectar a puerto **9090**
- Pero execute el comando que cambió a **9090** sin actualizar correctamente

**Causa 2:** Arquitectura confusion
- El archivo `index.html` se abría como archivo local (`file:///...`)
- El JavaScript dentro no sabía cómo conectar a `localhost:8080`

### ✅ Soluciones aplicadas

**Solución A:** Unificar puertos (la que usamos)
```javascript
// En app.js
const apiUrl = 'http://localhost:8080/api/animales';
```

```java
// En ServidorAPI.java
int puerto = 8080;
```

**Solución B:** Usar servidor web separado
```powershell
python -m http.server 3000 --directory "Frontend"
# Luego accede a: http://localhost:3000
```

### 💡 Cómo prevenirlo
- **SIEMPRE verifica que Frontend y Backend usen EL MISMO PUERTO**
- Si vas a cambiar puerto:
  1. Cambia en `ServidorAPI.java` (línea de `int puerto`)
  2. Cambia en `app.js` (línea de `const apiUrl`)
  3. Recompila y reinicia el servidor
- Nunca dejes valores hardcodeados sin verificar si son consistentes

---

## 🔴 ERROR #5: Confusión sobre Dashboard vs JSON

### ¿Cuándo pasó?
Cuando el servidor devolvía JSON pero esperabas ver un dashboard.

### 🔍 Causa raíz
Malentendido sobre cómo funciona la arquitectura cliente-servidor:

```
ANTES (sin entenderlo):
- URL `/api/animales` → debería mostrar dashboard

REALIDAD:
- URL `/api/animales` → devuelve JSON (datos puros)
- URL `/` → sirve index.html (dashboard con UI)
- El dashboard hace fetch a `/api/animales` para obtener datos
```

### ✅ Solución aplicada
Modificé `ServidorAPI.java` para que sirva archivos estáticos también:

```java
// Ruta "/" → sirve dashboard
server.createContext("/", new HttpHandler() {
    // Sirve index.html, app.js, styles.css
});

// Ruta "/api/animales" → sirve JSON
server.createContext("/api/animales", new HttpHandler() {
    // Devuelve datos en JSON
});
```

### 💡 Cómo prevenirlo
- Entiende la diferencia:
  - **API**: devuelve datos (JSON)
  - **página web**: devuelve HTML + CSS + JS
- Dos peticiones diferentes = dos respuestas diferentes
- El navegador primero pide la página HTML, luego el JavaScript hace peticiones a la API

---

## 🟠 ERRORES MENORES (pero importantes)

### Error 6: "ls -Recurse src/" - Comando incorrecto
- **Causa:** Usé sintaxis Linux en Windows PowerShell
- **Solución:** Usar `Get-ChildItem -Recurse src/` en lugar de `ls`

### Error 7: "tail -1" - Comando no existe en PowerShell
- **Causa:** Intenté usar comando Unix en Windows
- **Solución:** Usar `Select-Object -Last 1` o simplemente omitir

### Error 8: Puerto todavía ocupado después de `Stop-Process`
- **Causa:** Windows tarda en liberar puertos en estado `FIN_WAIT_2`
- **Solución:** Usar `Start-Sleep -Seconds 3` para esperar

---

## 📊 Tabla Resumen de Errores

| # | Error | Causa | Solución Rápida |
|---|-------|-------|-----------------|
| 1 | Address already in use | Proceso Java anterior activo | `Stop-Process -Name java -Force` |
| 2 | ClassNotFoundException | Archivo en ubicación duplicada | Eliminar carpeta `backend/api/` |
| 3 | Sin driver JDBC | Falta classpath en JAR | Agregar `-cp "...;lib/mariadb-java-client-3.5.7.jar"` |
| 4 | No conecta con servidor | Desajuste de puertos | Unificar puerto 8080 en ambos archivos |
| 5 | Confusión API vs Dashboard | Malentendido arquitectura | `/api/*` devuelve JSON, `/` devuelve HTML |
| 6 | Comandos incorrectos | Mezclar Linux con Windows | Usar cmdlets PowerShell (Get-ChildItem, etc) |

---

## ✅ Checklist para evitar estos errores en el futuro

Cada vez que vayas a ejecutar el servidor:

```powershell
# 1. Verificar procesos Java
Get-Process java -ErrorAction SilentlyContinue
# Si ves algo, ejecutar: Stop-Process -Name java -Force

# 2. Verificar que puerto está libre
netstat -ano | Select-String ":8080"
# Si sale algo, el puerto está ocupado. Esperar o cambiar el número

# 3. Verificar estructura de directorios
Get-ChildItem backend/ -Recurse | Select-Object Name | head -20
# No debe haber archivos duplicados en dos lugares

# 4. Compilar TODO
javac -d backend -encoding UTF-8 backend/src/com/arcadigital/model/Animal.java backend/src/com/arcadigital/database/ConexionDB.java backend/src/com/arcadigital/database/AnimalDAO.java backend/src/com/arcadigital/api/ServidorAPI.java backend/src/com/arcadigital/Main.java

# 5. Ejecutar con classpath correcto
java -cp "backend;lib/mariadb-java-client-3.5.7.jar" com.arcadigital.api.ServidorAPI

# 6. Verificar que está activo
netstat -ano | Select-String ":8080"
# Deberías ver: TCP 0.0.0.0:8080 ... LISTENING

# 7. Probar en navegador
# http://localhost:8080/
```

---

## 🎯 Lecciones aprendidas

1. **Estructura clara desde el inicio**
   - No duplicar archivos
   - Usar convenciones estándar

2. **Entender la arquitectura**
   - Frontend ≠ Backend
   - API devuelve datos, página devuelve HTML

3. **Verificación antes de ejecutar**
   - Verificar puertos libres
   - Verificar procesos activos
   - Verificar classpath

4. **Mensajes de error son tus amigos**
   - Lee completamente el stack trace
   - La causa raíz está al final

5. **Windows vs Linux**
   - PowerShell no es Bash
   - Semicolon para separar rutas en Windows

---

## 📞 Próximas mejoras sugeridas

Para evitar este tipo de problemas en el futuro:

- [ ] Usar **Maven** o **Gradle** para gestionar dependencias automáticamente
- [ ] Usar **Docker** para empaquetar la aplicación completa
- [ ] Usar **Git** para versionamiento y evitar duplicados
- [ ] Usar un **IDE like IntelliJ IDEA** que maneja automáticamente compilación y rutas
- [ ] Agregar un **script startup.sh** o `startup.bat` para iniciar el servidor automáticamente

---

**Fecha:** 17 de febrero de 2026  
**Proyecto:** Arca Digital  
**Estado:** FUNCIONANDO CORRECTAMENTE  
**Lecciones:** 8 principales errores identificados y resueltos
