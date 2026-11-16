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

@Component
public class JwtUtil {

    private static final String ISSUER = "puntoagil";
    private static final String CLAIM_INICIO = "inicio";

    private final SecretKey key;
    private final JwtParser parser;
    private final long expirationMs;
    private final long maxSesionMs;

    public JwtUtil(@Value("${jwt.secret}") String secret,
                   @Value("${jwt.expiration}") long expirationMs,
                   @Value("${jwt.max-session}") long maxSesionMs) {
        byte[] bytes = Decoders.BASE64.decode(secret);
        if (bytes.length < 32) {
            throw new IllegalStateException("jwt.secret debe tener al menos 256 bits (32 bytes) en Base64");
        }
        this.key = Keys.hmacShaKeyFor(bytes);
        this.parser = Jwts.parser().verifyWith(key).requireIssuer(ISSUER).build();
        this.expirationMs = expirationMs;
        this.maxSesionMs = maxSesionMs;
    }

    /** Token de un login nuevo: la sesión empieza ahora. */
    public String generarToken(String email) {
        return generarToken(email, System.currentTimeMillis());
    }

    /** Token renovado: conserva el inicio original de la sesión. */
    public String generarToken(String email, long inicioSesionMs) {
        Date ahora = new Date();
        return Jwts.builder()
                .issuer(ISSUER)
                .subject(email)
                .claim(CLAIM_INICIO, inicioSesionMs)
                .issuedAt(ahora)
                .expiration(new Date(ahora.getTime() + expirationMs))
                .signWith(key)
                .compact();
    }

    /** Claims si el token es válido (firma, emisor y expiración); vacío en cualquier otro caso. */
    public Optional<Claims> validar(String token) {
        try {
            return Optional.of(parser.parseSignedClaims(token).getPayload());
        } catch (JwtException | IllegalArgumentException e) {
            return Optional.empty();
        }
    }

    public Optional<String> validarYExtraerEmail(String token) {
        return validar(token).map(Claims::getSubject);
    }

    public long inicioSesion(Claims claims) {
        Object valor = claims.get(CLAIM_INICIO);
        if (valor instanceof Number n) return n.longValue();
        return claims.getIssuedAt().getTime(); // tokens anteriores a este cambio
    }

    public boolean sesionAgotada(long inicioSesionMs) {
        return System.currentTimeMillis() - inicioSesionMs > maxSesionMs;
    }
}