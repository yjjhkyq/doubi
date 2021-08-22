package com.x.provider.customer.service;

public interface PasswordEncoderService {
    String encode(String rawPassword, String passwordSalt);

    boolean matches(String rawPassword, String passwordSalt, String encodedPassword);
}
