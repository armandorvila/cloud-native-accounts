package com.armandorvila.poc.transactions.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

import com.armandorvila.poc.transactions.domain.Transaction;

import reactor.core.publisher.Flux;

@Repository
public interface TransactionRepository extends ReactiveMongoRepository<Transaction, String> {

    Flux<Transaction> findByAccountId(String accountId, Pageable page);
    
    Flux<Transaction> findFirstByAccountIdOrderByTimestampDesc(String accountId);
}
