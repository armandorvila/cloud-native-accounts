package com.armandorvila.poc.accounts.resource;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.OK;

import javax.validation.Valid;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.armandorvila.poc.accounts.domain.Account;
import com.armandorvila.poc.accounts.domain.AccountTransaction;
import com.armandorvila.poc.accounts.domain.Customer;
import com.armandorvila.poc.accounts.repository.AccountRepository;
import com.armandorvila.poc.accounts.repository.CustomerRepository;
import com.armandorvila.poc.accounts.resource.dto.AccountDTO;
import com.armandorvila.poc.accounts.service.TransactionsService;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
public class AccountResource {
	
	private static final String DEFAULT_OFFSET = "0";
	private static final String DEFAULT_LIMIT = "100";

	private TransactionsService transactionsService;
	
	private CustomerRepository customerRepository;
	private AccountRepository accountRepository;

	public AccountResource(TransactionsService transactionsService, AccountRepository accountRepository, 
			CustomerRepository customerRepository) {
		this.customerRepository = customerRepository;
		this.accountRepository = accountRepository;
		this.transactionsService = transactionsService;
	}
	
	//TODO POST Transaction and update balance.
	@PostMapping("/accounts")
	public Mono<ResponseEntity<Account>> createAccount(@Valid @RequestBody AccountDTO accountDTO) {
		
		return customerRepository.findById(accountDTO.getCustomerId())
				.map(c -> new Account(accountDTO.getDescription(), c))
			    .flatMap(accountRepository::save)
			    .map(a -> new ResponseEntity<>(a, CREATED))
			    .switchIfEmpty(Mono.just(new ResponseEntity<>(BAD_REQUEST)));		    
	}
	
	@GetMapping("/accounts")
	public Flux<Account> listAccounts(
			@RequestParam(required = false) String customerId,
			@RequestParam(defaultValue = DEFAULT_LIMIT) Integer limit,
			@RequestParam(defaultValue = DEFAULT_OFFSET) Integer offset) {
		
		final Pageable pagination = toPageRequest(offset, limit);
		
		if(customerId == null) {
			return accountRepository.findAll(pagination);
		}
		
		Mono<Customer> customer = customerRepository.findById(customerId);
		return accountRepository.findByCustomer(customer, pagination);
	}

	@GetMapping("/accounts/{accountId}")
	public Mono<ResponseEntity<Account>> getAccount(@PathVariable("accountId") String id) {
		return accountRepository.findById(id)
				.map(account -> new ResponseEntity<>(account, OK))
				.defaultIfEmpty(new ResponseEntity<>(NOT_FOUND));
	}

	@GetMapping("/accounts/{accountId}/transactions")
	public Flux<AccountTransaction> getAccountTransactions(
			@PathVariable("accountId") String accountId,
			@RequestParam(defaultValue = DEFAULT_LIMIT) Integer limit,
			@RequestParam(defaultValue = DEFAULT_OFFSET) Integer offset) {
		return transactionsService.getAccountTransactions(accountId, limit, offset);
	}
	
	private Pageable toPageRequest(int offset, int limit) {
		int page = offset >= limit ? offset / limit : 0;
		return PageRequest.of(page, limit);
	}
}
