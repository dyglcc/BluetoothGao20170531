/*
 * Copyright 2019 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.display.google_auth;

import com.google.common.io.BaseEncoding;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * Unit tests for {@link CryptoUtils}.
 */
@RunWith(JUnit4.class)
public class CryptoUtilsTest {

    @Test
    public void testDigestSha256() {
        // Expected values obtained via "openssl dgst" command-line tool.
        assertArrayEquals(CryptoUtils.digest(CryptoUtils.DIGEST_SHA_256, Utilities.getAsciiBytes(
                "test")),
                BaseEncoding.base16().decode(
                        "9F86D081884C7D659A2FEAA0C55AD015A3BF4F1B2B0B822CD15D6C15B0F00A08"));
        assertArrayEquals(
                CryptoUtils.digest(
                        CryptoUtils.DIGEST_SHA_256,
                        Utilities.getAsciiBytes("A long an winding road led nowhere")),
                BaseEncoding.base16().decode(
                        "1557A27B56A681B06995F25173275358618BADA2306A1D384ED3AF7BB534ABC2"));
    }

    @Test
    public void testDigestSha512() {
        // Expected values obtained via "openssl dgst" command-line tool.
        assertArrayEquals(CryptoUtils.digest(CryptoUtils.DIGEST_SHA_512, Utilities.getAsciiBytes(
                "test")),
                BaseEncoding.base16().decode(
                        "EE26B0DD4AF7E749AA1A8EE3C10AE9923F618980772E473F8819A5D4940E0DB27AC1"
                                + "85F8A0E1D5F84F88BC887FD67B143732C304CC5FA9AD8E6F57F50028A8FF"));
        assertArrayEquals(
                CryptoUtils.digest(
                        CryptoUtils.DIGEST_SHA_512,
                        Utilities.getAsciiBytes("A long an winding road led nowhere")),
                BaseEncoding.base16().decode(
                        "EABC9886A00F391ED7566BFC25C023394850D698C0318C6F066A0AA4722B54EE8E66"
                                + "4DC50438A29C1157EE327C262F80AB019C33A866B9E14B5C51E309B69ACF"));
    }

    @Test
    public void testGenerateAndVerifyHmacSha256() {
        // Expected values obtained via "openssl dgst -hmac <key ASCII>" command-line tool.
        SecretKey key =
                new SecretKeySpec(Utilities.getAsciiBytes("very secret key"),
                        CryptoUtils.HMAC_SHA_256);
        checkMacGenerationAndVerification(
                CryptoUtils.HMAC_SHA_256,
                key,
                Utilities.getAsciiBytes("test"),
                BaseEncoding.base16().decode(
                        "3CE0018B7377335BCCD9201A98FA14D95F06C52BFD8D5417249B59BA42EAC37A"));
        checkMacGenerationAndVerification(
                CryptoUtils.HMAC_SHA_256,
                key,
                Utilities.getAsciiBytes("A long an winding road led nowhere"),
                BaseEncoding.base16().decode(
                        "652A95EB2DD10AD8C3627A053DD0A1C0B8BF2529FBE50F6C3556C237681DBC99"));

        // Check that wrong MACs fail verification
        // Last byte missing
        assertFalse(
                CryptoUtils.verifyMac(
                        CryptoUtils.HMAC_SHA_256,
                        key,
                        Utilities.getAsciiBytes("test"),
                        BaseEncoding.base16().decode(
                                "3CE0018B7377335BCCD9201A98FA14D95F06C52BFD8D5417249B59BA42EAC3")));
        // One extra byte at the end
        assertFalse(
                CryptoUtils.verifyMac(
                        CryptoUtils.HMAC_SHA_256,
                        key,
                        Utilities.getAsciiBytes("test"),
                        BaseEncoding.base16().decode(
                                "3CE0018B7377335BCCD9201A98FA14D95F06C52BFD8D5417249B59BA42EAC37A7A")));
        // One bit flipped in the third byte
        assertFalse(
                CryptoUtils.verifyMac(
                        CryptoUtils.HMAC_SHA_256,
                        key,
                        Utilities.getAsciiBytes("test"),
                        BaseEncoding.base16().decode(
                                "3CE0118B7377335BCCD9201A98FA14D95F06C52BFD8D5417249B59BA42EAC37A"
                        )));
    }

    @Test
    public void testDigestWithUnknownAlgorithm() {
        try {
            CryptoUtils.digest("weird", Utilities.getAsciiBytes("test"));
            fail();
        } catch (IllegalArgumentException expected) {
        }
    }

    @Test
    public void testGenerateMacWithUnknownAlgorithm() {
        SecretKeySpec key = new SecretKeySpec(new byte[16], "test");
        try {
            CryptoUtils.generateMac("weird", key, new byte[10]);
            fail();
        } catch (IllegalArgumentException expected) {
        }
    }

    @Test
    public void testVerifyMacWithUnknownAlgorithm() {
        SecretKeySpec key = new SecretKeySpec(new byte[16], "test");
        try {
            CryptoUtils.verifyMac("weird", key, new byte[10], new byte[12]);
            fail();
        } catch (IllegalArgumentException expected) {
        }
    }

    private void checkMacGenerationAndVerification(
            String algorithm, SecretKey key, byte[] data, byte[] expectedMac) {
        assertArrayEquals(CryptoUtils.generateMac(algorithm, key, data), expectedMac);
        assertTrue(CryptoUtils.verifyMac(algorithm, key, data, expectedMac));
    }
}
