package net.javaguides.api_gateway.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.security.Key;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
public class JwtUtil {

    private static final String SECRET = "5367566B59703373367639792F423F4528482B4D6251655468576D5A71347437";

    // Validate the token
    public void validateToken(String token) {
        Jwts.parser().setSigningKey(getSignKey()).build().parseClaimsJws(token);
    }

    // Extract roles from the token
    public List<String> extractRoles(String token) {
        Claims claims = getClaims(token);
        List<Map<String, String>> rolesClaim = claims.get("roles", List.class);

        List<String> roles = new ArrayList<>();
        if (rolesClaim != null) {
            for (Map<String, String> roleMap : rolesClaim) {
                String authority = roleMap.get("authority");
                if (authority != null) {
                    roles.add(authority);
                }
            }
        }
        return roles;
    }

    private Claims getClaims(String token) {
        return Jwts.parser()
                .setSigningKey(getSignKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private Key getSignKey() {
        byte[] keyBytes = io.jsonwebtoken.io.Decoders.BASE64.decode(SECRET);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
