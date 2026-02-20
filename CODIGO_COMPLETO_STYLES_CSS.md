# CODIGO COMPLETO: styles.css

Este archivo contiene todos los estilos CSS para el dashboard de Arca Digital.

## Ubicación
`resources/frontend/styles.css`

## Contenido Completo - Primera Seccion

Ver archivo: [styles.css](resources/frontend/styles.css)

## Estructura del CSS

### 1. Variables Globales (CSS Custom Properties)
```css
:root {
    --bg-color: #1a1a1a;              /* Fondo oscuro principal */
    --card-bg-color: #2c2c2c;         /* Fondo de tarjetas */
    --text-color: #f0f0f0;            /* Texto principal */
    --accent-color: #00a8ff;          /* Color acentos (azul) */
    --shadow-color: rgba(0, 0, 0, 0.4); /* Sombras */
    --success-color: #2ecc71;         /* Verde éxito */
    --urgent-color: #e74c3c;          /* Rojo urgencia */
    --warning-color: #f39c12;         /* Amarillo advertencia */
}
```

### 2. Reset y Configuracion Base
- Eliminación de márgenes y padding por defecto
- Box-sizing: border-box para todo
- Scroll suave
- Fuente: Inter (Google Fonts)
- Colores del tema oscuro

### 3. Componentes Principales

#### Header
- Gradiente azul-verde de fondo
- Padding: 2rem 1rem
- Titulo con text-shadow
- Flex layout con user-actions

#### Main
- Min-height: calc(100vh - 300px)
- Padding: 2rem 1rem

#### Footer
- Centr ado
- Borde superior
- Color: #888

### 4. Grid de Animales

```css
.animal-grid {
    display: grid;
    grid-template-columns: repeat(auto-fill, minmax(320px, 1fr));
    gap: 2rem;
    max-width: 1400px;
    margin: 0 auto;
}
```

- Responsive: minimo 320px por tarjeta
- Auto-fill: llena el espacio disponible
- Máximo ancho: 1400px
- Centro: margin 0 auto

### 5. Tarjeta de Animal

#### .animal-card
- Background: var(--card-bg-color)
- Border-radius: 16px
- Box-shadow: 0 8px 20px
- Transition: 0.3s cubic-bezier
- Hover: translateY(-8px) + shadow azul

#### .animal-card__image
- width: 100%, height: 240px
- object-fit: cover
- Scale en hover: 1.05

#### .animal-card__overlay
- Gradiente negro bottom
- Opacity: 0 en reposo, 0.4 en hover

#### .animal-card__urgente
- Badge rojo pulsante
- Animation: pulse 2s infinite

#### Botones
- .btn-ver-detalle: Azul, hover con translateX
- .btn-delete: Rojo, hover con scale
- .btn-change-photo: Azul, hover con scale

### 6. Modales

#### Modal Principal (.modal)
- Position: fixed, z-index: 1000
- Background: rgba(0,0,0,0.7)
- Backdrop-filter: blur(5px)

#### Modal Content
- Width: 90%, max-width: 500px
- Margin: 5% auto (centrado vertical)
- Animation: slideDown 0.3s
- Border-radius: 15px

#### Modal Detalle
- Similar al principal
- Max-width: 600px
- Z-index: 1001 (encima del principal)

### 7. Animaciones

```css
@keyframes slideDown {
    from { transform: translateY(-60px); opacity: 0; }
    to { transform: translateY(0); opacity: 1; }
}

@keyframes slideUp {
    from { transform: translateY(30px); opacity: 0; }
    to { transform: translateY(0); opacity: 1; }
}

@keyframes pulse {
    0%, 100% { opacity: 1; }
    50% { opacity: 0.7; }
}

@keyframes fadeIn {
    from { opacity: 0; }
    to { opacity: 1; }
}
```

### 8. Formulario

#### Inputs
```css
input, select, textarea {
    width: 100%;
    padding: 10px 12px;
    border-radius: 8px;
    border: 1px solid #555;
    background-color: #3a3a3a;
    color: white;
}

input:focus, select:focus, textarea:focus {
    border-color: var(--accent-color);
    background-color: #424242;
    box-shadow: 0 0 10px rgba(0, 168, 255, 0.3);
}
```

#### Form Layout
- .form-group: margin-bottom 1.2rem
- .form-row: display flex, gap 1rem
- Responsive: flex-direction column en móvil

#### Botones Submit
- Background: green (#2ecc71)
- Hover: darker green, translateY(-2px)
- Width: 100%

### 9. Detalle Modal

#### Estructura
```
detalle-header (flex, gap 1.5rem)
  ├─ detalle-imagen (150x150px)
  └─ detalle-titulo
      ├─ h1
      └─ .detalle-especie

detalle-body
  ├─ detalle-grid (grid auto-fit minmax(200px, 1fr))
  │   └─ detalle-item x8
  ├─ detalle-descripcion
  └─ detalle-fecha
```

#### Estilos
- detalle-item: padding 1rem, border-left azul
- detalle-descripcion: background rgba, padding
- detalle-fecha: text-align center
- Grid: 2 columnas desktop, 1 móvil

### 10. Autenticacion

#### User Badge
- Display: flex, gap 15px
- Background: rgba azul
- Border: 1px rgba azul

#### Botones
- .btn-login: border azul, hover fill
- .btn-logout: fondo rojo, scale en hover

### 11. Responsive

#### Tablet (768px)
- Grid: minmax(280px, 1fr)
- Header: flex-direction column
- Detalle: flex-direction column

#### Móvil (600px)
- Grid: 1 columna
- Main: padding 1rem 0.5rem
- Inputs: font-size 16px (evita zoom)
- Modal: width 95%, margin 20% auto

## Tema de Colores

### Paleta Principal
- Azul (Accent): #00a8ff
- Oscuro (BG): #1a1a1a
- Gris Oscuro (Cards): #2c2c2c
- Texto: #f0f0f0

### Estados
- Éxito: #2ecc71 (Verde)
- Error/Urgente: #e74c3c (Rojo)
- Advertencia: #f39c12 (Amarillo)

## Performance

- Transiciones: 0.3s cubic-bezier para suavidad
- Hover effects: transform + box-shadow
- Box-shadow: single shadow para mobile
- Backdrop-filter: solo en modales
- Grid auto-fill: mejor performance que media queries

## Características Especiales

1. **Dark Mode**: Tema oscuro completo
2. **Responsive**: Mobile-first con breakpoints claros
3. **Animaciones**: Smooth transitions sin lag
4. **Accesibilidad**: Contrastes adecuados
5. **Components**: Basado en BEM (Block__Element--Modifier)
6. **Variables CSS**: Fácil actualización de temas

## Notas Tecnicas

- Uso de CSS Grid para layouts profundos
- Flexbox para componentes simples
- Cubic-bezier personalizado para animaciones
- Backdrop-filter para efecto glass-morphism
- CSS variables para mantenibilidad
- Media queries mobile-first
- Usar transform en lugar de top/left para mejor performance
