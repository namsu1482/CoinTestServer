package coin.gson;

import java.util.List;


public class Root {
    public String address;
    public int total_received;
    public int total_sent;
    public int balance;
    public int unconfirmed_balance;
    public int final_balance;
    public int n_tx;
    public int unconfirmed_n_tx;
    public int final_n_tx;
    public boolean hasMore;
    public List<Tx> txs;

    public Root(String address,
                int total_received,
                int total_sent,
                int balance,
                int unconfirmed_balance,
                int final_balance,
                int n_tx,
                int unconfirmed_n_tx,
                int final_n_tx,
                boolean hasMore,
                List<Tx> txs) {
        this.address = address;
        this.total_received = total_received;
        this.total_sent = total_sent;
        this.balance = balance;
        this.unconfirmed_balance = unconfirmed_balance;
        this.final_balance = final_balance;
        this.n_tx = n_tx;
        this.unconfirmed_n_tx = unconfirmed_n_tx;
        this.final_n_tx = final_n_tx;
        this.hasMore = hasMore;
        this.txs = txs;
    }

    @Override
    public String toString() {
        return "Root{" +
                "address='" + address + '\'' +
                ", total_received=" + total_received +
                ", total_sent=" + total_sent +
                ", balance=" + balance +
                ", unconfirmed_balance=" + unconfirmed_balance +
                ", final_balance=" + final_balance +
                ", n_tx=" + n_tx +
                ", unconfirmed_n_tx=" + unconfirmed_n_tx +
                ", final_n_tx=" + final_n_tx +
                ", hasMore=" + hasMore +
                ", txs=" + txs +
                '}';
    }

    public String getAddress() {
        return address;
    }

    public int getTotal_received() {
        return total_received;
    }

    public int getTotal_sent() {
        return total_sent;
    }

    public int getBalance() {
        return balance;
    }

    public int getUnconfirmed_balance() {
        return unconfirmed_balance;
    }

    public int getFinal_balance() {
        return final_balance;
    }

    public int getN_tx() {
        return n_tx;
    }

    public int getUnconfirmed_n_tx() {
        return unconfirmed_n_tx;
    }

    public int getFinal_n_tx() {
        return final_n_tx;
    }

    public boolean isHasMore() {
        return hasMore;
    }

    public List<Tx> getTxs() {
        return txs;
    }
}
