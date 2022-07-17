package com.x.provider.mc.service;

public interface SmsService {
    void sendVerificationCode(String phoneNumber);
    void validateVerificationCode(String phoneNumber, String sms);
}
