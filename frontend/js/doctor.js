/**
 * Doctor Dashboard Management
 * Hospital Appointment Booking System
 */

let currentDoctor = null;

document.addEventListener('DOMContentLoaded', async () => {
    const user = requireAuth(['DOCTOR']);
    if (!user) return;

    initSidebarUser('menu-dashboard');

    try {
        await loadDoctorProfile(user);
        if (currentDoctor) {
            loadDoctorDashboardStats();
            loadDoctorAppointments();
        }
    } catch (e) {
        console.error(e);
    }
});

async function loadDoctorProfile(user) {
    let docId = user.doctorId;
    if (!docId) {
        const res = await apiRequest(`/doctors/user/${user.userId}`);
        if (res.success && res.data) {
            currentDoctor = res.data;
            user.doctorId = res.data.id;
            setCurrentUser(user);
        }
    } else {
        const res = await apiRequest(`/doctors/${docId}`);
        if (res.success) {
            currentDoctor = res.data;
        }
    }
}

async function loadDoctorDashboardStats() {
    if (!currentDoctor) return;

    try {
        const res = await apiRequest(`/doctors/${currentDoctor.id}/dashboard-stats`);
        if (res.success && res.data) {
            const data = res.data;
            const elToday = document.getElementById('stat-today');
            const elUpcoming = document.getElementById('stat-upcoming');
            const elPatients = document.getElementById('stat-patients');
            const elPending = document.getElementById('stat-pending');

            if (elToday) elToday.textContent = data.todayAppointments;
            if (elUpcoming) elUpcoming.textContent = data.upcomingAppointments;
            if (elPatients) elPatients.textContent = data.totalPatients;
            if (elPending) elPending.textContent = data.pendingAppointments;
        }
    } catch (e) {
        console.error('Failed to load doctor stats:', e);
    }
}

async function loadDoctorAppointments() {
    if (!currentDoctor) return;

    const tbody = document.getElementById('doctor-appointments-table');
    if (!tbody) return;

    try {
        const res = await apiRequest(`/appointments/doctor/${currentDoctor.id}`);
        if (res.success && res.data) {
            if (res.data.length > 0) {
                tbody.innerHTML = res.data.map(app => renderDoctorAppointmentRow(app)).join('');
            } else {
                tbody.innerHTML = `
                    <tr>
                        <td colspan="7" style="text-align:center;padding:32px;color:#64748b;">
                            No appointments currently scheduled for your profile.
                        </td>
                    </tr>
                `;
            }
        }
    } catch (e) {
        showToast('Failed to load appointments', 'error');
    }
}

function renderDoctorAppointmentRow(app) {
    const patientName = app.patient?.user?.name || 'Unknown Patient';
    const patientPhone = app.patient?.user?.phone || 'N/A';
    const reason = app.reason || 'General Consultation';

    let actionButtons = '';
    if (app.status === 'PENDING') {
        actionButtons = `
            <button class="btn btn-secondary btn-sm" onclick="updateStatus(${app.id}, 'CONFIRMED')">Accept</button>
            <button class="btn btn-danger btn-sm" onclick="updateStatus(${app.id}, 'CANCELLED')">Cancel</button>
        `;
    } else if (app.status === 'CONFIRMED') {
        actionButtons = `
            <button class="btn btn-primary btn-sm" onclick="updateStatus(${app.id}, 'COMPLETED')">Complete</button>
            <button class="btn btn-danger btn-sm" onclick="updateStatus(${app.id}, 'CANCELLED')">Cancel</button>
        `;
    } else {
        actionButtons = `<span style="color:#94a3b8;font-size:0.85rem;">No actions</span>`;
    }

    return `
        <tr>
            <td><strong>#APT-${app.id}</strong></td>
            <td>
                <div style="font-weight:600;">${patientName}</div>
                <small style="color:#64748b;">📞 ${patientPhone}</small>
            </td>
            <td>${formatDate(app.appointmentDate)}</td>
            <td>${formatTime(app.appointmentTime)}</td>
            <td style="max-width:220px;overflow:hidden;text-overflow:ellipsis;white-space:nowrap;" title="${reason}">
                ${reason}
            </td>
            <td>${getStatusBadge(app.status)}</td>
            <td>
                <div style="display:flex;gap:6px;align-items:center;">
                    ${actionButtons}
                    <button class="btn btn-outline btn-sm" onclick='viewPatientDetails(${JSON.stringify(app.patient)})'>Details</button>
                </div>
            </td>
        </tr>
    `;
}

async function updateStatus(appointmentId, newStatus) {
    try {
        const res = await apiRequest(`/appointments/${appointmentId}/status`, 'PUT', { status: newStatus });
        if (res.success) {
            showToast(`Appointment marked as ${newStatus}`, 'success');
            loadDoctorDashboardStats();
            loadDoctorAppointments();
        }
    } catch (e) {
        showToast(e.message || 'Status update failed', 'error');
    }
}

function viewPatientDetails(patient) {
    if (!patient) return;
    const modal = document.getElementById('patient-modal');
    if (!modal) return;

    document.getElementById('modal-patient-name').textContent = patient.user?.name || 'N/A';
    document.getElementById('modal-patient-email').textContent = patient.user?.email || 'N/A';
    document.getElementById('modal-patient-phone').textContent = patient.user?.phone || 'N/A';
    document.getElementById('modal-patient-dob').textContent = formatDate(patient.dateOfBirth);
    document.getElementById('modal-patient-gender').textContent = patient.gender || 'N/A';
    document.getElementById('modal-patient-address').textContent = patient.address || 'N/A';

    modal.classList.add('active');
}

function closePatientModal() {
    const modal = document.getElementById('patient-modal');
    if (modal) modal.classList.remove('active');
}
