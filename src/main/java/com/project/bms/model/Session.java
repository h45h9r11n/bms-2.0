package com.project.bms.model;

import java.sql.Time;

public class Session {
    private String sessionId;
    private Long userId;
    private Time createdAt;

    public Session() {
        this.sessionId = generateSessionId();
        this.createdAt = new Time(System.currentTimeMillis());
    }

    public Session(String sessionId, Long userId) {
        this.sessionId = sessionId;
        this.userId = userId;
        this.createdAt = new Time(System.currentTimeMillis());
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

    public Time getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Time createdAt) {
        this.createdAt = createdAt;
    }




}
