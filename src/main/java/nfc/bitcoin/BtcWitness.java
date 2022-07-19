package nfc.bitcoin;

import org.bitcoinj.core.ECKey;
import org.bitcoinj.core.Sha256Hash;
import util.Base58;
import util.Utils;

public class BtcWitness {
    String base58PrivateKey;
    String sigHash;
    private ECKey ecKey;
    private String signatureHex;

    public BtcWitness(String base58PrivateKey, String sigHash) {
        this.base58PrivateKey = base58PrivateKey;
        this.sigHash = sigHash;
        setKeyPair();

        signing();
    }

    private void setKeyPair() {
        byte[] key = Base58.decode(base58PrivateKey);
        String hexWif = Utils.byteArrayToHex(key);
        String privateKey = hexWif.substring(2, 66);

        ecKey = ECKey.fromPrivate(Utils.hexStringToByteArray(privateKey));
    }

    private void signing() {
        ECKey.ECDSASignature signature = ecKey.sign(Sha256Hash.wrap(sigHash));
        signatureHex = Utils.byteArrayToBinaryString(signature.encodeToDER());
    }

    private String getSignatureHex() {
        return signatureHex + "01";
    }

    public String getWitness() {
        String sig = getSignatureHex();
        StringBuilder witnessBuilder = new StringBuilder();
        witnessBuilder.append("02");
        witnessBuilder.append(Integer.toHexString(Utils.hexStringToByteArray(sig).length));
        String pubKey = ecKey.getPublicKeyAsHex();
        witnessBuilder.append(Integer.toHexString(Utils.hexStringToByteArray(pubKey).length));
        witnessBuilder.append(pubKey);

        return witnessBuilder.toString();

    }

}
