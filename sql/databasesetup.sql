-- 1. Crear la base de datos (si no existe)
CREATE DATABASE IF NOT EXISTS arca_digital;

-- 2. Seleccionar la base de datos para usarla
USE arca_digital;

-- 3. Borrar la tabla si ya existe para empezar de cero
DROP TABLE IF EXISTS animales;

-- 4. Crear la tabla 'animales' con los nuevos campos
CREATE TABLE animales (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    especie VARCHAR(50) NOT NULL,
    raza VARCHAR(50),
    edad INT,
    descripcion TEXT,
    estado VARCHAR(20) NOT NULL DEFAULT 'RESCATADO',
    urgente BOOLEAN DEFAULT FALSE,
    foto_url VARCHAR(255),
    foto BLOB,
    fecha_ingreso TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB;

-- 5. Insertar datos de prueba más detallados-- Opcional: Limpiamos la tabla primero para no tener duplicados
TRUNCATE TABLE animales;

INSERT INTO animales (nombre, especie, raza, edad, descripcion, estado, urgente, foto_url) VALUES 
-- LOS DATOS ORIGINALES
('Rex', 'Perro', 'Pastor Alemán', 5, 'Un perro leal y enérgico. Le encanta jugar a la pelota y necesita espacio.', 'EN_ADOPCION', false, 'rex.png'),
('Luna', 'Gato', 'Siamés', 2, 'Una gata tranquila y cariñosa. Perfecta para un hogar sin mucho ruido.', 'EN_TRATAMIENTO', true, 'luna.png'),
('Thor', 'Perro', 'Mestizo', 3, 'Juguetón y amigable con otros perros. Necesita un dueño activo que le guste correr.', 'RESCATADO', false, 'thor.png'),

-- NUEVOS REGISTROS (PERROS)
('Balto', 'Perro', 'Husky Siberiano', 7, 'Perro senior muy noble. Tiene artrosis leve, requiere paseos cortos y tranquilos.', 'EN_ACOGIDA', true, 'balto.png'),
('Coco', 'Perro', 'Bulldog Francés', 4, 'Sufre de alergias alimentarias. Muy cariñoso con niños, pero ronca fuerte.', 'EN_TRATAMIENTO', false, 'coco.png'),
('Linda', 'Perro', 'Galgo', 2, 'Rescatada de la caza. Es muy tímida al principio, necesita paciencia y suavidad.', 'RESCATADO', true, 'https://placedog.net/500/300?id=6'),

-- NUEVOS REGISTROS (GATOS)
('Garfield', 'Gato', 'Común Europeo', 6, 'Gato con sobrepeso en dieta estricta. Muy tranquilo, le encanta dormir al sol.', 'EN_ADOPCION', false, 'https://placekitten.com/500/300?image=7'),
('Nala', 'Gato', 'Persa', 3, 'Requiere cepillado diario. Muy independiente y con mucho carácter.', 'EN_ADOPCION', false, 'https://placekitten.com/500/300?image=9'),
('Sombra', 'Gato', 'Bombay (Negro)', 1, 'Cachorro muy activo y curioso. Se lleva bien con otros gatos.', 'EN_ACOGIDA', false, 'https://placekitten.com/500/300?image=12'),

-- NUEVOS REGISTROS (OTRAS ESPECIES)
('Blue', 'Ave', 'Guacamayo', 15, 'Loro rescatado. Habla bastante y requiere una jaula grande y estimulación mental.', 'EN_ADOPCION', false, 'https://images.unsplash.com/photo-1552728089-57bdde30ebd1?q=80&w=500&auto=format&fit=crop'),
('Tambor', 'Conejo', 'Belier', 1, 'Conejo doméstico abandonado. Muy suave y dócil, ideal para pisos.', 'RESCATADO', false, 'https://images.unsplash.com/photo-1585110396063-8355845b3728?q=80&w=500&auto=format&fit=crop'),
('Pippin', 'Hámster', 'Sirio', 1, 'Pequeño roedor nocturno. Rescatado de una tienda de mascotas.', 'ADOPTADO', false, 'https://images.unsplash.com/photo-1425082661705-1834bfd905bf?q=80&w=500&auto=format&fit=crop');