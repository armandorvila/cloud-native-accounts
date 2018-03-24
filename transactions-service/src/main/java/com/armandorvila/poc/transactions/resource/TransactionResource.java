package com.armandorvila.poc.transactions.resource;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.armandorvila.poc.transactions.domain.Transaction;
import com.armandorvila.poc.transactions.repository.TransactionRepository;

import reactor.core.publisher.Flux;

@RestController
public class TransactionResource {
	
	private static final String DEFAULT_OFFSET = "0";
	private static final String DEFAULT_LIMIT = "100";
	
	private TransactionRepository transactionRepository;
	
	public TransactionResource(TransactionRepository transactionRepository) {
		this.transactionRepository = transactionRepository;
	}

	@GetMapping("/transactions")
	public Flux<Transaction> listTransactions(
			@RequestParam(required = true) String accountId,
			@RequestParam(defaultValue = DEFAULT_LIMIT) Integer limit,
			@RequestParam(defaultValue = DEFAULT_OFFSET) Integer offset) {
		
		Pageable pageable = toPageRequest(offset, limit);
		return transactionRepository.findAll(pageable);
	}
	
	private Pageable toPageRequest(int offset, int limit) {
		int page = offset >= limit ? offset / limit : 0;
		return PageRequest.of(page, limit);
	}
}
