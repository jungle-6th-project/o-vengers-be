package jungle.ovengers.support;

import io.jsonwebtoken.Header;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jungle.ovengers.model.response.Token;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;

@Slf4j
@RequiredArgsConstructor
@Component
public class TokenGenerator {

    private static final String MEMBER_ID_CLAIM_KEY = "memberId";
    @Value("${security.jwt.token.secretkey}")
    private String secretKey;
    @Value("${security.jwt.token.validtime.access}")
    private Long accessTokenValidTime;
    @Value("${security.jwt.token.validtime.refresh}")
    private Long refreshTokenValidTime;

    String generateAccessToken(Long memberId) {
        Date now = new Date();

        return Jwts.builder()
                   .setHeaderParam(Header.TYPE, Header.JWT_TYPE)
                   .claim(MEMBER_ID_CLAIM_KEY, memberId)
                   .setIssuedAt(now)
                   .setExpiration(new Date(now.getTime() + accessTokenValidTime))
                   .signWith(Keys.hmacShaKeyFor(secretKey.getBytes()))
                   .compact();
    }

    String generateRefreshToken(Long memberId) {
        Date now = new Date();

        return Jwts.builder()
                   .setHeaderParam(Header.TYPE, Header.JWT_TYPE)
                   .claim(MEMBER_ID_CLAIM_KEY, memberId)
                   .setIssuedAt(now)
                   .setExpiration(new Date(now.getTime() + refreshTokenValidTime))
                   .signWith(Keys.hmacShaKeyFor(secretKey.getBytes()))
                   .compact();
    }

    public Token generateToken(Long memberId) {
        return Token.builder()
                    .accessToken(generateAccessToken(memberId))
                    .refreshToken(generateRefreshToken(memberId))
                    .build();
    }
}
