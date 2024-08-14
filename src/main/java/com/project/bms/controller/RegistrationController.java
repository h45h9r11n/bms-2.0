
import com.project.bms.model.Session;
import com.project.bms.model.User;
import com.project.bms.repository.UserRepository;
import com.project.bms.service.SessionService;
import com.project.bms.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

    @Controller
    public class AuthController {

        private final UserService userService;
        private final SessionService sessionService;
        private final UserRepository userRepository;

        public AuthController(UserService userService, SessionService sessionService, UserRepository userRepository) {
            this.userService = userService;
            this.sessionService = sessionService;
            this.userRepository = userRepository;
        }

        @Autowired
        private UserService userService;

        @Autowired
        private SessionService sessionService;

        @Autowired
        private UserRepository userRepository;

        @GetMapping("/")
        public String home() {
            return "login";
        }

        @GetMapping("/req/login")
        public String login() {
            return "login";
        }

        @GetMapping("/req/signup")
        public String register() {
            return "signup";
        }

        @GetMapping("/admin")
        public String admin() {
            return "admin";
        }

        @GetMapping("/index")
        public String index() {
            return "index";
        }

        @PostMapping("/req/signup")
        @ResponseBody
        public ResponseEntity register(@RequestBody User user) {
            if (userService.register(user.getUsername(), user.getPassword(), user.getEmail())) {
                return ResponseEntity.status(HttpStatus.CREATED).body("User registered successfully. Redirecting to login...");
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Username already exists!");
            }
        }

        @PostMapping("/req/login")
        public ResponseEntity<String> login(@RequestBody User user, HttpServletResponse response) {
            boolean isAuthenticated = userService.login(user.getUsername(), user.getPassword());

            if (isAuthenticated) {
                User authenticatedUser = userRepository.findByUsername(user.getUsername());
                Session session = sessionService.createSession(authenticatedUser.getId());

                Cookie sessionCookie = new Cookie("SESSIONID", session.getSessionId());
                Cookie roleCookie = new Cookie("ROLE", authenticatedUser.getRole());

                sessionCookie.setMaxAge(60 * 60);
                sessionCookie.setPath("/");
                sessionCookie.setHttpOnly(true);

                roleCookie.setMaxAge(60 * 60);
                roleCookie.setPath("/");
                roleCookie.setHttpOnly(true);

                response.addCookie(sessionCookie);
                response.addCookie(roleCookie);

                return ResponseEntity.ok("Login successful!");
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Incorrect username or password!");
            }
        }
    }

