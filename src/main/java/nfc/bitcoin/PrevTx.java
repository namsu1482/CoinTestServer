package nfc.bitcoin;

public class PrevTx {
    String hash;
    String index;
    String sigScriptLength;
    String sequence;
    String amt;
    String scriptCode;

    public PrevTx(String hash, String index, String sigScriptLength, String sequence, String amt, String scriptKey) {
        this.hash = hash;
        this.index = index;
        this.sigScriptLength = sigScriptLength;
        this.sequence = sequence;
        this.amt = amt;
        this.scriptCode = "1976a914" + scriptKey.substring(4) + "88ac";
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public String getIndex() {
        return index;
    }

    public void setIndex(String index) {
        this.index = index;
    }

    public String getSigScriptLength() {
        return sigScriptLength;
    }

    public void setSigScriptLength(String sigScriptLength) {
        this.sigScriptLength = sigScriptLength;
    }

    public String getSequence() {
        return sequence;
    }

    public void setSequence(String sequence) {
        this.sequence = sequence;
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

    @Override
    public String toString() {
        return "PrevTx{" +
                "hash='" + hash + '\'' +
                ", index='" + index + '\'' +
                ", sigScriptLength='" + sigScriptLength + '\'' +
                ", sequence='" + sequence + '\'' +
                ", amt='" + amt + '\'' +
                ", scriptCode='" + scriptCode + '\'' +
                '}';
    }

    public String getOutputPoint() {
        return hash + index;
    }
}
