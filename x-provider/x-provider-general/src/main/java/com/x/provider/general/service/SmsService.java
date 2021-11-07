package com.x.provider.general.service;

public interface SmsService {
    void sendVerificationCode(String phoneNumber);
    void validateVerificationCode(String phoneNumber, String sms);
}
