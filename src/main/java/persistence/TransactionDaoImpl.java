package persistence;

import org.jdbi.v3.core.Jdbi;

public class TransactionDaoImpl {
    private Jdbi jdbi;

    public TransactionDaoImpl(Jdbi jdbi) {
        this.jdbi = jdbi;
    }
}
