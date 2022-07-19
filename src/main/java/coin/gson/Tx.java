package coin.gson;

import java.util.Date;
import java.util.List;

public class Tx {
    public String block_hash;
    public int block_height;
    public int block_index;
    public String hash;
    public List<String> addresses;
    public int total;
    public int fees;
    public int size;
    public int vsize;
    public String preference;
    public String relayed_by;
    public Date confirmed;
    public Date received;
    public int ver;
    public boolean double_spend;
    public int vin_sz;
    public int vout_sz;
    public int confirmations;
    public int confidence;
    public List<Input> inputs;
    public List<Output> outputs;

    public Tx(String block_hash,
              int block_height,
              int block_index,
              String hash,
              List<String> addresses,
              int total,
              int fees,
              int size,
              int vsize,
              String preference,
              String relayed_by,
              Date confirmed,
              Date received,
              int ver,
              boolean double_spend,
              int vin_sz,
              int vout_sz,
              int confirmations,
              int confidence,
              List<Input> inputs,
              List<Output> outputs) {
        this.block_hash = block_hash;
        this.block_height = block_height;
        this.block_index = block_index;
        this.hash = hash;
        this.addresses = addresses;
        this.total = total;
        this.fees = fees;
        this.size = size;
        this.vsize = vsize;
        this.preference = preference;
        this.relayed_by = relayed_by;
        this.confirmed = confirmed;
        this.received = received;
        this.ver = ver;
        this.double_spend = double_spend;
        this.vin_sz = vin_sz;
        this.vout_sz = vout_sz;
        this.confirmations = confirmations;
        this.confidence = confidence;
        this.inputs = inputs;
        this.outputs = outputs;
    }

    @Override
    public String toString() {
        return "Tx{" +
                "block_hash='" + block_hash + '\'' +
                ", block_height=" + block_height +
                ", block_index=" + block_index +
                ", hash='" + hash + '\'' +
                ", addresses=" + addresses +
                ", total=" + total +
                ", fees=" + fees +
                ", size=" + size +
                ", vsize=" + vsize +
                ", preference='" + preference + '\'' +
                ", relayed_by='" + relayed_by + '\'' +
                ", confirmed=" + confirmed +
                ", received=" + received +
                ", ver=" + ver +
                ", double_spend=" + double_spend +
                ", vin_sz=" + vin_sz +
                ", vout_sz=" + vout_sz +
                ", confirmations=" + confirmations +
                ", confidence=" + confidence +
                ", inputs=" + inputs +
                ", outputs=" + outputs +
                '}';
    }

    public String getBlock_hash() {
        return block_hash;
    }

    public int getBlock_height() {
        return block_height;
    }

    public int getBlock_index() {
        return block_index;
    }

    public String getHash() {
        return hash;
    }

    public List<String> getAddresses() {
        return addresses;
    }

    public int getTotal() {
        return total;
    }

    public int getFees() {
        return fees;
    }

    public int getSize() {
        return size;
    }

    public int getVsize() {
        return vsize;
    }

    public String getPreference() {
        return preference;
    }

    public String getRelayed_by() {
        return relayed_by;
    }

    public Date getConfirmed() {
        return confirmed;
    }

    public Date getReceived() {
        return received;
    }

    public int getVer() {
        return ver;
    }

    public boolean isDouble_spend() {
        return double_spend;
    }

    public int getVin_sz() {
        return vin_sz;
    }

    public int getVout_sz() {
        return vout_sz;
    }

    public int getConfirmations() {
        return confirmations;
    }

    public int getConfidence() {
        return confidence;
    }

    public List<Input> getInputs() {
        return inputs;
    }

    public List<Output> getOutputs() {
        return outputs;
    }

    public void setBlock_hash(String block_hash) {
        this.block_hash = block_hash;
    }

    public void setBlock_height(int block_height) {
        this.block_height = block_height;
    }

    public void setBlock_index(int block_index) {
        this.block_index = block_index;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public void setAddresses(List<String> addresses) {
        this.addresses = addresses;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public void setFees(int fees) {
        this.fees = fees;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public void setVsize(int vsize) {
        this.vsize = vsize;
    }

    public void setPreference(String preference) {
        this.preference = preference;
    }

    public void setRelayed_by(String relayed_by) {
        this.relayed_by = relayed_by;
    }

    public void setConfirmed(Date confirmed) {
        this.confirmed = confirmed;
    }

    public void setReceived(Date received) {
        this.received = received;
    }

    public void setVer(int ver) {
        this.ver = ver;
    }

    public void setDouble_spend(boolean double_spend) {
        this.double_spend = double_spend;
    }

    public void setVin_sz(int vin_sz) {
        this.vin_sz = vin_sz;
    }

    public void setVout_sz(int vout_sz) {
        this.vout_sz = vout_sz;
    }

    public void setConfirmations(int confirmations) {
        this.confirmations = confirmations;
    }

    public void setConfidence(int confidence) {
        this.confidence = confidence;
    }

    public void setInputs(List<Input> inputs) {
        this.inputs = inputs;
    }

    public void setOutputs(List<Output> outputs) {
        this.outputs = outputs;
    }
}
