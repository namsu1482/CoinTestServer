package nfc.bitcoin;


import coin.Coin;
import coin.RawTransaction;
import network.api.CoinData;
import network.api.SendingTransaction;
import org.bouncycastle.util.encoders.Hex;
import org.json.JSONObject;
import util.Bech32;
import util.Utils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Map;

public class RawTransactionBuilder {
    private static final String TAG = RawTransactionBuilder.class.getSimpleName();
    String unSignedRawTransaction;
    ArrayList<PrevTx> prevTxList = new ArrayList<>();
    ArrayList<TxOutPut> ouputList = new ArrayList<>();
    String version;
    String lockTime;

    String toCoinAddress;
    Coin fromCoin;
    double sendAmt;

    ArrayList<String> sigHashList = new ArrayList<>();

    public RawTransactionBuilder(String toCoinAddress, Coin fromCoin, double sendAmt) {
        this.toCoinAddress = toCoinAddress;
        this.fromCoin = fromCoin;
        this.sendAmt = sendAmt;

        version = Hex.toHexString(Utils.convertToLittleEndian(2), 0, 4);
        lockTime = "00000000";
        buildRawTransaction();
    }

    private String buildInputPart(ArrayList<RawTransaction> rawList) {
        StringBuilder stringBuilder = new StringBuilder();

        String inputCnt = Integer.toHexString(rawList.size());
        if (String.valueOf(rawList.size()).length() % 2 == 1) {
            inputCnt = "0" + inputCnt;
        }
        stringBuilder.append(inputCnt);
        for (RawTransaction rawTransaction : rawList) {
            //hash
            byte[] reversedHex = Utils.hexStringToByteArray(rawTransaction.getTxId());
            Utils.reverse(reversedHex);
            String prevTxHash = Utils.byteArrayToHex(reversedHex);
            stringBuilder.append(prevTxHash);
            //index
            rawTransaction.getvOut();
            byte[] indexEndian = Utils.convertToLittleEndian(rawTransaction.getvOut());
            String indexString = Utils.byteArrayToHex(indexEndian);
            stringBuilder.append(indexString);
            String scriptLength = "00";
            stringBuilder.append(scriptLength);
            String sequence = "ffffffff";
            stringBuilder.append(sequence);
            String amtHex = Utils.getEndianValHex(BigDecimal.valueOf(Utils.convertBtcToSatoshi(rawTransaction.getAmount())).intValue());
            PrevTx prevTx = new PrevTx(prevTxHash, indexString, scriptLength, sequence, amtHex, rawTransaction.getScriptKey());
            prevTxList.add(prevTx);
        }

        System.out.println(String.format("TxinPut    : %s", stringBuilder.toString()));

        return stringBuilder.toString();
    }

    private void buildRawTransaction() {
        StringBuilder stringBuilder = new StringBuilder();

        stringBuilder.append(version);
        stringBuilder.append("00");
        stringBuilder.append("01");

//        fromCoin.parseBtcRawTransaction(NetworkHelper.GetBtcRawTransaction(fromCoin));
        JSONObject result = CoinData.GetBtcRawTransaction(fromCoin);
        fromCoin.parseBtcRawTransaction(result);
        Map<String, Object> paramMap = SendingTransaction.getTxParams(sendAmt, fromCoin);
        ArrayList<RawTransaction> rawList = (ArrayList<RawTransaction>) paramMap.get("input");
        double exchange = (double) paramMap.get("exchange");

        String inputData = buildInputPart(rawList);
        stringBuilder.append(inputData);

        String outputData = buildOutput(toCoinAddress, sendAmt, fromCoin, exchange);
        stringBuilder.append(outputData);

        unSignedRawTransaction = stringBuilder.toString();
    }


    public String getBtcSignedRawTransaction(String witnesses) {        // input
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(unSignedRawTransaction);

        stringBuilder.append(witnesses);
        stringBuilder.append(lockTime);

        return stringBuilder.toString();
    }

    private String buildOutput(String toAddress, double sendAmt, Coin fromCoin, double exchange) {
        // output Cnt,잔액 반환 object 생성 필요
        StringBuilder stringBuilder = new StringBuilder();
        int outputCnt = 0;

        if (exchange > 0) {
            outputCnt = 2;

        } else {
            outputCnt = 1;
        }

        String outCnt = "0" + outputCnt;
        stringBuilder.append(outCnt);

        String witnessVersion = "00";
        String toScriptKey = Bech32.getScriptPubKeyFromBech32Address(toAddress);
        toScriptKey = witnessVersion + Integer.toHexString(Utils.hexStringToByteArray(toScriptKey).length) + toScriptKey;
        System.out.println("toScriptKey : "+toScriptKey);

        String fromScriptKey = Bech32.getScriptPubKeyFromBech32Address(fromCoin.getCoinAddress());
        fromScriptKey = witnessVersion + Integer.toHexString(Utils.hexStringToByteArray(fromScriptKey).length) + fromScriptKey;
        System.out.println("fromScriptKey : "+fromScriptKey);

        //satoshi value 8byte length little endian
        int sendVal = BigDecimal.valueOf(Utils.convertBtcToSatoshi(sendAmt)).intValue();
        int exchangeVal = BigDecimal.valueOf(Utils.convertBtcToSatoshi(exchange)).intValue();

        String endianSendAmt = Utils.getEndianValHex(sendVal);
        String endianExchangeAmt = Utils.getEndianValHex(exchangeVal);

        // toOutput
        stringBuilder.append(endianSendAmt);
        stringBuilder.append(Integer.toHexString(Utils.hexStringToByteArray(toScriptKey).length));
        stringBuilder.append(toScriptKey);

        //exchange Output
        stringBuilder.append(endianExchangeAmt);
        stringBuilder.append(Integer.toHexString(Utils.hexStringToByteArray(fromScriptKey).length));
        stringBuilder.append(fromScriptKey);

        TxOutPut toOutPut = new TxOutPut(endianSendAmt, toScriptKey);
        TxOutPut exchangeOutput = new TxOutPut(endianExchangeAmt, fromScriptKey);
        ouputList.add(toOutPut);
        ouputList.add(exchangeOutput);

        System.out.println(String.format("TxOutPut    : %s", stringBuilder.toString()));


//
//        String hashType = "01000000";
//        stringBuilder.append(hashType);

        return stringBuilder.toString();
    }

    private String buildOutputR(String toAddress, double sendAmt, Coin fromCoin, double exchange) {
        // output Cnt,잔액 반환 object 생성 필요
        StringBuilder stringBuilder = new StringBuilder();
        int outputCnt = 0;

        if (exchange > 0) {
            outputCnt = 2;

        } else {
            outputCnt = 1;
        }

        String outCnt = "0" + outputCnt;
        stringBuilder.append(outCnt);

        String witnessVersion = "00";
        String toScriptKey = Bech32.getScriptPubKeyFromBech32Address(toAddress);
        toScriptKey = witnessVersion + Integer.toHexString(Utils.hexStringToByteArray(toScriptKey).length) + toScriptKey;
        System.out.println("toScriptKey : "+toScriptKey);

        String fromScriptKey = Bech32.getScriptPubKeyFromBech32Address(fromCoin.getCoinAddress());
        fromScriptKey = witnessVersion + Integer.toHexString(Utils.hexStringToByteArray(fromScriptKey).length) + fromScriptKey;
        System.out.println("fromScriptKey : "+fromScriptKey);

        //satoshi value 8byte length little endian
        int sendVal = BigDecimal.valueOf(Utils.convertBtcToSatoshi(sendAmt)).intValue();
        int exchangeVal = BigDecimal.valueOf(Utils.convertBtcToSatoshi(exchange)).intValue();

        String endianSendAmt = Utils.getEndianValHex(sendVal);
        String endianExchangeAmt = Utils.getEndianValHex(exchangeVal);

        // toOutput
        stringBuilder.append(endianSendAmt);
        stringBuilder.append(Integer.toHexString(Utils.hexStringToByteArray(toScriptKey).length));
        stringBuilder.append(toScriptKey);

        //exchange Output
        stringBuilder.append(endianExchangeAmt);
        stringBuilder.append(Integer.toHexString(Utils.hexStringToByteArray(fromScriptKey).length));
        stringBuilder.append(fromScriptKey);

        TxOutPut toOutPut = new TxOutPut(endianSendAmt, toScriptKey);
        TxOutPut exchangeOutput = new TxOutPut(endianExchangeAmt, fromScriptKey);
        ouputList.add(toOutPut);
        ouputList.add(exchangeOutput);

        System.out.println(String.format("TxOutPut    : %s", stringBuilder.toString()));


//
//        String hashType = "01000000";
//        stringBuilder.append(hashType);

        return stringBuilder.toString();
    }

    public String unSignedRawTransaction() {
        return unSignedRawTransaction;
    }


    public ArrayList<String> getSigHashList() {
        BtcSigHash btcSigHash = new BtcSigHash(version, prevTxList, ouputList, lockTime);
        for (int i = 0; i < prevTxList.size(); i++) {
            String sigHash = btcSigHash.getBtcSigHash(prevTxList.get(i));
            System.out.println(String.format("tx[%d] Sig Hash : %s", i, sigHash));
            sigHashList.add(sigHash);

        }
        return sigHashList;
    }

    public String getWitness(String signature, String pubKey) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("02");
//        stringBuilder.append("47");
        stringBuilder.append(Integer.toHexString(Utils.hexStringToByteArray(signature).length + 1));
        stringBuilder.append(signature);
        stringBuilder.append("01");
        stringBuilder.append(Integer.toHexString(Utils.hexStringToByteArray(pubKey).length));
        stringBuilder.append(pubKey);


        return stringBuilder.toString();
    }
}
