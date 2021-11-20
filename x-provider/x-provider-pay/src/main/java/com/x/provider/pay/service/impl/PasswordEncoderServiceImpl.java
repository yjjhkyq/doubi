package com.x.provider.pay.service.impl;

import cn.hutool.crypto.SecureUtil;
import com.x.provider.pay.service.PasswordEncoderService;
import org.springframework.stereotype.Service;

@Service
public class PasswordEncoderServiceImpl implements PasswordEncoderService {

    public String encode(String rawPassword, String passwordSalt){
        return SecureUtil.md5(rawPassword + passwordSalt);
    }

    public boolean matches(String rawPassword, String passwordSalt, String encodedPassword){
        return encode(rawPassword, passwordSalt).equals(encodedPassword);
    }
}
