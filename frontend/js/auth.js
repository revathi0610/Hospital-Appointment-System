/**
 * Authentication and Session Management
 * Hospital Appointment Booking System
 */

function getCurrentUser() {
    const userStr = localStorage.getItem('hospital_user');
    if (!userStr) return null;
    try {
        return JSON.parse(userStr);
    } catch (e) {
        return null;
    }
}

function setCurrentUser(user) {
    localStorage.setItem('hospital_user', JSON.stringify(user));
    if (user.token) {
        localStorage.setItem('token', user.token);
    }
}

function clearUser() {
    localStorage.removeItem('hospital_user');
    localStorage.removeItem('token');
}

function logout() {
    clearUser();
    window.location.href = 'login.html';
}

/**
 * Route Guard: Redirects unauthorized users
 */
function requireAuth(allowedRoles = []) {
    const user = getCurrentUser();
    if (!user) {
        window.location.href = 'login.html';
        return null;
    }

    if (allowedRoles.length > 0 && !allowedRoles.includes(user.role)) {
        // Redirect to appropriate dashboard based on user role
        if (user.role === 'PATIENT') window.location.href = 'patient-dashboard.html';
        else if (user.role === 'DOCTOR') window.location.href = 'doctor-dashboard.html';
        else if (user.role === 'ADMIN') window.location.href = 'admin-dashboard.html';
        else window.location.href = 'login.html';
        return null;
    }

    return user;
}

/**
 * Initializes Public Navigation Header based on session
 */
function initPublicNav() {
    const user = getCurrentUser();
    const navActions = document.getElementById('nav-actions');
    if (!navActions) return;

    if (user) {
        let dashboardUrl = 'patient-dashboard.html';
        if (user.role === 'DOCTOR') dashboardUrl = 'doctor-dashboard.html';
        if (user.role === 'ADMIN') dashboardUrl = 'admin-dashboard.html';

        navActions.innerHTML = `
            <a href="${dashboardUrl}" class="btn btn-outline btn-sm">My Dashboard</a>
            <button onclick="logout()" class="btn btn-danger btn-sm">Logout</button>
        `;
    } else {
        navActions.innerHTML = `
            <a href="login.html" class="btn btn-outline btn-sm">Login</a>
            <a href="register.html" class="btn btn-primary btn-sm">Register</a>
        `;
    }
}

/**
 * Initializes Dashboard Sidebar user profile and logout handler
 */
function initSidebarUser(activeMenuId) {
    const user = getCurrentUser();
    if (!user) return;

    const nameElem = document.getElementById('sidebar-user-name');
    const roleElem = document.getElementById('sidebar-user-role');
    const avatarElem = document.getElementById('sidebar-user-avatar');

    if (nameElem) nameElem.textContent = user.name;
    if (roleElem) roleElem.textContent = user.role;
    if (avatarElem) {
        const initials = user.name.split(' ').map(n => n[0]).join('').substring(0, 2).toUpperCase();
        avatarElem.textContent = initials || 'U';
    }

    if (activeMenuId) {
        const activeLink = document.getElementById(activeMenuId);
        if (activeLink) activeLink.classList.add('active');
    }
}
