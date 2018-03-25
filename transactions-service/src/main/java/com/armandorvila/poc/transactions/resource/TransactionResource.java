package com.armandorvila.poc.transactions.resource;

import javax.validation.Valid;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.armandorvila.poc.transactions.domain.Transaction;
import com.armandorvila.poc.transactions.repository.TransactionRepository;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
public class TransactionResource {
	
	private static final String DEFAULT_OFFSET = "0";
	private static final String DEFAULT_LIMIT = "100";
	
	private TransactionRepository transactionRepository;
	
	public TransactionResource(TransactionRepository transactionRepository) {
		this.transactionRepository = transactionRepository;
	}

	@GetMapping("/transactions")
	public Flux<Transaction> retrieveAccountTransactions(
			@RequestParam(required = true) String accountId,
			@RequestParam(defaultValue = DEFAULT_LIMIT) Integer limit,
			@RequestParam(defaultValue = DEFAULT_OFFSET) Integer offset) {
		
		return transactionRepository.findByAccountId(accountId, toPageRequest(offset, limit));
	}
	
	@PostMapping("/transactions")
	@ResponseStatus(HttpStatus.CREATED)
	public Mono<@Valid Transaction> registerTransaction(@Valid @RequestBody Transaction transaction) {
		final String accountId = transaction.getAccountId();
		
		return transactionRepository.findFirstByAccountIdOrderByTimestampDesc(accountId).limitRequest(1).elementAt(0)
		.map(tx -> {
			transaction.updateBalance(tx.getBalance());
			return transaction;
		}).flatMap(tx -> {
			return transactionRepository.save(tx);
		});
	}
	
	private Pageable toPageRequest(int offset, int limit) {
		int page = offset >= limit ? offset / limit : 0;
		return PageRequest.of(page, limit);
	}
}
