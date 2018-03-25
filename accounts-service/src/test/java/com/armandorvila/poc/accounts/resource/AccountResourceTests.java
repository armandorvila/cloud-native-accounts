package com.armandorvila.poc.accounts.resource;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8;

import java.math.BigDecimal;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.WebTestClient;

import com.armandorvila.poc.accounts.domain.Account;
import com.armandorvila.poc.accounts.domain.Customer;
import com.armandorvila.poc.accounts.exception.CustomerNotFoundExeption;
import com.armandorvila.poc.accounts.repository.AccountRepository;
import com.armandorvila.poc.accounts.resource.dto.AccountDTO;
import com.armandorvila.poc.accounts.service.AccountService;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RunWith(SpringRunner.class)
@WebFluxTest(AccountResource.class)
@AutoConfigureWebTestClient
public class AccountResourceTests {
	
	private Customer customer = new Customer("57f4dadc6d138cf005711f4d", "some@mail.com", "some", "customer");
	private Account account = new Account("57f4dadc6d138cf005711f4d", "Some account");
	
	@MockBean
	private AccountService accountService;
	
	@MockBean
	private AccountRepository accountRespository;
	
	@Autowired
	private WebTestClient webClient;
	
	@Test
	public void should_CreateNewAccount_When_ValidCustomer_Description_And_Credit() {
		final BigDecimal initialCredit = BigDecimal.valueOf(2000);
		
		final String description = "Personal bank account";
		final String customerId = customer.getId();

		final Account account = new Account(description, customer);
		
		given(accountService.openCustomerAccount(customerId, description, initialCredit))
		.willReturn(Mono.just(account));
						
		webClient.post().uri("/accounts")
					.accept(APPLICATION_JSON)
					.syncBody(new AccountDTO(customerId, description, initialCredit))
					.exchange()
					.expectStatus().isCreated()
					.expectBody(Account.class)
					.isEqualTo(account);
		
		then(accountService).should(times(1)).openCustomerAccount(customerId, description, initialCredit);
	}
	
	@Test
	public void should_CreateNewAccount_When_InitialCreditIsZero() {
		final BigDecimal initialCredit = BigDecimal.ZERO;
		final String description = "Some Account";
		
		final String customerId = customer.getId();
		
		final Account account = new Account(description, customer);
		
		given(accountService.openCustomerAccount(customerId, description, initialCredit))
		.willReturn(Mono.just(account));
				
		webClient.post().uri("/accounts")
					.accept(APPLICATION_JSON)
					.syncBody(new AccountDTO(customerId, description, initialCredit))
					.exchange()
					.expectStatus().isCreated()
					.expectBody(Account.class)
					.isEqualTo(account);
		
		then(accountService).should(times(1)).openCustomerAccount(customerId, description, initialCredit);
	}
	
	@Test
	public void should_GetBadRequest_When_EmptyDescription() {
		webClient.post().uri("/accounts")
					.accept(APPLICATION_JSON)
					.syncBody(new AccountDTO(customer.getId(), "", BigDecimal.TEN))
					.exchange()
					.expectStatus().isBadRequest()
					.expectBody()
					.isEmpty();
		
		then(accountService).should(never()).openCustomerAccount(anyString(), anyString(), any(BigDecimal.class));
	}
	
	@Test
	public void should_GetBadRequest_When_EmptyInitialCredit() {				
		webClient.post().uri("/accounts")
					.syncBody(new AccountDTO(customer.getId(), "", null))
					.exchange()
					.expectStatus().isBadRequest()
					.expectBody()
					.isEmpty();
		
		then(accountService).should(never()).openCustomerAccount(anyString(), anyString(), any(BigDecimal.class));
	}
	
	@Test
	public void should_GetBadRequest_When_NegativeInitialCredit() {
		webClient.post().uri("/accounts")
					.syncBody(new AccountDTO(customer.getId(), "some desc", new BigDecimal(-20.0)))
					.exchange()
					.expectStatus().isBadRequest()
					.expectBody()
					.isEmpty();
		
		then(accountService).should(never()).openCustomerAccount(anyString(), anyString(), any(BigDecimal.class));
	}
	
	@Test
	public void should_GetBadRequest_When_EmptyCustomerId() {
		webClient.post().uri("/accounts")
					.syncBody(new AccountDTO("", "some desc", new BigDecimal(2000.0)))
					.exchange()
					.expectStatus().isBadRequest()
					.expectBody()
					.isEmpty();
		
		then(accountService).should(never()).openCustomerAccount(anyString(), anyString(), any(BigDecimal.class));
	}
	
	@Test
	public void should_GetBadRequest_When_CustomerIdDoesNotExist() {
		final BigDecimal initialCredit = BigDecimal.TEN;
		final String description = "Some Account";
		final String customerId = "someId";
								
		given(accountService.openCustomerAccount(customerId, description, initialCredit))
		.willReturn(Mono.error(new CustomerNotFoundExeption("Customer not found")));
		
		webClient.post().uri("/accounts")
					.syncBody(new AccountDTO(customerId, description, initialCredit))
					.exchange()
					.expectStatus().isBadRequest()
					.expectBody()
					.isEmpty();
		
		then(accountService).should(times(1)).openCustomerAccount(customerId, description, initialCredit);
	}
	
	@Test  
	public void should_GetCustomerAccounts_When_GivenValidCustomerId() throws Exception {	
		given(accountService.getCustomerAccounts(customer.getId(), 100, 0))
		.willReturn(Flux.just(account));
	    
		 webClient.get().uri("/accounts?customerId={customerId}", customer.getId())
						.accept(APPLICATION_JSON)
						.exchange()
						.expectStatus().isOk()
						.expectHeader().contentType(APPLICATION_JSON_UTF8)
						.expectBodyList(Account.class)
				        .contains(account);
		 
		 then(accountService).should(times(1)).getCustomerAccounts(customer.getId(), 100, 0);
	}
	
	@Test
	public void should_GetAccount_When_GivenExistentAccountId() {
		given(accountRespository.findById(account.getId()))
		  .willReturn(Mono.just(account));
		
		webClient.get().uri("/accounts/{accountId}", account.getId())
					.accept(APPLICATION_JSON)
					.exchange()
					.expectStatus().isOk()
					.expectHeader().contentType(APPLICATION_JSON_UTF8)
					.expectBody(Account.class)
					.isEqualTo(account);
		
	 then(accountRespository).should(times(1)).findById(account.getId());
	}

	@Test
	public void should_GetNotFound_When_GivenNonExistentAccountId() {
		given(accountRespository.findById("someid"))
		  .willReturn(Mono.empty());
		
		webClient.get().uri("/accounts/{accountId}", "someid")
					.accept(APPLICATION_JSON)
					.exchange()
					.expectStatus().isNotFound()
					.expectBody()
					.isEmpty();
		
		then(accountRespository).should(times(1)).findById("someid");
	}
}