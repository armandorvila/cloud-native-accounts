package com.armandorvila.poc.accounts;

import static java.util.Arrays.asList;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8;

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
import com.armandorvila.poc.accounts.exception.ServiceNotFoundExeption;
import com.armandorvila.poc.accounts.repository.AccountRepository;
import com.armandorvila.poc.accounts.service.TransactionsService;

import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class AccountsApplicationTests {

	@Autowired
	private AccountRepository accountRepository;
	
	@MockBean
	private TransactionsService transactionsService;
	
	@Autowired
	protected WebTestClient webClient;
	
	private Flux<Account> accounts;
	
	@Before
	public void setUp() {
		accounts = this.accountRepository.deleteAll()
				.thenMany(accountRepository.saveAll(asList(
						new Account("Some account"),
						new Account("Some other account"))));
		
		StepVerifier.create(accounts).expectNextCount(2).verifyComplete();
	}
	
	@Test  
	public void should_GetAccountTransactions_WhenGivenValidAccountId() {
		given(transactionsService.getAccountTransactions(anyString(), anyInt(), anyInt()))
		  .willReturn(Flux.just(new AccountTransaction("Some transaction")));
	    
		final String accountId = accounts.blockFirst().getId();
		
		 webClient.get().uri("/accounts/{acountId}/transactions", accountId)
						.accept(APPLICATION_JSON)
						.exchange()
						.expectStatus().isOk()
						.expectHeader()
						.contentType(APPLICATION_JSON_UTF8)
						.expectBodyList(AccountTransaction.class)
						.hasSize(1);
		 
		 then(transactionsService)
		 .should(times(1)).getAccountTransactions(anyString(), anyInt(), anyInt());
	}
	
	@Test  
	public void should_GetBadGateway_WhenTransactionServiceNotFound() throws Exception {		
		given(transactionsService.getAccountTransactions("some", 100, 0))
		  .willThrow(ServiceNotFoundExeption.class);
	    
		 webClient.get().uri("/accounts/some/transactions")
						.accept(APPLICATION_JSON)
						.exchange()
						.expectStatus().isEqualTo(HttpStatus.BAD_GATEWAY);
		 
		 then(transactionsService).should(times(1)).getAccountTransactions("some", 100, 0);
	}
	
	@Test  
	public void should_GetInternalServerError_WhenIllegalArgumentException() throws Exception {		
		given(transactionsService.getAccountTransactions("some", 100, 0))
		  .willThrow(IllegalArgumentException.class);
	    
		 webClient.get().uri("/accounts/some/transactions")
						.accept(APPLICATION_JSON)
						.exchange()
						.expectStatus().isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
		 
		 then(transactionsService).should(times(1)).getAccountTransactions("some", 100, 0);
	}
}
