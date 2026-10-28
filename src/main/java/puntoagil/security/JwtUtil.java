package puntoagil.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.Optional;
import java.util.function.Function;

@Component
public class JwtUtil {

    private static final String ISSUER = "puntoagil";

    private final SecretKey key;
    private final JwtParser parser;
    private final long expirationMs;

    public JwtUtil(@Value("${jwt.secret}") String secret,
                   @Value("${jwt.expiration}") long expirationMs) {
        byte[] bytes = Decoders.BASE64.decode(secret);
        if (bytes.length < 32) {
            throw new IllegalStateException("jwt.secret debe tener al menos 256 bits (32 bytes) en Base64");
        }
        this.key = Keys.hmacShaKeyFor(bytes);
        this.parser = Jwts.parser().verifyWith(key).requireIssuer(ISSUER).build();
        this.expirationMs = expirationMs;
    }

    public String generarToken(String email) {
        Date ahora = new Date();
        return Jwts.builder()
                .issuer(ISSUER)
                .subject(email)
                .issuedAt(ahora)
                .expiration(new Date(ahora.getTime() + expirationMs))
                .signWith(key)
                .compact();
    }

    /** Email si el token es válido (firma, emisor y expiración); vacío en cualquier otro caso. */
    public Optional<String> validarYExtraerEmail(String token) {
        try {
            return Optional.ofNullable(parser.parseSignedClaims(token).getPayload().getSubject());
        } catch (JwtException | IllegalArgumentException e) {
            return Optional.empty();
        }
    }
}