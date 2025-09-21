package org.example.trainingapp.jwt;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.EncodedKeySpec;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.time.Instant;
import java.util.Date;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.JWT;

import static org.example.trainingapp.constant.Constant.ROLE;


@Service
public class JwtTokenUtil {

    private final JWTVerifier verifier;
    private final Path keysPath;
    private final Integer jwtLifetime;


    public JwtTokenUtil(@Value("${jwt.keys-path:secret}") String keysDir,
                        @Value("${jwt.lifetime}") Integer jwtLifetime) throws Exception {
        this.keysPath = Paths.get(keysDir);
        this.jwtLifetime = jwtLifetime;
        PublicKey publicKey = loadPublicKey();
        this.verifier  = JWT.require(Algorithm.RSA256((RSAPublicKey) publicKey, null))
                .build();
    }


    private PublicKey loadPublicKey() throws Exception {
        byte[] publicKeyBytes = Files.readAllBytes(keysPath.resolve("public.key"));
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(publicKeyBytes);
        return KeyFactory.getInstance("RSA").generatePublic(keySpec);
    }


    public DecodedJWT validateAndParseToken(String token) {
        return verifier.verify(token);                          // thrown Exceptions to be handled in @ExceptionHandler
    }


    public String getUsernameFromToken(String token) {
        return validateAndParseToken(token).getSubject();
    }


    public String getRole(String token) {
        return validateAndParseToken(token).getClaim(ROLE).asString();
    }


    public Instant getTokenExpiration(String token) {
        DecodedJWT jwt = JWT.decode(token);
        return jwt.getExpiresAt().toInstant();
    }


    private Algorithm buildJwtAlgorithm(byte[] publicKeyBytes, byte[] privateKeyBytes) throws NoSuchAlgorithmException,
            InvalidKeySpecException {
        KeyFactory kf = KeyFactory.getInstance("RSA");
        EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(publicKeyBytes);
        PublicKey publicKey = kf.generatePublic(publicKeySpec);
        PrivateKey privateKey = kf.generatePrivate(new PKCS8EncodedKeySpec(privateKeyBytes));
        return Algorithm.RSA256((RSAPublicKey) publicKey, (RSAPrivateKey) privateKey);
    }


    public String generateToken(UserDetails userDetails) throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
        String role = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority).toList().getFirst();   //  UserDetails has only 1 role
        Date issuedDate = new Date();                                       //  token creation datetime
        Date expiredDate = new Date(issuedDate.getTime() + jwtLifetime);    //  token expiry datetime

        return JWT.create()
                .withClaim(ROLE, role)                           //  user role
                .withSubject(userDetails.getUsername())                     //  username
                .withIssuedAt(issuedDate)                                   //  creation datetime
                .withExpiresAt(expiredDate)                                 //  expiry datetime
                .sign(buildJwtAlgorithm(getPublicKey(), getPrivateKey()));  //  signature
    }


    private byte[] getPrivateKey() throws IOException {
        return Files.readAllBytes(keysPath.resolve("private.key"));
    }


    private byte[] getPublicKey() throws IOException {
        return Files.readAllBytes(keysPath.resolve("public.key"));
    }
}

