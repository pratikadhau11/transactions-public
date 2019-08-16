package service;

public class ToAccountTransactionRequest extends TransactionRequest {
    public final String to;

    public ToAccountTransactionRequest(long amount, String to) {
        super(amount);
        this.to = to;
    }
}
