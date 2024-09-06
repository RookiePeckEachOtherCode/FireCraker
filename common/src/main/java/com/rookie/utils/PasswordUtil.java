package com.rookie.utils;

import org.springframework.security.crypto.bcrypt.BCrypt;

public class PasswordUtil {
    public static String encrypt(String password) {
        return BCrypt.hashpw(password, BCrypt.gensalt());
    }

    public static Boolean check(String password, String hash) {
        return BCrypt.checkpw(password, hash);
    }
}
