package com.bobo.request;

import cn.hutool.crypto.SecureUtil;
import cn.hutool.crypto.symmetric.AES;
import cn.hutool.crypto.symmetric.SymmetricAlgorithm;

import javax.crypto.SecretKey;

public class EncryptionUtil {

    public static void main(String[] args) {
        String json = "1";
        byte[] key = SecureUtil.generateKey(SymmetricAlgorithm.AES.getValue(),"0CoJUm6Qyw8W8jud".getBytes()).getEncoded();
        AES aes = SecureUtil.aes(key);
        String s1 = aes.encryptBase64(json);
        System.out.println(s1);
        System.out.println(aes.decryptStr(s1));

    }
}
