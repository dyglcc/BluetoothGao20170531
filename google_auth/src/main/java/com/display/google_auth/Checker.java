package com.display.google_auth;


import android.content.Context;

import java.security.GeneralSecurityException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

public class Checker {
    private static final int PIN_LENGTH = 6; // HOTP or TOTP
    private static final int REFLECTIVE_PIN_LENGTH = 9; // ROTP

    private String check(Context context, String secret) {
        TotpClock clock = new TotpClock(context, new SystemWallClock());
        TotpCounter mTotpCounter = new TotpCounter(30);
        long otpState = mTotpCounter.getValueAtTime(Utilities.millisToSeconds(clock.nowMillis()));
        String result = computePin(secret, otpState, null);
        return result;
    }

    public static PasscodeGenerator.Signer getSigningOracle(String secret) {
        try {
            byte[] keyBytes = decodeKey(secret);
            final Mac mac = Mac.getInstance("HMACSHA1");
            mac.init(new SecretKeySpec(keyBytes, ""));

            // Create a signer object out of the standard Java MAC implementation.
            return new PasscodeGenerator.Signer() {
                @Override
                public byte[] sign(byte[] data) {
                    return mac.doFinal(data);
                }
            };
        } catch (NoSuchAlgorithmException
                | InvalidKeyException
                | IllegalArgumentException error) {
        }

        return null;
    }

    private static byte[] decodeKey(String secret) {
        try {
            return Base32String.decode(secret);
        } catch (Base32String.DecodingException e) {
            e.printStackTrace();
            return null;
        }
    }

    private static String computePin(String secret, long otpState, byte[] challenge) {

        try {
            PasscodeGenerator.Signer signer = getSigningOracle(secret);
            PasscodeGenerator pcg = new PasscodeGenerator(signer,
                    (challenge == null) ? PIN_LENGTH : REFLECTIVE_PIN_LENGTH);

            return (challenge == null) ?
                    pcg.generateResponseCode(otpState) :
                    pcg.generateResponseCode(otpState, challenge);
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean verifyCode(String trim, Context context, String secret_key) {
        String code = check(context, secret_key);
        if (code.equals(trim)) {
            return true;
        }
        return false;
    }
}
