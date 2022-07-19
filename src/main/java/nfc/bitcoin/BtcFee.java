package nfc.bitcoin;

import util.Utils;

public class BtcFee {
    int baseSize = 0;
    int totalSize = 0;
    double weight;

    String unsignedTransaction;
    int vSize;

    int txInCnt = 0;
    int txOutCnt = 0;

    public BtcFee(int txInCnt, int txOutCnt) {
        this.txInCnt = txInCnt;
        this.txOutCnt = txOutCnt;
        setBaseSize();
        setTotalSize();
        setWeight();
        setVSize();
    }

    //    base size
//    version 4bytes
//    txInCnt 1bytes
//    txIn 41*txInCnt bytes
//    txoutCnt 1bytes
//    txOut 31*txoutCnt bytes
//    locktime 4bytes
    private void setBaseSize() {
        baseSize = 4 + 1 + 41 * txInCnt + 1 + 31 * txOutCnt + 4;


    }

    //    total size
//    version 4bytes
//    flag 2byte
//    txInCnt 1bytes
//    txIn 41*txInCnt bytes
//    txoutCnt 1bytes
//    txOut 31*txoutCnt bytes
//    witness 107~108 *txInCnt bytes
//    locktime 4bytes

    private void setTotalSize() {
        totalSize = 2 + baseSize + 107 * txInCnt;

    }

    // weight = base size * 3 + total size
    private void setWeight() {
        weight = baseSize * 3 + totalSize;
    }

    //    vsize = weight/4
    private void setVSize() {
        vSize = (int) weight / 4;
    }

    public int getBaseSize() {
        return baseSize;
    }

    public int getTotalSize() {
        return totalSize;
    }

    public double getWeight() {
        return weight;
    }

    public int getvSize() {
        return vSize;
    }

    //    fee = vsize*satoshi/bytes
    public double getFee(int satoshiPerByte) {

        return Utils.convertSatoshiToBtc(getvSize() * satoshiPerByte);
    }

}
