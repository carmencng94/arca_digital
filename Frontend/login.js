document.addEventListener('DOMContentLoaded', () => {
    const loginForm = document.getElementById('login-form');
    const loginError = document.getElementById('login-error');

    // Usuarios autorizados (en un entorno real, esto vendría de un backend seguro)
    const allowedUsers = {
        'admin': 'admin123',
        'voluntario': 'voluntario123'
    };

    loginForm.addEventListener('submit', (event) => {
        event.preventDefault();
        const username = loginForm.username.value;
        const password = loginForm.password.value;

        if (allowedUsers[username] && allowedUsers[username] === password) {
            // Autenticación exitosa
            sessionStorage.setItem('user', JSON.stringify({ username: username, role: username }));
            window.location.href = 'index.html'; // Redirigir a la página principal
        } else {
            // Error de autenticación
            loginError.textContent = 'Usuario o contraseña incorrectos.';
            loginForm.password.value = ''; // Limpiar campo de contraseña
        }
    });
});
