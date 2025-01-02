package com.example.my_books_backend.util;

import java.security.Key;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.crypto.SecretKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
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
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;

@Component
public class JwtUtil {
    private static final Logger logger = LoggerFactory.getLogger(JwtUtil.class);

    @Value("${spring.app.jwtSecret}")
    private String secret;

    @Value("${spring.app.jwtAccessExpiration}")
    private int accessExpiration;

    @Value("${spring.app.jwtRefreshExpiration}")
    private int refreshExpiration;

    private static final String REFRESH_TOKEN_KEY = "refreshToken";

    // リフレッシュトークンの失効リスト（jtiをキー、トークンの有効期限（エポックタイム）を値とする）
    private Map<String, Long> invalidatedTokens = new ConcurrentHashMap<>();

    // アクセストークン生成
    public String generateAccessToken(User user) {
        String email = user.getEmail();
        String name = user.getName();
        String roles = user.getRoles().stream().map(Role::getName).collect(Collectors.joining(","));

        return Jwts.builder().subject(email).claim("name", name).claim("roles", roles)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + accessExpiration * 1000))
                .signWith(key()).compact();
    }

    // リフレッシュトークン生成
    public String generateRefreshToken(User user) {
        String email = user.getEmail();
        String jti = UUID.randomUUID().toString(); // 一意のトークンID（失効リストで使用）

        return Jwts.builder().subject(email).id(jti).issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + refreshExpiration * 1000))
                .signWith(key()).compact();
    }

    // リフレッシュトークンからCookieを作成
    public Cookie createRefreshTokenCookie(String refreshToken) {
        Cookie cookie = new Cookie(REFRESH_TOKEN_KEY, refreshToken);
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        cookie.setAttribute("SameSite", "None");
        cookie.setSecure(true);
        cookie.setMaxAge(refreshExpiration);
        return cookie;
    }

    // リフレッシュトークンを失効リストに追加
    public void addInvalidatedTokens(String refreshToken) {
        String jti = getJtiFromToken(refreshToken);
        Long expiryTime = getExpiryTimeFromToken(refreshToken).getTime();
        invalidatedTokens.put(jti, expiryTime);

        for (Map.Entry<String, Long> entry : invalidatedTokens.entrySet()) {
            String key = entry.getKey();
            Long time = entry.getValue();
            logger.info("expiryTime: " + time + " key: " + key);
        }
    }

    // リフレッシュトークンが失効リストに含まれているか
    public boolean isTokenInvalid(String refreshToken) {
        String jti = getJtiFromToken(refreshToken);
        Long expiryTime = invalidatedTokens.get(jti);
        if (expiryTime == null) {
            return false;
        }

        // 現在時刻が有効期限を過ぎている場合も無効
        return System.currentTimeMillis() > expiryTime;
    }

    // リフレッシュトークン失効リストの定期クリーンアップ
    @Scheduled(cron = "${spring.app.deleteInvalidRefreshTokens.schedule.cron}", zone = "Asia/Tokyo")
    public void cleanupInvalidatedTokens() {
        long currentTime = System.currentTimeMillis();
        invalidatedTokens.entrySet().removeIf(entry -> entry.getValue() < currentTime);
        logger.info("リフレッシュトークン失効リストをクリーンアップしました。");
    }

    // リフレッシュトークンを無効にしたCookieを取得
    public Cookie getInvalidateRefreshTokenCookie() {
        Cookie cookie = new Cookie(REFRESH_TOKEN_KEY, "");
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        cookie.setAttribute("SameSite", "None");
        cookie.setSecure(true);
        cookie.setMaxAge(0); // すぐに削除
        return cookie;
    }

    // リフレッシュトークンをCookieから取得
    public String getRefreshTokenFromCookie(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();

        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (REFRESH_TOKEN_KEY.equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }

    // トークンの検証
    public boolean validateToken(String token) {
        try {
            Jwts.parser().verifyWith((SecretKey) key()).build().parseSignedClaims(token);
            return true;
        } catch (MalformedJwtException e) {
            logger.error("無効な JWTトークン: {}", e.getMessage());
        } catch (ExpiredJwtException e) {
            logger.error("JWTトークンの有効期限が切れています: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            logger.error("JWTトークンはサポートされていません: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            logger.error("JWTクレーム文字列が空です: {}", e.getMessage());
        }
        return false;
    }

    // トークンからサブジェクトを取得
    public String getSubjectFromToken(String token) {
        return getClaimFromToken(token, claims -> claims.getSubject());
    }

    // トークンからJTIを取得
    public String getJtiFromToken(String token) {
        return getClaimFromToken(token, claims -> claims.getId());
    }

    // トークンから有効期限を取得
    public Date getExpiryTimeFromToken(String token) {
        return getClaimFromToken(token, claims -> claims.getExpiration());
    }

    // トークンのロールを取得
    public List<String> getRolesFromToken(String token) {
        return getClaimFromToken(token, claims -> {
            String rolesString = claims.get("roles", String.class);
            return rolesString != null ? Arrays.asList(rolesString.split(","))
                    : Collections.emptyList();
        });
    }

    // 秘密鍵の生成
    private Key key() {
        byte[] keyBytes = Decoders.BASE64.decode(secret);
        return Keys.hmacShaKeyFor(keyBytes);
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
