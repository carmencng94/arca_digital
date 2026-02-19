document.addEventListener('DOMContentLoaded', () => {
    const API_URL = 'http://localhost:8080/api';
    const animalGrid = document.getElementById('animal-grid');
    const user = JSON.parse(sessionStorage.getItem('user'));
    const formAnimal = document.getElementById('formAnimal');
    const modal = document.getElementById("miModal");

    /**
     * Inicializa la carga de animales al abrir el dashboard.
     */
    const fetchAnimals = async () => {
        try {
            const response = await fetch(`${API_URL}/animales`);
            if (!response.ok) throw new Error(`Error: ${response.status}`);
            const animals = await response.json();
            
            animalGrid.innerHTML = '';
            if (animals.length === 0) {
                animalGrid.innerHTML = '<p>No hay animales registrados.</p>';
                return;
            }

            animals.forEach(animal => {
                animalGrid.appendChild(createAnimalCard(animal));
            });
        } catch (error) {
            console.error("Error al obtener animales:", error);
            animalGrid.innerHTML = '<p class="error-message">Error de conexión con el servidor.</p>';
        }
    };

    /**
     * Crea la tarjeta del animal. Renderiza botones de gestión si el usuario está logueado.
     */
    const createAnimalCard = (animal) => {
        const card = document.createElement('div');
        card.className = 'animal-card';
        card.dataset.id = animal.id;

        card.innerHTML = `
            <img class="animal-card__image" src="${animal.fotoUrl}" alt="Foto" onerror="this.src='/img/rex.png';">
            <div class="animal-card__content">
                <span class="animal-card__status">${animal.estado.replace('_', ' ')}</span>
                <h2 class="animal-card__name">${animal.nombre}</h2>
                <ul class="animal-card__details">
                    <li><strong>Especie:</strong> ${animal.especie}</li>
                    <li><strong>Edad:</strong> ${animal.edad} años</li>
                </ul>
            </div>`;

        // Solo añadir controles si hay sesión iniciada
        if (user) {
            const adminControls = document.createElement('div');
            adminControls.className = 'upload-controls';
            adminControls.innerHTML = `
                <button class="btn-admin btn-delete">Eliminar</button>
                <input type="file" class="input-file-upload" style="display: none;" accept="image/*">
                <button class="btn-admin btn-change-photo">Cambiar Foto</button>`;
            
            card.appendChild(adminControls);

            // Listener para eliminar
            adminControls.querySelector('.btn-delete').onclick = () => {
                if(confirm(`¿Eliminar a ${animal.nombre}?`)) eliminarAnimal(animal.id);
            };

            // Listener para subir foto
            const inputFile = adminControls.querySelector('.input-file-upload');
            adminControls.querySelector('.btn-change-photo').onclick = () => inputFile.click();
            inputFile.onchange = (e) => {
                if (e.target.files.length > 0) subirYActualizarFoto(animal.id, e.target.files[0]);
            };
        }
        return card;
    };

    // Funciones de API (eliminar, subir foto, crear)
    const eliminarAnimal = async (id) => {
        const resp = await fetch(`${API_URL}/animales/${id}`, { method: 'DELETE' });
        if (resp.ok) {
            document.querySelector(`.animal-card[data-id='${id}']`).remove();
            alert('Eliminado con éxito.');
        }
    };

    const subirYActualizarFoto = async (animalId, file) => {
        const formData = new FormData(); // Usar FormData para envío de archivos si la API lo soporta
        const uploadResponse = await fetch(`${API_URL}/upload`, {
            method: 'POST',
            headers: { 'X-Filename': file.name },
            body: file
        });
        
        if (uploadResponse.ok) {
            const result = await uploadResponse.json();
            await fetch(`${API_URL}/animales`, {
                method: 'PUT',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ id: animalId, fotoUrl: result.fotoUrl })
            });
            window.location.reload();
        }
    };

    if (formAnimal) {
        formAnimal.addEventListener('submit', async (e) => {
            e.preventDefault();
            const data = {
                nombre: document.getElementById('nombre').value,
                especie: document.getElementById('especie').value,
                // ... añadir resto de campos
            };
            const resp = await fetch(`${API_URL}/animales`, {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(data)
            });
            if (resp.ok) {
                modal.style.display = "none";
                fetchAnimals();
            }
        });
    }

    fetchAnimals();
});