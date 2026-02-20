# CODIGO COMPLETO: app.js

Este archivo contiene la lógica completa del dashboard frontend.

## Ubicacion
`resources/frontend/js/app.js`

## Contenido Completo

```javascript
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
        detalleModal.innerHTML = `
            <div class="modal-content modal-content-detalle">
                <span class="close-detalle">&times;</span>
                <div id="detalleContenido"></div>
            </div>
        `;
        document.body.appendChild(detalleModal);
    }

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

    const mostrarDetalle = (animal) => {
        const detalleContenido = document.getElementById('detalleContenido');
        const castrado = animal.castrado ? 'Si' : 'No';
        const medicacion = animal.medicacion ? animal.medicacion : 'Ninguna';
        
        detalleContenido.innerHTML = `
            <div class="detalle-header">
                <img src="${animal.fotoUrl}" alt="${animal.nombre}" class="detalle-imagen" onerror="this.src='/img/rex.png';">
                <div class="detalle-titulo">
                    <h1>${animal.nombre}</h1>
                    <p class="detalle-especie">${animal.especie}</p>
                </div>
            </div>
            <div class="detalle-body">
                <div class="detalle-grid">
                    <div class="detalle-item">
                        <label>ID Registro:</label>
                        <p>${animal.id}</p>
                    </div>
                    <div class="detalle-item">
                        <label>Especie:</label>
                        <p>${animal.especie}</p>
                    </div>
                    <div class="detalle-item">
                        <label>Raza:</label>
                        <p>${animal.raza || 'N/A'}</p>
                    </div>
                    <div class="detalle-item">
                        <label>Edad:</label>
                        <p>${animal.edad} anos</p>
                    </div>
                    <div class="detalle-item">
                        <label>Estado:</label>
                        <p><span class="estado-badge estado-${animal.estado.toLowerCase()}">${animal.estado.replace('_', ' ')}</span></p>
                    </div>
                    <div class="detalle-item">
                        <label>Urgente:</label>
                        <p>${animal.urgente ? 'Si - Urgente' : 'No'}</p>
                    </div>
                    <div class="detalle-item">
                        <label>Castrado:</label>
                        <p>${castrado}</p>
                    </div>
                    <div class="detalle-item">
                        <label>Medicacion:</label>
                        <p>${medicacion}</p>
                    </div>
                </div>
                <div class="detalle-descripcion">
                    <label>Descripcion:</label>
                    <p>${animal.descripcion || 'Sin descripcion disponible.'}</p>
                </div>
                <div class="detalle-fecha">
                    <label>Fecha de Ingreso:</label>
                    <p>${animal.fechaIngreso || 'N/A'}</p>
                </div>
            </div>
        `;

        detalleModal.style.display = "block";
    };

    const fetchAnimals = async () => {
        try {
            const response = await fetch(`${API_URL}/animales`);
            if (!response.ok) throw new Error(`Error: ${response.status}`);
            const animals = await response.json();
            
            animalGrid.innerHTML = '';
            if (animals.length === 0) {
                animalGrid.innerHTML = '<p class="sin-animales">No hay animales registrados todavía.</p>';
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

    const createAnimalCard = (animal) => {
        const card = document.createElement('div');
        card.className = 'animal-card';
        card.dataset.id = animal.id;

        const urgentClass = animal.urgente ? 'urgent' : '';
        const estadoClass = `estado-${animal.estado.toLowerCase()}`;

        card.innerHTML = `
            <div class="animal-card__container">
                <img class="animal-card__image" src="${animal.fotoUrl}" alt="Foto de ${animal.nombre}" onerror="this.src='/img/rex.png';">
                <div class="animal-card__overlay"></div>
                <div class="animal-card__content">
                    <span class="animal-card__status ${estadoClass}">${animal.estado.replace('_', ' ')}</span>
                    ${animal.urgente ? '<span class="animal-card__urgente">URGENTE</span>' : ''}
                    <h2 class="animal-card__name">${animal.nombre}</h2>
                    <p class="animal-card__especie">${animal.especie}</p>
                    <ul class="animal-card__details">
                        <li><strong>Raza:</strong> ${animal.raza || 'N/A'}</li>
                        <li><strong>Edad:</strong> ${animal.edad} años</li>
                    </ul>
                    <button type="button" class="btn-ver-detalle">Ver Detalle</button>
                </div>
            </div>`;

        const verDetalleBtn = card.querySelector('.btn-ver-detalle');
        if (verDetalleBtn) {
            verDetalleBtn.addEventListener('click', function(e) {
                e.preventDefault();
                e.stopPropagation();
                console.log("Mostrando detalle de:", animal.nombre);
                mostrarDetalle(animal);
            });
        }

        if (user) {
            const adminControls = document.createElement('div');
            adminControls.className = 'upload-controls';
            adminControls.innerHTML = `
                <button type="button" class="btn-admin btn-delete">Eliminar</button>
                <input type="file" class="input-file-upload" style="display: none;" accept="image/*">
                <button type="button" class="btn-admin btn-change-photo">Cambiar Foto</button>`;
            
            card.appendChild(adminControls);

            adminControls.querySelector('.btn-delete').addEventListener('click', (e) => {
                e.stopPropagation();
                if(confirm(`Eliminar a ${animal.nombre}? Esta acción no se puede deshacer.`)) {
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

    const eliminarAnimal = async (id) => {
        try {
            const resp = await fetch(`${API_URL}/animales/${id}`, { method: 'DELETE' });
            if (resp.ok) {
                const cardElement = document.querySelector(`.animal-card[data-id='${id}']`);
                if (cardElement) cardElement.remove();
                detalleModal.style.display = "none";
                alert('Animal eliminado correctamente.');
                fetchAnimals();
            } else {
                throw new Error(`Error al eliminar: ${resp.status}`);
            }
        } catch (error) {
            console.error("Error al eliminar animal:", error);
            alert('Error al eliminar el animal.');
        }
    };

    const subirFoto = async (file) => {
        if (!file) return '/img/rex.png';

        try {
            const uploadResponse = await fetch(`${API_URL}/upload`, {
                method: 'POST',
                headers: { 'X-Filename': file.name },
                body: file
            });

            if (!uploadResponse.ok) {
                throw new Error(`Error al subir foto: ${uploadResponse.status}`);
            }

            const result = await uploadResponse.json();
            return result.fotoUrl || '/img/rex.png';
        } catch (error) {
            console.error("Error al subir foto:", error);
            alert('Error al subir la foto. Se usará la foto por defecto.');
            return '/img/rex.png';
        }
    };

    const subirYActualizarFoto = async (animalId, file) => {
        try {
            const fotoUrl = await subirFoto(file);
            
            const updateResponse = await fetch(`${API_URL}/animales`, {
                method: 'PUT',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ id: animalId, fotoUrl: fotoUrl })
            });

            if (updateResponse.ok) {
                window.location.reload();
            } else {
                throw new Error(`Error al actualizar: ${updateResponse.status}`);
            }
        } catch (error) {
            console.error("Error al actualizar foto:", error);
            alert('Error al actualizar la foto.');
        }
    };

    const setupFormListener = () => {
        if (formAnimal) {
            formAnimal.addEventListener('submit', async (e) => {
                e.preventDefault();

                if (!user) {
                    alert('Debes iniciar sesión para registrar un animal.');
                    return;
                }

                try {
                    const fotoInput = document.getElementById('fotoAnimal');
                    let fotoUrl = '/img/rex.png';
                    
                    if (fotoInput && fotoInput.files.length > 0) {
                        console.log("Subiendo foto...");
                        fotoUrl = await subirFoto(fotoInput.files[0]);
                        console.log("Foto subida exitosamente:", fotoUrl);
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
                        medicacion: document.getElementById('medicacion').value || null,
                        castrado: document.getElementById('castrado').value === 'true',
                        fotoUrl: fotoUrl
                    };

                    console.log("Registrando animal con datos:", data);
                    const resp = await fetch(`${API_URL}/animales`, {
                        method: 'POST',
                        headers: { 'Content-Type': 'application/json' },
                        body: JSON.stringify(data)
                    });

                    if (resp.ok) {
                        const resultado = await resp.json();
                        console.log("Animal registrado exitosamente:", resultado);
                        formAnimal.reset();
                        modal.style.display = "none";
                        fetchAnimals();
                        alert('Animal registrado exitosamente con foto.');
                    } else {
                        const errorData = await resp.json();
                        throw new Error(errorData.error || `Error: ${resp.status}`);
                    }
                } catch (error) {
                    console.error("Error al registrar animal:", error);
                    alert(`Error al registrar: ${error.message}`);
                }
            });
        }
    };

    setupModalListeners();
    setupFormListener();
    fetchAnimals();
});
```

## Caracteristicas Principales

1. **Gestion de Animales**: Fetch, crear, editar, eliminar
2. **Modal Expandido**: Muestra 8 items de informacion (incluyendo medicacion y castrado)
3. **Upload de Fotos**: Soporta cambio de foto en animales existentes
4. **Autenticacion Simple**: Control de rol mediante sessionStorage
5. **Validacion**: Verificación de usuario logueado antes de operaciones
6. **Manejo de Errores**: Try-catch y console.log para depuracion
7. **Sin Emojis**: Codigo limpio sin simbolos decorativos
