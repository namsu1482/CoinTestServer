package coin;


import org.json.JSONObject;

import java.io.Serializable;
import java.math.BigDecimal;

public class RawTransaction implements Comparable<RawTransaction>, Serializable {
    private static final String TAG = RawTransaction.class.getSimpleName();
    private String txId;
    private int vOut;
    private String address;
    private String label;
    private String scriptKey;
    private double amount;
    private int confirmation;
    private boolean spendable = false;
    private boolean solvable = false;
    private String desc;
    private boolean safe = false;

    public void parseRawTransaction(JSONObject jsonObject) {
        txId = jsonObject.optString("txid");
        vOut = jsonObject.optInt("vout");
        address = jsonObject.optString("address");
        label = jsonObject.optString("label");
        scriptKey = jsonObject.optString("scriptPubKey");
        amount = jsonObject.optDouble("amount");
        confirmation = jsonObject.optInt("confirmations");
        spendable = jsonObject.optBoolean("spendable");
        solvable = jsonObject.optBoolean("solvable");
        desc = jsonObject.optString("desc");
        safe = jsonObject.optBoolean("safe");


//        Logger.i(TAG, "raw amount: " + BigDecimal.valueOf(amount).doubleValue());
    }

    public int getvOut() {
        return vOut;
    }

    public String getAddress() {
        return address;
    }

    public double getAmount() {
        return amount;
    }

    public String getTxId() {
        return txId;
    }

    public String getScriptKey() {
        return scriptKey;
    }

    @Override
    public int compareTo(RawTransaction o) {
        return Double.compare(amount, o.getAmount());
    }


    @Override
    public String toString() {
        return "RawTransaction{" +
                "txId='" + txId + '\'' +
                ", vOut=" + vOut +
                ", address='" + address + '\'' +
                ", label='" + label + '\'' +
                ", scriptKey='" + scriptKey + '\'' +
                ", amount=" + amount +
                ", confirmation=" + confirmation +
                ", spendable=" + spendable +
                ", solvable=" + solvable +
                ", desc='" + desc + '\'' +
                ", safe=" + safe +
                '}';
    }
}
