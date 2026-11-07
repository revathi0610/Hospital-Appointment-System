/**
 * Admin Dashboard & Management Script
 * Hospital Appointment Booking System
 */

document.addEventListener('DOMContentLoaded', async () => {
    const user = requireAuth(['ADMIN']);
    if (!user) return;

    initSidebarUser('menu-dashboard');

    if (document.getElementById('stat-total-appointments')) {
        loadAdminDashboard();
    }

    if (document.getElementById('admin-doctors-table')) {
        loadDoctorsTable();
    }

    if (document.getElementById('admin-patients-table')) {
        loadPatientsTable();
    }

    if (document.getElementById('admin-appointments-table')) {
        loadAppointmentsTable();
    }

    const addDoctorForm = document.getElementById('add-doctor-form');
    if (addDoctorForm) {
        addDoctorForm.addEventListener('submit', handleAddDoctor);
    }
});

async function loadAdminDashboard() {
    try {
        const res = await apiRequest('/admin/dashboard');
        if (res.success && res.data) {
            const data = res.data;

            const elTotalApp = document.getElementById('stat-total-appointments');
            const elTodayApp = document.getElementById('stat-today-appointments');
            const elTotalPat = document.getElementById('stat-total-patients');
            const elAvailDoc = document.getElementById('stat-available-doctors');

            if (elTotalApp) elTotalApp.textContent = data.totalAppointments;
            if (elTodayApp) elTodayApp.textContent = data.todayAppointments;
            if (elTotalPat) elTotalPat.textContent = data.totalPatients;
            if (elAvailDoc) elAvailDoc.textContent = data.doctorsAvailable;

            // Render upcoming appointments
            const upcomingTbody = document.getElementById('admin-upcoming-table');
            if (upcomingTbody && data.upcomingAppointments) {
                if (data.upcomingAppointments.length > 0) {
                    upcomingTbody.innerHTML = data.upcomingAppointments.map(app => `
                        <tr>
                            <td><strong>#APT-${app.id}</strong></td>
                            <td>${app.patient?.user?.name || 'N/A'}</td>
                            <td>${app.doctor?.user?.name || 'N/A'}</td>
                            <td>${formatDate(app.appointmentDate)}</td>
                            <td>${formatTime(app.appointmentTime)}</td>
                            <td>${getStatusBadge(app.status)}</td>
                        </tr>
                    `).join('');
                } else {
                    upcomingTbody.innerHTML = '<tr><td colspan="6" style="text-align:center;color:#64748b;padding:24px;">No upcoming appointments.</td></tr>';
                }
            }

            // Render recent patients
            const patientTbody = document.getElementById('admin-recent-patients-table');
            if (patientTbody && data.recentPatients) {
                if (data.recentPatients.length > 0) {
                    patientTbody.innerHTML = data.recentPatients.map(p => `
                        <tr>
                            <td><strong>${p.name}</strong></td>
                            <td>${p.email}</td>
                            <td>${p.phone || 'N/A'}</td>
                            <td>${p.gender || 'N/A'}</td>
                            <td>${formatDate(p.createdAt ? p.createdAt.split('T')[0] : '')}</td>
                        </tr>
                    `).join('');
                } else {
                    patientTbody.innerHTML = '<tr><td colspan="5" style="text-align:center;color:#64748b;padding:24px;">No patients registered yet.</td></tr>';
                }
            }
        }
    } catch (e) {
        showToast('Failed to load admin stats', 'error');
    }
}

async function loadDoctorsTable() {
    const tbody = document.getElementById('admin-doctors-table');
    if (!tbody) return;

    try {
        const res = await apiRequest('/admin/doctors');
        if (res.success && res.data) {
            tbody.innerHTML = res.data.map(doc => `
                <tr>
                    <td><strong>#DOC-${doc.id}</strong></td>
                    <td>
                        <div style="font-weight:600;">${doc.name}</div>
                        <small style="color:#64748b;">${doc.email}</small>
                    </td>
                    <td><span class="badge" style="background:#e0f2fe;color:#0369a1;">${doc.specialization}</span></td>
                    <td>${doc.qualification}</td>
                    <td>${doc.experience} Years</td>
                    <td>$${Number(doc.consultationFee).toFixed(2)}</td>
                    <td>
                        <span class="badge ${doc.availabilityStatus === 'AVAILABLE' ? 'badge-available' : 'badge-unavailable'}">
                            ${doc.availabilityStatus}
                        </span>
                    </td>
                    <td>
                        <div style="display:flex;gap:6px;">
                            <button class="btn btn-outline btn-sm" onclick="toggleDoctorStatus(${doc.id}, '${doc.availabilityStatus}')">
                                ${doc.availabilityStatus === 'AVAILABLE' ? 'Set Offline' : 'Set Active'}
                            </button>
                            <button class="btn btn-danger btn-sm" onclick="deleteDoctor(${doc.id})">Delete</button>
                        </div>
                    </td>
                </tr>
            `).join('');
        }
    } catch (e) {
        showToast('Failed to load doctors', 'error');
    }
}

async function loadPatientsTable() {
    const tbody = document.getElementById('admin-patients-table');
    if (!tbody) return;

    try {
        const res = await apiRequest('/admin/patients');
        if (res.success && res.data) {
            tbody.innerHTML = res.data.map(p => `
                <tr>
                    <td><strong>#PAT-${p.id}</strong></td>
                    <td><strong>${p.name}</strong></td>
                    <td>${p.email}</td>
                    <td>${p.phone || 'N/A'}</td>
                    <td>${p.gender || 'N/A'}</td>
                    <td>${formatDate(p.dateOfBirth)}</td>
                    <td>${p.address || 'N/A'}</td>
                </tr>
            `).join('');
        }
    } catch (e) {
        showToast('Failed to load patients', 'error');
    }
}

async function loadAppointmentsTable() {
    const tbody = document.getElementById('admin-appointments-table');
    if (!tbody) return;

    try {
        const res = await apiRequest('/admin/appointments');
        if (res.success && res.data) {
            tbody.innerHTML = res.data.map(app => `
                <tr>
                    <td><strong>#APT-${app.id}</strong></td>
                    <td>${app.patient?.user?.name || 'N/A'}</td>
                    <td>${app.doctor?.user?.name || 'N/A'}</td>
                    <td>${formatDate(app.appointmentDate)}</td>
                    <td>${formatTime(app.appointmentTime)}</td>
                    <td>${getStatusBadge(app.status)}</td>
                    <td>
                        <select onchange="updateAppointmentStatusAdmin(${app.id}, this.value)" class="form-control" style="padding:4px 8px;font-size:0.85rem;width:130px;">
                            <option value="">Update Status</option>
                            <option value="PENDING" ${app.status === 'PENDING' ? 'selected' : ''}>Pending</option>
                            <option value="CONFIRMED" ${app.status === 'CONFIRMED' ? 'selected' : ''}>Confirmed</option>
                            <option value="COMPLETED" ${app.status === 'COMPLETED' ? 'selected' : ''}>Completed</option>
                            <option value="CANCELLED" ${app.status === 'CANCELLED' ? 'selected' : ''}>Cancelled</option>
                        </select>
                    </td>
                </tr>
            `).join('');
        }
    } catch (e) {
        showToast('Failed to load appointments', 'error');
    }
}

async function handleAddDoctor(e) {
    e.preventDefault();

    const payload = {
        name: document.getElementById('doc-name').value.trim(),
        email: document.getElementById('doc-email').value.trim(),
        password: document.getElementById('doc-password').value || 'doctor123',
        phone: document.getElementById('doc-phone').value.trim(),
        specialization: document.getElementById('doc-specialization').value.trim(),
        qualification: document.getElementById('doc-qualification').value.trim(),
        experience: parseInt(document.getElementById('doc-experience').value, 10) || 0,
        consultationFee: parseFloat(document.getElementById('doc-fee').value) || 50.0,
        availabilityStatus: document.getElementById('doc-status').value
    };

    try {
        const res = await apiRequest('/admin/doctors', 'POST', payload);
        if (res.success) {
            showToast('Doctor added successfully!', 'success');
            closeAddDoctorModal();
            loadAdminDashboard();
            if (document.getElementById('admin-doctors-table')) {
                loadDoctorsTable();
            }
        }
    } catch (err) {
        showToast(err.message || 'Failed to add doctor', 'error');
    }
}

async function toggleDoctorStatus(doctorId, currentStatus) {
    const newStatus = currentStatus === 'AVAILABLE' ? 'UNAVAILABLE' : 'AVAILABLE';
    try {
        const res = await apiRequest(`/admin/doctors/${doctorId}`, 'PUT', { availabilityStatus: newStatus });
        if (res.success) {
            showToast(`Doctor marked as ${newStatus}`, 'success');
            loadDoctorsTable();
            loadAdminDashboard();
        }
    } catch (e) {
        showToast('Failed to update doctor status', 'error');
    }
}

async function deleteDoctor(doctorId) {
    if (!confirm('Are you sure you want to delete this doctor? This cannot be undone.')) {
        return;
    }
    try {
        const res = await apiRequest(`/admin/doctors/${doctorId}`, 'DELETE');
        if (res.success) {
            showToast('Doctor deleted successfully', 'success');
            loadDoctorsTable();
            loadAdminDashboard();
        }
    } catch (e) {
        showToast('Failed to delete doctor', 'error');
    }
}

async function updateAppointmentStatusAdmin(appointmentId, newStatus) {
    if (!newStatus) return;
    try {
        const res = await apiRequest(`/appointments/${appointmentId}/status`, 'PUT', { status: newStatus });
        if (res.success) {
            showToast(`Appointment status updated to ${newStatus}`, 'success');
            loadAppointmentsTable();
            loadAdminDashboard();
        }
    } catch (e) {
        showToast('Failed to update status', 'error');
    }
}

function openAddDoctorModal() {
    const modal = document.getElementById('add-doctor-modal');
    if (modal) modal.classList.add('active');
}

function closeAddDoctorModal() {
    const modal = document.getElementById('add-doctor-modal');
    if (modal) modal.classList.remove('active');
}
