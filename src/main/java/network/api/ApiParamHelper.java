package network.api;


import coin.Coin;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import util.Utils;

import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

public class ApiParamHelper {
    public enum ETH_REQ_METHOD {
        @Deprecated BALANCE("eth_getBalance"),
        @Deprecated ACCOUNT_INFO("personal_listAccounts"),
        ACCOUNT_UNLOCK("personal_unlockAccount"),
        SEND_COIN("eth_sendTransaction"),
        CHECK_RECEIPT("eth_getTransactionReceipt"),
        ACCOUNT_LOCK("personal_lockAccount"),
        TRANSACTION_RECEIPT("eth_getTransactionReceipt"),
        SIGN_TRANSACTION("eth_signTransaction"),
        SEND_RAW_TRANSACTION("eth_sendRawTransaction"),
        TRASNSACTION_COUNT("eth_getTransactionCount"),
        ETHER_SCAN_BALANCE("balance"),
        ETHER_SCAN_TX_LIST("txlist");

        String methodValue = "";

        ETH_REQ_METHOD(String methodValue) {
            this.methodValue = methodValue;
        }

        public String getMethod() {
            return methodValue;
        }
    }

    public enum BTC_REQ_METHOD {
        LIST_UNSPENT("listunspent"),
        @Deprecated GET_TRANSACTION_LIST("listtransactions"),
        SEND_COIN("sendtoaddress"),
        CREATETRANSACTION("createrawtransaction"),
        DUMPKEY("dumpprivkey"),
        SIGNTRANSACTION("signrawtransactionwithkey"),
        SEND_TRANSACTION("sendrawtransaction"),
        @Deprecated LIST_RECEIVED("listreceivedbyaddress"),
        WALLET_PASS_PHRASE("walletpassphrase"),
        WALLET_LOCK("walletlock"),
        VALIDATION_ADDRESS("validateaddress");


        String method;

        BTC_REQ_METHOD(String method) {
            this.method = method;
        }

        public String getMethod() {
            return method;
        }
    }

    public static JSONObject getBtcJson(BTC_REQ_METHOD btc_req_METHOD, Coin coin) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("jsonrpc", "1.0");
            jsonObject.put("id", "curltest");
            jsonObject.put("method", btc_req_METHOD.getMethod());
            JSONArray jsonArray = new JSONArray();
            switch (btc_req_METHOD) {
                case LIST_UNSPENT:
                    jsonArray.put(0);
                    jsonArray.put(9999999);
                    JSONArray addressArray = new JSONArray();
                    addressArray.put(coin.getCoinAddress());
                    jsonArray.put(addressArray);
                    jsonObject.put("params", jsonArray);
                    break;

                case GET_TRANSACTION_LIST:
                    jsonArray.put("*");
                    jsonArray.put(10);
                    jsonObject.put("params", jsonArray);
                    break;

                case SEND_COIN:
                    break;
                case LIST_RECEIVED:
                    jsonArray.put(6);
                    jsonArray.put(true);
                    jsonArray.put(true);
                    jsonArray.put(coin.getCoinAddress());
                    jsonObject.put("params", jsonArray);
                    break;

                case WALLET_LOCK:
                    break;

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

    public static JSONObject getBtcJson(BTC_REQ_METHOD btc_req_METHOD, JSONArray paramArray) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("jsonrpc", "1.0");
            jsonObject.put("id", "curltest");
            jsonObject.put("method", btc_req_METHOD.getMethod());
            jsonObject.put("params", paramArray);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

    public static JSONObject getEthJson(ETH_REQ_METHOD ethReqMethod, String param) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("jsonrpc", "2.0");
            jsonObject.put("method", ethReqMethod.getMethod());
            JSONArray jsonArray = new JSONArray();
            switch (ethReqMethod) {
                case BALANCE:
                    jsonArray.put(param);
                    jsonArray.put("latest");
                    break;
                case ACCOUNT_INFO:
                    break;
                case SEND_COIN:
                    jsonArray.put(param);
                    jsonArray.put("testuser4");
                    jsonArray.put(0);
                    break;

                case TRANSACTION_RECEIPT:
                    jsonArray.put(param);
                    break;
            }
            jsonObject.put("params", jsonArray);
            jsonObject.put("id", "100");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

    public static JSONObject getEthJson(ETH_REQ_METHOD ETHReq_type, JSONArray paramArray) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("jsonrpc", "2.0");
            jsonObject.put("method", ETHReq_type.getMethod());
            jsonObject.put("params", paramArray);
            jsonObject.put("id", "100");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }


    public static String getEtherScanParam(String coinAddress, ETH_REQ_METHOD eth_req_METHOD) {
        Map<String, Object> params = new HashMap<>();
        params.put("module", "account");
        params.put("action", eth_req_METHOD.getMethod());
        params.put("address", coinAddress);
        params.put("tag", "latest");
        params.put("apikey", "AWFA8H651I2RH6QS1EYGKYVMZ7M37E99QM");
        if (eth_req_METHOD.equals(ETH_REQ_METHOD.ETHER_SCAN_TX_LIST)) {
            params.put("sort", "desc");
            params.put("offset", 10);
            params.put("page", 1);

        }

        return Utils.getQueryString(params);
    }

}
