package com.armandorvila.poc.accounts.service;

import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;

import java.net.URI;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.web.reactive.function.client.WebClient;

import com.armandorvila.poc.accounts.domain.AccountTransaction;
import com.armandorvila.poc.accounts.exception.ServiceNotFoundExeption;

import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

public class TransactionServiceTests {

	private static final String ACCOUNT_ID = "5ab698238d14c64fc85b3d38";
	
	private TransactionsService transactionService;
	
	private LoadBalancerClient loadBalancer;
	
	private WebClient webClient;
	
	@Before
	public void setUp() {
		webClient  = mock(WebClient.class, Mockito.RETURNS_DEEP_STUBS);
		loadBalancer = mock(LoadBalancerClient.class);
		transactionService = new TransactionsService(loadBalancer, webClient);
	}
	
	@Test(expected= ServiceNotFoundExeption.class)
	public void should_ThrowServiceNotFoundExeption_When_NoInstnaceFound() {
		
		given(loadBalancer.choose(TransactionsService.TRANSACTIONS_SERVICE_ID))
		  .willReturn(null);
		
		transactionService.getAccountTransactions(ACCOUNT_ID, 100, 0);
		
		then(webClient).should(never()).get();
	}
	
	@Test
	public void should_CallTransactionsService_When_InstnaceFound() {
		AccountTransaction transaction = new AccountTransaction("Some tx");
		ServiceInstance instance = mock(ServiceInstance.class);
		
		given(loadBalancer.choose(TransactionsService.TRANSACTIONS_SERVICE_ID))
		  .willReturn(instance);
		
		given(instance.getUri()).willReturn(URI.create("http://someuri"));
		
		given(webClient.get()
				.uri("http://someuri/transactions?limit={limit}&offset={offset}", 100, 0)
				.retrieve()
				.bodyToFlux(AccountTransaction.class)).willReturn(Flux.just(transaction));
		
		Flux<AccountTransaction> transactions = transactionService.getAccountTransactions(ACCOUNT_ID, 100, 0);
		
		StepVerifier.create(transactions)
		.expectNext(transaction)
		.expectNextCount(0)
		.verifyComplete();
	}
	
}
