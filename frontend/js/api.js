/**
 * API Utility Client & Helper Functions
 * Hospital Appointment Booking System
 */

const API_BASE = 'http://localhost:8080/api';

/**
 * Perform an HTTP fetch request against the backend REST API
 */
async function apiRequest(endpoint, method = 'GET', body = null) {
    const url = `${API_BASE}${endpoint}`;
    const headers = {
        'Content-Type': 'application/json',
        'Accept': 'application/json'
    };

    const token = localStorage.getItem('token');
    if (token) {
        headers['Authorization'] = `Bearer ${token}`;
    }

    const config = {
        method,
        headers
    };

    if (body) {
        config.body = JSON.stringify(body);
    }

    try {
        const response = await fetch(url, config);
        const data = await response.json();

        if (!response.ok) {
            const errorMsg = data.message || `Request failed with status ${response.status}`;
            throw new Error(errorMsg);
        }

        return data;
    } catch (error) {
        console.error(`API Error on [${method} ${endpoint}]:`, error);
        throw error;
    }
}

/**
 * Toast Notification Helper
 */
function showToast(message, type = 'info') {
    let container = document.getElementById('toast-container');
    if (!container) {
        container = document.createElement('div');
        container.id = 'toast-container';
        container.className = 'toast-container';
        document.body.appendChild(container);
    }

    const toast = document.createElement('div');
    toast.className = `toast toast-${type}`;
    toast.innerHTML = `
        <span>${message}</span>
        <button style="background:none;border:none;cursor:pointer;font-size:1.1rem;color:#64748b;" onclick="this.parentElement.remove()">×</button>
    `;

    container.appendChild(toast);

    setTimeout(() => {
        if (toast.parentElement) {
            toast.remove();
        }
    }, 4000);
}

/**
 * Format Date into user-friendly format: e.g. "Oct 12, 2026"
 */
function formatDate(dateStr) {
    if (!dateStr) return 'N/A';
    const parts = dateStr.split('-');
    if (parts.length === 3) {
        const date = new Date(parts[0], parts[1] - 1, parts[2]);
        return date.toLocaleDateString('en-US', { year: 'numeric', month: 'short', day: 'numeric' });
    }
    return dateStr;
}

/**
 * Format Time: e.g. "14:30:00" -> "02:30 PM"
 */
function formatTime(timeStr) {
    if (!timeStr) return '';
    const parts = timeStr.split(':');
    let hours = parseInt(parts[0], 10);
    const minutes = parts[1] || '00';
    const ampm = hours >= 12 ? 'PM' : 'AM';
    hours = hours % 12;
    hours = hours ? hours : 12;
    const strHours = hours < 10 ? '0' + hours : hours;
    return `${strHours}:${minutes} ${ampm}`;
}

/**
 * Render Status Badge HTML
 */
function getStatusBadge(status) {
    const s = (status || '').toUpperCase();
    switch (s) {
        case 'CONFIRMED':
            return `<span class="badge badge-confirmed">Confirmed</span>`;
        case 'COMPLETED':
            return `<span class="badge badge-completed">Completed</span>`;
        case 'CANCELLED':
            return `<span class="badge badge-cancelled">Cancelled</span>`;
        case 'RELEASED':
            return `<span class="badge badge-pending" style="background:#fef3c7;color:#92400e;">Released</span>`;
        case 'PENDING':
        default:
            return `<span class="badge badge-pending">Pending</span>`;
    }
}
