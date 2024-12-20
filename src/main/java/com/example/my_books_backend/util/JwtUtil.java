package com.example.my_books_backend.util;

import java.security.Key;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.crypto.SecretKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import com.example.my_books_backend.entity.Role;
import com.example.my_books_backend.entity.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

@Component
public class JwtUtil {
    private static final Logger logger = LoggerFactory.getLogger(JwtUtil.class);

    @Value("${spring.app.jwtSecret}")
    private String secret;

    @Value("${spring.app.jwtAccessExpirationMs}")
    private int accessExpiration;

    @Value("${spring.app.jwtRefreshExpirationMs}")
    private int refreshExpiration;

    // アクセストークン生成
    public String generateAccessToken(User user) {
        String email = user.getEmail();
        String username = user.getName();
        String roles = user.getRoles().stream().map(Role::getName).collect(Collectors.joining(","));

        return Jwts.builder().subject(email).claim("username", username).claim("roles", roles)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + accessExpiration)).signWith(key())
                .compact();
    }

    // リフレッシュトークン生成
    public String generateRefreshToken(User user) {
        String email = user.getEmail();

        return Jwts.builder().subject(email).issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + refreshExpiration))
                .signWith(key()).compact();
    }

    // 秘密鍵の生成
    private Key key() {
        byte[] keyBytes = Decoders.BASE64.decode(secret);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    // アクセストークンの検証
    public boolean validateAccessToken(String token) {
        try {
            Jwts.parser().verifyWith((SecretKey) key()).build().parseSignedClaims(token);
            return true;
        } catch (MalformedJwtException e) {
            logger.error("Invalid JWT token: {}", e.getMessage());
        } catch (ExpiredJwtException e) {
            logger.error("JWT token is expired: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            logger.error("JWT token is unsupported: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            logger.error("JWT claims string is empty: {}", e.getMessage());
        }
        return false;
    }

    // リフレッシュトークンの検証
    public boolean validateRefreshToken(String token) {
        try {
            Jwts.parser().verifyWith((SecretKey) key()).build().parseSignedClaims(token);
            return true;
        } catch (Exception e) {
            logger.error("Invalid JWT token: {}", e.getMessage());
        }
        return false;
    }

    // トークンからサブジェクトを取得
    public String getSubjectFromToken(String token) {
        return getClaimFromToken(token, claims -> claims.getSubject());
    }

    // トークンのロールを取得
    public List<String> getRolesFromToken(String token) {
        return getClaimFromToken(token, claims -> {
            String rolesString = claims.get("roles", String.class);
            return rolesString != null ? Arrays.asList(rolesString.split(","))
                    : Collections.emptyList();
        });
    }

    // トークンの有効期限を取得
    public Date getExpirationDateFromToken(String token) {
        return getClaimFromToken(token, claims -> claims.getExpiration());
    }

    // トークンの有効期限チェック
    public Boolean isTokenExpired(String token) {
        return getExpirationDateFromToken(token).before(new Date());
    }

    // 汎用的なクレーム取得メソッド
    private <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = getAllClaimsFromToken(token);
        return claimsResolver.apply(claims);
    }

    // すべてのクレームを取得
    private Claims getAllClaimsFromToken(String token) {
        return Jwts.parser().verifyWith((SecretKey) key()).build().parseSignedClaims(token)
                .getPayload();
    }
}

// ---------------------------------------------------------------------------
// JWT秘密鍵
// @Value("${spring.app.jwtSecret}")
// private String secret;
//
// 以下のようなプログラムで生成した値を環境変数に設定
// import java.security.SecureRandom;
// import java.util.Base64;
//
// public class JwtSecretGenerator {
// public static void main(String[] args) {
// // 256ビット（32バイト）のランダムな秘密鍵を生成
// byte[] keyBytes = new byte[32];
// new SecureRandom().nextBytes(keyBytes);
//
// // Base64エンコード
// String base64EncodedSecret = Base64.getEncoder().encodeToString(keyBytes);
//
// System.out.println("生成されたBase64エンコード秘密鍵:");
// System.out.println(base64EncodedSecret);
// }
// }
