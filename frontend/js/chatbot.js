/**
 * AI Hospital Assistant Chatbot Widget
 * MediCare Hospital Appointment Booking System
 */

(function () {
    // Initialize Chatbot when DOM is ready
    document.addEventListener('DOMContentLoaded', initChatbot);

    function initChatbot() {
        if (document.getElementById('hospital-chat-widget')) return;

        const widgetContainer = document.createElement('div');
        widgetContainer.id = 'hospital-chat-widget';
        widgetContainer.innerHTML = `
            <!-- Floating Launcher Button -->
            <button class="chat-launcher" id="chat-launcher" title="AI Hospital Assistant">
                <svg viewBox="0 0 24 24">
                    <path d="M20 2H4c-1.1 0-2 .9-2 2v18l4-4h14c1.1 0 2-.9 2-2V4c0-1.1-.9-2-2-2zm0 14H6l-2 2V4h16v12z"/>
                    <circle cx="8" cy="9" r="1.5"/><circle cx="12" cy="9" r="1.5"/><circle cx="16" cy="9" r="1.5"/>
                </svg>
                <span class="chat-badge-pulse"></span>
            </button>

            <!-- Chat Window -->
            <div class="chat-window" id="chat-window">
                <div class="chat-header">
                    <div class="chat-header-title">
                        <span class="chat-status-dot"></span>
                        <span>AI Hospital Assistant</span>
                    </div>
                    <button class="chat-close-btn" id="chat-close-btn">&times;</button>
                </div>
                <div class="chat-disclaimer-banner">
                    <span>🛡️ Informational assistance only. Does not diagnose or prescribe.</span>
                </div>
                <div class="chat-messages" id="chat-messages">
                    <div class="chat-msg chat-msg-bot">
                        Hello! 👋 I am your AI Hospital Assistant. I can help you with:
                        <br>• Finding specialists based on your symptoms
                        <br>• Checking your upcoming appointments & wait times
                        <br>• Slot Marketplace releases & waitlists
                        <br>• Hospital hours & check-in QR passes
                        <br><br>How can I assist you today?
                    </div>
                </div>
                <div class="chat-quick-chips">
                    <button class="chat-quick-chip" onclick="window.sendChatPrompt('What time is my appointment?')">📅 My Appointments</button>
                    <button class="chat-quick-chip" onclick="window.sendChatPrompt('Which doctor should I see for frequent headaches?')">🩺 Headache Specialist</button>
                    <button class="chat-quick-chip" onclick="window.sendChatPrompt('How long is the wait time?')">⏳ Wait Times</button>
                    <button class="chat-quick-chip" onclick="window.sendChatPrompt('How does the Slot Marketplace work?')">🔄 Marketplace</button>
                    <button class="chat-quick-chip" onclick="window.sendChatPrompt('What are hospital timings?')">🏥 Hospital Hours</button>
                </div>
                <div class="chat-input-bar">
                    <input type="text" class="chat-input" id="chat-input" placeholder="Type your question or symptoms..." autocomplete="off">
                    <button class="chat-send-btn" id="chat-send-btn">
                        <svg viewBox="0 0 24 24" width="18" height="18" fill="#ffffff">
                            <path d="M2.01 21L23 12 2.01 3 2 10l15 2-15 2z"/>
                        </svg>
                    </button>
                </div>
            </div>
        `;

        document.body.appendChild(widgetContainer);

        // Bind events
        const launcher = document.getElementById('chat-launcher');
        const chatWindow = document.getElementById('chat-window');
        const closeBtn = document.getElementById('chat-close-btn');
        const sendBtn = document.getElementById('chat-send-btn');
        const input = document.getElementById('chat-input');

        launcher.addEventListener('click', () => {
            chatWindow.classList.toggle('open');
            if (chatWindow.classList.contains('open')) {
                input.focus();
            }
        });

        closeBtn.addEventListener('click', () => {
            chatWindow.classList.remove('open');
        });

        sendBtn.addEventListener('click', handleChatSubmit);
        input.addEventListener('keydown', (e) => {
            if (e.key === 'Enter') handleChatSubmit();
        });
    }

    window.sendChatPrompt = function (text) {
        const input = document.getElementById('chat-input');
        if (input) {
            input.value = text;
            handleChatSubmit();
        }
    };

    async function handleChatSubmit() {
        const input = document.getElementById('chat-input');
        const messagesContainer = document.getElementById('chat-messages');
        const text = input.value.trim();
        if (!text) return;

        // Append user message
        const userMsg = document.createElement('div');
        userMsg.className = 'chat-msg chat-msg-user';
        userMsg.textContent = text;
        messagesContainer.appendChild(userMsg);
        input.value = '';
        messagesContainer.scrollTop = messagesContainer.scrollHeight;

        // Append typing indicator
        const typingElem = document.createElement('div');
        typingElem.className = 'chat-msg chat-msg-bot';
        typingElem.id = 'chat-typing';
        typingElem.innerHTML = '<em>Consulting hospital assistant...</em>';
        messagesContainer.appendChild(typingElem);
        messagesContainer.scrollTop = messagesContainer.scrollHeight;

        const user = typeof getCurrentUser === 'function' ? getCurrentUser() : null;
        const payload = {
            message: text,
            patientId: (user && user.patientId) ? user.patientId : null,
            doctorId: (user && user.role === 'DOCTOR') ? user.userId : null
        };

        try {
            const res = await apiRequest('/chatbot/message', 'POST', payload);
            typingElem.remove();

            if (res.success && res.data) {
                const botData = res.data;
                const botMsg = document.createElement('div');
                botMsg.className = botData.emergency ? 'chat-msg chat-msg-emergency' : 'chat-msg chat-msg-bot';

                // Format text with linebreaks
                let formattedReply = botData.reply.replace(/\n/g, '<br>');
                botMsg.innerHTML = formattedReply;

                // Action buttons
                if (botData.buttons && botData.buttons.length > 0) {
                    const btnContainer = document.createElement('div');
                    btnContainer.className = 'chat-actions-container';

                    botData.buttons.forEach(b => {
                        const actionBtn = document.createElement('button');
                        actionBtn.className = 'chat-action-btn';
                        actionBtn.textContent = b.label;
                        actionBtn.onclick = () => handleChatAction(b);
                        btnContainer.appendChild(actionBtn);
                    });

                    botMsg.appendChild(btnContainer);
                }

                messagesContainer.appendChild(botMsg);
                messagesContainer.scrollTop = messagesContainer.scrollHeight;
            }
        } catch (e) {
            typingElem.remove();
            const errElem = document.createElement('div');
            errElem.className = 'chat-msg chat-msg-bot';
            errElem.textContent = "I'm having trouble connecting right now. Please try again or browse doctors directly.";
            messagesContainer.appendChild(errElem);
            messagesContainer.scrollTop = messagesContainer.scrollHeight;
        }
    }

    function handleChatAction(button) {
        if (button.action === 'NAVIGATE' && button.url) {
            window.location.href = button.url;
        } else if (button.action === 'CALL' && button.url) {
            window.location.href = button.url;
        } else if (button.action === 'VIEW_QR') {
            if (typeof openDigitalPassModal === 'function') {
                openDigitalPassModal(button.url);
            } else {
                window.location.href = 'appointments.html';
            }
        } else if (button.action === 'RELEASE_SLOT') {
            window.location.href = 'appointments.html';
        }
    }
})();
