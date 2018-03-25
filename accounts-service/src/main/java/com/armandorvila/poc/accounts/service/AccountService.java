package com.armandorvila.poc.accounts.service;

import java.math.BigDecimal;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.armandorvila.poc.accounts.domain.Account;
import com.armandorvila.poc.accounts.domain.AccountTransaction;
import com.armandorvila.poc.accounts.domain.Customer;
import com.armandorvila.poc.accounts.exception.CustomerNotFoundExeption;
import com.armandorvila.poc.accounts.repository.AccountRepository;
import com.armandorvila.poc.accounts.repository.CustomerRepository;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class AccountService {

	private TransactionsService transactionsService;

	private CustomerRepository customerRepository;
	
	private AccountRepository accountRepository;

	public AccountService(TransactionsService transactionsService, AccountRepository accountRepository,
			CustomerRepository customerRepository) {

		this.customerRepository = customerRepository;
		this.accountRepository = accountRepository;
		this.transactionsService = transactionsService;
	}
	
	public Flux<Account> getCustomerAccounts(String customerId, Integer limit,Integer offset){
		final Pageable pagination = toPageRequest(offset, limit);
		
		if(customerId == null) {
			return accountRepository.findAll(pagination);
		}
		Mono<Customer> customer = customerRepository.findById(customerId);
		return accountRepository.findByCustomer(customer, pagination);
	}

	public Mono<Account> openCustomerAccount(String customerId, String description, BigDecimal initialCredit) {
		if(initialCredit.intValue() == 0) {
			return saveAccount(customerId, description);
    	}
		
		return saveAccount(customerId, description).flatMap(account -> {
				   return transactionsService
						   .registerTransaction(account.getId(), description, initialCredit)
						   .map(tx -> account.updateBalance(tx.getBalance()));
				  }).flatMap(accountRepository::save);
		
	}

	private Mono<Account> saveAccount(String customerId, String description) {
		return customerRepository.findById(customerId)
				.switchIfEmpty(customerDoesNotExistError())
				.map(customer -> new Account(description, customer))    
			    .flatMap(accountRepository::save);
	}

	private Mono<Customer> customerDoesNotExistError() {
		return Mono.error(new CustomerNotFoundExeption("The specified customer doesn't exist."));
	} 
	
	public Flux<AccountTransaction> getAccountTransactions(String accountId, Integer limit,Integer offset){
		return accountRepository.findById(accountId).flatMapMany(account -> 
	 	transactionsService.getAccountTransactions(account.getId(), limit, offset));
	}
	
	private Pageable toPageRequest(int offset, int limit) {
		int page = offset >= limit ? offset / limit : 0;
		return PageRequest.of(page, limit);
	}
}
