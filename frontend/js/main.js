/**
 * Main Public Homepage and Doctors Directory Script
 * Hospital Appointment Booking System
 */

document.addEventListener('DOMContentLoaded', () => {
    initPublicNav();

    // If on homepage or doctors page, load doctors
    const doctorsContainer = document.getElementById('doctors-list');
    if (doctorsContainer) {
        loadDoctors();
    }

    const specFilter = document.getElementById('specialization-filter');
    if (specFilter) {
        loadSpecializations(specFilter);
        specFilter.addEventListener('change', () => loadDoctors());
    }

    const searchInput = document.getElementById('doctor-search');
    if (searchInput) {
        let debounceTimer;
        searchInput.addEventListener('input', () => {
            clearTimeout(debounceTimer);
            debounceTimer = setTimeout(() => loadDoctors(), 300);
        });
    }
});

async function loadSpecializations(selectElement) {
    try {
        const res = await apiRequest('/doctors/specializations');
        if (res.success && res.data) {
            res.data.forEach(spec => {
                const opt = document.createElement('option');
                opt.value = spec;
                opt.textContent = spec;
                selectElement.appendChild(opt);
            });
        }
    } catch (e) {
        console.error('Failed to load specializations:', e);
    }
}

async function loadDoctors() {
    const container = document.getElementById('doctors-list');
    if (!container) return;

    const specFilter = document.getElementById('specialization-filter');
    const searchInput = document.getElementById('doctor-search');

    const spec = specFilter ? specFilter.value : '';
    const query = searchInput ? searchInput.value : '';

    let endpoint = '/doctors';
    const params = new URLSearchParams();
    if (spec && spec !== 'ALL') params.append('specialization', spec);
    if (query.trim()) params.append('search', query.trim());

    if (params.toString()) {
        endpoint += `?${params.toString()}`;
    }

    container.innerHTML = '<div style="text-align:center;padding:40px;color:#64748b;grid-column:1/-1;">Loading doctors...</div>';

    try {
        const res = await apiRequest(endpoint);
        if (res.success && res.data && res.data.length > 0) {
            container.innerHTML = res.data.map(doc => renderDoctorCard(doc)).join('');
        } else {
            container.innerHTML = `
                <div style="text-align:center;padding:40px;color:#64748b;grid-column:1/-1;">
                    <p style="font-size:1.1rem;margin-bottom:8px;">No doctors found matching your criteria.</p>
                    <small>Try selecting a different specialization or clearing search terms.</small>
                </div>
            `;
        }
    } catch (error) {
        container.innerHTML = `
            <div style="text-align:center;padding:40px;color:#ef4444;grid-column:1/-1;">
                <p>Failed to connect to backend service. Ensure Spring Boot is running on port 8080.</p>
            </div>
        `;
    }
}

function renderDoctorCard(doc) {
    const initials = doc.name.replace('Dr. ', '').split(' ').map(n => n[0]).join('').substring(0, 2).toUpperCase();
    const isAvailable = doc.availabilityStatus === 'AVAILABLE';

    return `
        <div class="card doctor-card">
            <div style="display:flex;justify-content:space-between;align-items:flex-start;">
                <div class="doctor-avatar">${initials}</div>
                <span class="badge ${isAvailable ? 'badge-available' : 'badge-unavailable'}">
                    ${doc.availabilityStatus}
                </span>
            </div>
            <h3 class="doctor-name">${doc.name}</h3>
            <div class="doctor-specialty">${doc.specialization}</div>
            <div class="doctor-meta">
                <span>🎓 ${doc.qualification}</span>
                <span>⏳ ${doc.experience} Years Experience</span>
                <span>📞 ${doc.phone || 'Available via hospital'}</span>
            </div>
            <div class="doctor-fee">
                <div>
                    <span style="font-size:0.8rem;color:#64748b;display:block;">Consultation Fee</span>
                    <span>$${Number(doc.consultationFee).toFixed(2)}</span>
                </div>
                <a href="book-appointment.html?doctorId=${doc.id}" class="btn btn-primary btn-sm" ${!isAvailable ? 'style="pointer-events:none;opacity:0.6;"' : ''}>
                    Book Now
                </a>
            </div>
        </div>
    `;
}
