package br.com.petshop.security;

import javax.crypto.Cipher;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.GeneralSecurityException;
import java.security.SecureRandom;
import java.util.Base64;

/**
 * Criptografia AES-256-GCM. A chave NUNCA fica no código:
 *  1) variável de ambiente PETSHOP_KEY (Base64 de 32 bytes), ou
 *  2) arquivo config/chave.key (ignorado pelo Git). Se não existir, é gerado no 1º uso.
 */
public final class CryptoUtil {

    private static final SecureRandom RNG = new SecureRandom();
    private static SecretKeySpec chave;

    private CryptoUtil() {}

    private static synchronized SecretKeySpec chave() {
        if (chave == null) {
            try {
                String b64 = System.getenv("PETSHOP_KEY");
                Path arq = Paths.get("config", "chave.key");
                if (b64 == null || b64.isBlank()) {
                    if (Files.exists(arq)) {
                        b64 = Files.readString(arq).trim();
                    } else {
                        byte[] nova = new byte[32];
                        RNG.nextBytes(nova);
                        b64 = Base64.getEncoder().encodeToString(nova);
                        Files.createDirectories(arq.getParent());
                        Files.writeString(arq, b64);
                        System.out.println("[PETSHOP] Nova chave gerada em " + arq.toAbsolutePath()
                                + " - FACA BACKUP! Sem ela os dados nao podem ser recuperados.");
                    }
                }
                byte[] bytes = Base64.getDecoder().decode(b64.trim());
                if (bytes.length != 32) {
                    throw new IllegalStateException("A chave deve ter 32 bytes em Base64.");
                }
                chave = new SecretKeySpec(bytes, "AES");
            } catch (IOException e) {
                throw new IllegalStateException("Nao foi possivel carregar a chave de criptografia", e);
            }
        }
        return chave;
    }

    public static String criptografar(String texto) {
        if (texto == null) return null;
        try {
            byte[] iv = new byte[12];
            RNG.nextBytes(iv);
            Cipher c = Cipher.getInstance("AES/GCM/NoPadding");
            c.init(Cipher.ENCRYPT_MODE, chave(), new GCMParameterSpec(128, iv));
            byte[] ct = c.doFinal(texto.getBytes(StandardCharsets.UTF_8));
            ByteBuffer bb = ByteBuffer.allocate(iv.length + ct.length);
            bb.put(iv).put(ct);
            return Base64.getEncoder().encodeToString(bb.array());
        } catch (GeneralSecurityException e) {
            throw new IllegalStateException("Falha ao criptografar", e);
        }
    }

    public static String descriptografar(String b64) {
        if (b64 == null) return null;
        try {
            byte[] tudo = Base64.getDecoder().decode(b64);
            Cipher c = Cipher.getInstance("AES/GCM/NoPadding");
            c.init(Cipher.DECRYPT_MODE, chave(), new GCMParameterSpec(128, tudo, 0, 12));
            return new String(c.doFinal(tudo, 12, tudo.length - 12), StandardCharsets.UTF_8);
        } catch (GeneralSecurityException e) {
            throw new IllegalStateException("Falha ao descriptografar (chave incorreta?)", e);
        }
    }
}
