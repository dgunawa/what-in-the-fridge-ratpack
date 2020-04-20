package com.maskapai.security;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.jose4j.jwa.AlgorithmConstraints;
import org.jose4j.jwe.ContentEncryptionAlgorithmIdentifiers;
import org.jose4j.jwe.JsonWebEncryption;
import org.jose4j.jwe.KeyManagementAlgorithmIdentifiers;
import org.jose4j.keys.AesKey;
import org.jose4j.lang.JoseException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.Key;
import java.util.HashMap;
import java.util.Map;

public class Authentication {
    private final static Logger LOGGER = LoggerFactory.getLogger(Authentication.class);

    // in real world this will be using Auth server ie: AD
    Map<String, String> usernamePassword = new HashMap() {{
        put("foo", "bar");
    }};

    Key key = new AesKey("mysecretverylong".getBytes());

    public Boolean verifyUsernamePassword(String username, String password) {
        if (usernamePassword.get(username) != null && usernamePassword.get(username).equals(password)) {
            return true;
        }
        return false;
    }

    public String getToken(String username, String password) {
        String serializedJwe = null;
        try {
            JsonWebEncryption jwe = new JsonWebEncryption();
            jwe.setPayload(new JSONObject().put("username", username).put("password", password).toString());
            jwe.setAlgorithmHeaderValue(KeyManagementAlgorithmIdentifiers.A128KW);
            jwe.setEncryptionMethodHeaderParameter(ContentEncryptionAlgorithmIdentifiers.AES_128_CBC_HMAC_SHA_256);
            jwe.setKey(key);
            serializedJwe = jwe.getCompactSerialization();
        } catch (Exception e){
            LOGGER.error(ExceptionUtils.getStackTrace(e));
        }
        return new JSONObject().put("token", serializedJwe).toString();
    }

    public Boolean verifyToken(String serializedJwe) {
        boolean result = false;
        if (serializedJwe != null) {
            try {
                JsonWebEncryption jwe = new JsonWebEncryption();
                jwe.setAlgorithmConstraints(new AlgorithmConstraints(AlgorithmConstraints.ConstraintType.WHITELIST,
                        KeyManagementAlgorithmIdentifiers.A128KW));
                jwe.setContentEncryptionAlgorithmConstraints(new AlgorithmConstraints(AlgorithmConstraints.ConstraintType.WHITELIST,
                        ContentEncryptionAlgorithmIdentifiers.AES_128_CBC_HMAC_SHA_256));
                jwe.setKey(key);
                jwe.setCompactSerialization(serializedJwe);
                JSONObject payloadJson = new JSONObject(jwe.getPayload());
                if (usernamePassword.get(payloadJson.get("username")).equals(payloadJson.get("password"))) {
                    result = true;
                }
            } catch (JoseException e) {
                result = false;
                LOGGER.error(ExceptionUtils.getStackTrace(e));
            }
        }
        return result;
    }
}
