package network.api;


import coin.COIN_TYPE;
import coin.Coin;
import network.NetworkUrl;
import network.RequestHttpConnection;
import org.json.JSONException;
import org.json.JSONObject;

public class CoinData {
    public static JSONObject getCoinData(Coin coin) {
        switch (coin.getCoin_type()) {
            case BITCOIN:
                return getBtcBalance(coin.getCoinAddress());

            case ETHEREUM:
                JSONObject resultObject = null;
                try {
                    resultObject = new JSONObject();
                    JSONObject balanceObject = getEthBalance(coin.getCoinAddress());
                    if (balanceObject == null) {
                        return null;
                    }
                    String balance = balanceObject.optString("result");

                    JSONObject transactionObject = getEthTransaction(coin.getCoinAddress());
                    if (transactionObject == null) {
                        return null;
                    }

                    resultObject.put("balance", balance);
                    resultObject.put("txlist", transactionObject.optJSONArray("result"));

                } catch (JSONException e) {
                    e.printStackTrace();

                } catch (Exception e) {
                    e.printStackTrace();
                }

                return resultObject;

            default:
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("result", "default");

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                return jsonObject;
        }
    }

    //BTC
    public static JSONObject GetBtcRawTransaction(Coin coin) {
        JSONObject response = null;
        RequestHttpConnection requestHttpConnection = new RequestHttpConnection();
        response = requestHttpConnection.request(COIN_TYPE.BITCOIN,
                RequestHttpConnection.REQ_TYPE.JSON,
                NetworkUrl.COIN_WALLET_BTC_LOCAL,
                ApiParamHelper.getBtcJson(ApiParamHelper.BTC_REQ_METHOD.LIST_UNSPENT, coin).toString());

        return response;
    }

    //BTC
    //blockcypher 이용
    private static JSONObject getBtcBalance(String coinAddress) {
        JSONObject response = null;
        RequestHttpConnection requestHttpConnection = new RequestHttpConnection();
        response = requestHttpConnection.request(COIN_TYPE.NONE,
                RequestHttpConnection.REQ_TYPE.JSON,
                NetworkUrl.getBtcBalanceUrl(coinAddress),
                "");


        return response;
    }

    //ETH
    //etherscan 이용
    private static JSONObject getEthBalance(String coinAddress) {
        JSONObject response = null;
        RequestHttpConnection requestHttpConnection = new RequestHttpConnection();
        response = requestHttpConnection.request(COIN_TYPE.NONE,
                RequestHttpConnection.REQ_TYPE.PARAM,
                NetworkUrl.GET_ETH_BASE_URL,
                ApiParamHelper.getEtherScanParam(coinAddress, ApiParamHelper.ETH_REQ_METHOD.ETHER_SCAN_BALANCE));

        return response;
    }

    private static JSONObject getEthTransaction(String coinAddress) {
        JSONObject response = null;
        RequestHttpConnection requestHttpConnection = new RequestHttpConnection();
        response = requestHttpConnection.request(COIN_TYPE.NONE,
                RequestHttpConnection.REQ_TYPE.PARAM,
                NetworkUrl.GET_ETH_BASE_URL,
                ApiParamHelper.getEtherScanParam(coinAddress, ApiParamHelper.ETH_REQ_METHOD.ETHER_SCAN_TX_LIST));

        return response;
    }
}
