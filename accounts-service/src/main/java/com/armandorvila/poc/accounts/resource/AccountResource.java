package com.armandorvila.poc.accounts.resource;

import java.net.URI;

import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;

import com.armandorvila.poc.accounts.domain.Account;
import com.armandorvila.poc.accounts.domain.AccountTransaction;
import com.armandorvila.poc.accounts.repository.AccountRepository;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
public class AccountResource {

	private LoadBalancerClient loadBalancer;
	
	private AccountRepository accountRepository;

	public AccountResource(LoadBalancerClient loadBalancer, AccountRepository accountRepository) {
		this.accountRepository = accountRepository;
		this.loadBalancer = loadBalancer;
	}

	@GetMapping("/accounts")
	public Flux<Account> listAccounts() {
		return accountRepository.findAll();
	}

	@GetMapping("/accounts/{id}")
	public Mono<Account> getAccount(@PathVariable("id") String id) {
		return accountRepository.findById(id);
	}

	@GetMapping("/accounts/{id}/transactions")
	public Flux<AccountTransaction> getAccountTransactions(@PathVariable("id") String id) {

		return WebClient.create().get()
				.uri(getTransactionServiceUri().resolve("/transactions"))
				.accept(MediaType.APPLICATION_JSON)
				.retrieve()
				.bodyToFlux(AccountTransaction.class);

	}

	private URI getTransactionServiceUri() {
		return loadBalancer.choose("TRANSACTIONS-SERVICE").getUri();
	}
}
