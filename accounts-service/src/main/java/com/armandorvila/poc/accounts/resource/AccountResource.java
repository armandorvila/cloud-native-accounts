package com.armandorvila.poc.accounts.resource;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.OK;

import java.math.BigDecimal;

import javax.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.armandorvila.poc.accounts.domain.Account;
import com.armandorvila.poc.accounts.domain.AccountTransaction;
import com.armandorvila.poc.accounts.exception.CustomerNotFoundExeption;
import com.armandorvila.poc.accounts.repository.AccountRepository;
import com.armandorvila.poc.accounts.resource.dto.AccountDTO;
import com.armandorvila.poc.accounts.service.AccountService;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
public class AccountResource {
	
	private static final String DEFAULT_OFFSET = "0";
	private static final String DEFAULT_LIMIT = "100";

	private AccountService accountService;
	private AccountRepository accountRepository;
	
	public AccountResource(AccountService accountService, AccountRepository accountRepository) {
		this.accountService = accountService;
		this.accountRepository = accountRepository;
	}
	
	@PostMapping("/accounts")
	@ResponseStatus(CREATED)
	public Mono<Account> openCustomerAccount(@Valid @RequestBody AccountDTO accountDTO) {
		final String customerId = accountDTO.getCustomerId();
		final String description = accountDTO.getDescription();
		final BigDecimal initialCredit = accountDTO.getInitialCredit();
		
		return accountService.openCustomerAccount(customerId, description, initialCredit);
	}
	
	@GetMapping("/accounts")
	public Flux<Account> getCustomerAccounts(
			@RequestParam(required = false) String customerId,
			@RequestParam(defaultValue = DEFAULT_LIMIT) Integer limit,
			@RequestParam(defaultValue = DEFAULT_OFFSET) Integer offset) {
	
		return accountService.getCustomerAccounts(customerId, limit, offset);
	}

	@GetMapping("/accounts/{accountId}")
	public Mono<ResponseEntity<Account>> getAccount(@PathVariable("accountId") String accountId) {
		return accountRepository.findById(accountId)
				.map(account -> new ResponseEntity<>(account, OK))
				.defaultIfEmpty(new ResponseEntity<>(NOT_FOUND));
	}

	@GetMapping("/accounts/{accountId}/transactions")
	public Flux<AccountTransaction> getAccountTransactions(
			@PathVariable("accountId") String accountId,
			@RequestParam(defaultValue = DEFAULT_LIMIT) Integer limit,
			@RequestParam(defaultValue = DEFAULT_OFFSET) Integer offset) {
		
		return accountService.getAccountTransactions(accountId, limit, offset);
	}
	
	@ExceptionHandler(CustomerNotFoundExeption.class)
	public ResponseEntity<?> CustomerNotFoundExeption(CustomerNotFoundExeption ex) {
		return new ResponseEntity<>(BAD_REQUEST);
	}
}
