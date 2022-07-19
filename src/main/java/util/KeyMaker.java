package util;

import org.bitcoinj.core.ECKey;
import org.bouncycastle.jce.ECNamedCurveTable;
import org.bouncycastle.jce.spec.ECParameterSpec;

import java.io.UnsupportedEncodingException;
import java.security.*;

public class KeyMaker {
    public static void GetTimestamp(String info) {

    }/*ww  w.ja va  2s  .  com*/

    public static byte[] GenerateSignature(String plaintext, KeyPair keys)
            throws SignatureException, UnsupportedEncodingException,
            InvalidKeyException, NoSuchAlgorithmException,
            NoSuchProviderException {

        Signature ecdsaSign = Signature
                .getInstance("SHA256withECDSA", "BC");
        ecdsaSign.initSign(keys.getPrivate());
        ecdsaSign.update(Utils.hexStringToByteArray(plaintext));
        byte[] signature = ecdsaSign.sign();
        return signature;
    }

    public static byte[] GenerateSignatureByPrivateKey(String plaintext, ECKey privateKey)
            throws SignatureException, UnsupportedEncodingException,
            InvalidKeyException, NoSuchAlgorithmException,
            NoSuchProviderException {

        Signature ecdsaSign = Signature.getInstance("SHA256withECDSA", "BC");

        ecdsaSign.update(Utils.hexStringToByteArray(plaintext));
        byte[] signature = ecdsaSign.sign();

        return signature;
    }

    public static boolean ValidateSignature(String plaintext, KeyPair pair,
                                            byte[] signature) throws SignatureException,
            InvalidKeyException, UnsupportedEncodingException,
            NoSuchAlgorithmException, NoSuchProviderException {
        Signature ecdsaVerify = Signature.getInstance("SHA256withECDSA",
                "BC");
        ecdsaVerify.initVerify(pair.getPublic());
        ecdsaVerify.update(Utils.hexStringToByteArray(plaintext));
        return ecdsaVerify.verify(signature);
    }

    public static KeyPair GenerateKeys() throws NoSuchAlgorithmException,
            NoSuchProviderException, InvalidAlgorithmParameterException {
        //  Other named curves can be found in http://www.bouncycastle.org/wiki/display/JA1/Supported+Curves+%28ECDSA+and+ECGOST%29
        ECParameterSpec ecSpec = ECNamedCurveTable
                .getParameterSpec("SECP256k1");

        KeyPairGenerator g = KeyPairGenerator.getInstance("ECDSA", "BC");

        g.initialize(ecSpec, new SecureRandom());

        return g.generateKeyPair();
    }
}
