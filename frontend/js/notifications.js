/**
 * In-App Notifications and Reminder Alerts
 * MediCare Hospital Appointment Booking System
 */

(function () {
    let unreadCount = 0;
    let pollInterval = null;

    document.addEventListener('DOMContentLoaded', () => {
        const user = typeof getCurrentUser === 'function' ? getCurrentUser() : null;
        if (!user) return;

        mountNotificationBell(user);
        fetchNotificationsCount(user);

        // Poll every 30 seconds
        pollInterval = setInterval(() => {
            fetchNotificationsCount(user);
        }, 30000);
    });

    function mountNotificationBell(user) {
        // Try to mount in topbar or navbar
        let mountPoint = document.querySelector('.topbar > div:last-child') ||
                         document.getElementById('nav-actions') ||
                         document.querySelector('.topbar');

        if (!mountPoint) return;

        const container = document.createElement('div');
        container.className = 'notification-bell-container';
        container.id = 'notification-bell-container';
        container.innerHTML = `
            <button class="notification-bell-btn" id="notif-bell-btn" title="Notifications & Reminders">
                <svg viewBox="0 0 24 24" width="20" height="20" fill="none" stroke="currentColor" stroke-width="2">
                    <path d="M18 8A6 6 0 0 0 6 8c0 7-3 9-3 9h18s-3-2-3-9"></path>
                    <path d="M13.73 21a2 2 0 0 1-3.46 0"></path>
                </svg>
                <span class="notification-badge" id="notif-badge" style="display:none;">0</span>
            </button>
            <div class="notification-dropdown" id="notif-dropdown">
                <div class="notification-header">
                    <span>Notifications & Alerts</span>
                    <button style="background:none;border:none;color:#0284c7;font-size:0.75rem;cursor:pointer;font-weight:600;" id="notif-mark-all-btn">Mark all read</button>
                </div>
                <div class="notification-list" id="notif-list">
                    <div style="padding:24px;text-align:center;color:#94a3b8;font-size:0.85rem;">
                        Loading notifications...
                    </div>
                </div>
            </div>
        `;

        mountPoint.insertBefore(container, mountPoint.firstChild);

        const bellBtn = document.getElementById('notif-bell-btn');
        const dropdown = document.getElementById('notif-dropdown');
        const markAllBtn = document.getElementById('notif-mark-all-btn');

        bellBtn.addEventListener('click', (e) => {
            e.stopPropagation();
            dropdown.classList.toggle('show');
            if (dropdown.classList.contains('show')) {
                loadNotificationsList(user);
            }
        });

        markAllBtn.addEventListener('click', async (e) => {
            e.stopPropagation();
            try {
                await apiRequest(`/notifications/user/${user.userId}/read-all`, 'PUT');
                unreadCount = 0;
                updateBadge();
                loadNotificationsList(user);
            } catch (err) {
                console.error(err);
            }
        });

        document.addEventListener('click', (e) => {
            if (!container.contains(e.target)) {
                dropdown.classList.remove('show');
            }
        });
    }

    async function fetchNotificationsCount(user) {
        try {
            const res = await apiRequest(`/notifications/user/${user.userId}/count`);
            if (res.success) {
                unreadCount = res.data;
                updateBadge();
            }
        } catch (e) {
            // Silently ignore if offline
        }
    }

    function updateBadge() {
        const badge = document.getElementById('notif-badge');
        if (!badge) return;

        if (unreadCount > 0) {
            badge.textContent = unreadCount > 9 ? '9+' : unreadCount;
            badge.style.display = 'flex';
        } else {
            badge.style.display = 'none';
        }
    }

    async function loadNotificationsList(user) {
        const listElem = document.getElementById('notif-list');
        if (!listElem) return;

        try {
            const res = await apiRequest(`/notifications/user/${user.userId}`);
            if (res.success && res.data && res.data.length > 0) {
                listElem.innerHTML = res.data.map(n => {
                    let iconBg = '#e0f2fe';
                    let icon = '🔔';
                    if (n.type === 'REMINDER' || n.type === 'URGENT_REMINDER') {
                        iconBg = '#fef3c7';
                        icon = '⏰';
                    } else if (n.type === 'WAITLIST_ALERT') {
                        iconBg = '#dcfce7';
                        icon = '🎟️';
                    } else if (n.type === 'SLOT_RELEASED') {
                        iconBg = '#f1f5f9';
                        icon = '🔄';
                    }

                    return `
                        <div class="notification-item ${!n.read ? 'unread' : ''}" onclick="window.onNotificationClick(${n.id}, ${n.appointmentId || 'null'}, ${n.read})">
                            <div class="notification-item-icon" style="background:${iconBg}">
                                ${icon}
                            </div>
                            <div class="notification-item-content">
                                <div class="notification-item-title">${n.title}</div>
                                <div class="notification-item-msg">${n.message}</div>
                                <div class="notification-item-time">${formatNotificationTime(n.createdAt)}</div>
                            </div>
                        </div>
                    `;
                }).join('');
            } else {
                listElem.innerHTML = `
                    <div style="padding:24px;text-align:center;color:#94a3b8;font-size:0.85rem;">
                        No notifications yet. You're all caught up!
                    </div>
                `;
            }
        } catch (e) {
            listElem.innerHTML = `<div style="padding:16px;color:#ef4444;font-size:0.85rem;">Failed to load notifications.</div>`;
        }
    }

    window.onNotificationClick = async function (id, appointmentId, isRead) {
        const user = getCurrentUser();
        if (!isRead && user) {
            try {
                await apiRequest(`/notifications/${id}/read?userId=${user.userId}`, 'PUT');
                if (unreadCount > 0) unreadCount--;
                updateBadge();
            } catch (e) {
                console.error(e);
            }
        }

        if (appointmentId) {
            if (typeof openDigitalPassModal === 'function') {
                openDigitalPassModal(appointmentId);
            } else {
                window.location.href = 'appointments.html';
            }
        }
    };

    function formatNotificationTime(dtStr) {
        if (!dtStr) return '';
        try {
            const dt = new Date(dtStr);
            return dt.toLocaleTimeString('en-US', { hour: '2-digit', minute: '2-digit' }) + ', ' +
                   dt.toLocaleDateString('en-US', { month: 'short', day: 'numeric' });
        } catch (e) {
            return dtStr;
        }
    }
})();
