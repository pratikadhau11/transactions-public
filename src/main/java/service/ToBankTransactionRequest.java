package service;

public class ToBankTransactionRequest extends TransactionRequest {
    public final String to;

    public ToBankTransactionRequest(long amount, String to) {
        super(amount);
        this.to = to;
    }
}
