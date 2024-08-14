package com.project.bms.model;

public class Session {
    private String sessionId;
    private Long userId;

    public Session() {
        this.sessionId = generateSessionId();
    }

    public Session(String sessionId, Long userId) {
        this.sessionId = sessionId;
        this.userId = userId;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    private static String generateSessionId() {
        String uuid = java.util.UUID.randomUUID().toString().replace("-", "");
        return uuid;
    }
}
