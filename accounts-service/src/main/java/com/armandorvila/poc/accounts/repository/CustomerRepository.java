package com.armandorvila.poc.accounts.repository;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

import com.armandorvila.poc.accounts.domain.Customer;

@Repository
public interface CustomerRepository extends ReactiveMongoRepository<Customer, String> {

}
