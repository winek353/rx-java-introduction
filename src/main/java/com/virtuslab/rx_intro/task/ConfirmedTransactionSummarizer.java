package com.virtuslab.rx_intro.task;


import io.reactivex.Observable;
import io.reactivex.Single;
import javafx.util.Pair;

import java.math.BigDecimal;
import java.util.function.Supplier;

/**
 * ConfirmedTransactionSummarizer is responsible for calculation of total confirmed transactions value.
 * HINT:
 * - Use zip operator to match transactions with confirmations. They will appear in order
 * - Filter only confirmed
 * - Aggregate value of confirmed transactions using reduce operator
 *
 * HINT2:
 * - add error handling which will wrap an error into SummarizationException
 *
 */
class ConfirmedTransactionSummarizer {

    private final Supplier<Observable<Transaction>> transactions;
    private final Supplier<Observable<Confirmation>> confirmations;

    ConfirmedTransactionSummarizer(Supplier<Observable<Transaction>> transactions,
                                   Supplier<Observable<Confirmation>> confirmations) {
        this.transactions = transactions;
        this.confirmations = confirmations;
    }


    Single<BigDecimal> summarizeConfirmedTransactions() {
        return transactions.get().zipWith(confirmations.get(), (t,c) -> new Pair<>(t.value, c.isConfirmed))
                .onErrorResumeNext(ex -> { return Observable.error(new SummarizationException("Booom"));})
                .filter(pair -> (pair.getValue()))
                .flatMap( pair -> Observable.just(pair.getKey()))
                .reduce(BigDecimal::add)
                .toSingle();
    }

    static class SummarizationException extends RuntimeException {

        public SummarizationException(String message) {
            super(message);
        }
    }
}
