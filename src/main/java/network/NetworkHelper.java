package network;

import coin.Coin;
import network.api.CoinData;
import network.api.SendingTransaction;
import org.json.JSONObject;

public class NetworkHelper {

    public interface NetworkListener {
        void onComplete(JSONObject result);

    }

    interface BackgroundTask {
        JSONObject onBackgroundTask();
    }


    public static void getCoinData(final Coin coin, NetworkListener networkListener) {
        NetworkTaskHelper networkTask = new NetworkTaskHelper(new BackgroundTask() {
            @Override
            public JSONObject onBackgroundTask() {
                return CoinData.getCoinData(coin);
            }
        }, networkListener);
        networkTask.execute();

    }

    public static void getBtcRawTransaction(final Coin coin, NetworkListener networkListener) {
        NetworkTaskHelper networkTask = new NetworkTaskHelper(new BackgroundTask() {
            @Override
            public JSONObject onBackgroundTask() {
                return CoinData.GetBtcRawTransaction(coin);
            }
        }, networkListener);
        networkTask.execute();

    }

    public static void sendTransaction(final String toAddress,
                                       final double amt,
                                       Coin coin,
                                       NetworkListener networkListener) {
        NetworkTaskHelper networkTask = new NetworkTaskHelper(new BackgroundTask() {
            @Override
            public JSONObject onBackgroundTask() {
                return SendingTransaction.sendTransaction(toAddress, amt, coin);
            }
        }, networkListener);
        networkTask.execute();

    }

//    public static void getAllCoinData(Map<COIN_TYPE, Coin> coinMap, final NetworkListener networkListener) {
//        final Iterator<Map.Entry<COIN_TYPE, Coin>> iterator = coinMap.entrySet().iterator();
//        final JSONObject jsonObject = new JSONObject();
//        final JSONArray jsonArray = new JSONArray();
//        new AsyncTask<Void, JSONObject, JSONObject>() {
//            @Override
//            protected JSONObject doInBackground(Void... voids) {
//                while (iterator.hasNext()) {
//                    Coin coin = iterator.next().getValue();
//                    if (coin.isEnable()) {
//                        JSONObject coinData = CoinData.getCoinData(coin);
//                        coin.parseCoinData(coinData);
//                        JSONObject coinObject = new JSONObject();
//                        try {
//                            coinObject.put("coin_type", coin.getCoin_type().getCoinSymbol());
//                            jsonArray.put(coinObject);
//                        } catch (JSONException e) {
//                            e.printStackTrace();
//                        }
//                    }
//                }
//
//                try {
//                    jsonObject.put("result", jsonArray);
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//                return jsonObject;
//            }
//
//            @Override
//            protected void onPostExecute(JSONObject jsonObject) {
//                super.onPostExecute(jsonObject);
//                networkListener.onComplete(jsonObject);
//            }
//        }.execute();
//
//    }

    private static class NetworkTaskHelper {
        Thread thread;
        BackgroundTask backgroundTask;
        NetworkListener networkListener;

        public NetworkTaskHelper(BackgroundTask backgroundTask, NetworkListener networkListener) {
            this.backgroundTask = backgroundTask;
            this.networkListener = networkListener;

        }

        public void execute() {
            thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    JSONObject response = backgroundTask.onBackgroundTask();
                    networkListener.onComplete(response);
                    thread.interrupt();
                }
            });
            thread.start();
        }
    }
}
