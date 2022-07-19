package coin;


import org.json.JSONArray;
import org.json.JSONObject;
import util.Utils;

import java.io.Serializable;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.TimeZone;

public class CoinTransaction implements Serializable, Comparable<CoinTransaction> {
    private static final String TAG = CoinTransaction.class.getSimpleName();
    COIN_TYPE coin_type;
    String coinAddress;
    String transactionType;
    String transactionDtime;
    double transactionAmt;
    String amt;
    double fee;
    String txId;
    String transactionAddress;

    public CoinTransaction(COIN_TYPE coin_type, String coinAddress) {
        this.coin_type = coin_type;
        this.coinAddress = coinAddress;
    }

    public String getAmt() {
        return amt;
    }

    public void setAmt(String amt) {
        this.amt = amt;
    }

    public COIN_TYPE getCoin_type() {
        return coin_type;
    }

    public void setCoin_type(COIN_TYPE coin_type) {
        this.coin_type = coin_type;
    }

    public String getCoinAddress() {
        return coinAddress;
    }

    public void setCoinAddress(String coinAddress) {
        this.coinAddress = coinAddress;
    }

    public String getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(String transactionType) {
        this.transactionType = transactionType;
    }

    public String getTransactionDtime() {
        return transactionDtime;
    }

    public void setTransactionDtime(String transactionDtime) {
        this.transactionDtime = transactionDtime;
    }

    public double getTransactionAmt() {
        return transactionAmt;
    }

    public void setTransactionAmt(double transactionAmt) {
        this.transactionAmt = transactionAmt;
    }

    public String getTxId() {
        return txId;
    }

    public void setTxId(String txId) {
        this.txId = txId;
    }

    private static String getTransDtime(String timeZone) {
        String transactionDtime = "";
        SimpleDateFormat dateFormat = null;

        if (timeZone.contains(".")) {
            dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        } else {
            dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        }

        try {
            dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
            Date date = dateFormat.parse(timeZone);

            SimpleDateFormat yyyymmddFormat = new SimpleDateFormat("yyyyMMddHHmmss");
            yyyymmddFormat.setTimeZone(TimeZone.getDefault());
            transactionDtime = yyyymmddFormat.format(date);

        } catch (ParseException e) {
            e.printStackTrace();
        }

        return transactionDtime;
    }

    public static ArrayList<CoinTransaction> parseBtcTransactions(String coinAddress, JSONObject jsonObject) {
        String result = "";
        double value = 0;
        String fromAddress = "";
        String toAddress = "";
        double fee = 0;

        ArrayList<CoinTransaction> transactions = new ArrayList<>();

        CoinTransaction coinTransaction = new CoinTransaction(COIN_TYPE.BITCOIN, coinAddress);
        coinTransaction.setTransactionDtime(getTransDtime(jsonObject.optString("received")));

        // 송신
        JSONArray inputArray = jsonObject.optJSONArray("inputs");
        for (int i = 0; i < inputArray.length(); i++) {
            JSONObject inputObject = inputArray.optJSONObject(i);
            JSONArray addressArray = inputObject.optJSONArray("addresses");
            for (int j = 0; j < addressArray.length(); j++) {
                String inputAddress = addressArray.optString(j);
                fromAddress = inputAddress;

            }
        }

        // 수신
        JSONArray outputArray = jsonObject.optJSONArray("outputs");
        for (int i = 0; i < outputArray.length(); i++) {
            JSONObject outputObject = outputArray.optJSONObject(i);
            JSONArray outputAddressArray = outputObject.optJSONArray("addresses");
            for (int j = 0; j < outputAddressArray.length(); j++) {
                String outputAddress = outputAddressArray.optString(j);
                toAddress = outputAddress;
                if (coinAddress.equals(fromAddress)) {
                    //잔돈반환
                    if (toAddress.equals(fromAddress)) {
                        value = outputObject.optDouble("value");
//                        Log.i(TAG, "self " + value);
                        //send
                    } else {
                        coinTransaction.setTransactionAddress(toAddress);
                        value = outputObject.optDouble("value");
                        fee = jsonObject.optDouble("fees");
//                        Log.i(TAG, "send " + value);
                        result = "send";

                        coinTransaction.setTransactionType(result);
                        coinTransaction.setTransactionAmt(Utils.convertSatoshiToBtc(value));
                        coinTransaction.setFee(Utils.convertSatoshiToBtc(fee));
                        transactions.add(coinTransaction);

                    }

                } else if (toAddress.equals(coinAddress)) {
                    coinTransaction.setTransactionAddress(fromAddress);
                    value = outputObject.optDouble("value");
//                    Log.i(TAG, "receive " + value);
                    //recv
                    result = "receive";
                    coinTransaction.setTransactionType(result);
                    coinTransaction.setTransactionAmt(Utils.convertSatoshiToBtc(value));
                    transactions.add(coinTransaction);

                } else {
                    value = outputObject.optDouble("value");
//                    Log.i(TAG, "else " + value);
                }
            }

        }
        return transactions;
    }

    public double getFee() {
        return fee;
    }

    public void setFee(double fee) {
        this.fee = fee;
    }

    public void parseEthTransaction(String coinAddress, JSONObject transactionObject) {
        String fromAddress = transactionObject.optString("from");
        String toAddress = transactionObject.optString("to");
        if (fromAddress.equals(coinAddress)) {
            transactionAddress = toAddress;
            transactionType = "send";

        } else if (toAddress.equals(coinAddress)) {
            transactionAddress = fromAddress;
            transactionType = "receive";
        }

        String value = transactionObject.optString("value");
        fee = Utils.convertEtcGas(transactionObject.optString("gas"), transactionObject.optString("gasPrice"));
        transactionAmt = Utils.convertEthBalance(value);
        String dtime = transactionObject.optString("timeStamp");
        transactionDtime = Utils.convertUnixTime(dtime);
    }

    public String getTransactionAddress() {
        return transactionAddress;
    }

    public void setTransactionAddress(String transactionAddress) {
        this.transactionAddress = transactionAddress;
    }

    @Override
    public int compareTo(CoinTransaction o) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
        int result = 0;
        try {
            result = simpleDateFormat.parse(transactionDtime).compareTo(simpleDateFormat.parse(o.transactionDtime));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return result;
    }

    public JSONObject buildJson() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("tx_id", txId);
        jsonObject.put("transaction_type", transactionType);
        jsonObject.put("transaction_dtime", transactionDtime);
        jsonObject.put("transaction_amt", BigDecimal.valueOf(transactionAmt).toString());
        jsonObject.put("fee", BigDecimal.valueOf(fee).toString());
        jsonObject.put("transaction_address", transactionAddress);

        return jsonObject;
    }
}
