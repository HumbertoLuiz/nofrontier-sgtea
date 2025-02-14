package br.com.xfrontier.sgetea.core.services.token.providers;

import java.time.Instant;
import java.util.Date;
import java.util.HashMap;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import br.com.xfrontier.sgetea.core.exceptions.TokenServiceException;
import br.com.xfrontier.sgetea.core.services.token.adapters.TokenService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.JwtParserBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@Service
public class JjwtService implements TokenService {

    @Value("${br.com.nofrontier.access.key}")
    private String accessKey;

    @Value("${br.com.nofrontier.access.expiration}")
    private int accessExpiration;

    @Value("${br.com.nofrontier.refresh.key}")
    private String refreshKey;

    @Value("${br.com.nofrontier.refresh.expiration}")
    private int refreshExpiration;

    @Override
    public String generateAccessToken(String subject) {
        return generateToken(accessKey, accessExpiration, subject);
    }

    @Override
    public String getSubjetDoAccessToken(String accessToken) {
        var claims = getClaims(accessToken, accessKey);

        return claims.getSubject();
    }

    @Override
    public String generateRefreshToken(String subject) {
        return generateToken(refreshKey, refreshExpiration, subject);
    }

    @Override
    public String getSubjectDoRefreshToken(String refreshToken) {
        var claims = getClaims(refreshToken, refreshKey);

        return claims.getSubject();
    }


	private String generateToken(String signKey, int expiration, String subject) {
        var claims = new HashMap<String, Object>();

        var dataHoraAtual = Instant.now();
        var dataHoraExpiracao = dataHoraAtual.plusSeconds(expiration);

        
        return Jwts.builder()
            .setClaims(claims)
            .setSubject(subject)
            .setIssuedAt(new Date(dataHoraAtual.toEpochMilli()))
            .setExpiration(new Date(dataHoraExpiracao.toEpochMilli()))
            .signWith(Keys.hmacShaKeyFor(signKey.getBytes()))
            .compact();
    }


    private Claims getClaims(String token, String signKey) {
        try {
            return tryGetClaims(token, signKey);
        } catch (JwtException exception) {
            throw new TokenServiceException(exception.getLocalizedMessage());
        }
    }


	private Claims tryGetClaims(String token, String signKey) {
        return ((JwtParserBuilder) Jwts.builder())
            .setSigningKey(Keys.hmacShaKeyFor(signKey.getBytes()))
            .build()
            .parseClaimsJws(token)
            .getBody();
    }

}
