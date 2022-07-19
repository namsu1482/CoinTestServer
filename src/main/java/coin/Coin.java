package coin;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import util.Utils;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;

import static coin.CoinTransaction.parseBtcTransactions;

public class Coin implements Serializable {
    private static final String TAG = Coin.class.getSimpleName();
    private COIN_TYPE coin_type;
    private double coinBalance = 0;
    //    private int krwBalance;
    private String coinAddress = "";
    private ArrayList<RawTransaction> rawTransactionList = new ArrayList<>();

    private ArrayList<CoinTransaction> transactionList = new ArrayList<>();

    private boolean enable = false;


    private double fee = 0;

    public Coin(COIN_TYPE coin_type, String coinAddress) {
        this.coin_type = coin_type;
        this.coinAddress = coinAddress;
    }

    public COIN_TYPE getCoin_type() {
        return coin_type;
    }

    public void setCoin_type(COIN_TYPE coin_type) {
        this.coin_type = coin_type;
    }

    public double getCoinBalance() {
        return coinBalance;
    }

    public void setCoinBalance(double coinBalance) {
        this.coinBalance = coinBalance;
    }

//    public int getKrwBalance() {
//        return krwBalance;
//    }

//    public void setKrwBalance(int krwBalance) {
//        this.krwBalance = krwBalance;
//    }

    public String getCoinAddress() {
        return coinAddress;
    }

    public void setCoinAddress(String coinAddress) {
        this.coinAddress = coinAddress;
    }

    public ArrayList<CoinTransaction> getTransactionList() {
        return transactionList;
    }

    public void setFee(double fee) {
        this.fee = fee;
    }

    public double getFee() {
        return fee;
    }

    public void parseBtcRawTransaction(JSONObject result) {
        rawTransactionList = new ArrayList<>();
        JSONArray array = result.optJSONArray("result");

        for (int i = 0; i < array.length(); i++) {
            try {
                JSONObject object = array.getJSONObject(i);
                RawTransaction rawTransaction = new RawTransaction();
                rawTransaction.parseRawTransaction(object);
                double bal = rawTransaction.getAmount();
                rawTransactionList.add(rawTransaction);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        Collections.sort(rawTransactionList);
        Collections.reverse(rawTransactionList);

    }

    public ArrayList<RawTransaction> getRawTransactionList() {
        return rawTransactionList;
    }


    public void setTransactionList(ArrayList<CoinTransaction> transactionList) {
        this.transactionList = transactionList;
    }


    public void parseCoinData(JSONObject jsonObject) {
        switch (coin_type) {
            case BITCOIN:
                parseBtcTXArray(jsonObject);
                fee = 0.0002;
                break;
            case ETHEREUM:
                fee = Utils.convertEtcGas("21000", "20000000000");
//                fee = 21000;
                parseEthTxArray(jsonObject);
                break;

        }

    }

    public boolean isEnable() {
        return enable;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }

    private void parseBtcTXArray(JSONObject resultObject) {
        transactionList = new ArrayList<>();
        coinBalance = Utils.convertSatoshiToBtc(resultObject.optDouble("final_balance"));

        ArrayList<CoinTransaction> txList = new ArrayList<>();
        JSONArray transactionArray = resultObject.optJSONArray("txs");

        for (int i = 0; i < transactionArray.length(); i++) {
            JSONObject txObject = transactionArray.optJSONObject(i);
            txList.addAll(parseBtcTransactions(coinAddress, txObject));
        }
        transactionList = txList;
        Collections.sort(transactionList);

        Collections.reverse(transactionList);

    }

    private void parseEthTxArray(JSONObject resultObject) {
        String balance = resultObject.optString("balance");
        BigInteger bigInteger = new BigInteger(balance);
        coinBalance = BigDecimal.valueOf(Utils.convertEthBalance(bigInteger.doubleValue())).doubleValue();

        transactionList = new ArrayList<>();

        JSONArray jsonArray = resultObject.optJSONArray("txlist");
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject transactionObject = jsonArray.optJSONObject(i);
            CoinTransaction coinTransaction = new CoinTransaction(coin_type, coinAddress);
            coinTransaction.parseEthTransaction(coinAddress, transactionObject);
            transactionList.add(coinTransaction);
        }

//        Collections.reverse(transactionList);
    }

    public JSONObject buildJsonObject() {
        JSONObject jsonObject = new JSONObject();
        JSONArray txArray = new JSONArray();
        jsonObject.put("coin_type", coin_type.coinSymbol);
        jsonObject.put("balance", BigDecimal.valueOf(coinBalance).toString());
        for (int i = 0; i < transactionList.size(); i++) {
            txArray.put(transactionList.get(i).buildJson());
        }
        jsonObject.put("tx_ist", txArray);
        return jsonObject;
    }

}

