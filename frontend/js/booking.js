/**
 * Interactive Appointment Booking Flow
 * Hospital Appointment Booking System
 */

let selectedTime = null;
let currentPatientId = null;

document.addEventListener('DOMContentLoaded', async () => {
    // Check if user is logged in
    const user = getCurrentUser();
    if (!user) {
        showToast('Please login to book an appointment', 'info');
        setTimeout(() => {
            window.location.href = 'login.html';
        }, 1500);
        return;
    }

    if (user.role !== 'PATIENT') {
        showToast('Only registered patients can book appointments', 'error');
        return;
    }

    currentPatientId = user.patientId;
    if (!currentPatientId) {
        try {
            const patientRes = await apiRequest(`/patients/user/${user.userId}`);
            if (patientRes.success && patientRes.data) {
                currentPatientId = patientRes.data.id;
                user.patientId = currentPatientId;
                setCurrentUser(user);
            }
        } catch (e) {
            console.error('Error fetching patient profile:', e);
        }
    }

    // Set today as minimum selectable date
    const dateInput = document.getElementById('appointment-date');
    if (dateInput) {
        const todayStr = new Date().toISOString().split('T')[0];
        dateInput.min = todayStr;
        dateInput.value = todayStr;
    }

    await loadDoctorsDropdown();

    // Check URL params for pre-selected doctor
    const urlParams = new URLSearchParams(window.location.search);
    const doctorIdParam = urlParams.get('doctorId');
    const doctorSelect = document.getElementById('doctor-select');
    if (doctorIdParam && doctorSelect) {
        doctorSelect.value = doctorIdParam;
        onDoctorOrDateChange();
    }

    // Event listeners
    if (doctorSelect) doctorSelect.addEventListener('change', onDoctorOrDateChange);
    if (dateInput) dateInput.addEventListener('change', onDoctorOrDateChange);

    const bookingForm = document.getElementById('booking-form');
    if (bookingForm) {
        bookingForm.addEventListener('submit', handleBookingSubmit);
    }
});

async function loadDoctorsDropdown() {
    const doctorSelect = document.getElementById('doctor-select');
    if (!doctorSelect) return;

    try {
        const res = await apiRequest('/doctors');
        if (res.success && res.data) {
            doctorSelect.innerHTML = '<option value="">-- Choose a Doctor --</option>';
            res.data.forEach(doc => {
                if (doc.availabilityStatus === 'AVAILABLE') {
                    const opt = document.createElement('option');
                    opt.value = doc.id;
                    opt.textContent = `${doc.name} (${doc.specialization}) - $${Number(doc.consultationFee).toFixed(2)}`;
                    opt.dataset.fee = doc.consultationFee;
                    opt.dataset.specialty = doc.specialization;
                    doctorSelect.appendChild(opt);
                }
            });
        }
    } catch (e) {
        showToast('Failed to load doctors list', 'error');
    }
}

async function onDoctorOrDateChange() {
    const doctorSelect = document.getElementById('doctor-select');
    const dateInput = document.getElementById('appointment-date');
    const slotsContainer = document.getElementById('slots-container');
    const summaryCard = document.getElementById('doctor-summary');

    selectedTime = null;
    const submitBtn = document.getElementById('submit-booking-btn');
    if (submitBtn) submitBtn.disabled = true;

    if (!doctorSelect || !dateInput || !slotsContainer) return;

    const doctorId = doctorSelect.value;
    const selectedDate = dateInput.value;

    if (!doctorId || !selectedDate) {
        slotsContainer.innerHTML = '<p style="color:#64748b;font-size:0.9rem;">Select a doctor and date to view available time slots.</p>';
        if (summaryCard) summaryCard.style.display = 'none';
        return;
    }

    // Fetch doctor info for summary preview
    try {
        const docRes = await apiRequest(`/doctors/${doctorId}`);
        if (docRes.success && summaryCard) {
            const doc = docRes.data;
            document.getElementById('summary-doc-name').textContent = doc.name;
            document.getElementById('summary-doc-spec').textContent = doc.specialization;
            document.getElementById('summary-doc-fee').textContent = `$${Number(doc.consultationFee).toFixed(2)}`;
            summaryCard.style.display = 'block';
        }
    } catch (e) {
        console.error(e);
    }

    // Fetch smart slots from backend
    slotsContainer.innerHTML = '<p style="color:#0284c7;font-size:0.9rem;">Calculating smart slots & estimated waiting times...</p>';

    try {
        const res = await apiRequest(`/doctors/${doctorId}/smart-slots?date=${selectedDate}`);
        if (res.success && res.data && res.data.length > 0) {
            slotsContainer.innerHTML = `
                <div class="slot-grid">
                    ${res.data.map(slot => `
                        <button type="button" 
                                class="slot-btn smart-slot-btn" 
                                data-time="${slot.time}"
                                ${!slot.available ? 'disabled title="' + slot.reason + '"' : ''}
                                onclick="selectSlot(this, '${slot.time}')">
                            <span style="font-weight:700;">${formatTime(slot.time)}</span>
                            ${slot.available ? `
                                <span class="smart-slot-tag ${slot.badgeClass || 'info'}">
                                    ${slot.released ? '⚡ Released' : (slot.estimatedWaitMinutes === 0 ? '⭐️ 0m Wait' : '~' + slot.estimatedWaitMinutes + 'm Wait')}
                                </span>
                            ` : `
                                <span class="smart-slot-tag danger">Booked</span>
                            `}
                        </button>
                    `).join('')}
                </div>
                <div style="display:flex;gap:16px;margin-top:14px;font-size:0.8rem;color:#64748b;flex-wrap:wrap;">
                    <span><span style="display:inline-block;width:12px;height:12px;background:#ffffff;border:1px solid #e2e8f0;border-radius:2px;margin-right:4px;"></span>Available</span>
                    <span><span style="display:inline-block;width:12px;height:12px;background:#dcfce7;border-radius:2px;margin-right:4px;"></span>⭐️ Shortest Wait</span>
                    <span><span style="display:inline-block;width:12px;height:12px;background:#fef3c7;border-radius:2px;margin-right:4px;"></span>⚡ Marketplace Slot</span>
                    <span><span style="display:inline-block;width:12px;height:12px;background:#f1f5f9;border:1px solid #e2e8f0;border-radius:2px;margin-right:4px;"></span>Booked / Past</span>
                </div>
            `;
        } else {
            slotsContainer.innerHTML = `
                <div style="background:#fee2e2;color:#991b1b;padding:16px;border-radius:10px;font-size:0.9rem;">
                    <div style="font-weight:700;margin-bottom:4px;">Doctor is fully booked or off duty on this date.</div>
                    <div style="font-size:0.85rem;margin-bottom:10px;">Would you like to join the priority waitlist to get alerted if a slot opens?</div>
                    <button type="button" class="btn btn-outline btn-sm" onclick="openWaitlistModal()" style="border-color:#b91c1c;color:#991b1b;">
                        🎟️ Join Waitlist for this Doctor
                    </button>
                </div>
            `;
        }
    } catch (e) {
        slotsContainer.innerHTML = '<p style="color:#ef4444;font-size:0.9rem;">Failed to fetch slots. Please try again.</p>';
    }
}

function selectSlot(btn, time) {
    document.querySelectorAll('.slot-btn').forEach(b => b.classList.remove('selected'));
    btn.classList.add('selected');
    selectedTime = time;

    const submitBtn = document.getElementById('submit-booking-btn');
    if (submitBtn) submitBtn.disabled = false;
}

async function handleBookingSubmit(e) {
    e.preventDefault();

    if (!currentPatientId) {
        showToast('Patient account not detected. Please log in again.', 'error');
        return;
    }

    const doctorSelect = document.getElementById('doctor-select');
    const dateInput = document.getElementById('appointment-date');
    const reasonInput = document.getElementById('appointment-reason');

    const doctorId = doctorSelect.value;
    const appointmentDate = dateInput.value;
    const reason = reasonInput.value.trim();

    if (!doctorId) {
        showToast('Please select a doctor', 'error');
        return;
    }
    if (!appointmentDate) {
        showToast('Please select a date', 'error');
        return;
    }
    if (!selectedTime) {
        showToast('Please select an available time slot', 'error');
        return;
    }

    const bookingPayload = {
        patientId: Number(currentPatientId),
        doctorId: Number(doctorId),
        appointmentDate: appointmentDate,
        appointmentTime: selectedTime + ':00',
        reason: reason || 'General Consultation'
    };

    const submitBtn = document.getElementById('submit-booking-btn');
    if (submitBtn) {
        submitBtn.disabled = true;
        submitBtn.textContent = 'Processing Booking...';
    }

    try {
        const res = await apiRequest('/appointments', 'POST', bookingPayload);
        if (res.success) {
            showToast('Appointment booked successfully!', 'success');
            // Show success modal or redirect
            setTimeout(() => {
                window.location.href = 'appointments.html';
            }, 1200);
        } else {
            showToast(res.message || 'Booking failed', 'error');
            if (submitBtn) {
                submitBtn.disabled = false;
                submitBtn.textContent = 'Confirm & Book Appointment';
            }
        }
    } catch (error) {
        showToast(error.message || 'Error booking appointment', 'error');
        if (submitBtn) {
            submitBtn.disabled = false;
            submitBtn.textContent = 'Confirm & Book Appointment';
        }
    }
}
