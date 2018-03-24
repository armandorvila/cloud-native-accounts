package com.armandorvila.poc.accounts.service;

import static org.springframework.http.MediaType.APPLICATION_JSON;

import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.armandorvila.poc.accounts.domain.AccountTransaction;
import com.armandorvila.poc.accounts.exception.ServiceNotFoundExeption;

import reactor.core.publisher.Flux;

@Service
public class TransactionsService {
	
	private static final String TRANSACTIONS_SERVICE_PATH = "/transactions?limit={limit}&offset={offset}";
	private static final String TRANSACTIONS_SERVICE_ID = "transactions-service";

	private LoadBalancerClient loadBalancer;

	public TransactionsService(LoadBalancerClient loadBalancer) {
		this.loadBalancer = loadBalancer;
	}

	public Flux<AccountTransaction> getAccountTransactions(String accountId, Integer limit, Integer offset) {
		return WebClient.create(getServiceUrl())
				.get()
				.uri(TRANSACTIONS_SERVICE_PATH, limit, offset)
				.accept(APPLICATION_JSON)
				.retrieve()
				.bodyToFlux(AccountTransaction.class);
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
