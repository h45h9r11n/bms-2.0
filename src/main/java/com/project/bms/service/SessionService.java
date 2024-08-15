package com.project.bms.service;

import com.project.bms.model.Session;
import com.project.bms.repository.SessionRepository;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.HashMap;

@Service
public class SessionService {
    @Autowired
    private SessionRepository sessionRepository;

//    @Autowired
//    private SessionService sessionService;

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

    public Cookie[] getCookies(HttpServletRequest request){
        Cookie[] cookies = request.getCookies();
        return cookies;
    }

    public String getSessionId(Cookie[] cookies){
        String session = null;
        for (Cookie cookie : cookies){
            if (cookie.getName().equals("SESSIONID")){
                session = cookie.getValue();
            }
        }
        return session;
    }

    public Long getUserId(String sessionId) {
        return sessionRepository.findBySessionId(sessionId).getUserId();
    }

    public boolean isLogged(HttpServletRequest request){
        Cookie[] cookies = getCookies(request);
        if (cookies != null){
            for (Cookie cookie : cookies){
                if (cookie.getName().equals("SESSIONID")){
                    return true;
                }
            }
        }
        return false;
    }

    public boolean isExpired(String sessionId) {
        return findBySessionId(sessionId).isExpired();
    }


}
