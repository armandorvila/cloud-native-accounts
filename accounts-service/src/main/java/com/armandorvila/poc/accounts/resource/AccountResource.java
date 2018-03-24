package com.armandorvila.poc.accounts.resource;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.armandorvila.poc.accounts.domain.Account;
import com.armandorvila.poc.accounts.domain.AccountTransaction;
import com.armandorvila.poc.accounts.repository.AccountRepository;
import com.armandorvila.poc.accounts.service.TransactionsService;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
public class AccountResource {
	
	private static final String DEFAULT_OFFSET = "0";
	private static final String DEFAULT_LIMIT = "100";

	private TransactionsService transactionsService;
	
	private AccountRepository accountRepository;

	public AccountResource(TransactionsService transactionsService, AccountRepository accountRepository) {
		this.accountRepository = accountRepository;
		this.transactionsService = transactionsService;
	}

	@GetMapping("/accounts")
	public Flux<Account> listAccounts(
			@RequestParam(defaultValue = DEFAULT_LIMIT) Integer limit,
			@RequestParam(defaultValue = DEFAULT_OFFSET) Integer offset) {
		
		Pageable pageable = toPageRequest(offset, limit);
		return accountRepository.findAll(pageable);
	}

	@GetMapping("/accounts/{id}")
	public Mono<Account> getAccount(@PathVariable("id") String id) {
		return accountRepository.findById(id);
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
