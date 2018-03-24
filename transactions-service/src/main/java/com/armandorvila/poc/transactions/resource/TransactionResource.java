package com.armandorvila.poc.transactions.resource;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.armandorvila.poc.transactions.domain.Transaction;
import com.armandorvila.poc.transactions.repository.TransactionRepository;

import reactor.core.publisher.Flux;

@RestController
public class TransactionResource {
	
	private TransactionRepository transactionRepository;
	
	public TransactionResource(TransactionRepository transactionRepository) {
		this.transactionRepository = transactionRepository;
	}

	@GetMapping("/transactions")
	public Flux<Transaction> listTransactions() {
		return transactionRepository.findAll();
	}
}
