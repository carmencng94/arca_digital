document.addEventListener('DOMContentLoaded', () => {
    // Configuración del servidor (Puerto 8080)
    const apiUrl = 'http://localhost:8080/api/animales';
    
    // Referencias al DOM
    const animalGrid = document.getElementById('animal-grid');
    const errorMessage = document.getElementById('error-message');
    const userActionsDiv = document.getElementById('user-actions');
    const btnRegistrar = document.getElementById('btnAbrirModal');

    // === GESTIÓN DE USUARIOS (PÚBLICO VS PRIVADO) ===
    // Obtenemos el usuario del almacenamiento de sesión
    const user = JSON.parse(sessionStorage.getItem('user'));

    if (user) {
        // --- CASO 1: USUARIO LOGUEADO (Administrador/Voluntario) ---
        
        // 1. Mostrar información del usuario y botón de salir en la cabecera
        if (userActionsDiv) {
            userActionsDiv.innerHTML = `
                <div class="user-badge">
                    <span>Usuario: ${user.username}</span>
                    <button id="btnLogout" class="btn-logout">Cerrar Sesión</button>
                </div>
            `;
            
            // Funcionalidad para cerrar sesión
            document.getElementById('btnLogout').addEventListener('click', () => {
                sessionStorage.removeItem('user');
                window.location.reload(); // Recargar la página para volver al modo invitado
            });
        }
        
        // 2. Mostrar el botón de "Registrar Nuevo Animal"
        if (btnRegistrar) {
            btnRegistrar.style.display = 'block';
        }

    } else {
        // --- CASO 2: INVITADO (PÚBLICO) ---
        
        // 1. Mostrar botón para ir al Login
        if (userActionsDiv) {
            userActionsDiv.innerHTML = `
                <a href="login.html" class="btn-login">Iniciar Sesión</a>
            `;
        }
        
        // 2. Asegurar que el botón de registrar esté oculto
        if (btnRegistrar) {
            btnRegistrar.style.display = 'none';
        }
    }

    // === LÓGICA DE LA VENTANA MODAL ===
    const modal = document.getElementById("miModal");
    const spanCerrar = document.getElementsByClassName("close")[0];

    // Abrir modal al hacer clic en el botón (solo si el botón existe y es visible)
    if (btnRegistrar) {
        btnRegistrar.onclick = function() {
            modal.style.display = "block";
        }
    }

    // Cerrar modal al hacer clic en la X
    if (spanCerrar) {
        spanCerrar.onclick = function() {
            modal.style.display = "none";
        }
    }

    // Cerrar modal si se hace clic fuera del formulario (fondo oscuro)
    window.onclick = function(event) {
        if (event.target == modal) {
            modal.style.display = "none";
        }
    }

    // === EVENT LISTENERS ===
    // Escuchar el envío del formulario de creación
    const form = document.getElementById('formAnimal');
    if (form) {
        form.addEventListener('submit', crearAnimal);
    }

    // === FUNCIONES DE LA APLICACIÓN ===

    /**
     * Muestra un mensaje de error en la interfaz de usuario.
     * @param {string} message - El mensaje a mostrar.
     */
    const showError = (message) => {
        if (errorMessage) {
            errorMessage.textContent = message;
            errorMessage.style.display = 'block';
        }
    };

    /**
     * Limpia el grid de animales y oculta el mensaje de error.
     */
    const resetUI = () => {
        if (animalGrid) animalGrid.innerHTML = '';
        if (errorMessage) errorMessage.style.display = 'none';
    };
    
    /**
     * Devuelve la clase CSS correcta para el estado del animal.
     * @param {string} estado - El estado del animal (ej: 'EN_ADOPCION').
     * @returns {string} La clase CSS correspondiente.
     */
    const getStatusClass = (estado) => {
        if (!estado) return '';
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

    /**
     * Crea y devuelve un elemento HTML de tarjeta para un animal.
     * Gestiona la visualización de la imagen y los controles de administración.
     * @param {object} animal - El objeto animal con sus datos.
     * @returns {HTMLElement} El elemento div de la tarjeta.
     */
    const createAnimalCard = (animal) => {
        const card = document.createElement('div');
        card.className = 'animal-card';

        const statusClass = getStatusClass(animal.estado);
        
        // Selección de imagen: prioriza base64, luego URL, luego imagen por defecto
        let imagenMostrar = 'img/test.png';
        if (animal.fotoBase64 && animal.fotoBase64.trim() !== '') {
            imagenMostrar = `data:image/*;base64,${animal.fotoBase64}`;
        } else if (animal.fotoUrl && animal.fotoUrl.trim() !== '') {
            const fotoUrlTrim = animal.fotoUrl.trim();
            // Si es data URI o URL absoluta/relativa válida
            if (fotoUrlTrim.startsWith('data:') || fotoUrlTrim.startsWith('http') || fotoUrlTrim.startsWith('/')) {
                imagenMostrar = fotoUrlTrim;
            } else {
                // Si es un nombre de archivo simple, añadir la ruta de la carpeta 'img'
                imagenMostrar = 'img/' + fotoUrlTrim;
            }
        }

        // Crear elemento de imagen con gestión de errores (fallback)
        const imgEl = document.createElement('img');
        imgEl.className = 'animal-card__image';
        imgEl.alt = `Foto de ${animal.nombre}`;
        imgEl.src = imagenMostrar;
        
        // Si la imagen falla al cargar, mostrar imagen por defecto
        imgEl.onerror = function() {
            this.onerror = null;
            this.src = 'img/test.png';
        };

        // Construir contenido de la tarjeta
        const content = document.createElement('div');
        content.className = 'animal-card__content';
        content.innerHTML = `
            <span class="animal-card__status ${statusClass}">${animal.estado ? animal.estado.replace('_', ' ') : 'DESCONOCIDO'}</span>
            <h2 class="animal-card__name">${animal.nombre}</h2>
            <ul class="animal-card__details">
                <li><strong>ID:</strong> ${animal.id}</li>
                <li><strong>Especie:</strong> ${animal.especie} (${animal.raza})</li>
                <li><strong>Edad:</strong> ${animal.edad} años</li>
                <li><strong>Urgente:</strong> ${animal.urgente ? 'Si' : 'No'}</li>
                <li><strong>Ingreso:</strong> ${animal.fechaIngreso || 'N/A'}</li>
            </ul>
        `;

        card.appendChild(imgEl);
        card.appendChild(content);

        // --- CONTROLES DE ADMINISTRACIÓN (SOLO USUARIOS LOGUEADOS) ---
        if (user) {
            const adminControls = document.createElement('div');
            adminControls.className = 'upload-controls'; // Reutilizamos estilo existente
            adminControls.style.flexDirection = 'column';
            adminControls.style.alignItems = 'stretch';

            // Botón de eliminar (ejemplo de funcionalidad extra)
            const deleteBtn = document.createElement('button');
            deleteBtn.className = 'btn-upload'; // Reutilizamos estilo de botón
            deleteBtn.style.backgroundColor = '#e74c3c'; // Rojo para borrar
            deleteBtn.style.marginTop = '10px';
            deleteBtn.textContent = 'Eliminar Registro';
            
            deleteBtn.onclick = function() {
                if(confirm('¿Estás seguro de que deseas eliminar a ' + animal.nombre + '?')) {
                    eliminarAnimal(animal.id);
                }
            };

            adminControls.appendChild(deleteBtn);
            card.appendChild(adminControls);
        }

        return card;
    };

    /**
     * Convierte la foto seleccionada en el input a una cadena Base64.
     * @returns {Promise<string|null>} Cadena base64 o null si no hay archivo.
     */
    async function obtenerFotoBase64() {
        const input = document.getElementById('fotoInput');
        if (!input || input.files.length === 0) return null;
        
        const file = input.files[0];
        const status = document.getElementById('fotoStatus');
        
        if (status) status.textContent = "Leyendo imagen...";
        
        return new Promise((resolve, reject) => {
            const reader = new FileReader();
            reader.onload = () => {
                const dataUrl = reader.result;
                if (status) status.textContent = "Imagen lista";
                // Extraer solo la parte base64 (después de la coma)
                resolve(dataUrl.split(',')[1]);
            };
            reader.onerror = (e) => {
                if (status) status.textContent = "Error leyendo imagen";
                reject(e);
            };
            reader.readAsDataURL(file);
        });
    }

    /**
     * Maneja el proceso de crear un nuevo animal: procesa la foto y envía los datos.
     * @param {Event} e - El evento del formulario.
     */
    async function crearAnimal(e) {
        e.preventDefault();
        
        // 1. Leemos la foto en Base64 si existe
        let fotoBase64 = await obtenerFotoBase64();

        // Obtener valor del select de urgente
        const urgenteInput = document.getElementById('urgente');
        const esUrgente = urgenteInput ? urgenteInput.value === 'true' : false;

        // 2. Construimos el objeto animal alineado con el modelo Java
        const animal = {
            id: document.getElementById('id').value,
            nombre: document.getElementById('nombre').value,
            especie: document.getElementById('especie').value,
            raza: document.getElementById('raza').value,
            edad: document.getElementById('edad').value,
            estado: document.getElementById('estado').value,
            urgente: esUrgente,
            descripcion: document.getElementById('descripcion').value,
            fotoUrl: '', // Dejamos vacío, usaremos fotoBase64
            fotoBase64: fotoBase64
        };
        
        // Validaciones básicas
        if (!animal.id || !animal.nombre || !animal.especie) {
            alert('Por favor completa los campos obligatorios: ID, Nombre y Especie');
            return;
        }
        
        console.log('Enviando datos al servidor:', animal);
        
        // 3. Enviamos los datos al backend mediante POST
        fetch(apiUrl, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(animal)
        })
        .then(response => {
            if (!response.ok) throw new Error('Error en el servidor: ' + response.status);
            return response.json();
        })
        .then(data => {
            console.log('Respuesta del servidor:', data);
            alert('Animal registrado con éxito');
            
            // Limpiar formulario y estado
            document.getElementById('formAnimal').reset();
            const status = document.getElementById('fotoStatus');
            if(status) status.textContent = "Ningún archivo seleccionado";
            
            // Cerrar el modal
            if (modal) modal.style.display = "none";

            // Recargar la lista para ver el nuevo animal
            fetchAnimals();
        })
        .catch(error => {
            console.error('Error al crear:', error);
            alert('Error al registrar: ' + error.message);
        });
    }

    /**
     * Función para eliminar un animal (Solo accesible si el botón se renderiza).
     * @param {string} id - El ID del animal a eliminar.
     */
    function eliminarAnimal(id) {
        fetch(apiUrl + '/' + id, {
            method: 'DELETE'
        })
        .then(response => {
            if (!response.ok) throw new Error('Error al eliminar');
            return response.json();
        })
        .then(data => {
            alert('Registro eliminado correctamente');
            fetchAnimals(); // Recargar lista
        })
        .catch(error => {
            console.error(error);
            alert('No se pudo eliminar el registro');
        });
    }

    /**
     * Obtiene los datos de la API y puebla el grid de animales.
     */
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
                if (animalGrid) animalGrid.appendChild(card);
            });

        } catch (error) {
            console.error("Error al obtener los datos de la API:", error);
            showError("No se pudo conectar con el servidor. Asegúrate de que el backend de Java está funcionando.");
        }
    };

    // Iniciar la carga de datos al abrir la página
    fetchAnimals();
});