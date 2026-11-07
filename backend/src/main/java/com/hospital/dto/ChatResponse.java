package com.hospital.dto;

import java.util.ArrayList;
import java.util.List;

public class ChatResponse {
    private String reply;
    private String actionType; // NAVIGATE, VIEW_DOCTORS, VIEW_APPOINTMENTS, CANCEL_APPOINTMENT, EMERGENCY
    private Object actionData;
    private List<ChatActionButton> buttons = new ArrayList<>();
    private boolean emergency;

    public ChatResponse() {
    }

    public ChatResponse(String reply) {
        this.reply = reply;
    }

    public ChatResponse(String reply, String actionType, List<ChatActionButton> buttons) {
        this.reply = reply;
        this.actionType = actionType;
        this.buttons = buttons;
    }

    public static class ChatActionButton {
        private String label;
        private String action;
        private String url;

        public ChatActionButton() {
        }

        public ChatActionButton(String label, String action, String url) {
            this.label = label;
            this.action = action;
            this.url = url;
        }

        public String getLabel() {
            return label;
        }

        public void setLabel(String label) {
            this.label = label;
        }

        public String getAction() {
            return action;
        }

        public void setAction(String action) {
            this.action = action;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }
    }

    public String getReply() {
        return reply;
    }

    public void setReply(String reply) {
        this.reply = reply;
    }

    public String getActionType() {
        return actionType;
    }

    public void setActionType(String actionType) {
        this.actionType = actionType;
    }

    public Object getActionData() {
        return actionData;
    }

    public void setActionData(Object actionData) {
        this.actionData = actionData;
    }

    public List<ChatActionButton> getButtons() {
        return buttons;
    }

    public void setButtons(List<ChatActionButton> buttons) {
        this.buttons = buttons;
    }

    public boolean isEmergency() {
        return emergency;
    }

    public void setEmergency(boolean emergency) {
        this.emergency = emergency;
    }
}
