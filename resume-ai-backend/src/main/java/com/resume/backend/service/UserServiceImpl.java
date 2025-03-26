    package com.resume.backend.service;

    import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.jdbc.core.JdbcTemplate;
    import org.springframework.jdbc.core.RowMapper;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.user.OAuth2User;
    import org.springframework.stereotype.Service;
    import java.util.Collections;

    import com.resume.backend.model.User;

    import java.sql.ResultSet;
    import java.sql.SQLException;
    import java.time.LocalDateTime;
import java.util.List;

    @Service
    public class UserServiceImpl implements UserService, UserDetailsService{

        @Autowired
        private JdbcTemplate jdbcTemplate;
       
        @Autowired
        @Lazy
private PasswordEncoder passwordEncoder;
        
private RowMapper<User> userRowMapper = new RowMapper<User>() {
    @Override
    public User mapRow(ResultSet rs, int rowNum) throws SQLException {
        User user = new User();
        user.setId(rs.getLong("id"));
        user.setOauthId(rs.getString("oauth_id"));
        user.setName(rs.getString("name"));
        user.setEmail(rs.getString("email"));
        user.setPicture(rs.getString("picture"));
        user.setProvider(rs.getString("provider"));
        user.setCreatedAt(rs.getObject("created_at", LocalDateTime.class));
        user.setLastLogin(rs.getObject("last_login", LocalDateTime.class));
        user.setPassword(rs.getString("password"));
        return user;
    }
};
        
        @Override
        public String extractUserId(OAuth2User principal) {
            if (principal.getAttribute("sub") != null) {
                return principal.getAttribute("sub");
            } else if (principal.getAttribute("id") != null) {
                return principal.getAttribute("id").toString();
            }
            return null;
        }
        
        @Override
        public String getCurrentUserName(String oauthId) {
            List<User> users = jdbcTemplate.query(
                "SELECT * FROM users WHERE oauth_id = ?", 
                userRowMapper, 
                oauthId
            );
            
            return users.isEmpty() ? null : users.get(0).getName();
        }
        
        @Override
        public User processOAuthUser(OAuth2User principal, String registrationId) {
            String oauthId = extractUserId(principal);
            if (oauthId == null) {
                return null;
            }
            
            List<User> existingUsers = jdbcTemplate.query(
                "SELECT * FROM users WHERE oauth_id = ?", 
                userRowMapper, 
                oauthId
            );
            
            LocalDateTime now = LocalDateTime.now();
            
            if (!existingUsers.isEmpty()) {
                // Update existing user
                User existingUser = existingUsers.get(0);
                jdbcTemplate.update(
                    "UPDATE users SET last_login = ? WHERE id = ?",
                    now, existingUser.getId()
                );
                existingUser.setLastLogin(now);
                return existingUser;
            } else {
                // Create new user
                String name = principal.getAttribute("name");
                String email = principal.getAttribute("email");
                String picture = principal.getAttribute("picture");
                
                jdbcTemplate.update(
                    "INSERT INTO users (oauth_id, name, email, picture, provider, created_at, last_login) VALUES (?, ?, ?, ?, ?, ?, ?)",
                    oauthId, name, email, picture, registrationId, now, now
                );
                
                List<User> newUsers = jdbcTemplate.query(
                    "SELECT * FROM users WHERE oauth_id = ?", 
                    userRowMapper, 
                    oauthId
                );
                
                return newUsers.isEmpty() ? null : newUsers.get(0);
            }
        }
        @Override
    public User getUserByOauthId(String oauthId) {
        if (oauthId == null) {
            return null;
        }
        
        List<User> users = jdbcTemplate.query(
            "SELECT * FROM users WHERE oauth_id = ?", 
            userRowMapper, 
            oauthId
        );
        
        return users.isEmpty() ? null : users.get(0);
    }
    @Override
public User validateCredentials(String email, String password) {
    List<User> users = jdbcTemplate.query(
        "SELECT * FROM users WHERE email = ?", 
        userRowMapper, 
        email
    );
    
    if (users.isEmpty()) {
        return null; 
    }
    
    User user = users.get(0);
    if (passwordEncoder.matches(password, user.getPassword())) {
        return user;
    }
    
    return null;
}

    @Override
    public boolean existsByEmail(String email) {
        Integer count = jdbcTemplate.queryForObject(
            "SELECT COUNT(*) FROM users WHERE email = ?", 
            Integer.class, 
            email
        );
        return count != null && count > 0;
    }

    @Override
    public User createUser(String name, String email, String password) {
        // In a real app, hash the password before storing
        // String hashedPassword = passwordEncoder.encode(password);
        
        LocalDateTime now = LocalDateTime.now();
        
        jdbcTemplate.update(
            "INSERT INTO users (name, email, password, provider, created_at, last_login) VALUES (?, ?, ?, ?, ?, ?)",
            name, email, password, "local", now, now
        );
        List<User> users = jdbcTemplate.query(
            "SELECT * FROM users WHERE email = ?", 
            userRowMapper, 
            email
        );
        
        return users.isEmpty() ? null : users.get(0);
    }

    @Override
    public void updateLastLogin(Long userId) {
        LocalDateTime now = LocalDateTime.now();
        
        jdbcTemplate.update(
            "UPDATE users SET last_login = ? WHERE id = ?",
            now, userId
        );
    }
   

    public RowMapper<User> getUserRowMapper() {
        return userRowMapper;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        List<User> users = jdbcTemplate.query(
            "SELECT * FROM users WHERE email = ?", 
            userRowMapper, 
            username
        );
        
        if (users.isEmpty()) {
            throw new UsernameNotFoundException("User not found with email: " + username);
        }
        
        User user = users.get(0);
        String password = user.getPassword() != null ? user.getPassword() : "";
        
        return new org.springframework.security.core.userdetails.User(
            user.getEmail(),
            password,
            Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"))
        );
    }
    @Override
    public User getUserByEmail(String email) {
        List<User> users = jdbcTemplate.query(
            "SELECT * FROM users WHERE email = ?", 
            userRowMapper, 
            email
        );
        
        return users.isEmpty() ? null : users.get(0);
    }
}

