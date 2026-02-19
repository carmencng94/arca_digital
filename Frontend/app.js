document.addEventListener('DOMContentLoaded', () => {
    // URL base de la API. Cambiar si el servidor está en otro dominio o puerto.
    const API_URL = 'http://localhost:8080/api';

    // Referencias a elementos clave del DOM.
    const animalGrid = document.getElementById('animal-grid');
    const btnAbrirModal = document.getElementById('btnAbrirModal');
    const userActionsDiv = document.getElementById('user-actions');
    const modal = document.getElementById("miModal");
    const formAnimal = document.getElementById('formAnimal');
    
    // Obtener el usuario de la sesión para determinar el nivel de acceso.
    const user = JSON.parse(sessionStorage.getItem('user'));

    /**
     * Función principal que inicializa la aplicación.
     */
    const init = () => {
        configurarInterfazUsuario();
        configurarEventListeners();
        fetchAnimals();
    };

    /**
     * Configura la visibilidad de los botones y la información del usuario
     * basándose en si hay una sesión activa o no.
     */
    const configurarInterfazUsuario = () => {
        if (user) {
            // MODO ADMINISTRADOR/VOLUNTARIO
            userActionsDiv.innerHTML = `
                <div class="user-badge">
                    <span>Usuario: ${user.username}</span>
                    <button id="btnLogout" class="btn-logout">Cerrar Sesión</button>
                </div>`;
            btnAbrirModal.style.display = 'block'; // Mostrar botón de registrar.
        } else {
            // MODO PÚBLICO/INVITADO
            userActionsDiv.innerHTML = `<a href="login.html" class="btn-login">Iniciar Sesión</a>`;
            btnAbrirModal.style.display = 'none';
        }
    };
    
    /**
     * Agrupa todos los event listeners de la aplicación para mantener el código organizado.
     */
    const configurarEventListeners = () => {
        // Clic en el botón de "Cerrar Sesión".
        userActionsDiv.addEventListener('click', (e) => {
            if (e.target.id === 'btnLogout') {
                sessionStorage.removeItem('user');
                window.location.reload(); // Recargar para reflejar el cambio de estado.
            }
        });
        
        // Abrir y cerrar el modal de registro.
        if (btnAbrirModal) btnAbrirModal.onclick = () => modal.style.display = "block";
        document.querySelector(".close").onclick = () => modal.style.display = "none";
        window.onclick = (event) => {
            if (event.target == modal) modal.style.display = "none";
        };
        
        // Envío del formulario para crear un nuevo animal.
        if(formAnimal) formAnimal.addEventListener('submit', crearAnimal);
    };

    /**
     * Obtiene la lista de animales de la API y los renderiza en el DOM.
     */
    const fetchAnimals = async () => {
        try {
            const response = await fetch(`${API_URL}/animales`);
            if (!response.ok) throw new Error(`Error del servidor: ${response.status}`);
            
            const animals = await response.json();
            animalGrid.innerHTML = ''; // Limpiar el grid antes de añadir las nuevas tarjetas.
            
            if (animals.length === 0) {
                animalGrid.innerHTML = '<p>No hay animales registrados por el momento.</p>';
            } else {
                animals.forEach(animal => {
                    const card = createAnimalCard(animal);
                    animalGrid.appendChild(card);
                });
            }
        } catch (error) {
            console.error("Error al obtener animales:", error);
            animalGrid.innerHTML = '<p class="error-message">No se pudo conectar con el servidor. Inténtalo más tarde.</p>';
        }
    };

    /**
     * Crea el elemento HTML para la tarjeta de un animal.
     * @param {object} animal - El objeto animal con sus datos.
     * @returns {HTMLElement} El elemento div de la tarjeta.
     */
    const createAnimalCard = (animal) => {
        const card = document.createElement('div');
        card.className = 'animal-card';
        card.dataset.id = animal.id; // Guardar el ID en el dataset para un fácil acceso.

        card.innerHTML = `
            <img class="animal-card__image" src="${animal.fotoUrl}" alt="Foto de ${animal.nombre}" onerror="this.src='/img/rex.png';">
            <div class="animal-card__content">
                <span class="animal-card__status">${animal.estado.replace('_', ' ')}</span>
                <h2 class="animal-card__name">${animal.nombre}</h2>
                <ul class="animal-card__details">
                    <li><strong>ID:</strong> ${animal.id}</li>
                    <li><strong>Especie:</strong> ${animal.especie} (${animal.raza})</li>
                    <li><strong>Edad:</strong> ${animal.edad} años</li>
                    <li><strong>Urgente:</strong> ${animal.urgente ? 'Sí' : 'No'}</li>
                    <li><strong>Ingreso:</strong> ${animal.fechaIngreso}</li>
                </ul>
            </div>
        `;
        
        // Si hay un usuario logueado, añadir los controles de administración.
        if (user) {
            const adminControls = document.createElement('div');
            adminControls.className = 'animal-card__admin-controls';
            adminControls.innerHTML = `
                <button class="btn-admin btn-delete">Eliminar</button>
                <input type="file" class="input-file-upload" style="display: none;" accept="image/*">
                <button class="btn-admin btn-change-photo">Cambiar Foto</button>
            `;
            card.appendChild(adminControls);

            // Añadir listeners específicos para los botones de esta tarjeta.
            const btnDelete = adminControls.querySelector('.btn-delete');
            btnDelete.onclick = () => {
                if(confirm(`¿Estás seguro de que quieres eliminar a ${animal.nombre}?`)) {
                    eliminarAnimal(animal.id);
                }
            };
            
            const btnChangePhoto = adminControls.querySelector('.btn-change-photo');
            const inputFile = adminControls.querySelector('.input-file-upload');
            
            btnChangePhoto.onclick = () => inputFile.click(); // Abrir el selector de archivo.
            inputFile.onchange = (e) => {
                if (e.target.files.length > 0) {
                    subirYActualizarFoto(animal.id, e.target.files[0]);
                }
            };
        }
        return card;
    };
    
    /**
     * Envía la petición para crear un nuevo animal.
     * @param {Event} e - El evento de envío del formulario.
     */
    const crearAnimal = async (e) => {
        e.preventDefault();
        
        const animalData = {
            nombre: document.getElementById('nombre').value,
            especie: document.getElementById('especie').value,
            raza: document.getElementById('raza').value,
            edad: document.getElementById('edad').value,
            estado: document.getElementById('estado').value,
            urgente: document.getElementById('urgente').value === 'true',
            descripcion: document.getElementById('descripcion').value
        };

        try {
            const response = await fetch(`${API_URL}/animales`, {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(animalData)
            });

            if (!response.ok) throw new Error('La respuesta del servidor no fue OK');
            
            alert('Animal registrado con éxito.');
            formAnimal.reset();
            modal.style.display = "none";
            fetchAnimals(); // Recargar la lista para ver el nuevo animal.

        } catch (error) {
            console.error('Error al crear animal:', error);
            alert('Hubo un error al registrar el animal.');
        }
    };
    
    /**
     * Orquesta la subida de una nueva foto y la actualización en la BD.
     * @param {number} animalId - El ID del animal a modificar.
     * @param {File} file - El archivo de imagen a subir.
     */
    const subirYActualizarFoto = async (animalId, file) => {
        try {
            // 1. Subir el archivo al endpoint de upload.
            const uploadResponse = await fetch(`${API_URL}/upload`, {
                method: 'POST',
                headers: {
                    'X-Filename': file.name
                },
                body: file
            });
            if (!uploadResponse.ok) throw new Error('Error al subir el archivo.');
            
            const uploadResult = await uploadResponse.json();
            const nuevaFotoUrl = uploadResult.fotoUrl;

            // 2. Actualizar la URL de la foto en la base de datos.
            const updateResponse = await fetch(`${API_URL}/animales`, {
                method: 'PUT',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ id: animalId, fotoUrl: nuevaFotoUrl })
            });
            if (!updateResponse.ok) throw new Error('Error al actualizar la URL en la base de datos.');

            // 3. Actualizar la imagen en el DOM sin recargar la página.
            const card = document.querySelector(`.animal-card[data-id='${animalId}']`);
            if (card) {
                const img = card.querySelector('.animal-card__image');
                img.src = nuevaFotoUrl;
            }
            alert('Foto actualizada correctamente.');

        } catch (error) {
            console.error('Error en el proceso de cambio de foto:', error);
            alert('No se pudo cambiar la foto.');
        }
    };
    
    /**
     * Envía la petición para eliminar un animal.
     * @param {number} animalId - El ID del animal a eliminar.
     */
    const eliminarAnimal = async (animalId) => {
        try {
            const response = await fetch(`${API_URL}/animales/${animalId}`, {
                method: 'DELETE'
            });

            if (!response.ok) throw new Error('La respuesta del servidor no fue OK');
            
            // Eliminar la tarjeta del DOM sin recargar toda la lista.
            const card = document.querySelector(`.animal-card[data-id='${animalId}']`);
            if (card) card.remove();
            
            alert('Animal eliminado correctamente.');
            
        } catch (error) {
            console.error('Error al eliminar animal:', error);
            alert('Hubo un error al eliminar el animal.');
        }
    };

    // Iniciar la aplicación.
    init();
});
