package coin;

import java.io.Serializable;

public enum COIN_TYPE implements Serializable {
    BITCOIN("BTC", "비트코인"),
    ETHEREUM("ETH", "이더리움"),
    ADA("ADA", "에이다"),
    DOGECOIN("DOGE", "도지코인"),
    RIPPLE("XRP", "리플"),
    POLKADOT("DOT", "폴카닷"),
    LITECOIN("LTC", "라이트코인"),
    BITCOIN_CASH("BCH", "비트코인캐시"),
    THETA("THETA", "세타토큰"),
    FILECOIN("FIL", "파일코인"),
    EOS("EOS", "이오스"),
    TRON("TRX", "트론"),
    MAKER("MKR", "메이커"),
    IOTA("MIOTA", "아이오타"),
    STELLA_LUMENS("XLM", "스텔라루멘"),
    BASIC_ATTENTION_TOKEN("BAT", "베이직어텐션토큰"),
    ANKOR("ANKR", "앵커"),
    STEEM("STEEM", "스팀"),
    NEO("NEO", "네오"),
    STEX("STX", "스텍스"),
    TSHP("TSHP", "트웰브쉽스"),


    //    BINANCE("BNB", "BINANCE"),
//    CARDANO("ADA", "CARDANO"),
//    MATIC("MATIC", "MATIC"),
//    ETHER_CLASSIC("ETC", "ETHER_CLASSIC"),
//
//    POLKADOT("DOT", "POLKADOT"),
    NONE("", "");


    String coinSymbol = "";
    String coinName = "";
    boolean checked = false;

    COIN_TYPE(String coinSymbol, String coinName) {
        this.coinSymbol = coinSymbol;
        this.coinName = coinName;
    }

    public String getCoinSymbol() {
        return coinSymbol;
    }

    public String getCoinName() {
        return coinName;
    }

    public static COIN_TYPE getCoinTypeByHex(String coinTypeHex) {
        switch (coinTypeHex) {
            case "80000000":
                return BITCOIN;

            case "80000003":
                return DOGECOIN;

            case "8000003C":
                return ETHEREUM;

//            case "800002CA":
//                return BINANCE;
//            case "80000717":
//                return CARDANO;
//
//            case "800003C6":
//                return MATIC;
//
//            case "8000003D":
//                return ETHER_CLASSIC;
//
//            case "80000090":
//                return RIPPLE;
//
//            case "80000162":
//                return POLKADOT;

            default:
                return null;
        }
    }

    public static COIN_TYPE getCoinType(String coinSymbol) {
        switch (coinSymbol) {
            case "BTC":
                return BITCOIN;
            case "ETH":
                return ETHEREUM;
            case "DOGE":
                return DOGECOIN;
//            case "BNB":
//                return BINANCE;
//            case "ADA":
//                return CARDANO;
//            case "MATIC":
//                return MATIC;
//            case "ETC":
//                return ETHER_CLASSIC;
            case "XRP":
                return RIPPLE;
//            case "DOT":
//                return POLKADOT;

            default:
                return null;
        }
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }


}

