package org.example.trainingapp.jwt;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;


@Component
public class RsaKeyGenerator {                              //  runs only once to generate an RSA keypair
    private final Path keysPath;

    public RsaKeyGenerator(@Value("${jwt.keys-path:secret}") String keysDir) {
        this.keysPath = Paths.get(keysDir);
    }

    public void generateKeyPair() throws NoSuchAlgorithmException, IOException {
        KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
        generator.initialize(2048);
        KeyPair keyPair = generator.generateKeyPair();
        Files.write(keysPath.resolve("private.key"), keyPair.getPrivate().getEncoded());
        Files.write(keysPath.resolve("public.key"),  keyPair.getPublic().getEncoded());
    }
}
