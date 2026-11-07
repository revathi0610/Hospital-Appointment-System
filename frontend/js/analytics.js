/**
 * Hospital Operational Intelligence & Analytics
 * MediCare Hospital Appointment Booking System
 */

document.addEventListener('DOMContentLoaded', () => {
    // If on Doctor Dashboard, load doctor analytics
    if (document.getElementById('doctor-analytics-section')) {
        initDoctorAnalytics();
    }

    // If on Admin Dashboard, load hospital analytics & keyword manager
    if (document.getElementById('admin-analytics-section')) {
        initAdminAnalytics();
        initKeywordManager();
    }

    // QR Verification Widget (if present on dashboard)
    const verifyBtn = document.getElementById('btn-verify-qr-token');
    if (verifyBtn) {
        verifyBtn.addEventListener('click', handleVerifyQr);
    }
});

/* --------------------------------------------------------------------------
   DOCTOR OPERATIONAL ANALYTICS
   -------------------------------------------------------------------------- */
async function initDoctorAnalytics() {
    const user = typeof getCurrentUser === 'function' ? getCurrentUser() : null;
    if (!user) return;

    let docId = user.doctorId;
    if (!docId) {
        try {
            const res = await apiRequest(`/doctors/user/${user.userId}`);
            if (res.success && res.data) {
                docId = res.data.id;
            }
        } catch (e) {
            console.error(e);
        }
    }

    if (!docId) return;

    try {
        const res = await apiRequest(`/doctors/${docId}/analytics`);
        if (res.success && res.data) {
            const data = res.data;

            const elCompRate = document.getElementById('stat-completion-rate');
            const elCancRate = document.getElementById('stat-cancellation-rate');
            const elAvgDur = document.getElementById('stat-avg-duration');
            const elDurationInput = document.getElementById('input-avg-duration');

            if (elCompRate) elCompRate.textContent = `${data.completionRate}%`;
            if (elCancRate) elCancRate.textContent = `${data.cancellationRate}%`;
            if (elAvgDur) elAvgDur.textContent = `${data.avgConsultationDuration} mins`;
            if (elDurationInput) elDurationInput.value = data.avgConsultationDuration;

            // Render day of week distribution bars
            const dayBarsContainer = document.getElementById('doc-day-distribution-bars');
            if (dayBarsContainer && data.dayOfWeekDistribution) {
                const maxVal = Math.max(1, ...Object.values(data.dayOfWeekDistribution));
                dayBarsContainer.innerHTML = Object.entries(data.dayOfWeekDistribution).map(([day, count]) => {
                    const pct = Math.round((count / maxVal) * 100);
                    return `
                        <div class="bar-row">
                            <span class="bar-label">${day.substring(0, 3)}</span>
                            <div class="bar-track">
                                <div class="bar-fill" style="width: ${pct}%; background: #0284c7;"></div>
                            </div>
                            <span class="bar-val">${count}</span>
                        </div>
                    `;
                }).join('');
            }
        }
    } catch (e) {
        console.error('Failed to load doctor analytics:', e);
    }
}

window.saveDoctorConsultationDuration = async function () {
    const user = getCurrentUser();
    const input = document.getElementById('input-avg-duration');
    if (!user || !user.doctorId || !input) return;

    const val = parseInt(input.value, 10);
    if (isNaN(val) || val < 5 || val > 120) {
        showToast('Please enter a duration between 5 and 120 minutes', 'error');
        return;
    }

    try {
        const res = await apiRequest(`/doctors/${user.doctorId}/consultation-duration?duration=${val}`, 'PUT');
        if (res.success) {
            showToast('Average consultation duration updated!', 'success');
            const elAvgDur = document.getElementById('stat-avg-duration');
            if (elAvgDur) elAvgDur.textContent = `${val} mins`;
        }
    } catch (e) {
        showToast(e.message || 'Failed to update duration', 'error');
    }
};

/* --------------------------------------------------------------------------
   ADMIN HOSPITAL-WIDE OPERATIONAL ANALYTICS
   -------------------------------------------------------------------------- */
async function initAdminAnalytics() {
    try {
        const res = await apiRequest('/admin/analytics');
        if (res.success && res.data) {
            const data = res.data;

            const elUtil = document.getElementById('stat-hospital-utilization');
            const elAvgWait = document.getElementById('stat-hospital-avg-wait');
            const elOverallComp = document.getElementById('stat-hospital-completion-rate');

            if (elUtil) elUtil.textContent = `${data.capacityUtilizationPercentage}%`;
            if (elAvgWait) elAvgWait.textContent = `~${data.averageHospitalWaitTimeMinutes} mins`;
            if (elOverallComp) elOverallComp.textContent = `${data.overallCompletionRate}%`;

            // Department utilization breakdown
            const deptContainer = document.getElementById('admin-dept-breakdown-bars');
            if (deptContainer && data.appointmentsByDepartment) {
                const maxVal = Math.max(1, ...Object.values(data.appointmentsByDepartment));
                deptContainer.innerHTML = Object.entries(data.appointmentsByDepartment).map(([dept, count]) => {
                    const pct = Math.round((count / maxVal) * 100);
                    return `
                        <div class="bar-row">
                            <span class="bar-label" style="width:110px;" title="${dept}">${dept}</span>
                            <div class="bar-track">
                                <div class="bar-fill" style="width: ${pct}%; background: #0f766e;"></div>
                            </div>
                            <span class="bar-val">${count}</span>
                        </div>
                    `;
                }).join('');
            }

            // 14-day appointment trend bars
            const volContainer = document.getElementById('admin-14day-volume-bars');
            if (volContainer && data.dailyVolumeRecentDays) {
                const maxVol = Math.max(1, ...Object.values(data.dailyVolumeRecentDays));
                volContainer.innerHTML = Object.entries(data.dailyVolumeRecentDays).map(([date, count]) => {
                    const pct = Math.round((count / maxVol) * 100);
                    const shortDate = date.substring(5); // MM-DD
                    return `
                        <div class="bar-row">
                            <span class="bar-label" style="width:60px;">${shortDate}</span>
                            <div class="bar-track">
                                <div class="bar-fill" style="width: ${pct}%; background: #0284c7;"></div>
                            </div>
                            <span class="bar-val">${count}</span>
                        </div>
                    `;
                }).join('');
            }
        }
    } catch (e) {
        console.error('Failed to load admin analytics:', e);
    }
}

/* --------------------------------------------------------------------------
   SYMPTOM KEYWORD MANAGER (Admin Tool)
   -------------------------------------------------------------------------- */
async function initKeywordManager() {
    const tableBody = document.getElementById('admin-keywords-table-body');
    if (!tableBody) return;

    loadKeywordsTable();

    const form = document.getElementById('add-keyword-form');
    if (form) {
        form.addEventListener('submit', handleAddKeyword);
    }
}

async function loadKeywordsTable() {
    const tableBody = document.getElementById('admin-keywords-table-body');
    if (!tableBody) return;

    try {
        const res = await apiRequest('/specialists/keywords');
        if (res.success && res.data) {
            if (res.data.length > 0) {
                tableBody.innerHTML = res.data.map(kw => `
                    <tr>
                        <td><strong>${kw.specialization}</strong></td>
                        <td>${kw.department || kw.specialization}</td>
                        <td style="font-size:0.8rem;color:#475569;">${kw.keywords}</td>
                        <td>
                            <button class="btn btn-outline btn-sm" style="color:#ef4444;border-color:#fca5a5;padding:2px 8px;font-size:0.75rem;" onclick="deleteKeywordMapping(${kw.id})">
                                Delete
                            </button>
                        </td>
                    </tr>
                `).join('');
            } else {
                tableBody.innerHTML = `<tr><td colspan="4" style="text-align:center;color:#94a3b8;padding:16px;">No keywords defined.</td></tr>`;
            }
        }
    } catch (e) {
        console.error(e);
    }
}

async function handleAddKeyword(e) {
    e.preventDefault();
    const specInput = document.getElementById('kw-specialization');
    const deptInput = document.getElementById('kw-department');
    const wordsInput = document.getElementById('kw-keywords');
    const descInput = document.getElementById('kw-description');

    if (!specInput || !wordsInput) return;

    const payload = {
        specialization: specInput.value.trim(),
        department: deptInput ? deptInput.value.trim() : specInput.value.trim(),
        keywords: wordsInput.value.trim(),
        description: descInput ? descInput.value.trim() : ''
    };

    try {
        const res = await apiRequest('/specialists/keywords', 'POST', payload);
        if (res.success) {
            showToast('Specialization keywords added!', 'success');
            specInput.value = '';
            if (deptInput) deptInput.value = '';
            wordsInput.value = '';
            if (descInput) descInput.value = '';
            loadKeywordsTable();
        }
    } catch (err) {
        showToast(err.message || 'Error saving keywords', 'error');
    }
}

window.deleteKeywordMapping = async function (id) {
    if (!confirm('Are you sure you want to remove this keyword mapping?')) return;
    try {
        const res = await apiRequest(`/specialists/keywords/${id}`, 'DELETE');
        if (res.success) {
            showToast('Keyword mapping deleted', 'info');
            loadKeywordsTable();
        }
    } catch (e) {
        showToast(e.message || 'Delete failed', 'error');
    }
};

/* --------------------------------------------------------------------------
   QR PASS VERIFICATION TOOL
   -------------------------------------------------------------------------- */
async function handleVerifyQr() {
    const input = document.getElementById('qr-verify-token-input');
    const output = document.getElementById('qr-verify-result-output');
    if (!input || !output) return;

    const token = input.value.trim();
    if (!token) {
        showToast('Please enter or scan a pass token UUID', 'info');
        return;
    }

    output.innerHTML = `<div style="color:#0284c7;padding:12px;">Verifying pass with hospital records...</div>`;

    try {
        const res = await apiRequest('/appointments/verify-qr', 'POST', { token });
        if (res.success && res.data) {
            const v = res.data;
            if (v.valid) {
                output.innerHTML = `
                    <div style="background:#f0fdf4;border:1px solid #bbf7d0;border-radius:10px;padding:16px;color:#166534;">
                        <div style="font-weight:800;font-size:1.05rem;display:flex;align-items:center;gap:8px;margin-bottom:8px;">
                            <span>✅</span> Verified Active Pass (#APT-${v.appointmentId})
                        </div>
                        <div style="font-size:0.88rem;line-height:1.4;">
                            <strong>Patient:</strong> ${v.patientName}<br>
                            <strong>Doctor:</strong> ${v.doctorName} (${v.specialization})<br>
                            <strong>Scheduled:</strong> ${formatDate(v.appointmentDate)} at ${formatTime(v.appointmentTime)}<br>
                            <strong>Status:</strong> <span class="badge badge-confirmed">${v.status}</span>
                        </div>
                        <div style="font-size:0.78rem;color:#15803d;margin-top:8px;">${v.message}</div>
                    </div>
                `;
            } else {
                output.innerHTML = `
                    <div style="background:#fef2f2;border:1px solid #fecaca;border-radius:10px;padding:16px;color:#991b1b;">
                        <div style="font-weight:800;font-size:1.05rem;display:flex;align-items:center;gap:8px;margin-bottom:6px;">
                            <span>❌</span> Invalid Pass Token
                        </div>
                        <div style="font-size:0.85rem;">${v.message}</div>
                    </div>
                `;
            }
        }
    } catch (e) {
        output.innerHTML = `<div style="color:#ef4444;padding:12px;">Verification error: ${e.message}</div>`;
    }
}
