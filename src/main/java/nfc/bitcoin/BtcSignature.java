package nfc.bitcoin;

import org.bitcoinj.core.ECKey;
import org.bitcoinj.core.Sha256Hash;
import util.Base58;
import util.Utils;

public class BtcSignature {
    String base58PrivateKey;
    String sigHashPreImage;
    private ECKey ecKey;
    private String signatureHex;

    public BtcSignature(String base58PrivateKey, String sigHashPreImage) {
        this.base58PrivateKey = base58PrivateKey;
        this.sigHashPreImage = sigHashPreImage;
        setKeyPair(base58PrivateKey);
        signing();
    }



    public String getPubKey() {
        return ecKey.getPublicKeyAsHex();
    }

    private void setKeyPair(String base58PrivateKey) {
        byte[] key = Base58.decode(base58PrivateKey);
        String hexWif = Utils.byteArrayToHex(key);
        String privateKey = hexWif.substring(2, 66);

        ecKey = ECKey.fromPrivate(Utils.hexStringToByteArray(privateKey));
    }


    private void signing() {
        Sha256Hash sigHash = Sha256Hash.wrap(Utils.hexStringToByteArray(sigHashPreImage));
        ECKey.ECDSASignature signature = ecKey.sign(sigHash);

        signatureHex = Utils.byteArrayToHex(signature.encodeToDER());
    }

    public String getSignatureHex() {
        return signatureHex;
    }

}
