package nfc.bitcoin;

public class TxOutPut {
    String amt;
    String scriptCode;

    public TxOutPut(String amt, String scriptCode) {
        this.amt = amt;
        this.scriptCode = scriptCode;
    }

    public String getAmt() {
        return amt;
    }

    public void setAmt(String amt) {
        this.amt = amt;
    }

    public String getScriptCode() {
        return scriptCode;
    }

    public void setScriptCode(String scriptCode) {
        this.scriptCode = scriptCode;
    }

    @Override
    public String toString() {
        return "TxOutPut{" +
                "amt='" + amt + '\'' +
                ", scriptCode='" + scriptCode + '\'' +
                '}';
    }

    public String getSerializedOutput() {
        return amt + scriptCode;
    }

}
