package com.leesh.devlab;

import org.jasypt.encryption.pbe.PooledPBEStringEncryptor;
import org.junit.jupiter.api.Test;

public class JasyptTest {

    @Test
    public void test() {

        PooledPBEStringEncryptor encryptor = new PooledPBEStringEncryptor();
        encryptor.setPoolSize(4);
        encryptor.setPassword("17314042aa!@");
        encryptor.setAlgorithm("PBEWithMD5AndTripleDES");

        String content = "fZZd1y9wTrTF1yy6Ofx50ITz9TPsg1AuBBndtbltjxo=";
        String encrypted = encryptor.encrypt(content);
        String decrypted = encryptor.decrypt(encrypted);

        System.out.println("ENC = " + encrypted + " || DEC = " + decrypted);
    }

}
