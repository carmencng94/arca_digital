document.addEventListener('DOMContentLoaded', () => {
    // --- GESTIÓN DE AUTENTICACIÓN ---
    const user = JSON.parse(sessionStorage.getItem('user'));
    const isLoginPage = window.location.pathname.endsWith('login.html');

    if (user && isLoginPage) {
        // Si el usuario ya está logueado y está en la página de login,
        // lo redirigimos al dashboard.
        window.location.href = 'index.html';
        return;
    }

    if (!user && !isLoginPage) {
        // Si el usuario no está logueado y NO está en la página de login,
        // lo redirigimos al login.
        window.location.href = 'login.html';
        return;
    }

    // --- LÓGICA COMÚN PARA PÁGINAS AUTENTICADAS ---
    if (user && !isLoginPage) {
        const header = document.querySelector('header');
        if (header) {
            const userInfo = document.createElement('div');
            userInfo.className = 'user-info';
            userInfo.innerHTML = `
                <span>Bienvenido, <strong>${user.username}</strong></span>
                <button id="logout-btn" class="btn-nuevo">Salir</button>
            `;
            header.appendChild(userInfo);

            const logoutBtn = document.getElementById('logout-btn');
            if (logoutBtn) {
                logoutBtn.addEventListener('click', () => {
                    sessionStorage.removeItem('user');
                    window.location.href = 'login.html';
                });
            }
        }

        // Mostrar botones/controles de administrador
        const adminControls = document.querySelectorAll('.admin-only');
        adminControls.forEach(control => {
            control.style.display = 'block';
        });
    }
});
