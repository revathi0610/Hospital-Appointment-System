/**
 * Digital QR Appointment Pass & Verification Modal
 * MediCare Hospital Appointment Booking System
 */

(function () {
    // Generate or inject Digital Pass Modal into document
    document.addEventListener('DOMContentLoaded', () => {
        if (document.getElementById('digital-pass-modal')) return;

        const modal = document.createElement('div');
        modal.id = 'digital-pass-modal';
        modal.className = 'modal-backdrop';
        modal.innerHTML = `
            <div class="modal-content qr-pass-modal">
                <div class="modal-header">
                    <div class="modal-title">Digital Appointment Pass</div>
                    <button class="modal-close" onclick="closeDigitalPassModal()">&times;</button>
                </div>
                <div class="modal-body" id="digital-pass-body" style="padding: 16px 20px;">
                    <div style="text-align:center;padding:32px;color:#64748b;">
                        Generating your secure QR pass...
                    </div>
                </div>
                <div class="modal-footer" style="justify-content:space-between;">
                    <button type="button" class="btn btn-outline btn-sm" onclick="closeDigitalPassModal()">Close</button>
                    <button type="button" class="btn btn-primary btn-sm" onclick="window.print()">🖨️ Print / Save Pass</button>
                </div>
            </div>
        `;
        document.body.appendChild(modal);
    });

    window.closeDigitalPassModal = function () {
        const modal = document.getElementById('digital-pass-modal');
        if (modal) modal.classList.remove('active');
    };

    window.openDigitalPassModal = async function (appointmentId) {
        const modal = document.getElementById('digital-pass-modal');
        const body = document.getElementById('digital-pass-body');
        if (!modal || !body) return;

        modal.classList.add('active');
        body.innerHTML = `
            <div style="text-align:center;padding:32px;color:#0284c7;">
                <div style="font-size:1.5rem;margin-bottom:8px;">⏳</div>
                <div>Retrieving verified pass from hospital records...</div>
            </div>
        `;

        try {
            const res = await apiRequest(`/appointments/${appointmentId}/card`);
            if (res.success && res.data) {
                const pass = res.data;
                const qrUrl = `https://api.qrserver.com/v1/create-qr-code/?size=180x180&data=${encodeURIComponent(pass.qrDataString)}`;

                body.innerHTML = `
                    <div class="digital-pass-card">
                        <div class="pass-header">
                            <div>
                                <div class="pass-hospital-name">🏥 MediCare Health</div>
                                <div style="font-size:0.75rem;opacity:0.9;">Digital Outpatient Check-In Pass</div>
                            </div>
                            <div class="pass-status-pill">${pass.status}</div>
                        </div>

                        <div class="pass-body">
                            <div class="pass-grid">
                                <div>
                                    <div class="pass-field-label">Patient Name</div>
                                    <div class="pass-field-val">${pass.patientName}</div>
                                </div>
                                <div>
                                    <div class="pass-field-label">Pass #</div>
                                    <div class="pass-field-val">#APT-${pass.appointmentId}</div>
                                </div>
                                <div>
                                    <div class="pass-field-label">Consulting Doctor</div>
                                    <div class="pass-field-val">${pass.doctorName}</div>
                                    <div style="font-size:0.75rem;color:#0284c7;">${pass.doctorSpecialization}</div>
                                </div>
                                <div>
                                    <div class="pass-field-label">Consultation Room</div>
                                    <div class="pass-field-val">${pass.consultationRoom || 'Main Clinic'}</div>
                                    <div style="font-size:0.75rem;color:#64748b;">${pass.doctorDepartment || 'OPD'}</div>
                                </div>
                                <div>
                                    <div class="pass-field-label">Appointment Date</div>
                                    <div class="pass-field-val">${formatDate(pass.appointmentDate)}</div>
                                </div>
                                <div>
                                    <div class="pass-field-label">Scheduled Time</div>
                                    <div class="pass-field-val">${formatTime(pass.appointmentTime)}</div>
                                </div>
                            </div>

                            <!-- Live Queue & Wait Time Indicator -->
                            <div class="pass-wait-box">
                                <div class="pass-wait-icon">⏱️</div>
                                <div>
                                    <div style="font-weight:700;color:#15803d;font-size:0.9rem;">
                                        ${pass.estimatedWaitMinutes === 0 ? 'Next in Queue' : '~' + pass.estimatedWaitMinutes + ' Mins Estimated Wait'}
                                    </div>
                                    <div class="pass-wait-text">
                                        Live queue position calculated dynamically from doctor consultation pace.
                                    </div>
                                </div>
                            </div>

                            <!-- QR Code Block -->
                            <div class="pass-qr-center">
                                <div class="pass-qr-code">
                                    <img src="${qrUrl}" alt="Appointment QR Code" width="160" height="160"
                                         onerror="this.src='https://chart.googleapis.com/chart?cht=qr&chs=160x160&chl=${encodeURIComponent(pass.qrToken)}'">
                                </div>
                                <div class="pass-qr-token">
                                    Token: ${pass.qrToken}
                                </div>
                                <div style="font-size:0.72rem;color:#94a3b8;margin-top:4px;">
                                    Present this QR Pass at the reception desk for instant check-in.
                                </div>
                            </div>
                        </div>
                    </div>
                `;
            } else {
                body.innerHTML = `<div style="color:#ef4444;text-align:center;padding:24px;">Failed to generate digital pass.</div>`;
            }
        } catch (e) {
            body.innerHTML = `<div style="color:#ef4444;text-align:center;padding:24px;">Error retrieving pass: ${e.message}</div>`;
        }
    };
})();
