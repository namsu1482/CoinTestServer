package coin.gson;

import com.google.gson.annotations.Expose;

import java.util.List;

public class Input{
    public String prev_hash;
    public int output_index;
    public int output_value;
    public Object sequence;
    public List<String> addresses;
    public String script_type;
    public int age;
    public List<String> witness;

    public Input(String prev_hash, int output_index, int output_value, Object sequence, List<String> addresses, String script_type, int age, List<String> witness) {
        this.prev_hash = prev_hash;
        this.output_index = output_index;
        this.output_value = output_value;
        this.sequence = sequence;
        this.addresses = addresses;
        this.script_type = script_type;
        this.age = age;
        this.witness = witness;
    }

    public String getPrev_hash() {
        return prev_hash;
    }

    public void setPrev_hash(String prev_hash) {
        this.prev_hash = prev_hash;
    }

    public int getOutput_index() {
        return output_index;
    }

    public void setOutput_index(int output_index) {
        this.output_index = output_index;
    }

    public int getOutput_value() {
        return output_value;
    }

    public void setOutput_value(int output_value) {
        this.output_value = output_value;
    }

    public Object getSequence() {
        return sequence;
    }

    public void setSequence(Object sequence) {
        this.sequence = sequence;
    }

    public List<String> getAddresses() {
        return addresses;
    }

    public void setAddresses(List<String> addresses) {
        this.addresses = addresses;
    }

    public String getScript_type() {
        return script_type;
    }

    public void setScript_type(String script_type) {
        this.script_type = script_type;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public List<String> getWitness() {
        return witness;
    }

    public void setWitness(List<String> witness) {
        this.witness = witness;
    }

    @Override
    public String toString() {
        return "Input{" +
                "prev_hash='" + prev_hash + '\'' +
                ", output_index=" + output_index +
                ", output_value=" + output_value +
                ", sequence=" + sequence +
                ", addresses=" + addresses +
                ", script_type='" + script_type + '\'' +
                ", age=" + age +
                ", witness=" + witness +
                '}';
    }
}
