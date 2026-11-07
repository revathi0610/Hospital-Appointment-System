/**
 * Patient Dashboard & Appointments Management
 * Hospital Appointment Booking System
 */

let currentPatient = null;

document.addEventListener('DOMContentLoaded', async () => {
    const user = requireAuth(['PATIENT']);
    if (!user) return;

    initSidebarUser('menu-dashboard');

    try {
        await loadPatientProfile(user);

        // Check if we are on patient-dashboard.html
        if (document.getElementById('patient-upcoming-table')) {
            loadPatientDashboard();
        }

        // Check if we are on appointments.html
        if (document.getElementById('patient-all-appointments')) {
            loadAllAppointments();
        }

        // Check if profile form exists
        const profileForm = document.getElementById('patient-profile-form');
        if (profileForm) {
            setupProfileForm();
        }
    } catch (e) {
        console.error(e);
    }
});

async function loadPatientProfile(user) {
    let patientId = user.patientId;
    if (!patientId) {
        const res = await apiRequest(`/patients/user/${user.userId}`);
        if (res.success && res.data) {
            currentPatient = res.data;
            user.patientId = res.data.id;
            setCurrentUser(user);
        }
    } else {
        const res = await apiRequest(`/patients/${patientId}`);
        if (res.success) {
            currentPatient = res.data;
        }
    }
}

async function loadPatientDashboard() {
    if (!currentPatient) return;

    try {
        const upcomingRes = await apiRequest(`/appointments/patient/${currentPatient.id}/upcoming`);
        const pastRes = await apiRequest(`/appointments/patient/${currentPatient.id}/past`);

        const upcomingCount = (upcomingRes.success && upcomingRes.data) ? upcomingRes.data.length : 0;
        const pastCount = (pastRes.success && pastRes.data) ? pastRes.data.length : 0;

        const upCountElem = document.getElementById('stat-upcoming');
        const pastCountElem = document.getElementById('stat-past');
        if (upCountElem) upCountElem.textContent = upcomingCount;
        if (pastCountElem) pastCountElem.textContent = pastCount;

        // Populate upcoming table
        const tbody = document.getElementById('patient-upcoming-table');
        if (tbody) {
            if (upcomingRes.data && upcomingRes.data.length > 0) {
                tbody.innerHTML = upcomingRes.data.slice(0, 5).map(app => renderAppointmentRow(app, true)).join('');
            } else {
                tbody.innerHTML = `
                    <tr>
                        <td colspan="6" style="text-align:center;padding:32px;color:#64748b;">
                            No upcoming appointments scheduled. <a href="book-appointment.html">Book an appointment now</a>.
                        </td>
                    </tr>
                `;
            }
        }
    } catch (e) {
        showToast('Error loading dashboard statistics', 'error');
    }
}

async function loadAllAppointments() {
    if (!currentPatient) return;

    const tbody = document.getElementById('patient-all-appointments');
    const statusFilter = document.getElementById('filter-status');

    try {
        const res = await apiRequest(`/appointments/patient/${currentPatient.id}`);
        if (res.success && res.data) {
            const renderFiltered = () => {
                const filter = statusFilter ? statusFilter.value : 'ALL';
                const filtered = res.data.filter(app => {
                    if (filter === 'ALL') return true;
                    return app.status === filter;
                });

                if (filtered.length > 0) {
                    tbody.innerHTML = filtered.map(app => renderAppointmentRow(app, true)).join('');
                } else {
                    tbody.innerHTML = `
                        <tr>
                            <td colspan="6" style="text-align:center;padding:32px;color:#64748b;">
                                No appointments found.
                            </td>
                        </tr>
                    `;
                }
            };

            renderFiltered();
            if (statusFilter) statusFilter.addEventListener('change', renderFiltered);
        }
    } catch (e) {
        showToast('Failed to load appointments', 'error');
    }
}

function renderAppointmentRow(app, showDoctor = true) {
    const doctorName = app.doctor?.user?.name || 'Dr. Assigned';
    const specialty = app.doctor?.specialization || 'General';
    const canCancel = (app.status === 'PENDING' || app.status === 'CONFIRMED');

    return `
        <tr>
            <td><strong>#APT-${app.id}</strong></td>
            <td>
                <strong>${doctorName}</strong>
                <div style="font-size:0.8rem;color:#0f766e;">${specialty}</div>
            </td>
            <td>${formatDate(app.appointmentDate)}</td>
            <td>${formatTime(app.appointmentTime)}</td>
            <td>${getStatusBadge(app.status)}</td>
            <td>
                <div style="display:flex;gap:6px;align-items:center;flex-wrap:wrap;">
                    <button class="btn btn-outline btn-sm" style="padding:4px 8px;font-size:0.75rem;" onclick="openDigitalPassModal(${app.id})" title="View Digital Pass & Live Queue">
                        🎟️ QR Pass
                    </button>
                    ${canCancel ? `
                        <button class="btn btn-sm" style="background:#fef3c7;color:#92400e;border:1px solid #fde68a;padding:4px 8px;font-size:0.75rem;" onclick="releaseAppointmentToMarketplace(${app.id})" title="Release to marketplace so others can claim">
                            🔄 Release
                        </button>
                        <button class="btn btn-danger btn-sm" style="padding:4px 8px;font-size:0.75rem;" onclick="cancelAppointment(${app.id})">
                            Cancel
                        </button>
                    ` : ''}
                </div>
            </td>
        </tr>
    `;
}

async function cancelAppointment(appointmentId) {
    if (!confirm('Are you sure you want to cancel this appointment?')) {
        return;
    }

    try {
        const res = await apiRequest(`/appointments/${appointmentId}/cancel`, 'PUT');
        if (res.success) {
            showToast('Appointment cancelled successfully', 'success');
            if (document.getElementById('patient-upcoming-table')) {
                loadPatientDashboard();
            }
            if (document.getElementById('patient-all-appointments')) {
                loadAllAppointments();
            }
        }
    } catch (e) {
        showToast(e.message || 'Failed to cancel appointment', 'error');
    }
}

function setupProfileForm() {
    if (!currentPatient) return;

    document.getElementById('profile-name').value = currentPatient.name || '';
    document.getElementById('profile-email').value = currentPatient.email || '';
    document.getElementById('profile-phone').value = currentPatient.phone || '';
    document.getElementById('profile-dob').value = currentPatient.dateOfBirth || '';
    document.getElementById('profile-gender').value = currentPatient.gender || 'MALE';
    document.getElementById('profile-address').value = currentPatient.address || '';

    const form = document.getElementById('patient-profile-form');
    form.addEventListener('submit', async (e) => {
        e.preventDefault();

        const payload = {
            name: document.getElementById('profile-name').value.trim(),
            phone: document.getElementById('profile-phone').value.trim(),
            dateOfBirth: document.getElementById('profile-dob').value,
            gender: document.getElementById('profile-gender').value,
            address: document.getElementById('profile-address').value.trim()
        };

        try {
            const res = await apiRequest(`/patients/${currentPatient.id}`, 'PUT', payload);
            if (res.success) {
                showToast('Profile updated successfully!', 'success');
                const user = getCurrentUser();
                user.name = payload.name;
                setCurrentUser(user);
                initSidebarUser();
            }
        } catch (err) {
            showToast(err.message || 'Failed to update profile', 'error');
        }
    });
}
