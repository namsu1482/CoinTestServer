package network;

public class NetworkUrl {
    private static final String BTC_BASE_URL = "http://211.41.109.56:38332";
    public static final String BTC_TRANSACTION_URL = BTC_BASE_URL + "/wallet/";
    public static final String BTC_WALLET_URL = BTC_BASE_URL + "/wallet/smavis1";

    public static String COIN_INFO_ETH_LOCAL = "http://211.41.109.56:18541";
    public static String COIN_WALLET_BTC_LOCAL = BTC_BASE_URL + "/wallet/smavis1";

    private static String GET_BTC_BALANCE_BLOC_CYPHER = "https://api.blockcypher.com/v1/btc/test3/addrs/";
    public static String GET_BTC_BALANCE_BLOCK_CYPHER = "https://api.blockcypher.com/v1/btc/test3/addrs/";

    public static String GET_ETH_BASE_URL = "https://api-ropsten.etherscan.io/api";

    public static String ETH_GEN_KEY_URL = "http://211.41.109.56:8585/generateSignSimul";
    public static String ETH_SEND_RAW_TRANSACTION_URL = "http://211.41.109.56:8585/sendRawTransaction";

    public static String getBtcBalanceUrl(String coinAddress) {
        StringBuilder builder = new StringBuilder(GET_BTC_BALANCE_BLOC_CYPHER);
        builder.append(coinAddress + "/full");
        return builder.toString();
    }


}
