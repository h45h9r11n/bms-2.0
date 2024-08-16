package com.project.bms.service;

import com.project.bms.model.Session;
import com.project.bms.repository.SessionRepository;
import com.project.bms.repository.UserRepository;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Time;
import java.time.Instant;
import java.time.LocalTime;


@Service
public class SessionService {
    @Autowired
    private SessionRepository sessionRepository;

    @Autowired
    private UserRepository userRepository;

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

    public Cookie[] getCookies(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        return cookies;
    }

    public String getSessionId(Cookie[] cookies) {
        String session = null;
        for (Cookie cookie : cookies) {
            if (cookie.getName().equals("SESSIONID")) {
                session = cookie.getValue();
            }
        }
        return session;
    }

    public String getRole(Cookie[] cookies){
        String role = null;
        for (Cookie cookie : cookies) {
            if (cookie.getName().equals("ROLE")) {
                role = cookie.getValue();
            }
        }
        return role;
    }

    public Long getUserId(HttpServletRequest request) {
        Cookie[] cookies = getCookies(request);
        String sessionId = getSessionId(cookies);
        return sessionRepository.findBySessionId(sessionId).getUserId();
    }

    public boolean isLogged(HttpServletRequest request) {
        Cookie[] cookies = getCookies(request);
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals("SESSIONID")) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean isExpired(Session session) {
        LocalTime now = LocalTime.now();
        LocalTime sessionTime = session.getCreatedAt().toLocalTime();
        if (now.getMinute() - sessionTime.getMinute() > 15) {
            deleteSession(session.getSessionId());
            return true;
        }
        return false;
    }

    public boolean isAdmin(HttpServletRequest request) {
        if (isLogged(request)) {
            String sessionId = getSessionId(getCookies(request));
            Session session = findBySessionId(sessionId);

            if (!isExpired(session)) {
                Long userid = getUserId(request);
                String role = userRepository.findById(userid).getRole();
                if (role.equals("ADMIN")) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean isSessionExpired(HttpServletRequest request) {
        if (isLogged(request)) {
            String sessionId = getSessionId(getCookies(request));
            Session session = findBySessionId(sessionId);
            return isExpired(session);
        }
        return false;
    }

    public void printSession(HttpServletRequest request) {
        if (isLogged(request)) {
            String sessionId = getSessionId(getCookies(request));
            String role = getRole(getCookies(request));
            System.out.println("Cookie SESSIONID" + sessionId);
            System.out.println("Cookie ROLE" + role);
            Session session = findBySessionId(sessionId);
            System.out.println("SessionID" + session.getSessionId());
            System.out.println("UserID" + session.getUserId());
        }
    }
}
