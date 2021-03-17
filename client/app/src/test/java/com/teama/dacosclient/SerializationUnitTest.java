package com.teama.dacosclient;

import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.security.KeyPair;
import java.security.KeyPairGenerator;

import static org.junit.Assert.assertTrue;

public class SerializationUnitTest {
    @Test
    public void serializeRsaKeyPairTest() throws Exception {

        KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
        KeyPair keyPair = kpg.generateKeyPair();
        ByteArrayOutputStream b = new ByteArrayOutputStream();
        ObjectOutputStream o =  new ObjectOutputStream(b);
        o.writeObject(keyPair);
        byte[] res = b.toByteArray();

        o.close();
        b.close();

        ByteArrayInputStream bi = new ByteArrayInputStream(res);
        ObjectInputStream oi = new ObjectInputStream(bi);
        Object obj = oi.readObject();
        assertTrue(obj instanceof KeyPair);

        oi.close();
        bi.close();
    }
}
