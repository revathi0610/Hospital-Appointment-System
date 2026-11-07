/**
 * Smart Patient Capabilities: Symptom Finder, Slot Marketplace & Waitlists
 * MediCare Hospital Appointment Booking System
 */

document.addEventListener('DOMContentLoaded', () => {
    initSymptomFinder();
    initMarketplace();
    initWaitlist();
});

/* --------------------------------------------------------------------------
   FEATURE 2: SYMPTOM -> SPECIALIST FINDER
   -------------------------------------------------------------------------- */
function initSymptomFinder() {
    const btn = document.getElementById('symptom-search-btn');
    const input = document.getElementById('symptom-input');
    const resultsContainer = document.getElementById('symptom-results-container');

    if (!btn || !input || !resultsContainer) return;

    btn.addEventListener('click', () => runSymptomAnalysis(input.value.trim()));
    input.addEventListener('keydown', (e) => {
        if (e.key === 'Enter') runSymptomAnalysis(input.value.trim());
    });
}

async function runSymptomAnalysis(query) {
    const resultsContainer = document.getElementById('symptom-results-container');
    if (!resultsContainer) return;

    if (!query) {
        showToast('Please describe your symptoms first.', 'info');
        return;
    }

    resultsContainer.innerHTML = `
        <div style="text-align:center;padding:24px;color:#0284c7;">
            <div style="font-size:1.5rem;margin-bottom:6px;">🔍</div>
            <div>Analyzing clinical symptoms and matching medical departments...</div>
        </div>
    `;

    try {
        const res = await apiRequest(`/specialists/recommend?query=${encodeURIComponent(query)}`);
        if (res.success && res.data) {
            const data = res.data;
            let html = '';

            // Emergency Alert Banner
            if (data.emergency) {
                html += `
                    <div class="emergency-alert-card">
                        <div style="font-size:1.8rem;line-height:1;">🚨</div>
                        <div>
                            <div style="font-weight:800;font-size:1rem;margin-bottom:4px;">EMERGENCY MEDICAL NOTICE</div>
                            <div style="font-size:0.9rem;line-height:1.4;">${data.emergencyAlert}</div>
                            <div style="margin-top:10px;">
                                <a href="tel:112" class="btn btn-danger btn-sm" style="font-weight:700;">📞 Call Emergency (112 / 911)</a>
                            </div>
                        </div>
                    </div>
                `;
            }

            // Recommendations Grid
            if (data.recommendations && data.recommendations.length > 0) {
                html += `<div class="recommendations-grid">`;
                data.recommendations.forEach(rec => {
                    html += `
                        <div class="recommendation-card">
                            <div class="rec-header">
                                <div class="rec-spec">${rec.specialization}</div>
                                <span class="rec-confidence ${rec.confidence}">${rec.confidence} MATCH</span>
                            </div>
                            <div style="font-size:0.75rem;color:#0f766e;font-weight:600;margin-bottom:6px;">
                                Department: ${rec.department}
                            </div>
                            <div class="rec-explanation">${rec.explanation}</div>

                            ${rec.matchedKeywords && rec.matchedKeywords.length > 0 ? `
                                <div class="rec-keywords">
                                    ${rec.matchedKeywords.map(k => `<span class="rec-keyword-chip">#${k}</span>`).join('')}
                                </div>
                            ` : ''}

                            <div style="font-weight:600;font-size:0.8rem;color:#334155;margin-bottom:8px;">
                                Available Specialists:
                            </div>
                            ${rec.doctors && rec.doctors.length > 0 ? rec.doctors.map(d => `
                                <div class="rec-doc-mini">
                                    <div>
                                        <div style="font-weight:700;font-size:0.85rem;color:#0f172a;">${d.name}</div>
                                        <div style="font-size:0.75rem;color:#64748b;">${d.qualification} • $${Number(d.consultationFee).toFixed(2)}</div>
                                    </div>
                                    <a href="book-appointment.html?doctorId=${d.id}" class="btn btn-primary btn-sm" style="padding:4px 10px;font-size:0.75rem;">
                                        Book Now
                                    </a>
                                </div>
                            `).join('') : `
                                <div style="font-size:0.8rem;color:#94a3b8;padding:4px 0;">
                                    No doctors currently active in this department.
                                </div>
                            `}
                        </div>
                    `;
                });
                html += `</div>`;
            }

            // Medical Disclaimer Box
            html += `
                <div style="background:#f8fafc;border:1px solid #e2e8f0;border-radius:10px;padding:12px 16px;margin-top:16px;font-size:0.78rem;color:#64748b;display:flex;align-items:center;gap:8px;">
                    <span>⚖️</span>
                    <span><strong>Disclaimer:</strong> ${data.disclaimer}</span>
                </div>
            `;

            resultsContainer.innerHTML = html;
        }
    } catch (e) {
        resultsContainer.innerHTML = `<div style="color:#ef4444;padding:16px;">Error analyzing symptoms. Please try again.</div>`;
    }
}

/* --------------------------------------------------------------------------
   FEATURE 3: SLOT MARKETPLACE & WAITLIST
   -------------------------------------------------------------------------- */
async function initMarketplace() {
    const container = document.getElementById('marketplace-slots-container');
    if (!container) return;

    loadMarketplaceSlots();
}

async function loadMarketplaceSlots() {
    const container = document.getElementById('marketplace-slots-container');
    if (!container) return;

    try {
        const res = await apiRequest('/appointments/marketplace');
        if (res.success && res.data) {
            if (res.data.length > 0) {
                container.innerHTML = `
                    <div style="display:grid;grid-template-columns:repeat(auto-fill, minmax(300px, 1fr));gap:16px;">
                        ${res.data.map(slot => `
                            <div class="card" style="border:2px solid #fde68a;background:#fffdfa;">
                                <div style="display:flex;justify-content:space-between;align-items:center;margin-bottom:8px;">
                                    <span class="marketplace-badge">⚡ Released Slot</span>
                                    <span style="font-weight:700;color:#0284c7;font-size:0.9rem;">$${Number(slot.consultationFee).toFixed(2)}</span>
                                </div>
                                <h4 style="margin:0 0 4px 0;font-size:1rem;color:#0f172a;">${slot.doctorName}</h4>
                                <div style="font-size:0.8rem;color:#0f766e;font-weight:600;margin-bottom:8px;">${slot.specialization}</div>
                                <div style="font-size:0.85rem;color:#475569;margin-bottom:4px;">
                                    📅 <strong>${formatDate(slot.appointmentDate)}</strong> at <strong>${formatTime(slot.appointmentTime)}</strong>
                                </div>
                                <div style="font-size:0.75rem;color:#94a3b8;margin-bottom:12px;">
                                    Room: ${slot.consultationRoom} • Released by fellow patient
                                </div>
                                <button class="btn btn-primary btn-sm" style="width:100%;font-weight:700;" onclick="claimMarketplaceSlot(${slot.appointmentId})">
                                    Claim This Slot Now
                                </button>
                            </div>
                        `).join('')}
                    </div>
                `;
            } else {
                container.innerHTML = `
                    <div style="background:#f8fafc;border:1px dashed #cbd5e1;border-radius:12px;padding:32px;text-align:center;color:#64748b;">
                        <div style="font-size:1.5rem;margin-bottom:6px;">🔄</div>
                        <div style="font-weight:600;color:#334155;">No Released Slots in Marketplace Right Now</div>
                        <div style="font-size:0.85rem;margin-top:4px;">When patients release their appointments, they appear here instantly. You can also join a doctor's waitlist below to get notified.</div>
                    </div>
                `;
            }
        }
    } catch (e) {
        container.innerHTML = `<div style="color:#ef4444;padding:16px;">Failed to load slot marketplace.</div>`;
    }
}

window.claimMarketplaceSlot = async function (appointmentId) {
    const user = getCurrentUser();
    if (!user || !user.patientId) {
        showToast('Please login as a patient to claim a slot', 'info');
        return;
    }

    if (!confirm('Would you like to claim this released appointment slot?')) return;

    try {
        const res = await apiRequest(`/appointments/${appointmentId}/claim?patientId=${user.patientId}`, 'POST');
        if (res.success) {
            showToast('Slot claimed successfully! Your appointment is confirmed.', 'success');
            loadMarketplaceSlots();
            if (typeof loadPatientDashboard === 'function') loadPatientDashboard();
            if (typeof loadAllAppointments === 'function') loadAllAppointments();
            if (typeof openDigitalPassModal === 'function') {
                openDigitalPassModal(appointmentId);
            }
        }
    } catch (err) {
        showToast(err.message || 'Slot claim failed. It may have just been claimed by another patient.', 'error');
        loadMarketplaceSlots();
    }
};

window.releaseAppointmentToMarketplace = async function (appointmentId) {
    const user = getCurrentUser();
    if (!user || !user.patientId) return;

    if (!confirm('Are you sure you want to release this slot? It will be made available in the Marketplace and waitlisted patients will be alerted.')) {
        return;
    }

    try {
        const res = await apiRequest(`/appointments/${appointmentId}/release?patientId=${user.patientId}`, 'PUT');
        if (res.success) {
            showToast('Appointment released to marketplace successfully!', 'success');
            if (typeof loadPatientDashboard === 'function') loadPatientDashboard();
            if (typeof loadAllAppointments === 'function') loadAllAppointments();
            loadMarketplaceSlots();
        }
    } catch (err) {
        showToast(err.message || 'Failed to release slot', 'error');
    }
};

/* --------------------------------------------------------------------------
   FEATURE 3: WAITLIST MANAGEMENT
   -------------------------------------------------------------------------- */
function initWaitlist() {
    const btn = document.getElementById('open-waitlist-modal-btn');
    if (btn) {
        btn.addEventListener('click', openWaitlistModal);
    }
    loadPatientWaitlists();
}

window.openWaitlistModal = async function () {
    let modal = document.getElementById('waitlist-modal');
    if (!modal) {
        modal = document.createElement('div');
        modal.id = 'waitlist-modal';
        modal.className = 'modal-backdrop';
        modal.innerHTML = `
            <div class="modal-content">
                <div class="modal-header">
                    <div class="modal-title">Join Doctor Waitlist</div>
                    <button class="modal-close" onclick="document.getElementById('waitlist-modal').classList.remove('active')">&times;</button>
                </div>
                <form id="waitlist-form">
                    <div class="modal-body">
                        <p style="font-size:0.85rem;color:#64748b;margin-bottom:14px;">
                            If all slots are booked on your desired date, join the waitlist! You'll receive priority alerts the moment an appointment is cancelled or released.
                        </p>
                        <div class="form-group">
                            <label class="form-label">Doctor</label>
                            <select id="waitlist-doc-select" class="form-control" required></select>
                        </div>
                        <div class="form-group">
                            <label class="form-label">Preferred Date</label>
                            <input type="date" id="waitlist-date" class="form-control" required>
                        </div>
                        <div class="form-group">
                            <label class="form-label">Notes (Optional)</label>
                            <input type="text" id="waitlist-notes" class="form-control" placeholder="e.g. Any time after 2 PM is preferred">
                        </div>
                    </div>
                    <div class="modal-footer">
                        <button type="button" class="btn btn-outline btn-sm" onclick="document.getElementById('waitlist-modal').classList.remove('active')">Cancel</button>
                        <button type="submit" class="btn btn-primary btn-sm">Join Waitlist</button>
                    </div>
                </form>
            </div>
        `;
        document.body.appendChild(modal);

        // Bind form submission
        document.getElementById('waitlist-form').addEventListener('submit', handleWaitlistSubmit);

        // Set min date
        const dateInput = document.getElementById('waitlist-date');
        const todayStr = new Date().toISOString().split('T')[0];
        dateInput.min = todayStr;
        dateInput.value = todayStr;
    }

    // Populate doctors dropdown
    const docSelect = document.getElementById('waitlist-doc-select');
    try {
        const res = await apiRequest('/doctors');
        if (res.success && res.data) {
            docSelect.innerHTML = res.data.map(d => `<option value="${d.id}">${d.name} (${d.specialization})</option>`).join('');
        }
    } catch (e) {
        console.error(e);
    }

    modal.classList.add('active');
};

async function handleWaitlistSubmit(e) {
    e.preventDefault();
    const user = getCurrentUser();
    if (!user || !user.patientId) return;

    const payload = {
        patientId: user.patientId,
        doctorId: parseInt(document.getElementById('waitlist-doc-select').value, 10),
        preferredDate: document.getElementById('waitlist-date').value,
        notes: document.getElementById('waitlist-notes').value.trim()
    };

    try {
        const res = await apiRequest('/waitlist', 'POST', payload);
        if (res.success) {
            showToast('You have been added to the waitlist!', 'success');
            document.getElementById('waitlist-modal').classList.remove('active');
            loadPatientWaitlists();
        }
    } catch (err) {
        showToast(err.message || 'Failed to join waitlist', 'error');
    }
}

async function loadPatientWaitlists() {
    const user = getCurrentUser();
    const container = document.getElementById('patient-waitlists-list');
    if (!user || !user.patientId || !container) return;

    try {
        const res = await apiRequest(`/waitlist/patient/${user.patientId}`);
        if (res.success && res.data && res.data.length > 0) {
            container.innerHTML = res.data.map(w => `
                <div style="background:#ffffff;border:1px solid #e2e8f0;border-radius:10px;padding:12px;margin-bottom:8px;display:flex;justify-content:space-between;align-items:center;">
                    <div>
                        <div style="font-weight:700;font-size:0.9rem;color:#0f172a;">${w.doctorName} (${w.specialization})</div>
                        <div style="font-size:0.8rem;color:#64748b;">Date: ${formatDate(w.preferredDate)} • Status: <span class="badge ${w.status === 'ACTIVE' ? 'badge-confirmed' : 'badge-completed'}">${w.status}</span></div>
                    </div>
                    ${w.status === 'ACTIVE' ? `
                        <button class="btn btn-outline btn-sm" style="color:#ef4444;border-color:#fca5a5;font-size:0.75rem;" onclick="cancelWaitlistEntry(${w.id})">Leave</button>
                    ` : ''}
                </div>
            `).join('');
        } else {
            container.innerHTML = `<div style="font-size:0.85rem;color:#94a3b8;padding:12px 0;">No active waitlist entries.</div>`;
        }
    } catch (e) {
        console.error(e);
    }
}

window.cancelWaitlistEntry = async function (id) {
    const user = getCurrentUser();
    if (!user || !user.patientId) return;

    try {
        await apiRequest(`/waitlist/${id}?patientId=${user.patientId}`, 'DELETE');
        showToast('Waitlist entry cancelled', 'info');
        loadPatientWaitlists();
    } catch (err) {
        showToast(err.message || 'Error cancelling waitlist', 'error');
    }
};
