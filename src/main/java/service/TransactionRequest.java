package service;

public abstract class TransactionRequest {

    public final long amount;

    TransactionRequest(long amount) {
        this.amount = amount;
    }
}
