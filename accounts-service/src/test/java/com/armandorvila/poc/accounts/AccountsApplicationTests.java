package com.armandorvila.poc.accounts;

import static java.util.Arrays.asList;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8;

import java.math.BigDecimal;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.WebTestClient;

import com.armandorvila.poc.accounts.domain.Account;
import com.armandorvila.poc.accounts.domain.AccountTransaction;
import com.armandorvila.poc.accounts.domain.Customer;
import com.armandorvila.poc.accounts.exception.ServiceNotFoundExeption;
import com.armandorvila.poc.accounts.repository.AccountRepository;
import com.armandorvila.poc.accounts.repository.CustomerRepository;
import com.armandorvila.poc.accounts.resource.dto.AccountDTO;
import com.armandorvila.poc.accounts.service.TransactionsService;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class AccountsApplicationTests {

	@Autowired
	private CustomerRepository customerRepository;
	
	@Autowired
	private AccountRepository accountRepository;
	
	@MockBean
	private TransactionsService transactionsService;
	
	@Autowired
	protected WebTestClient webClient;
	
	private Mono<Customer> customer;
	
	private Flux<Account> accounts;
	
	@Before
	public void setUp() {
		customer = this.customerRepository.deleteAll()
				.then(customerRepository.save(new Customer("some@email.com", "some", "customer")));
		
		StepVerifier.create(customer).expectNextCount(1).verifyComplete();
		
		accounts = this.accountRepository.deleteAll()
				.thenMany(accountRepository.saveAll(asList(
						new Account("Some account", customer.block()),
						new Account("Some other account", customer.block()))));
		
		StepVerifier.create(accounts).expectNextCount(2).verifyComplete();
	}
	
	@Test  
	public void should_GetAccountTransactions_WhenGivenValidAccountId() {
		final String accountId = accounts.blockFirst().getId();
		
		given(transactionsService.getAccountTransactions(anyString(), anyInt(), anyInt()))
		  .willReturn(Flux.just(new AccountTransaction(accountId, new BigDecimal(200.0), "Some transaction")));
	    
		 webClient.get().uri("/accounts/{acountId}/transactions", accountId)
						.accept(APPLICATION_JSON)
						.exchange()
						.expectStatus().isOk()
						.expectHeader()
						.contentType(APPLICATION_JSON_UTF8)
						.expectBodyList(AccountTransaction.class)
						.hasSize(1);
		 
		 then(transactionsService).should(times(1)).getAccountTransactions(anyString(), anyInt(), anyInt());
	}
	
	@Test  
	public void should_GetBadGateway_WhenTransactionServiceNotFound() throws Exception {
		final String accountId = accounts.blockFirst().getId();
		
		given(transactionsService.getAccountTransactions(accountId, 100, 0))
		  .willThrow(ServiceNotFoundExeption.class);
	    
		 webClient.get().uri("/accounts/{accountId}/transactions", accountId)
						.accept(APPLICATION_JSON)
						.exchange()
						.expectStatus().isEqualTo(HttpStatus.BAD_GATEWAY);
		 
		 then(transactionsService).should(times(1)).getAccountTransactions(accountId, 100, 0);
	}
	
	@Test  
	public void should_GetInternalServerError_WhenIllegalArgumentException() throws Exception {
		final String accountId = accounts.blockFirst().getId();
		
		given(transactionsService.getAccountTransactions(accountId, 100, 0))
		  .willThrow(IllegalArgumentException.class);
	    
		 webClient.get().uri("/accounts/{accountId}/transactions", accountId)
						.accept(APPLICATION_JSON)
						.exchange()
						.expectStatus().isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
		 
		 then(transactionsService).should(times(1)).getAccountTransactions(accountId, 100, 0);
	}
	
	@Test  
	public void should_GetEmptyAccountsList_When_CustomerIdDoesNotExist() throws Exception {
		 webClient.get().uri("/accounts?customerId={customerId}", "someID")
						.accept(APPLICATION_JSON)
						.exchange()
						.expectStatus().isOk()
						.expectHeader().contentType(APPLICATION_JSON_UTF8)
						.expectBodyList(Account.class)
						.hasSize(0);
	}
	
	@Test  
	public void should_GetCustomerAccounts_When_GivenValidCustomerId() throws Exception {
		final Customer customer = this.customer.block();
		 webClient.get().uri("/accounts?customerId={customerId}", customer.getId())
						.accept(APPLICATION_JSON)
						.exchange()
						.expectStatus().isOk()
						.expectHeader().contentType(APPLICATION_JSON_UTF8)
						.expectBodyList(Account.class)
						.hasSize(2);
	}
	
	@Test  
	public void should_CreateAccount_When_GivenValidCustomer() throws Exception {
		final String customerId = customer.block().getId();
		final BigDecimal initialCredit = new BigDecimal(2000.0);
		final String description = "Some Account";
		
		given(transactionsService.registerTransaction(anyString(), anyString(), any(BigDecimal.class)))
		 .willAnswer(invocation -> {
			 String accountId = (String) invocation.getArguments()[0];
			 return Mono.just(new AccountTransaction(accountId, initialCredit, description));
			 });
				
		webClient.post().uri("/accounts")
					.accept(APPLICATION_JSON)
					.syncBody(new AccountDTO(customerId, description, initialCredit))
					.exchange()
					.expectStatus().isCreated()
					.expectBody()
					.jsonPath("$.id").isNotEmpty()
					.jsonPath("$.createdAt").isNotEmpty()
					.jsonPath("$.lastModifiedAt").isNotEmpty()
					.jsonPath("$.description").isEqualTo(description)
					.jsonPath("$.customer").isNotEmpty()
					.jsonPath("$.customer.id").isEqualTo(customerId);
		
		 then(transactionsService).should(times(1)).registerTransaction(anyString(), anyString(), any(BigDecimal.class));
	}
	
	public void should_GetBadRequest_When_CustomerIdDoesNotExist() {
		final BigDecimal initialCredit = BigDecimal.TEN;
		final String description = "Some Account";
		final String customerId = "someId";
								
		webClient.post().uri("/accounts")
					.syncBody(new AccountDTO(customerId, description, initialCredit))
					.exchange()
					.expectStatus().isBadRequest()
					.expectBody()
					.isEmpty();
	}
}

