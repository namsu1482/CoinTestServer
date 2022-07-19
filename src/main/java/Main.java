import coin.COIN_TYPE;
import coin.Coin;
import network.TcpConnection;
import network.api.SendingTransaction;
import nfc.*;
import nfc.bitcoin.BtcSignature;
import nfc.bitcoin.RawTransactionBuilder;
import org.bitcoinj.core.ECKey;
import org.bitcoinj.core.Sha256Hash;
import org.json.JSONArray;
import org.json.JSONObject;
import util.Base58;
import util.Utils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;


public class Main {
    static TcpConnection tcpConnection = new TcpConnection(9000);

    public static void main(String[] args) {
        watchCmd();
        try {
            tcpConnection.runServer();
        } catch (IOException e) {
            e.printStackTrace();
        }


//        buildRawTransaction();

//        btcRawTransaction();

//        System.out.println(signing());

//        BtcFee btcFee = new BtcFee(1, 2);
//        System.out.println(BigDecimal.valueOf(btcFee.getFee(10)).toPlainString());

//        System.out.println(Base58.encode(Utils.hexStringToByteArray("6D8FB2DFA50120F682CAD427E1D3F7B7389C71144FFA2FC67D3BFAE40980CC79")));

    }

    private static void watchCmd() {
        Scanner scanner = new Scanner(System.in);
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                System.out.println("scanner prepared");
                for (; ; ) {
                    if (scanner.next().equals("Stop")) ;
                    tcpConnection.stopTcpServer();
                    break;
                }

            }
        });
        thread.start();
    }

    private static void ethTransactionByGenKey() {
        Coin coin = new Coin(COIN_TYPE.ETHEREUM, "0xfd0920cc55b95c94aecfdf76295dac5b32005c2d");
        Coin toAddress = new Coin(COIN_TYPE.ETHEREUM, "0x2ed6d98a0544d88e447b2787e98cbf192a206af3");
//        0x2ed6d98a0544d88e447b2787e98cbf192a206af3
        // sign 생성된 주소
//        0xfd0920cc55b95c94aecfdf76295dac5b32005c2d

//        String cmd = keyGenCmd(txCnt, "50000000000", 21000, coin.getCoinAddress().substring(2), "0.00105");
//        System.out.println(encrypt(cmd));

        JSONObject txCnt = SendingTransaction.getEthTransactionCount(coin);
        if (txCnt == null) {
            return;
        }
        String transactionCnt = txCnt.optString("result");


        String txData = ApduCommandHelper.keyGenCmd(transactionCnt,
                "50000000000",
                21000,
                toAddress.getCoinAddress(),
                0.00105);
        String hash = ApduCommandHelper.encrypt(txData);
        JSONObject genKeyResponse = SendingTransaction.ethGenPKey(coin, hash);
        JSONObject response = SendingTransaction.ethSendRawTransactionByGennedKey(txData, hash, genKeyResponse);
        System.out.println(response.toString());
    }


    private static void btcRawTransaction() {
//        Coin fromCoin = new Coin(COIN_TYPE.BITCOIN, "tb1qzexd2460edkcqv9gcv8untpeqwk0za4dny6w28");
        Coin fromCoin = new Coin(COIN_TYPE.BITCOIN, "tb1q7thmmslk3mrce30rx9m04q6zxd7q9dw5wrjc3t");
//        String prvKey = "cQEpCQiMW1dp8QYWMofWjsSrciUGv394pXhpFGNUK1e96UqPUZ6s";
        fromCoin.setFee(0.0002);
//        Coin toCoin = new Coin(COIN_TYPE.BITCOIN, "tb1q7thmmslk3mrce30rx9m04q6zxd7q9dw5wrjc3t");
        Coin toCoin = new Coin(COIN_TYPE.BITCOIN, "tb1qzexd2460edkcqv9gcv8untpeqwk0za4dny6w28");

//        JSONObject result = CoinData.GetBtcRawTransaction(fromCoin);
//        fromCoin.parseBtcRawTransaction(result);
//        SendingTransaction.CreateBtcTransaction(toCoin.getCoinAddress(), 0.00045, fromCoin);

        JSONObject jsonObject = SendingTransaction.sendTransaction(toCoin.getCoinAddress(), 0.001, fromCoin);
        System.out.println(jsonObject.toString());


    }

    private static String signing() {
        Coin fromCoin = new Coin(COIN_TYPE.BITCOIN, "tb1qzexd2460edkcqv9gcv8untpeqwk0za4dny6w28");
//        Coin fromCoin = new Coin(COIN_TYPE.BITCOIN, "tb1q7thmmslk3mrce30rx9m04q6zxd7q9dw5wrjc3t");
//        String prvKey = "cQEpCQiMW1dp8QYWMofWjsSrciUGv394pXhpFGNUK1e96UqPUZ6s";
        fromCoin.setFee(0.0002);
        Coin toCoin = new Coin(COIN_TYPE.BITCOIN, "tb1q46jjmw5q9mfzxsk54qm43vk9yag9p5h487k4hd");
//        Coin toCoin = new Coin(COIN_TYPE.BITCOIN, "tb1qzexd2460edkcqv9gcv8untpeqwk0za4dny6w28");


//        String result = ApduCommandHelper.buildBtcTransaction(toCoin.getCoinAddress(), fromCoin, 0.001);

        RawTransactionBuilder rawTransactionBuilder = new RawTransactionBuilder(toCoin.getCoinAddress(), fromCoin, 0.0002);
        System.out.println("UnSigned Transaction : " + rawTransactionBuilder.unSignedRawTransaction());
        StringBuilder witnessBuilder = new StringBuilder();
        ArrayList<String> sigHashList = rawTransactionBuilder.getSigHashList();
        for (int i = 0; i < sigHashList.size(); i++) {
            String sigHash = sigHashList.get(i);
            System.out.println(String.format("sigHash [%d] : %s", i, sigHash));
            BtcSignature btcSignature = new BtcSignature("cSBurNVyrtWmrRgsasSHk4MNKq76pFMMGm9GtSs3i2owmxkUd2xz", sigHash);
            String sigString = btcSignature.getSignatureHex();
            String pubKey = btcSignature.getPubKey();
            String witness = rawTransactionBuilder.getWitness(sigString, pubKey);
            witnessBuilder.append(witness);
            System.out.println(String.format("witness [%d] : %s ", i, witness));

        }
        String signedRawTransaction = rawTransactionBuilder.getBtcSignedRawTransaction(witnessBuilder.toString());

        JSONArray jsonArray = new JSONArray();
        jsonArray.put(signedRawTransaction);
        JSONObject response = SendingTransaction.SendBtcTransaction(jsonArray);


        return response.toString();
//        encrypt(encrypt("0100000096b827c8483d4e9b96712b6713a7b68d6e8003a781feba36c31143470b4efd3752b0a642eea2fb7ae638c36f6252b6750293dbe574a806984b8e4d8548339a3bef51e1b804cc89d182d279655c3aa89e815b1b309fe287d9b2b55d57b90ec68a010000001976a9141d0f172a0ecb48aee1be1f2687d2963ae33f71a188ac0046c32300000000ffffffff863ef3e1a92afbfdb97f31ad0fc7683ee943e9abcf2501590ff8f6551f47e5e51100000001000000"));

    }


    private static void buildRawTransaction() {
        Coin fromCoin = new Coin(COIN_TYPE.BITCOIN, "tb1qzexd2460edkcqv9gcv8untpeqwk0za4dny6w28");
        fromCoin.setFee(0.0002);
        Coin toCoin = new Coin(COIN_TYPE.BITCOIN, "tb1q46jjmw5q9mfzxsk54qm43vk9yag9p5h487k4hd");
        double sendAmt = 0.001;

        TxDataBuilder txDataBuilder = new TxDataBuilder(toCoin.getCoinAddress(), fromCoin, sendAmt);
        String txDataHash = txDataBuilder.getResult();
        System.out.println(String.format("Pre Image : %s", txDataHash));
        byte[] key = Base58.decode("cSBurNVyrtWmrRgsasSHk4MNKq76pFMMGm9GtSs3i2owmxkUd2xz");
//        byte[] key = Base58.decode("cQEpCQiMW1dp8QYWMofWjsSrciUGv394pXhpFGNUK1e96UqPUZ6s");


        String hexWif = Utils.byteArrayToHex(key);
        String privateKey = hexWif.substring(2, 66);

//        ECKey ecKey = ECKey.fromPrivate(Utils.hexStringToByteArray(privateKey));
        ECKey ecKey = ECKey.fromPrivate(Utils.hexStringToByteArray(privateKey));
        System.out.println("privateKey hex " + privateKey);
        System.out.println("pubKey hex " + ecKey.getPublicKeyAsHex());
        System.out.println("pubKey hex add" + "1976a914" + ecKey.getPublicKeyAsHex() + "88ac");

        Sha256Hash sigHash = Sha256Hash.twiceOf(Utils.hexStringToByteArray(txDataHash));
        ECKey.ECDSASignature signature = ecKey.sign(sigHash);


        StringBuilder stringBuilder = new StringBuilder();
        String sig = Utils.byteArrayToHex(signature.encodeToDER());

        stringBuilder.append("02");
        stringBuilder.append(Integer.toHexString(Utils.hexStringToByteArray(sig).length + 1));
        stringBuilder.append(sig);
        stringBuilder.append("01");
        String pubKey = ecKey.getPublicKeyAsHex();
        stringBuilder.append(Integer.toHexString(Utils.hexStringToByteArray(pubKey).length));
        stringBuilder.append(pubKey);
        stringBuilder.append("00000000");


        StringBuilder rawTx = new StringBuilder();
        String txData = txDataBuilder.getUnSignedTransaction();

//        txData = txData.substring(0, txData.length() - 8);
        rawTx.append(txData);
        rawTx.append(stringBuilder);

        System.out.println(String.format("txData : %s ", txData));

        System.out.println("raw Tx " + rawTx.toString().toLowerCase());

        JSONArray jsonArray = new JSONArray();
        jsonArray.put(rawTx.toString());
        JSONObject response = SendingTransaction.SendBtcTransaction(jsonArray);
        System.out.println(response.toString().toLowerCase());

    }

}


