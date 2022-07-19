package coin.gson;

import java.util.List;

public class Output{
    public int value;
    public String script;
    public List<String> addresses;
    public String script_type;
    public String spent_by;

    public Output(int value, String script, List<String> addresses, String script_type, String spent_by) {
        this.value = value;
        this.script = script;
        this.addresses = addresses;
        this.script_type = script_type;
        this.spent_by = spent_by;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public String getScript() {
        return script;
    }

    public void setScript(String script) {
        this.script = script;
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

    public String getSpent_by() {
        return spent_by;
    }

    public void setSpent_by(String spent_by) {
        this.spent_by = spent_by;
    }

    @Override
    public String toString() {
        return "Output{" +
                "value=" + value +
                ", script='" + script + '\'' +
                ", addresses=" + addresses +
                ", script_type='" + script_type + '\'' +
                ", spent_by='" + spent_by + '\'' +
                '}';
    }
}
