package nfc;

import coin.Coin;
import coin.RawTransaction;
import network.api.CoinData;
import network.api.SendingTransaction;
import org.bitcoinj.core.ECKey;
import org.bitcoinj.core.Sha256Hash;
import org.bouncycastle.jcajce.provider.digest.Keccak;
import org.bouncycastle.jcajce.provider.digest.SHA256;
import org.bouncycastle.util.encoders.Hex;
import org.json.JSONObject;
import util.Utils;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Map;

public class ApduCommandHelper {
    public static String keyGenCmd(String txCnt, String gasPrice, double gas, String toAddress, double amt) {
        StringBuilder rootBuilder = new StringBuilder();
        String nonce = "";
        if (txCnt.equals("0x0")) {
            nonce = "80";


        } else {
            int cnt = Integer.decode(txCnt);
            nonce = Integer.toHexString(cnt);
//            nonce = paddingZeroValue(txCnt.split("x")[1]);
        }
        rootBuilder.append(nonce);

        String gasPriceHex = paddingZeroValue(new BigInteger(gasPrice).toString(16));
        String gasPricePrefix = calcLength("80", gasPriceHex);
        rootBuilder.append(gasPricePrefix);
        rootBuilder.append(gasPriceHex);

        String gasHex = Integer.toString((int) gas, 16);
        String gasPrefix = calcLength("80", gasHex);
        rootBuilder.append(gasPrefix);
        rootBuilder.append(gasHex);

        rootBuilder.append("94");
        rootBuilder.append(toAddress.substring(2));

        String sendAmtHex = paddingZeroValue(Utils.convertToWei(amt).substring(2));
        String sendAmtPrefix = calcLength("80", sendAmtHex);
        rootBuilder.append(sendAmtPrefix);
        rootBuilder.append(sendAmtHex);

        String suffix = "80038080";
        rootBuilder.append(suffix);

        String dataLength = Integer.toHexString(Integer.parseInt("C0", 16) + Utils.hexStringToByteArray(rootBuilder.toString()).length);
        rootBuilder.insert(0, dataLength);

        System.out.println(rootBuilder.toString().toUpperCase());
        return rootBuilder.toString().toUpperCase();
    }

    private static String paddingZeroValue(String value) {
        if (value.length() % 2 == 1) {
            value = "0" + value;
        }
        return value;
    }

    private static String calcLength(String prefix, String hex) {
        String result = "";
        int prefixHexVal = Integer.parseInt(prefix, 16);
        int hexVal = Utils.hexStringToByteArray(hex).length;
        result = Integer.toHexString(prefixHexVal + hexVal);

        return result;
    }

    //    private static String encrypt(String hexVal) {
//        return Hash.sha3(hexVal);
//    }
    public static String encrypt(String hexVal) {
        Keccak.Digest256 digest256 = new Keccak.Digest256();
        byte[] hashbytes = digest256.digest(Utils.hexStringToByteArray(hexVal));
        String sha3Hex = new String(Hex.encode(hashbytes));
        System.out.println(sha3Hex);
        return sha3Hex;

    }

    private static String buildInputPart(ArrayList<RawTransaction> rawList) {
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

    public static String buildBtcTransaction(String toAddress, Coin fromCoin, double sendAmt) {        // input
        StringBuilder stringBuilder = new StringBuilder();
        String version = Hex.toHexString(Utils.convertToLittleEndian(2), 0, 4);
        stringBuilder.append(version);
//        stringBuilder.append("00");
//        stringBuilder.append("01");

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

    private static String buildOutput(String toAddress, double sendAmt, Coin fromCoin, double exchange) {
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

        JSONObject toAddressResponse = SendingTransaction.getAddressScriptKey(toAddress);
        String toScriptKey = toAddressResponse.optJSONObject("result").optString("scriptPubKey");

        JSONObject fromAddressResponse = SendingTransaction.getAddressScriptKey(fromCoin.getCoinAddress());
        String fromScriptKey = fromAddressResponse.optJSONObject("result").optString("scriptPubKey");

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

        String suffix = "00000000";
        stringBuilder.append(suffix);
//
//        String hashType = "01000000";
//        stringBuilder.append(hashType);

        return stringBuilder.toString();
    }


    private static String getEndianValHex(int val) {
        byte[] exchangeBytes = new byte[8];
        byte[] endianExchangeBytes = Utils.convertToLittleEndian(val);
        System.arraycopy(endianExchangeBytes, 0, exchangeBytes, 0, endianExchangeBytes.length);

        String endianExchangeAmt = Utils.byteArrayToHex(exchangeBytes);
        return endianExchangeAmt;
    }

    private static String getEndianValHex4(int val) {
        byte[] exchangeBytes = new byte[4];
        byte[] endianExchangeBytes = Utils.convertToLittleEndian(val);
        System.arraycopy(endianExchangeBytes, 0, exchangeBytes, 0, endianExchangeBytes.length);

        String endianExchangeAmt = Utils.byteArrayToHex(exchangeBytes);
        return endianExchangeAmt;
    }


}
