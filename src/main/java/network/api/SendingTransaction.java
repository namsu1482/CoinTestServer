package network.api;

import coin.COIN_TYPE;
import coin.Coin;

import coin.RawTransaction;
import network.NetworkUrl;
import network.RequestHttpConnection;
import nfc.ApduCommandHelper;
import nfc.bitcoin.BtcFee;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import util.Utils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SendingTransaction {
    public static JSONObject sendTransaction(String toAddress, double sendAmt, Coin coin) {
        switch (coin.getCoin_type()) {
            case BITCOIN:
                return sendBtcTransaction(toAddress, sendAmt, coin);
            case ETHEREUM:
                return sendEthTransaction(toAddress, sendAmt, coin);
            default:
                return null;
        }
    }

    public static JSONObject CreateBtcTransaction(String toAddress, double sendAmt, Coin coin) {
        JSONArray paramArray = new JSONArray();
        paramArray = buildTxParams(toAddress, sendAmt, coin);

        RequestHttpConnection requestHttpConnection = new RequestHttpConnection();
        JSONObject response = requestHttpConnection.request(COIN_TYPE.BITCOIN,
                RequestHttpConnection.REQ_TYPE.JSON,
                NetworkUrl.BTC_TRANSACTION_URL,
                ApiParamHelper.getBtcJson(ApiParamHelper.BTC_REQ_METHOD.CREATETRANSACTION, paramArray).toString());
        return response;
    }

    private static JSONObject DumpBtcPrivateKey(Coin coin) {
        JSONArray dumpKeyParamArray = new JSONArray();
        dumpKeyParamArray.put(coin.getCoinAddress());

        RequestHttpConnection requestHttpConnection = new RequestHttpConnection();
        JSONObject response = requestHttpConnection.request(COIN_TYPE.BITCOIN,
                RequestHttpConnection.REQ_TYPE.JSON,
                NetworkUrl.BTC_WALLET_URL,
                ApiParamHelper.getBtcJson(ApiParamHelper.BTC_REQ_METHOD.DUMPKEY, dumpKeyParamArray).toString());
        return response;
    }

    private static JSONObject SignBtcTransaction(String privateKey, String transactionHex) {
        JSONArray signKeyParamArray = new JSONArray();
        JSONArray keyArray = new JSONArray();
        keyArray.put(privateKey);
        signKeyParamArray.put(transactionHex);
        signKeyParamArray.put(keyArray);

        RequestHttpConnection requestHttpConnection = new RequestHttpConnection();
        JSONObject response = requestHttpConnection.request(COIN_TYPE.BITCOIN,
                RequestHttpConnection.REQ_TYPE.JSON,
                NetworkUrl.BTC_TRANSACTION_URL,
                ApiParamHelper.getBtcJson(ApiParamHelper.BTC_REQ_METHOD.SIGNTRANSACTION, signKeyParamArray).toString());
        return response;
    }

    public static JSONObject SendBtcTransaction(JSONArray paramArray) {
        RequestHttpConnection requestHttpConnection = new RequestHttpConnection();
        JSONObject response = requestHttpConnection.request(COIN_TYPE.BITCOIN,
                RequestHttpConnection.REQ_TYPE.JSON,
                NetworkUrl.BTC_TRANSACTION_URL,
                ApiParamHelper.getBtcJson(ApiParamHelper.BTC_REQ_METHOD.SEND_TRANSACTION, paramArray).toString());
        return response;
    }

    public static JSONObject getAddressScriptKey(String toAddress) {
        JSONArray paramArray = new JSONArray();
        paramArray.put(toAddress);
        RequestHttpConnection requestHttpConnection = new RequestHttpConnection();
        JSONObject response = requestHttpConnection.request(COIN_TYPE.BITCOIN,
                RequestHttpConnection.REQ_TYPE.JSON,
                NetworkUrl.BTC_WALLET_URL,
                ApiParamHelper.getBtcJson(ApiParamHelper.BTC_REQ_METHOD.VALIDATION_ADDRESS, paramArray).toString());
        return response;
    }

    private static JSONObject WalletPassPhrase() {
        JSONArray walletPhraseArray = new JSONArray();
        walletPhraseArray.put("smavis");
        walletPhraseArray.put(5);

        RequestHttpConnection requestHttpConnection = new RequestHttpConnection();
        JSONObject response = requestHttpConnection.request(COIN_TYPE.BITCOIN,
                RequestHttpConnection.REQ_TYPE.JSON,
                NetworkUrl.BTC_WALLET_URL,
                ApiParamHelper.getBtcJson(ApiParamHelper.BTC_REQ_METHOD.WALLET_PASS_PHRASE, walletPhraseArray).toString());
        return response;
    }

    private static JSONObject WalletLock(Coin coin) {
        RequestHttpConnection requestHttpConnection = new RequestHttpConnection();
        JSONObject response = requestHttpConnection.request(COIN_TYPE.BITCOIN,
                RequestHttpConnection.REQ_TYPE.JSON,
                NetworkUrl.BTC_WALLET_URL,
                ApiParamHelper.getBtcJson(ApiParamHelper.BTC_REQ_METHOD.WALLET_LOCK, coin).toString());
        return response;
    }

    private static JSONObject sendBtcTransaction(String toAddress, double amt, Coin coin) {
        JSONObject totalResult = null;
        //CreateTransacion
        try {
//            JSONObject createTransactionResponse = CreateBtcTransaction(toAddress, amt, coin);
//            if (createTransactionResponse == null) {
//                return null;
//            }
//            String transactionHex = createTransactionResponse.optString("result");

            String transactionHex = ApduCommandHelper.buildBtcTransaction(toAddress, coin, amt);


            // 지갑 잠금 해제
            WalletPassPhrase();

            //dumpkey
            JSONObject keyResponse = DumpBtcPrivateKey(coin);
            if (keyResponse == null) {
                return null;
            }
            String privateKey = keyResponse.optString("result");

            //지갑 잠금
            WalletLock(coin);
            //signTransaction
            JSONObject signResultResponse = SignBtcTransaction(privateKey, transactionHex);
            if (signResultResponse == null) {
                return null;
            }
            JSONObject signResult = signResultResponse.optJSONObject("result");
            String signResultHex = signResult.optString("hex");
            boolean signComplete = signResult.optBoolean("complete");

            //send Transaction
            if (signComplete) {
                JSONArray sendTransactionParamArray = new JSONArray();
                sendTransactionParamArray.put(signResultHex);
                totalResult = SendBtcTransaction(sendTransactionParamArray);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return totalResult;

    }

    private static JSONArray buildTxParams(String toAddress, double sendAmt, Coin coin) {
        ArrayList<RawTransaction> rawTransactions = coin.getRawTransactionList();
        JSONArray paramArray = new JSONArray();
        JSONArray txArray = new JSONArray();
        BigDecimal totalSumDecimal = BigDecimal.valueOf(0);

//        BigDecimal totalSendAmtDecimal = BigDecimal.valueOf(sendAmt).add(BigDecimal.valueOf(coin.getFee()));
        BigDecimal totalSendAmtDecimal = BigDecimal.valueOf(sendAmt);
        BigDecimal exchangeAmtDecimal = BigDecimal.valueOf(0);
        for (int i = 0; i < rawTransactions.size(); i++) {
            RawTransaction rawTransaction = rawTransactions.get(i);
            JSONObject rawTransactionObject = new JSONObject();
            try {
                rawTransactionObject.put("txid", rawTransaction.getTxId());
                rawTransactionObject.put("vout", rawTransaction.getvOut());

                totalSumDecimal = totalSumDecimal.add(BigDecimal.valueOf(rawTransaction.getAmount()));
                exchangeAmtDecimal = totalSumDecimal.subtract(totalSendAmtDecimal);
                txArray.put(rawTransactionObject);

                int txOut = 1;
                if (exchangeAmtDecimal.doubleValue() > 0) {
                    txOut = 2;
                }
                BtcFee btcFee = new BtcFee(txArray.length(), txOut);
                totalSendAmtDecimal = totalSendAmtDecimal.add(BigDecimal.valueOf(btcFee.getFee(10)));
                exchangeAmtDecimal = totalSumDecimal.subtract(totalSendAmtDecimal);

                if (exchangeAmtDecimal.doubleValue() >= 0) {
                    JSONObject toAddressObject = new JSONObject();

                    try {
                        toAddressObject.put(toAddress, BigDecimal.valueOf(sendAmt).toPlainString());
                        toAddressObject.put(coin.getCoinAddress(), exchangeAmtDecimal.toPlainString());
                        paramArray.put(txArray);
                        paramArray.put(toAddressObject);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    break;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
        System.out.println(paramArray.toString());
        return paramArray;
    }

    public static Map<String, Object> getTxParams(double sendAmt, Coin coin) {
        ArrayList<RawTransaction> rawTransactions = coin.getRawTransactionList();
        ArrayList<RawTransaction> paramList = new ArrayList<>();
        BigDecimal totalSumDecimal = BigDecimal.valueOf(0);
        Map<String, Object> map = new HashMap<>();

        BigDecimal totalSendAmtDecimal = BigDecimal.valueOf(sendAmt).add(BigDecimal.valueOf(coin.getFee()));
        BigDecimal exchangeAmtDecimal = BigDecimal.valueOf(0);
        for (int i = 0; i < rawTransactions.size(); i++) {
            RawTransaction rawTransaction = rawTransactions.get(i);
            paramList.add(rawTransaction);
            totalSumDecimal = totalSumDecimal.add(BigDecimal.valueOf(rawTransaction.getAmount()));
            exchangeAmtDecimal = totalSumDecimal.subtract(totalSendAmtDecimal);

            if (exchangeAmtDecimal.doubleValue() >= 0) {
                map.put("exchange", exchangeAmtDecimal.doubleValue());
                break;
            }


        }
        map.put("input", paramList);
        return map;
    }

    //ETH

    private static JSONObject sendEthTransaction(String toAddress, double sendAmt, Coin coin) {
        JSONObject response = null;
//        unLockEthAccount(coin.getCoinAddress());
//        response = SendEthTransaction(toAddress, sendAmt, coin);
//        LockEthAccount(coin.getCoinAddress());

        JSONObject txCnt = getEthTransactionCount(coin);
        if (txCnt == null) {
            return null;
        }
        String transactionCnt = txCnt.optString("result");
        unLockEthAccount(coin.getCoinAddress());

        JSONObject signResponse = signEtcTransaction(toAddress, sendAmt, coin, transactionCnt);
        if (signResponse == null) {
            return null;
        }
        String key = signResponse.optJSONObject("result").optString("raw");

        response = sendEtcRawTransaction(key);
        LockEthAccount(coin.getCoinAddress());

        return response;
    }

    private static JSONObject unLockEthAccount(String coinAddress) {
        JSONArray paramArray = new JSONArray();
        paramArray.put(coinAddress);
        //password
        paramArray.put("1234");
        paramArray.put(2);

        RequestHttpConnection requestHttpConnection = new RequestHttpConnection();
        JSONObject response = requestHttpConnection.request(COIN_TYPE.ETHEREUM,
                RequestHttpConnection.REQ_TYPE.JSON,
                NetworkUrl.COIN_INFO_ETH_LOCAL,
                ApiParamHelper.getEthJson(ApiParamHelper.ETH_REQ_METHOD.ACCOUNT_UNLOCK, paramArray).toString());
        return response;
    }

    private static JSONObject SendEthTransaction(String toAddress, double sendAmt, Coin coin) {
        JSONArray paramArray = new JSONArray();
        JSONObject transactionObject = new JSONObject();
        try {
            transactionObject.put("from", coin.getCoinAddress());
            transactionObject.put("to", toAddress);
            transactionObject.put("value", Utils.convertToWei(sendAmt));
            paramArray.put(transactionObject);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        RequestHttpConnection requestHttpConnection = new RequestHttpConnection();
        JSONObject response = requestHttpConnection.request(COIN_TYPE.ETHEREUM,
                RequestHttpConnection.REQ_TYPE.JSON,
                NetworkUrl.COIN_INFO_ETH_LOCAL,
                ApiParamHelper.getEthJson(ApiParamHelper.ETH_REQ_METHOD.SEND_COIN, paramArray).toString());
        return response;

    }

    private static JSONObject LockEthAccount(String coinAddress) {
        JSONArray paramArray = new JSONArray();
        paramArray.put(coinAddress);

        RequestHttpConnection requestHttpConnection = new RequestHttpConnection();
        JSONObject response = requestHttpConnection.request(COIN_TYPE.ETHEREUM,
                RequestHttpConnection.REQ_TYPE.JSON,
                NetworkUrl.COIN_INFO_ETH_LOCAL,
                ApiParamHelper.getEthJson(ApiParamHelper.ETH_REQ_METHOD.ACCOUNT_LOCK, paramArray).toString());
        return response;
    }

    private static JSONObject getEtcTransactionReceipt(String txId) {
        JSONObject response = new JSONObject();
        JSONArray paramArray = new JSONArray();
        paramArray.put(txId);

        RequestHttpConnection requestHttpConnection = new RequestHttpConnection();
        response = requestHttpConnection.request(COIN_TYPE.ETHEREUM,
                RequestHttpConnection.REQ_TYPE.JSON,
                NetworkUrl.COIN_INFO_ETH_LOCAL,
                ApiParamHelper.getEthJson(ApiParamHelper.ETH_REQ_METHOD.TRANSACTION_RECEIPT, paramArray).toString());
        return response;
    }

    private static JSONObject signEtcTransaction(String toAddress, double sendAmt, Coin coin, String transactionCnt) {
        JSONArray paramArray = new JSONArray();
        JSONObject transactionObject = new JSONObject();
        try {
            transactionObject.put("data", "");
            transactionObject.put("from", coin.getCoinAddress());
            transactionObject.put("to", toAddress);
            transactionObject.put("value", Utils.convertToWei(sendAmt));
            // gas value에 따라 거래 반영 속도가 달라짐
            transactionObject.put("gasPrice", "0x4A817C800");
            transactionObject.put("gas", "0x5208");
            transactionObject.put("nonce", transactionCnt);

            paramArray.put(transactionObject);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        RequestHttpConnection requestHttpConnection = new RequestHttpConnection();
        JSONObject response = requestHttpConnection.request(COIN_TYPE.ETHEREUM,
                RequestHttpConnection.REQ_TYPE.JSON,
                NetworkUrl.COIN_INFO_ETH_LOCAL,
                ApiParamHelper.getEthJson(ApiParamHelper.ETH_REQ_METHOD.SIGN_TRANSACTION, paramArray).toString());
        return response;
    }

    private static JSONObject sendEtcRawTransaction(String key) {
        JSONArray paramArray = new JSONArray();
        paramArray.put(key);

        RequestHttpConnection requestHttpConnection = new RequestHttpConnection();
        JSONObject response = requestHttpConnection.request(COIN_TYPE.ETHEREUM,
                RequestHttpConnection.REQ_TYPE.JSON,
                NetworkUrl.COIN_INFO_ETH_LOCAL,
                ApiParamHelper.getEthJson(ApiParamHelper.ETH_REQ_METHOD.SEND_RAW_TRANSACTION, paramArray).toString());
        return response;
    }

    public static JSONObject getEthTransactionCount(Coin coin) {
        JSONArray paramArray = new JSONArray();
        paramArray.put(coin.getCoinAddress());
        paramArray.put("latest");

        RequestHttpConnection requestHttpConnection = new RequestHttpConnection();
        JSONObject response = requestHttpConnection.request(COIN_TYPE.ETHEREUM,
                RequestHttpConnection.REQ_TYPE.JSON,
                NetworkUrl.COIN_INFO_ETH_LOCAL,
                ApiParamHelper.getEthJson(ApiParamHelper.ETH_REQ_METHOD.TRASNSACTION_COUNT, paramArray).toString());
        return response;
    }

    public static JSONObject ethGenPKey(Coin coin, String hash) {
        JSONObject jsonObject = new JSONObject();

        try {
            jsonObject.put("txHashData", hash);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        RequestHttpConnection requestHttpConnection = new RequestHttpConnection();
        JSONObject response = requestHttpConnection.request(COIN_TYPE.ETHEREUM,
                RequestHttpConnection.REQ_TYPE.JSON,
                NetworkUrl.ETH_GEN_KEY_URL,
                jsonObject.toString());
        return response;
    }

    public static JSONObject ethSendRawTransactionByGennedKey(String txData, String hash, JSONObject gennedKeyObject) {
        String sign = gennedKeyObject.optString("sign");
        String pubKey = gennedKeyObject.optString("pubKey");

        JSONObject sendObject = new JSONObject();
        try {
            sendObject.put("txData", txData);
            sendObject.put("txHashData", hash);
            sendObject.put("sign", sign);
            sendObject.put("pubKey", pubKey);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        RequestHttpConnection requestHttpConnection = new RequestHttpConnection();
        JSONObject response = requestHttpConnection.request(COIN_TYPE.ETHEREUM,
                RequestHttpConnection.REQ_TYPE.JSON,
                NetworkUrl.ETH_SEND_RAW_TRANSACTION_URL,
                sendObject.toString());
        return response;
    }
}
