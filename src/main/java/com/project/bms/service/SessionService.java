package com.project.bms.service;

import com.project.bms.model.Session;
import com.project.bms.repository.SessionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SessionService {
    @Autowired
    private SessionRepository sessionRepository;

    public Session createSession(Long userId) {
        Session session = new Session();
//        System.out.println("Session created: " + session.getSessionId());
        session.setUserId(userId);
        sessionRepository.save(session);
        return session;
    }

    public Session findBySessionId(String sessionId) {
        return sessionRepository.findBySessionId(sessionId);
    }

    public void deleteSession(String sessionId) {
        sessionRepository.delete(sessionId);
    }


}
