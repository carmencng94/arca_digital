# 📚 ARCA DIGITAL - CÓDIGOS COMPLETOS DEL PROYECTO

Fecha: 20 de febrero de 2026
Versión: v2.0 - Sistema de visualización dinámica con modal detalle

---

## 🗂️ ESTRUCTURA DEL PROYECTO

```
arca_digital/
├── resources/
│   └── frontend/
│       ├── index.html          [HTML Principal]
│       ├── login.html          [Página de Login]
│       ├── styles.css          [Estilos CSS]
│       ├── js/
│       │   ├── app.js          [Lógica Principal]
│       │   ├── auth.js         [Autenticación]
│       │   └── login.js        [Lógica Login]
│       └── img/                [Imágenes de animales]
├── sql/
│   └── databasesetup.sql       [Setup de BD]
└── src/
    └── com/arcadigital/
        ├── Main.java
        ├── model/Animal.java
        ├── database/
        │   ├── ConexionDB.java
        │   └── AnimalDAO.java
        └── api/ServidorAPI.java

```

---

## 1️⃣ HTML - resources/frontend/index.html

\`\`\`html
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Arca Digital - Dashboard</title>
    <link rel="stylesheet" href="styles.css">
    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;700&display=swap" rel="stylesheet">
</head>
<body>

    <header>
        <div class="header-top">
            <h1>🐾 Arca Digital</h1>
            <div id="user-actions"></div>
        </div>
        <p>Dashboard de Animales - Gestión integral del refugio</p>
        
        <button id="btnAbrirModal" class="btn-nuevo" style="display: none;">
            ➕ Registrar Nuevo Animal
        </button>
    </header>

    <main>
        <div id="error-message" class="error-message" style="display: none;"></div>
        <div id="animal-grid" class="animal-grid"></div>
    </main>

    <!-- Modal para Registro de Animales -->
    <div id="miModal" class="modal">
        <div class="modal-content">
            <span class="close">&times;</span>
            <h2>🐾 Nuevo Registro de Animal</h2>
            <form id="formAnimal">
                <div class="form-group">
                    <label>ID Registro:</label>
                    <input type="text" id="id" placeholder="Ej: JVM-001" required>
                </div>
                <div class="form-group">
                    <label>Nombre:</label>
                    <input type="text" id="nombre" required>
                </div>
                <div class="form-row">
                    <div class="form-group">
                        <label>Especie:</label>
                        <select id="especie">
                            <option value="Perro">🐕 Perro</option>
                            <option value="Gato">🐈 Gato</option>
                            <option value="Otro">🦁 Otro</option>
                        </select>
                    </div>
                    <div class="form-group">
                        <label>Raza:</label>
                        <input type="text" id="raza">
                    </div>
                </div>
                <div class="form-row">
                    <div class="form-group">
                        <label>Edad (años):</label>
                        <input type="number" id="edad" value="0" min="0">
                    </div>
                    <div class="form-group">
                        <label>Estado:</label>
                        <select id="estado">
                            <option value="RESCATADO">🆘 Rescatado</option>
                            <option value="EN_ADOPCION">🏠 En Adopción</option>
                            <option value="EN_TRATAMIENTO">⚕️ En Tratamiento</option>
                        </select>
                    </div>
                </div>
                <div class="form-group">
                    <label>¿Es Urgente?</label>
                    <select id="urgente">
                        <option value="false">No</option>
                        <option value="true">Sí - Urgente</option>
                    </select>
                </div>
                <div class="form-group">
                    <label>Descripción:</label>
                    <textarea id="descripcion" rows="3" placeholder="Escriba información adicional del animal..."></textarea>
                </div>
                <div class="form-group">
                    <label>📷 Foto del Animal:</label>
                    <input type="file" id="fotoAnimal" accept="image/*">
                    <small style="display: block; margin-top: 5px; color: #aaa;">
                        Opcional - Formatos: JPG, PNG, GIF (Máx. 5MB)
                    </small>
                </div>
                <button type="submit" class="btn-submit">💾 Guardar Ficha</button>
            </form>
        </div>
    </div>

    <footer>
        <p>© 2026 Proyecto Arca Digital - Panel de Control | Gestión de Refugio de Animales</p>
    </footer>

    <script src="js/auth.js"></script>
    <script src="js/app.js"></script>
</body>
</html>
\`\`\`

---

## 2️⃣ JavaScript - resources/frontend/js/app.js

\`\`\`javascript
document.addEventListener('DOMContentLoaded', () => {
    const API_URL = 'http://localhost:8080/api';
    const animalGrid = document.getElementById('animal-grid');
    const user = JSON.parse(sessionStorage.getItem('user'));
    const formAnimal = document.getElementById('formAnimal');
    const modal = document.getElementById("miModal");
    const btnAbrirModal = document.getElementById('btnAbrirModal');
    const closeBtn = document.querySelector(".close");

    // Modal de detalle
    let detalleModal = document.getElementById('detalleModal');
    if (!detalleModal) {
        detalleModal = document.createElement('div');
        detalleModal.id = 'detalleModal';
        detalleModal.className = 'modal modal-detalle';
        detalleModal.innerHTML = \`
            <div class="modal-content modal-content-detalle">
                <span class="close-detalle">&times;</span>
                <div id="detalleContenido"></div>
            </div>
        \`;
        document.body.appendChild(detalleModal);
    }

    /**
     * Configura los listeners de la modal de registro
     */
    const setupModalListeners = () => {
        if (btnAbrirModal) {
            btnAbrirModal.addEventListener('click', (e) => {
                e.preventDefault();
                modal.style.display = "block";
            });
        }

        if (closeBtn) {
            closeBtn.addEventListener('click', () => {
                modal.style.display = "none";
            });
        }

        window.addEventListener('click', (event) => {
            if (event.target === modal) {
                modal.style.display = "none";
            }
            if (event.target === detalleModal) {
                detalleModal.style.display = "none";
            }
        });

        const closeDetalle = document.querySelector('.close-detalle');
        if (closeDetalle) {
            closeDetalle.addEventListener('click', () => {
                detalleModal.style.display = "none";
            });
        }
    };

    /**
     * Muestra el detalle del animal en modal expandido
     */
    const mostrarDetalle = (animal) => {
        const detalleContenido = document.getElementById('detalleContenido');
        const urgente = animal.urgente ? '🔴 SÍ - URGENTE' : '✅ No';
        
        detalleContenido.innerHTML = \`
            <div class="detalle-header">
                <img src="\${animal.fotoUrl}" alt="\${animal.nombre}" class="detalle-imagen" onerror="this.src='/img/rex.png';">
                <div class="detalle-titulo">
                    <h1>\${animal.nombre}</h1>
                    <p class="detalle-especie">\${animal.especie}</p>
                </div>
            </div>
            <div class="detalle-body">
                <div class="detalle-grid">
                    <div class="detalle-item">
                        <label>ID Registro:</label>
                        <p>\${animal.id}</p>
                    </div>
                    <div class="detalle-item">
                        <label>Especie:</label>
                        <p>\${animal.especie}</p>
                    </div>
                    <div class="detalle-item">
                        <label>Raza:</label>
                        <p>\${animal.raza || 'N/A'}</p>
                    </div>
                    <div class="detalle-item">
                        <label>Edad:</label>
                        <p>\${animal.edad} años</p>
                    </div>
                    <div class="detalle-item">
                        <label>Estado:</label>
                        <p><span class="estado-badge estado-\${animal.estado.toLowerCase()}">\${animal.estado.replace('_', ' ')}</span></p>
                    </div>
                    <div class="detalle-item">
                        <label>¿Urgente?</label>
                        <p>\${urgente}</p>
                    </div>
                </div>
                <div class="detalle-descripcion">
                    <label>📝 Descripción:</label>
                    <p>\${animal.descripcion || 'Sin descripción disponible.'}</p>
                </div>
                <div class="detalle-fecha">
                    <label>📅 Fecha de Ingreso:</label>
                    <p>\${animal.fechaIngreso || 'N/A'}</p>
                </div>
            </div>
        \`;

        detalleModal.style.display = "block";
    };

    /**
     * Carga todos los animales desde la API
     */
    const fetchAnimals = async () => {
        try {
            const response = await fetch(\`\${API_URL}/animales\`);
            if (!response.ok) throw new Error(\`Error: \${response.status}\`);
            const animals = await response.json();
            
            animalGrid.innerHTML = '';
            if (animals.length === 0) {
                animalGrid.innerHTML = '<p class="sin-animales">📭 No hay animales registrados todavía.</p>';
                return;
            }

            animals.forEach(animal => {
                animalGrid.appendChild(createAnimalCard(animal));
            });
        } catch (error) {
            console.error("Error al obtener animales:", error);
            animalGrid.innerHTML = '<p class="error-message">❌ Error de conexión con el servidor.</p>';
        }
    };

    /**
     * Crea una tarjeta de animal con interactividad
     */
    const createAnimalCard = (animal) => {
        const card = document.createElement('div');
        card.className = 'animal-card';
        card.dataset.id = animal.id;

        const estadoClass = \`estado-\${animal.estado.toLowerCase()}\`;

        card.innerHTML = \`
            <div class="animal-card__container">
                <img class="animal-card__image" src="\${animal.fotoUrl}" alt="Foto de \${animal.nombre}" onerror="this.src='/img/rex.png';">
                <div class="animal-card__overlay"></div>
                <div class="animal-card__content">
                    <span class="animal-card__status \${estadoClass}">\${animal.estado.replace('_', ' ')}</span>
                    \${animal.urgente ? '<span class="animal-card__urgente">🔴 URGENTE</span>' : ''}
                    <h2 class="animal-card__name">\${animal.nombre}</h2>
                    <p class="animal-card__especie">\${animal.especie}</p>
                    <ul class="animal-card__details">
                        <li><strong>Raza:</strong> \${animal.raza || 'N/A'}</li>
                        <li><strong>Edad:</strong> \${animal.edad} años</li>
                    </ul>
                    <button class="btn-ver-detalle">Ver Detalle 👁️</button>
                </div>
            </div>\`;

        const verDetalleBtn = card.querySelector('.btn-ver-detalle');
        verDetalleBtn.addEventListener('click', (e) => {
            e.preventDefault();
            mostrarDetalle(animal);
        });

        if (user) {
            const adminControls = document.createElement('div');
            adminControls.className = 'upload-controls';
            adminControls.innerHTML = \`
                <button class="btn-admin btn-delete">🗑️ Eliminar</button>
                <input type="file" class="input-file-upload" style="display: none;" accept="image/*">
                <button class="btn-admin btn-change-photo">📷 Cambiar Foto</button>\`;
            
            card.appendChild(adminControls);

            adminControls.querySelector('.btn-delete').addEventListener('click', (e) => {
                e.stopPropagation();
                if(confirm(\`¿Eliminar a \${animal.nombre}? Esta acción no se puede deshacer.\`)) {
                    eliminarAnimal(animal.id);
                }
            });

            const inputFile = adminControls.querySelector('.input-file-upload');
            adminControls.querySelector('.btn-change-photo').addEventListener('click', (e) => {
                e.stopPropagation();
                inputFile.click();
            });
            inputFile.addEventListener('change', (e) => {
                if (e.target.files.length > 0) subirYActualizarFoto(animal.id, e.target.files[0]);
            });
        }
        return card;
    };

    /**
     * Elimina un animal
     */
    const eliminarAnimal = async (id) => {
        try {
            const resp = await fetch(\`\${API_URL}/animales/\${id}\`, { method: 'DELETE' });
            if (resp.ok) {
                const cardElement = document.querySelector(\`.animal-card[data-id='\${id}']\`);
                if (cardElement) cardElement.remove();
                detalleModal.style.display = "none";
                alert('✅ Animal eliminado correctamente.');
                fetchAnimals();
            } else {
                throw new Error(\`Error al eliminar: \${resp.status}\`);
            }
        } catch (error) {
            console.error("Error al eliminar animal:", error);
            alert('❌ Error al eliminar el animal.');
        }
    };

    /**
     * Sube una foto al servidor
     */
    const subirFoto = async (file) => {
        if (!file) return '/img/rex.png';

        try {
            const uploadResponse = await fetch(\`\${API_URL}/upload\`, {
                method: 'POST',
                headers: { 'X-Filename': file.name },
                body: file
            });

            if (!uploadResponse.ok) {
                throw new Error(\`Error al subir foto: \${uploadResponse.status}\`);
            }

            const result = await uploadResponse.json();
            return result.fotoUrl || '/img/rex.png';
        } catch (error) {
            console.error("Error al subir foto:", error);
            alert('❌ Error al subir la foto. Se usará la foto por defecto.');
            return '/img/rex.png';
        }
    };

    /**
     * Actualiza la foto de un animal existente
     */
    const subirYActualizarFoto = async (animalId, file) => {
        try {
            const fotoUrl = await subirFoto(file);
            
            const updateResponse = await fetch(\`\${API_URL}/animales\`, {
                method: 'PUT',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ id: animalId, fotoUrl: fotoUrl })
            });

            if (updateResponse.ok) {
                window.location.reload();
            } else {
                throw new Error(\`Error al actualizar: \${updateResponse.status}\`);
            }
        } catch (error) {
            console.error("Error al actualizar foto:", error);
            alert('❌ Error al actualizar la foto.');
        }
    };

    /**
     * Configura el formulario de registro
     */
    const setupFormListener = () => {
        if (formAnimal) {
            formAnimal.addEventListener('submit', async (e) => {
                e.preventDefault();

                if (!user) {
                    alert('❌ Debes iniciar sesión para registrar un animal.');
                    return;
                }

                try {
                    const fotoInput = document.getElementById('fotoAnimal');
                    let fotoUrl = '/img/rex.png';
                    
                    if (fotoInput && fotoInput.files.length > 0) {
                        console.log("📸 Subiendo foto...");
                        fotoUrl = await subirFoto(fotoInput.files[0]);
                        console.log("✅ Foto subida exitosamente:", fotoUrl);
                    }

                    const data = {
                        id: document.getElementById('id').value,
                        nombre: document.getElementById('nombre').value,
                        especie: document.getElementById('especie').value,
                        raza: document.getElementById('raza').value,
                        edad: parseInt(document.getElementById('edad').value, 10),
                        estado: document.getElementById('estado').value,
                        urgente: document.getElementById('urgente').value === 'true',
                        descripcion: document.getElementById('descripcion').value,
                        fotoUrl: fotoUrl
                    };

                    console.log("📝 Registrando animal... ", data);
                    const resp = await fetch(\`\${API_URL}/animales\`, {
                        method: 'POST',
                        headers: { 'Content-Type': 'application/json' },
                        body: JSON.stringify(data)
                    });

                    if (resp.ok) {
                        const resultado = await resp.json();
                        console.log("✅ Animal registrado:", resultado);
                        formAnimal.reset();
                        modal.style.display = "none";
                        fetchAnimals();
                        alert('✅ Animal registrado exitosamente con foto.');
                    } else {
                        const errorData = await resp.json();
                        throw new Error(errorData.error || \`Error: \${resp.status}\`);
                    }
                } catch (error) {
                    console.error("Error al registrar animal:", error);
                    alert(\`❌ Error al registrar: \${error.message}\`);
                }
            });
        }
    };

    setupModalListeners();
    setupFormListener();
    fetchAnimals();
});
\`\`\`

---

## 3️⃣ Autenticación - resources/frontend/js/auth.js

\`\`\`javascript
document.addEventListener('DOMContentLoaded', () => {
    const user = JSON.parse(sessionStorage.getItem('user'));
    const isLoginPage = window.location.pathname.endsWith('login.html');

    if (user && isLoginPage) {
        window.location.href = 'index.html';
        return;
    }

    const userActionsDiv = document.getElementById('user-actions');
    const btnAbrirModal = document.getElementById('btnAbrirModal');

    if (user) {
        if (userActionsDiv) {
            userActionsDiv.innerHTML = \`
                <div class="user-badge">
                    <span>👤 \${user.username}</span>
                    <button id="btnLogout" class="btn-logout">Cerrar Sesión</button>
                </div>\`;
        }
        
        if (btnAbrirModal) btnAbrirModal.style.display = 'block';

        const btnLogout = document.getElementById('btnLogout');
        if (btnLogout) {
            btnLogout.addEventListener('click', () => {
                sessionStorage.removeItem('user');
                window.location.reload();
            });
        }
    } else {
        if (userActionsDiv && !isLoginPage) {
            userActionsDiv.innerHTML = \`<a href="login.html" class="btn-login">🔒 Iniciar Sesión</a>\`;
        }
        
        if (btnAbrirModal) btnAbrirModal.style.display = 'none';
    }
});
\`\`\`

---

## 4️⃣ Backend Java - src/com/arcadigital/api/ServidorAPI.java (Fragmento)

\`\`\`java
static class AnimalApiHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
        exchange.getResponseHeaders().set("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        exchange.getResponseHeaders().set("Access-Control-Allow-Headers", "Content-Type, Authorization");

        if ("OPTIONS".equals(exchange.getRequestMethod())) {
            exchange.sendResponseHeaders(204, -1);
            return;
        }

        String method = exchange.getRequestMethod();
        AnimalDAO dao = new AnimalDAO();
        
        try {
            if ("GET".equals(method)) {
                List<Animal> lista = dao.listarTodos();
                String json = lista.stream().map(Animal::toJson).collect(Collectors.joining(",", "[", "]"));
                sendResponse(exchange, 200, json);

            } else if ("POST".equals(method)) {
                String requestBody = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
                Map<String, String> params = parseJsonBody(requestBody);
                
                if (!params.containsKey("nombre") || params.get("nombre").trim().isEmpty()) {
                    sendResponse(exchange, 400, "{\\"error\\":\\"El campo 'nombre' es requerido\\"}");
                    return;
                }

                Animal nuevo = new Animal();
                nuevo.setNombre(params.get("nombre"));
                nuevo.setEspecie(params.getOrDefault("especie", "Otro"));
                nuevo.setRaza(params.getOrDefault("raza", ""));
                nuevo.setEdad(Integer.parseInt(params.getOrDefault("edad", "0")));
                nuevo.setDescripcion(params.getOrDefault("descripcion", ""));
                nuevo.setEstado(params.getOrDefault("estado", "RESCATADO"));
                nuevo.setUrgente(Boolean.parseBoolean(params.getOrDefault("urgente", "false")));
                nuevo.setFotoUrl(params.getOrDefault("fotoUrl", "/img/rex.png"));
                
                Animal insertado = dao.insertar(nuevo);
                if (insertado != null) {
                    sendResponse(exchange, 201, insertado.toJson());
                } else {
                    throw new IOException("No se pudo crear el animal en la base de datos.");
                }
            }
        } catch (Exception e) {
            sendResponse(exchange, 500, "{\\"error\\":\\"Error interno del servidor: " + e.getMessage() + "\\"}");
        }
    }
}
\`\`\`

---

## 5️⃣ Base de Datos - SQL Setup

\`\`\`sql
CREATE TABLE animales (
    id INT PRIMARY KEY AUTO_INCREMENT,
    nombre VARCHAR(100) NOT NULL,
    especie VARCHAR(50),
    raza VARCHAR(100),
    edad INT,
    descripcion TEXT,
    estado VARCHAR(50),
    urgente BOOLEAN DEFAULT FALSE,
    foto_url VARCHAR(255) DEFAULT '/img/rex.png',
    fecha_ingreso TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
\`\`\`

---

## 📋 CARACTERÍSTICAS IMPLEMENTADAS

### ✅ Completado Viernes 20/02/2026:

1. **Visualización Dinámica de Animales**
   - Fetch desde API /api/animales
   - Generación automática de tarjetas (cards)
   - Grid responsivo (desktop/mobile)

2. **Sistema de Detalle con Modal**
   - Clic en tarjeta abre información completa
   - Mostra todos los campos del animal
   - Animaciones suaves

3. **Estilos Mejorados**
   - CSS moderno con Grid y animations
   - Tema oscuro profesional
   - Responsive design
   - Efectos hover y transiciones

4. **Sistema de Registro**
   - Carga de foto antes de registrar
   - Validación de campos
   - Integración con base de datos

5. **Gestión de Fotos**
   - Upload durante registro
   - Cambio de foto post-registro
   - Foto por defecto (rex.png)

6. **Autenticación Funcional**
   - Roles: admin y voluntario
   - Sesión en sessionStorage
   - Botones contextuales

---

## 🔧 CÓMO EJECUTAR

1. **Compilar Backend:**
   \`\`\`bash
   javac -d out -cp "lib/mariadb-java-client-3.5.7.jar;src" src/com/arcadigital/**/*.java
   \`\`\`

2. **Iniciar Servidor:**
   \`\`\`bash
   java -cp "out;lib/mariadb-java-client-3.5.7.jar" com.arcadigital.api.ServidorAPI
   \`\`\`

3. **Acceder a:**
   - Dashboard: http://localhost:8080
   - login: admin / admin123 o voluntario / voluntario123

---

## 📝 PRÓXIMAS TAREAS

- [ ] Filtros por estado y urgencia
- [ ] Búsqueda avanzada
- [ ] Reportes estadísticos
- [ ] Historial de cambios
- [ ] Integración de notificaciones
- [ ] Exportar a PDF

