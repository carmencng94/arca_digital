document.addEventListener('DOMContentLoaded', () => {
    // --- GESTIÓN DE AUTENTICACIÓN ---
    const user = JSON.parse(sessionStorage.getItem('user'));
    const isLoginPage = window.location.pathname.endsWith('login.html');

    // Si el usuario ya está logueado y está en la página de login, lo mandamos al dashboard.
    if (user && isLoginPage) {
        window.location.href = 'index.html';
        return;
    }

    // --- LÓGICA DE INTERFAZ PÚBLICA VS PRIVADA ---
    const userActionsDiv = document.getElementById('user-actions');
    const btnAbrirModal = document.getElementById('btnAbrirModal');

    if (user) {
        // MODO ADMINISTRADOR/VOLUNTARIO: Mostrar saludo y botón de salida
        if (userActionsDiv) {
            userActionsDiv.innerHTML = `
                <div class="user-badge">
                    <span>${user.username}</span>
                    <button id="btnLogout" class="btn-logout">Cerrar Sesión</button>
                </div>`;
        }
        
        // Mostrar botón de registro (clase admin-only)
        if (btnAbrirModal) btnAbrirModal.style.display = 'block';

        // Listener para el botón de logout generado dinámicamente
        const btnLogout = document.getElementById('btnLogout');
        if (btnLogout) {
            btnLogout.addEventListener('click', () => {
                sessionStorage.removeItem('user');
                window.location.reload(); // Recargar para volver al modo público
            });
        }
    } else {
        // MODO PÚBLICO: Mostrar botón de acceso si no se ha iniciado sesión
        if (userActionsDiv && !isLoginPage) {
            userActionsDiv.innerHTML = `<a href="login.html" class="btn-login">Iniciar Sesión</a>`;
        }
        
        // Asegurar que el botón de registro esté oculto para invitados
        if (btnAbrirModal) btnAbrirModal.style.display = 'none';
    }
});