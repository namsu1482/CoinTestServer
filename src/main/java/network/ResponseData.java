package network;

import coin.COIN_TYPE;
import coin.Coin;
import coin.gson.Tx;
import network.api.CoinData;
import network.api.SendingTransaction;
import nfc.TxDataBuilder;
import org.json.JSONArray;
import org.json.JSONObject;
import util.Utils;

import java.math.BigDecimal;

public class ResponseData {
    public static JSONObject responseParser(JSONObject appReqObject) {
        JSONObject responseObject = new JSONObject();
        String reqType = appReqObject.optString("req_type");
        JSONArray coinList = appReqObject.optJSONArray("coin_list");

        if (reqType.equals("COIN_DATA")) {
            JSONArray parsedCoinArray = new JSONArray();
            for (int i = 0; i < coinList.length(); i++) {
                //app req
                JSONObject coinObject = coinList.optJSONObject(i);
                String coinType = coinObject.optString("coin_type");
                String coinAddress = coinObject.optString("coin_address");

                Coin coin = new Coin(COIN_TYPE.getCoinType(coinType), coinAddress);
                JSONObject parsedCoinType = new JSONObject();
                // testnet data
                JSONObject responseCoinObject = CoinData.getCoinData(coin);
                coin.parseCoinData(responseCoinObject);
                JSONObject parsedObject = coin.buildJsonObject();
                parsedCoinType.put("coin_type", coinType);
                parsedCoinType.put("data", parsedObject);
                parsedCoinArray.put(parsedCoinType);
            }
            responseObject.put("result", parsedCoinArray);

        } else if (reqType.equals("TRANSACTION")) {
            String toAddress = appReqObject.optString("to_address");
            String amt = appReqObject.optString("amt");
            //app req
            JSONObject coinObject = coinList.optJSONObject(0);
            String coinType = coinObject.optString("coin_type");
            String coinAddress = coinObject.optString("coin_address");
            Coin coin = new Coin(COIN_TYPE.getCoinType(coinType), coinAddress);


            if (coin.getCoin_type().equals(COIN_TYPE.BITCOIN)) {
                coin.setFee(0.0002);
                coin.parseBtcRawTransaction(CoinData.GetBtcRawTransaction(coin));

                TxDataBuilder txDataBuilder = new TxDataBuilder(toAddress, coin, Double.parseDouble(amt));
                txDataBuilder.getUnSignedTransaction();
                JSONObject jsonObject = new JSONObject();

            } else if (coin.getCoin_type().equals(COIN_TYPE.ETHEREUM)) {
                coin.setFee(Utils.convertEtcGas("21000", "20000000000"));

            }

//            responseObject = SendingTransaction.sendTransaction(toAddress,
//                    BigDecimal.valueOf(Double.parseDouble(amt)).doubleValue(),
//                    coin);

        }
        return responseObject;
    }
}
