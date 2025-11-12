package com.me2u.gateway.util;

import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWT;
import com.nimbusds.jwt.JWTParser;
import com.nimbusds.jwt.SignedJWT;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtUtil {

  @Value("${jwt.secret:}")
  private String jwtSecret;

  public boolean validateToken(String token) {
    try {
      if (jwtSecret == null || jwtSecret.isBlank()) {
        return false;
      }

      JWT parsedToken = JWTParser.parse(token);

      if (!(parsedToken instanceof SignedJWT signedJWT)) {
        return false;
      }

      JWSVerifier verifier = new MACVerifier(jwtSecret.getBytes());
      if (!signedJWT.verify(verifier)) {
        return false;
      }

      return signedJWT.getJWTClaimsSet().getExpirationTime() == null
          || signedJWT.getJWTClaimsSet().getExpirationTime().getTime() > System.currentTimeMillis();
    } catch (Exception e) {
      log.error("Error when validating token {}", e.getMessage(), e);
      return false;
    }
  }
}
