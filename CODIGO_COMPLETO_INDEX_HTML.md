# CODIGO COMPLETO: index.html

Este archivo contiene la estructura HTML completa del dashboard frontend.

## Ubicacion
`resources/frontend/index.html`

## Contenido Completo

```html
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
            <h1>Arca Digital</h1>
            <div id="user-actions"></div>
        </div>
        <p>Dashboard de Animales</p>
        
        <button id="btnAbrirModal" class="btn-nuevo" style="display: none;"> + Registrar Nuevo Animal</button>
    </header>

    <main>
        <div id="error-message" class="error-message" style="display: none;"></div>
        <div id="animal-grid" class="animal-grid">
        </div>
    </main>

    <!-- MODAL DE REGISTRO -->
    <div id="miModal" class="modal">
        <div class="modal-content">
            <span class="close">&times;</span>
            <h2>Nuevo Registro</h2>
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
                            <option value="Perro">Perro</option>
                            <option value="Gato">Gato</option>
                            <option value="Otro">Otro</option>
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
                        <input type="number" id="edad" value="0">
                    </div>
                    <div class="form-group">
                        <label>Estado:</label>
                        <select id="estado">
                            <option value="RESCATADO">Rescatado</option>
                            <option value="EN_ADOPCION">En Adopcion</option>
                            <option value="EN_TRATAMIENTO">En Tratamiento</option>
                        </select>
                    </div>
                </div>
                <div class="form-group">
                    <label>¿Es Urgente?</label>
                    <select id="urgente">
                        <option value="false">No</option>
                        <option value="true">Si</option>
                    </select>
                </div>
                <div class="form-group">
                    <label>Descripcion:</label>
                    <textarea id="descripcion" rows="3"></textarea>
                </div>
                
                <!-- NUEVOS CAMPOS (20/02/2026) -->
                <div class="form-group">
                    <label>Medicacion:</label>
                    <textarea id="medicacion" rows="2" placeholder="Ej: Aspirina 500mg diarios"></textarea>
                </div>
                <div class="form-group">
                    <label>Castrado o Esterilizado:</label>
                    <select id="castrado">
                        <option value="false">No</option>
                        <option value="true">Si</option>
                    </select>
                </div>
                
                <div class="form-group">
                    <label>Foto del Animal:</label>
                    <input type="file" id="fotoAnimal" accept="image/*">
                    <small style="display: block; margin-top: 5px; color: #666;">La foto es opcional</small>
                </div>
                <button type="submit" class="btn-submit">Guardar Ficha</button>
            </form>
        </div>
    </div>

    <!-- MODAL DE DETALLE (Dinamicamente creado por app.js) -->
    <!-- El id="detalleModal" se crea en JavaScript mediante createElement -->
    <!-- Contiene: detalle-header, detalle-body, detalle-grid, detalle-item -->
    <!-- Muestra: ID, Especie, Raza, Edad, Estado, Urgente, Castrado, Medicacion -->

    <footer>
        <p>Proyecto Arca Digital - Panel de Control</p>
    </footer>

    <script src="js/auth.js"></script>
    <script src="js/app.js"></script>
</body>
</html>
```

## Estructura del Documento

### 1. Head
- Meta charset: UTF-8
- Meta viewport: Responsive design
- Links CSS: styles.css
- Google Fonts: Inter (400, 700)

### 2. Header
- Titulo: "Arca Digital"
- Div user-actions: Muestra usuario logueado o link de login
- Boton btn-nuevo: Abre modal de registro (mostrado solo si hay usuario)

### 3. Main
- error-message: Para mostrar errores (hidden por defecto)
- animal-grid: Grid responsivo que contiene las tarjetas dinamicas

### 4. Modal de Registro (id="miModal")
Estructura:
- Boton close: Cierra el modal
- Form id="formAnimal": Contiene los siguientes campos:
  - ID Registro (text, required)
  - Nombre (text, required)
  - Especie (select: Perro, Gato, Otro)
  - Raza (text)
  - Edad (number, min=0)
  - Estado (select: RESCATADO, EN_ADOPCION, EN_TRATAMIENTO)
  - Urgente (select: No, Si)
  - Descripcion (textarea)
  - Medicacion (textarea) - NUEVO CAMPO
  - Castrado (select: No, Si) - NUEVO CAMPO
  - Foto (file input, accept="image/*")
  - Boton submit: "Guardar Ficha"

### 5. Modal de Detalle (Dinamico)
Se crea en JavaScript mediante:
```javascript
detalleModal = document.createElement('div');
detalleModal.id = 'detalleModal';
detalleModal.className = 'modal modal-detalle';
```

Contiene:
- close-detalle: Boton para cerrar
- detalleContenido: Div que contiene el HTML dinamicamente construido
  - detalle-header: Imagen + titulo + especie
  - detalle-body:
    - detalle-grid: 8 items (ID, Especie, Raza, Edad, Estado, Urgente, Castrado, Medicacion)
    - detalle-descripcion: Descripcion del animal
    - detalle-fecha: Fecha de ingreso

## Interactividad

- Click en btnAbrirModal: Abre modal de registro
- Click en close: Cierra modal
- Click fuera del modal: Cierra modal
- Submit del formulario: Envia datos al servidor via POST /api/animales
- Click en "Ver Detalle" en tarjeta: Abre modal de detalle con informacion completa
- Click en "Eliminar" (solo para admin): Elimina el animal
- Click en "Cambiar Foto" (solo para admin): Permite subir nueva foto

## Estilos CSS
Todos los selectores CSS utilizados en index.html estan definidos en styles.css
Los selectores principales son:
- .modal, .modal-content, .modal-content-detalle
- .form-group, .form-row
- .animal-grid, .animal-card
- .detalle-*, .btn-*, etc.

## Scripts
- auth.js: Gestiona autenticacion y muestra acciones de usuario
- app.js: Carga animales, maneja eventos del formulario y modal detalle
