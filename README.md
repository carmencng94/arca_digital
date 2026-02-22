# Arca Digital - Sistema de Gestión de Refugios de Animales

Arca Digital es una solución técnica diseñada para la administración profesional de centros de rescate y adopción. El sistema permite el control detallado de la población de animales, integrando un seguimiento clínico y una interfaz de usuario dinámica.

## 1. Características Técnicas Destacadas

* **Gestión Clínica Avanzada**: Incorporación de campos específicos para el seguimiento de medicación y estado de castración/esterilización.
* **Interfaz Dinámica**: Panel de control (Dashboard) basado en CSS Grid que renderiza tarjetas de animales mediante peticiones asíncronas.
* **Visualización por Modales**: Sistema de vista detallada mediante ventanas emergentes que optimiza el flujo de navegación sin recargar la página.
* **Seguridad de Acceso**: Control de sesiones mediante sessionStorage para diferenciar entre usuarios públicos y administradores.
* **Persistencia de Datos**: Conexión robusta a MariaDB mediante el patrón DAO (Data Access Object).



## 2. Estructura de Directorios

El proyecto sigue una organización estandarizada para facilitar la escalabilidad y el mantenimiento:

```text
arca_digital/
├── src/com/arcadigital/           # Lógica del Servidor (Java)
│   ├── Main.java                  # Clase de inicio
│   ├── api/                       # Handlers de HTTP y Endpoints
│   ├── database/                  # Conexión JDBC y clases DAO
│   └── model/                     # Clases POJO (Entidades)
├── resources/frontend/            # Interfaz de Usuario
│   ├── index.html                 # Página principal
│   ├── styles.css                 # Hoja de estilos global
│   └── js/                        # Lógica del cliente (app.js, auth.js)
├── lib/                           # Librerías externas (Driver MariaDB)
├── sql/                           # Scripts de base de datos
└── out/                           # Binarios compilados