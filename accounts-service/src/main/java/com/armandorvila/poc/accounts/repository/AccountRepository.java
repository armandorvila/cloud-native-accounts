package com.armandorvila.poc.accounts.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

import com.armandorvila.poc.accounts.domain.Account;
import com.armandorvila.poc.accounts.domain.Customer;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface AccountRepository extends ReactiveMongoRepository<Account, String> {

    @Query("{ id: { $exists: true }}")
    Flux<Account> findAll(Pageable page);

	Flux<Account> findByCustomer(Mono<Customer> customer, Pageable page);
}
