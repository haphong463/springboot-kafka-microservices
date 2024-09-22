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
        List<String> rolesClaim = claims.get("roles", List.class);

        List<String> roles = new ArrayList<>();
        if (rolesClaim != null) {
            for (String role : rolesClaim) {
                if (role != null) {
                    roles.add(role);
                }
            }
        }
        return roles;
    }

    public List<String> extractPermissions(String token) {
        Claims claims = getClaims(token);
        List<String> permissionClaim = claims.get("permissions", List.class);

        List<String> permissions = new ArrayList<>();
        if (permissionClaim != null) {
            for (String permission : permissionClaim) {
                if (permission != null) {
                    permissions.add(permission);
                }
            }
        }
        return permissions;
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
