package nfc;

import coin.Coin;
import coin.RawTransaction;
import network.api.CoinData;
import network.api.SendingTransaction;
import nfc.bitcoin.PrevTx;
import org.bitcoinj.core.Sha256Hash;
import org.bouncycastle.util.encoders.Hex;
import org.json.JSONObject;
import util.Bech32;
import util.Utils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Map;

public class TxDataBuilder {
    static String outputPoint = "";
    static String pubHash = "";
    static String txInAmt = "";

    String result = "";
    double sendAmt = 0;

    StringBuilder txData;
    StringBuilder unSignedTransaction = new StringBuilder();

    ArrayList<PrevTx> prevTxList = new ArrayList<>();

    public TxDataBuilder(String toCoinAddress, Coin fromCoin, double sendAmt) {
        this.sendAmt = sendAmt;
        txData = new StringBuilder();

        result = buildTxHash(toCoinAddress, fromCoin, sendAmt);

    }

    public String getResult() {
        return result;
    }

    public String getUnSignedTransaction() {
        return unSignedTransaction.toString();
    }

    private String buildHashInputPart(ArrayList<RawTransaction> rawList) {
        StringBuilder stringBuilder = new StringBuilder();
        double txAmt = 0;
        for (RawTransaction rawTransaction : rawList) {
            //hash
            byte[] reversedHex = Utils.hexStringToByteArray(rawTransaction.getTxId());
            Utils.reverse(reversedHex);
            String prevTxHash = Utils.byteArrayToHex(reversedHex);
            stringBuilder.append(prevTxHash);

            String indexString = getEndianValHex4(rawTransaction.getvOut());
            stringBuilder.append(indexString);
            System.out.println(indexString);
            if (rawList.size() == 1) {
                outputPoint = Utils.byteArrayToHex(reversedHex) + indexString;
                pubHash = "1976a914" + rawTransaction.getScriptKey().substring(4) + "88ac";

            }
            outputPoint = Utils.byteArrayToHex(reversedHex) + indexString;
            txAmt = txAmt + BigDecimal.valueOf(rawTransaction.getAmount()).doubleValue();
//            PrevTx prevTx = new PrevTx(prevTxHash, indexString, "00", "ffffffff", StringtxAmt, rawTransaction.getScriptKey());
//            prevTxList.add(prevTx);
        }

        txInAmt = getEndianValHex(BigDecimal.valueOf(Utils.convertBtcToSatoshi(txAmt)).intValue());
        return stringBuilder.toString();
    }

    private String bulidHashSequence(ArrayList<RawTransaction> rawList) {
        StringBuilder stringBuilder = new StringBuilder();
        for (RawTransaction rawTransaction : rawList) {
            stringBuilder.append("ffffffff");
        }
        return stringBuilder.toString();
    }


    private String buildHashOutput(String toAddress, double sendAmt, Coin fromCoin, double exchange) {
        // output Cnt,잔액 반환 object 생성 필요
        StringBuilder stringBuilder = new StringBuilder();

        JSONObject toAddressResponse = SendingTransaction.getAddressScriptKey(toAddress);
        String toScriptKey = toAddressResponse.optJSONObject("result").optString("scriptPubKey");
        String toScriptPubHash = toAddressResponse.optJSONObject("result").optString("witness_program");

        JSONObject fromAddressResponse = SendingTransaction.getAddressScriptKey(fromCoin.getCoinAddress());
        String fromScriptKey = fromAddressResponse.optJSONObject("result").optString("scriptPubKey");
//        pubHash = fromAddressResponse.optJSONObject("result").optString("witness_program");
//        String froAddressPubHash = pubHash;


        int sendVal = BigDecimal.valueOf(Utils.convertBtcToSatoshi(sendAmt)).intValue();
        int exchangeVal = BigDecimal.valueOf(Utils.convertBtcToSatoshi(exchange)).intValue();

        String endianSendAmt = getEndianValHex(sendVal);
        String endianExchangeAmt = getEndianValHex(exchangeVal);

        // toOutput
        stringBuilder.append(endianSendAmt);
        stringBuilder.append(Integer.toHexString(Utils.hexStringToByteArray(toScriptKey).length));
        stringBuilder.append(toScriptKey);
//        stringBuilder.append(getScriptCode(toScriptPubHash));
        //exchange Output

        stringBuilder.append(endianExchangeAmt);
        stringBuilder.append(Integer.toHexString(Utils.hexStringToByteArray(fromScriptKey).length));
        stringBuilder.append(fromScriptKey);


//        stringBuilder.append(getScriptCode(froAddressPubHash));

//        String suffix = "00000000";
//        stringBuilder.append(suffix);


        return stringBuilder.toString();
    }


    private String buildTxHash(String toAddress, Coin fromCoin, double sendAmt) {
        StringBuilder stringBuilder = new StringBuilder();
        String version = Hex.toHexString(Utils.convertToLittleEndian(2), 0, 4);
        stringBuilder.append(version);

        unSignedTransaction.append(version);
        unSignedTransaction.append("00");
        unSignedTransaction.append("01");

        fromCoin.parseBtcRawTransaction(CoinData.GetBtcRawTransaction(fromCoin));

        Map<String, Object> paramMap = SendingTransaction.getTxParams(sendAmt, fromCoin);
        ArrayList<RawTransaction> rawList = (ArrayList<RawTransaction>) paramMap.get("input");
        double exchange = (double) paramMap.get("exchange");

        String inputData = buildInputPart(rawList);
        unSignedTransaction.append(inputData);

        String outputData = buildOutput(toAddress, sendAmt, fromCoin, exchange);
        unSignedTransaction.append(outputData);

        String hashPrevOuts = buildHashInputPart(rawList);
        String prevOutHash = hashBuild(hashPrevOuts);
        stringBuilder.append(prevOutHash);

        String hashSequence = bulidHashSequence(rawList);
        String sequenceHash = hashBuild(bulidHashSequence(rawList));

        stringBuilder.append(sequenceHash);

        stringBuilder.append(outputPoint);

        String hashOutputs = buildHashOutput(toAddress, sendAmt, fromCoin, exchange);
        String outputHash = hashBuild(hashOutputs);


        String scriptCode = pubHash;
        stringBuilder.append(scriptCode);

        stringBuilder.append(txInAmt);

        String nSequence = "ffffffff";
        stringBuilder.append(nSequence);

        stringBuilder.append(outputHash);

        String lockTime = "00000000";
        stringBuilder.append(lockTime);

        String nHashType = "01000000";
        stringBuilder.append(nHashType);

        preHashVal(version, hashPrevOuts, hashSequence, outputPoint, scriptCode, nSequence, hashOutputs);
        dHashVal(version, prevOutHash, sequenceHash, outputPoint, scriptCode, nSequence, outputHash, stringBuilder.toString());

        return stringBuilder.toString();
    }

    private String getEndianValHex(int val) {
        byte[] exchangeBytes = new byte[8];
        byte[] endianExchangeBytes = Utils.convertToLittleEndian(val);
        System.arraycopy(endianExchangeBytes, 0, exchangeBytes, 0, endianExchangeBytes.length);

        String endianExchangeAmt = Utils.byteArrayToHex(exchangeBytes);
        return endianExchangeAmt;
    }

    private String getEndianValHex4(int val) {
        byte[] exchangeBytes = new byte[4];
        byte[] endianExchangeBytes = Utils.convertToLittleEndian(val);
        System.arraycopy(endianExchangeBytes, 0, exchangeBytes, 0, endianExchangeBytes.length);

        String endianExchangeAmt = Utils.byteArrayToHex(exchangeBytes);
        return endianExchangeAmt;
    }


    private String hashBuild(String valueHex) {
        String val = Utils.byteArrayToHex(Sha256Hash.hashTwice(Utils.hexStringToByteArray(valueHex)));
        return val;
    }

    private String getScriptCode(String pubHashKey) {
        StringBuilder stringBuilder = new StringBuilder();
//        stringBuilder.append("1976a914");
        stringBuilder.append("0014");
        stringBuilder.append(pubHashKey);
//        stringBuilder.append("88ac");
        return stringBuilder.toString();

    }


    private void preHashVal(String version, String hashPrevOuts, String hashSequence, String outputPoint, String scriptCode, String nSequence, String hashOutputs) {
        System.out.println("PRE IMAGES ---------------------------------------------------------------------------------------------------------------");
        System.out.println(String.format("nVersion      : %s", version));
        System.out.println(String.format("hashPrevOut   : %s ", hashPrevOuts));
        System.out.println(String.format("hashSequence  : %s", hashSequence));
        System.out.println(String.format("outputPoint   : %s ", outputPoint));
        System.out.println(String.format("scriptCode    : %s ", scriptCode));
        System.out.println(String.format("txInAmt       : %s", txInAmt));
        System.out.println(String.format("nSequence     : %s", nSequence));
        System.out.println(String.format("hashOutputs   : %s", hashOutputs));
        System.out.println(String.format("lockTime      : %s ", "00000000"));
        System.out.println(String.format("nHashType     : %s ", "01000000"));
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(version)
                .append(hashPrevOuts)
                .append(hashSequence)
                .append(outputPoint)
                .append(scriptCode)
                .append(nSequence)
                .append(hashOutputs);

        System.out.println(String.format("Pre Image : %s", stringBuilder.toString()));

    }

    private void dHashVal(String version, String dHashPrevOut, String dHashSequence, String outputPoint, String scriptCode, String nSequence, String hashOutputs, String hashPreImage) {
        System.out.println("Double SHA 256 -------------------------------------------------------------------------------------------------------------");
        System.out.println(String.format("nVersion      : %s", version));
        System.out.println(String.format("DhashPrevOut  : %s ", dHashPrevOut));
        System.out.println(String.format("DhashSequence : %s", dHashSequence));
        System.out.println(String.format("outputPoint   : %s ", outputPoint));
        System.out.println(String.format("scriptCode    : %s ", scriptCode));
        System.out.println(String.format("txInAmt       : %s", txInAmt));
        System.out.println(String.format("nSequence     : %s", nSequence));
        System.out.println(String.format("DhashOutputs  : %s", hashOutputs));
        System.out.println(String.format("lockTime      : %s ", "00000000"));
        System.out.println(String.format("nHashType     : %s ", "01000000"));

        System.out.println(String.format("Hash Pre Image : %s", hashPreImage));

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
            stringBuilder.append(Utils.byteArrayToHex(reversedHex));
            //index
            rawTransaction.getvOut();
            byte[] indexEndian = Utils.convertToLittleEndian(rawTransaction.getvOut());
            String indexString = Utils.byteArrayToHex(indexEndian);
            stringBuilder.append(indexString);
            String scriptLength = "00";
            stringBuilder.append(scriptLength);
            String suffix = "ffffffff";
            stringBuilder.append(suffix);
        }

        return stringBuilder.toString();
    }

    public String buildBtcTransaction(String toAddress, Coin fromCoin, double sendAmt) {        // input
        StringBuilder stringBuilder = new StringBuilder();
        String version = Hex.toHexString(Utils.convertToLittleEndian(2), 0, 4);
        stringBuilder.append(version);
        stringBuilder.append("00");
        stringBuilder.append("01");

        fromCoin.parseBtcRawTransaction(CoinData.GetBtcRawTransaction(fromCoin));

        Map<String, Object> paramMap = SendingTransaction.getTxParams(sendAmt, fromCoin);
        ArrayList<RawTransaction> rawList = (ArrayList<RawTransaction>) paramMap.get("input");
        double exchange = (double) paramMap.get("exchange");

        String inputData = buildInputPart(rawList);
        stringBuilder.append(inputData);

        String outputData = buildOutput(toAddress, sendAmt, fromCoin, exchange);
        stringBuilder.append(outputData);


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

        String toScriptKey = Bech32.getScriptPubKeyFromBech32Address(toAddress);

        String fromScriptKey = Bech32.getScriptPubKeyFromBech32Address(fromCoin.getCoinAddress());

        //satoshi value 8byte length little endian
        int sendVal = BigDecimal.valueOf(Utils.convertBtcToSatoshi(sendAmt)).intValue();
        int exchangeVal = BigDecimal.valueOf(Utils.convertBtcToSatoshi(exchange)).intValue();

        String endianSendAmt = getEndianValHex(sendVal);
        String endianExchangeAmt = getEndianValHex(exchangeVal);

        // toOutput
        stringBuilder.append(endianSendAmt);
        stringBuilder.append(Integer.toHexString(Utils.hexStringToByteArray(toScriptKey).length));
        stringBuilder.append(toScriptKey);

        //exchange Output
        stringBuilder.append(endianExchangeAmt);
        stringBuilder.append(Integer.toHexString(Utils.hexStringToByteArray(fromScriptKey).length));
        stringBuilder.append(fromScriptKey);

//        String suffix = "00000000";
//        stringBuilder.append(suffix);
//
//        String hashType = "01000000";
//        stringBuilder.append(hashType);

        return stringBuilder.toString();
    }

}
