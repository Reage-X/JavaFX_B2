package com.example.demo;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

public class PasswordUtil {

    private static final int SALT_LENGTH = 16;


    public static String hashPassword(String password) {
        try {
            // Génération d'un salt aléatoire
            SecureRandom random = new SecureRandom();
            byte[] salt = new byte[SALT_LENGTH];
            random.nextBytes(salt);

            // Hash du mot de passe avec le salt
            String hash = hashWithSalt(password, salt);

            // Retourne: salt:hash (pour stocker les deux ensemble)
            return Base64.getEncoder().encodeToString(salt) + ":" + hash;
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Erreur lors du hachage du mot de passe", e);
        }
    }

    public static boolean verifyPassword(String password, String storedHash) {
        try {
            // Séparer le salt et le hash
            String[] parts = storedHash.split(":");
            if (parts.length != 2) {
                return false;
            }

            byte[] salt = Base64.getDecoder().decode(parts[0]);
            String originalHash = parts[1];

            // Hash le mot de passe fourni avec le même salt
            String hashToVerify = hashWithSalt(password, salt);

            // Compare les hash
            return hashToVerify.equals(originalHash);
        } catch (Exception e) {
            return false;
        }
    }

    private static String hashWithSalt(String password, byte[] salt) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        md.update(salt);
        byte[] hashedPassword = md.digest(password.getBytes());
        return Base64.getEncoder().encodeToString(hashedPassword);
    }
}