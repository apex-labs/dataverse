package org.apex.dataverse.utils;

import cn.hutool.core.codec.Base64;
import cn.hutool.crypto.Mode;
import cn.hutool.crypto.Padding;
import cn.hutool.crypto.symmetric.AES;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;

/**
 * @ClassName: OAuthUtil
 * @Author: wwd
 * @TODO:
 * @Date: 2024/3/20 14:47
 */
public class OAuthUtil {

    private static final String KEY_ALGORITHM = "AES";

    /**
     * 前端传递密码解密
     * @param encodeKey
     * @param passwordStr
     * @return
     */
    public static String decryptPassword(String encodeKey,String passwordStr) {
        AES aes = new AES(Mode.CBC, Padding.NoPadding, new SecretKeySpec(encodeKey.getBytes(), KEY_ALGORITHM),
                new IvParameterSpec(encodeKey.getBytes()));
        byte[] result = aes.decrypt(Base64.decode(passwordStr.getBytes(StandardCharsets.UTF_8)));
        String password = new String(result, StandardCharsets.UTF_8);
        return password.trim();
    }

    public static Boolean matchPassword(String password, String encodePassword) {
        PasswordEncoder encoder = new BCryptPasswordEncoder();
        return encoder.matches(password, encodePassword);
    }
}
