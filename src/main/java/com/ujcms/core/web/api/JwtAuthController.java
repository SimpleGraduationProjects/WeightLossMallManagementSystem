package com.ujcms.core.web.api;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTCreator;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.ofwise.util.security.CredentialsDigest;
import com.ofwise.util.security.jwt.JwtProperties;
import com.ofwise.util.security.jwt.JwtUtils;
import com.ofwise.util.web.Responses;
import com.ofwise.util.web.Responses.Body;
import com.ofwise.util.web.Servlets;
import com.ujcms.core.domain.User;
import com.ujcms.core.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static com.ofwise.util.security.jwt.JwtProperties.*;
import static com.ujcms.core.support.UrlConstants.API;

/**
 * @author PONY
 */
@RestController
@RequestMapping(API + "/auth/jwt")
public class JwtAuthController {
    private static Logger logger = LoggerFactory.getLogger(JwtAuthController.class);

    public JwtAuthController(JwtProperties properties) {
        this.properties = properties;
        this.algorithm = Algorithm.HMAC256(properties.getSecret());
        long leeway = properties.getLeeway();
        this.refreshTokenVerifier = JWT.require(algorithm).withIssuer(properties.getRefreshTokenIssuer())
                .acceptLeeway(leeway).build();
    }


    @PostMapping("/login")
    public ResponseEntity<Body> login(@RequestBody LoginParam params, HttpServletRequest request) {
        User user = userService.selectByUsername(params.getUsername());
        // ??????????????????
        if (user == null) {
            return Responses.failure(request, "error.usernameNotExist");
        }
        // ????????????
        if (!credentialsDigest.matches(user.getPassword(), params.getPassword(), user.getSalt())) {
            return Responses.failure(request, "error.passwordIncorrect");
        }
        // ???????????????
        if (user.isLocked()) {
            return Responses.failure(request, "error.userLocked");
        }
        // ???????????????
        if (user.isDisabled()) {
            return Responses.failure(request, "error.userDisabled");
        }
        // ?????? Access Token
        Date now = new Date();
        String loginId = UUID.randomUUID().toString().replace("-", "");
        String accessToken = createAccessToken(loginId, user.getId(), now, false);
        String refreshToken = createRefreshToken(loginId, now, user.getId(), now, params.browser);
        Map<String, Object> result = new HashMap<>(6);
        result.put(ACCESS_TOKEN, accessToken);
        result.put(EXPIRES_IN, properties.getExpiresSeconds());
        result.put(REMEMBERED, false);
        result.put(REFRESH_TOKEN, refreshToken);
        result.put(REFRESH_EXPIRES_IN, properties.getRefreshExpires());
        result.put(REFRESH_AUTH_EXPIRES_IN, params.browser ?
                properties.getExpiresSeconds() : properties.getRefreshAuthExpiresSeconds());
        result.put(SESSION_TIMEOUT, properties.getSessionTimeout());
        userService.updateLogin(user.getExt(), Servlets.getRemoteAddr(request));
        return Responses.ok(result);
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<Body> refreshToken(@RequestBody RefreshTokenParam params) {
        try {
            String refreshToken = params.getRefreshToken();
            DecodedJWT jwt = refreshTokenVerifier.verify(refreshToken);
            // ???????????? jwtId ???????????????????????????????????? token ????????????
            Integer userId = new Integer(jwt.getSubject());
            User user = userService.select(userId);
            if (user == null || user.isDisabled()) {
                return null;
            }
            Date now = new Date();
            String loginId = JwtUtils.getLoginIdClaim(jwt);
            Date loginTime = JwtUtils.getLoginTimeClaim(jwt);
            long authExpiresMillisAt = JwtUtils.getAuthExpiresAtClaim(jwt).getTime();
            boolean remembered = now.getTime() > authExpiresMillisAt;
            // ?????? Access Token
            String accessToken = createAccessToken(JwtUtils.getLoginIdClaim(jwt), userId, now, remembered);
            // ?????? Refresh Token ????????????????????????????????????????????????????????? Refresh Token
            long refreshExpiresIn = properties.getRefreshExpiresSeconds() -
                    (now.getTime() - loginTime.getTime()) / 1000;
            long refreshAuthExpiresIn = params.browser ?
                    properties.getExpiresSeconds() : properties.getRefreshExpiresSeconds();
            if (refreshAuthExpiresIn > refreshExpiresIn) {
                refreshAuthExpiresIn = refreshExpiresIn;
            }
            Map<String, Object> result = new HashMap<>(6);
            result.put(ACCESS_TOKEN, accessToken);
            result.put(EXPIRES_IN, properties.getExpiresSeconds());
            result.put(REMEMBERED, remembered);
            if (authExpiresMillisAt > now.getTime() && authExpiresMillisAt < jwt.getExpiresAt().getTime()) {
                refreshToken = createRefreshToken(loginId, loginTime, userId, now, params.browser);
                // ?????? refresh token ??????????????????????????????
                result.put(REFRESH_EXPIRES_IN, refreshExpiresIn);
                result.put(REFRESH_AUTH_EXPIRES_IN, refreshAuthExpiresIn);
            }
            result.put(REFRESH_TOKEN, refreshToken);
            return Responses.ok(result);
        } catch (JWTVerificationException e) {
            // ????????????
            String message = "refresh token JWT verification failed: " + params.refreshToken;
            logger.debug(message, e);
            return Responses.failure(message);
        }
    }

    private String createAccessToken(String loginId, long userId, Date now, boolean remembered) {
        Date expiresAt = new Date(now.getTime() + properties.getExpiresMillis());
        JWTCreator.Builder builder = JWT.create().withSubject(String.valueOf(userId)).withIssuedAt(now)
                .withExpiresAt(expiresAt).withIssuer(properties.getAccessTokenIssuer());
        JwtUtils.withLoginIdClaim(builder, loginId);
        JwtUtils.withRememberedClaim(builder, remembered);
        return builder.sign(algorithm);
    }

    private String createRefreshToken(String loginId, Date loginTime, long userId, Date now, boolean isBrowser) {
        Date expiresAt = new Date(loginTime.getTime() + properties.getRefreshExpiresMillis());
        // Access Token ?????????????????????30???????????????????????? Refresh Token ????????????????????????????????????????????????????????????????????????
        JWTCreator.Builder builder = JWT.create().withSubject(String.valueOf(userId))
                .withIssuer(properties.getRefreshTokenIssuer()).withIssuedAt(now).withExpiresAt(expiresAt);
        // ??????????????????????????? Refresh Token ?????????????????? Access Token ???????????????
        Date authExpiresAt = new Date(now.getTime() +
                (isBrowser ? properties.getExpiresMillis() : properties.getRefreshAuthExpiresMillis()));
        if (authExpiresAt.getTime() > expiresAt.getTime()) {
            authExpiresAt = expiresAt;
        }
        JwtUtils.withAuthExpiresAtClaim(builder, authExpiresAt);
        JwtUtils.withLoginIdClaim(builder, loginId);
        JwtUtils.withLoginTimeClaim(builder, loginTime);
        return builder.sign(algorithm);
    }

    private JwtProperties properties;
    private Algorithm algorithm;
    /**
     * ?????? Refresh Token
     */
    private JWTVerifier refreshTokenVerifier;

    private UserService userService;
    private CredentialsDigest credentialsDigest;

    @Autowired
    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    @Autowired
    public void setCredentialsDigest(CredentialsDigest credentialsDigest) {
        this.credentialsDigest = credentialsDigest;
    }

    public static class LoginParam {
        private String username;
        private String password;
        // ?????????????????????
        private boolean browser = true;

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public boolean isBrowser() {
            return browser;
        }

        public void setBrowser(boolean browser) {
            this.browser = browser;
        }
    }

    public static final class RefreshTokenParam {
        private String refreshToken;
        // ?????????????????????
        private boolean browser = true;

        public String getRefreshToken() {
            return refreshToken;
        }

        public void setRefreshToken(String refreshToken) {
            this.refreshToken = refreshToken;
        }

        public boolean isBrowser() {
            return browser;
        }

        public void setBrowser(boolean browser) {
            this.browser = browser;
        }
    }
}
