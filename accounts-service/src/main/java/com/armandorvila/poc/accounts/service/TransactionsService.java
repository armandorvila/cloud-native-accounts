package com.armandorvila.poc.accounts.service;

import java.math.BigDecimal;

import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.armandorvila.poc.accounts.domain.AccountTransaction;
import com.armandorvila.poc.accounts.exception.ServiceNotFoundExeption;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class TransactionsService {
	
	protected static final String GET_TRANSACTIONS_PATH = "/transactions?accountId={accountId}&limit={limit}&offset={offset}";
	
	protected static final String REGISTER_TRANSACTION_PATH = "/transactions";
	
	protected static final String TRANSACTIONS_SERVICE_ID = "transactions-service";

	private LoadBalancerClient loadBalancer;
	private WebClient webClient;

	public TransactionsService(LoadBalancerClient loadBalancer, WebClient webClient) {
		this.loadBalancer = loadBalancer;
		this.webClient = webClient;
	}

	public Flux<AccountTransaction> getAccountTransactions(String accountId, Integer limit, Integer offset) {
		final String uri = getServiceUrl() + GET_TRANSACTIONS_PATH;
		
		return webClient.get()
				.uri(uri, accountId, limit, offset)
				.retrieve()
				.bodyToFlux(AccountTransaction.class);
	}
	
	public Mono<AccountTransaction> registerTransaction(String accountId, String description, BigDecimal value){
		final String uri = getServiceUrl() + REGISTER_TRANSACTION_PATH;
		
		AccountTransaction tx = new AccountTransaction();
		
		tx.setAccountId(accountId);
		tx.setDescription(description);
		tx.setValue(value);
		
		return webClient.post().uri(uri).syncBody(tx).retrieve().bodyToMono(AccountTransaction.class);
	}
	
	private String getServiceUrl() {
		ServiceInstance instance = loadBalancer.choose(TRANSACTIONS_SERVICE_ID);

		if (instance == null) {
			throw new ServiceNotFoundExeption(
					String.format("Unable to find instance for service %s", TRANSACTIONS_SERVICE_ID));
		}
		return instance.getUri().toString();
	}
}
