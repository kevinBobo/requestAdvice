package com.bobo.request.util;

import cn.hutool.crypto.SecureUtil;
import cn.hutool.crypto.symmetric.AES;
import cn.hutool.crypto.symmetric.SymmetricAlgorithm;

public class SecurityUtils {

    private static byte[] key = SecureUtil.generateKey(SymmetricAlgorithm.AES.getValue(),"0CoJUm6Qyw8W8jud".getBytes()).getEncoded();
    private static AES aes = SecureUtil.aes(key);

    public static String encry(String str){
        return aes.encryptBase64(str);
    }

    public static String decry(String str){
        return aes.decryptStr(str);
    }


}
