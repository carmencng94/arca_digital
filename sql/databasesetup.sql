-- Eliminar la base de datos si existe para empezar desde cero (opcional, usar con cuidado)
DROP DATABASE IF EXISTS arca_digital;

-- 1. Crear la base de datos con el conjunto de caracteres adecuado
CREATE DATABASE arca_digital CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- 2. Seleccionar la base de datos para usarla
USE arca_digital;

-- 3. Borrar la tabla si ya existe para asegurar un entorno limpio
DROP TABLE IF EXISTS animales;

-- 4. Crear la tabla 'animales' con una estructura profesional
CREATE TABLE animales (
    id INT AUTO_INCREMENT PRIMARY KEY COMMENT 'Identificador único para cada animal',
    nombre VARCHAR(100) NOT NULL COMMENT 'Nombre del animal',
    especie VARCHAR(50) NOT NULL COMMENT 'Especie (ej: Perro, Gato)',
    raza VARCHAR(50) COMMENT 'Raza específica del animal',
    edad INT DEFAULT 0 COMMENT 'Edad del animal en años',
    descripcion TEXT COMMENT 'Descripción detallada, historial y carácter',
    estado VARCHAR(20) NOT NULL DEFAULT 'RESCATADO' COMMENT 'Estado actual (RESCATADO, EN_ADOPCION, ADOPTADO)',
    urgente BOOLEAN DEFAULT FALSE COMMENT 'Indica si el caso es de alta urgencia',
    foto_url VARCHAR(255) NOT NULL DEFAULT '/img/rex.png' COMMENT 'Ruta relativa a la imagen del animal',
    fecha_ingreso TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Fecha y hora de registro en el sistema'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 5. Insertar datos de prueba para el arranque inicial
INSERT INTO animales (nombre, especie, raza, edad, descripcion, estado) VALUES
('Rex', 'Perro', 'Pastor Alemán', 5, 'Un perro leal y enérgico que busca un hogar activo. Le encanta jugar a la pelota.', 'EN_ADOPCION'),
('Luna', 'Gato', 'Siamés', 2, 'Una gata tranquila y cariñosa. Perfecta para un hogar sin mucho ruido.', 'RESCATADO'),
('Thor', 'Perro', 'Mestizo', 3, 'Juguetón y amigable con otros perros. Necesita un dueño que le guste correr.', 'RESCATADO');

-- --- FIN DEL SCRIPT ---
