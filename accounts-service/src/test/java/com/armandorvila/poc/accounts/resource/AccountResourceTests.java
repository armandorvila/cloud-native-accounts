package com.armandorvila.poc.accounts.resource;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.any;
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
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.WebTestClient;

import com.armandorvila.poc.accounts.domain.Account;
import com.armandorvila.poc.accounts.domain.Customer;
import com.armandorvila.poc.accounts.repository.AccountRepository;
import com.armandorvila.poc.accounts.repository.CustomerRepository;
import com.armandorvila.poc.accounts.resource.dto.AccountDTO;
import com.armandorvila.poc.accounts.service.TransactionsService;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RunWith(SpringRunner.class)
@WebFluxTest(AccountResource.class)
@AutoConfigureWebTestClient
public class AccountResourceTests {
	
	@MockBean
	private AccountRepository accountRespository;
	
	@MockBean
	private CustomerRepository customerRepository;
	
	@MockBean
	private TransactionsService transactionsService;
	
	@Autowired
	private WebTestClient webClient;
	
	private Account account = new Account("57f4dadc6d138cf005711f4d", "Some account");
	
	private Customer customer = new Customer("57f4dadc6d138cf005711f4d", "some@mail.com", "some", "customer");
	
	@Test
	public void should_CreateNewAccount_When_ValidCustomer_Description_And_Credit() {
		final AccountDTO accountDTO = new AccountDTO(customer.getId(), "Some Account", new BigDecimal(2000.0));
		final Account account = new Account(accountDTO.getDescription(), customer);
		
		given(customerRepository.findById(customer.getId())).willReturn(Mono.just(customer));
		given(accountRespository.save(account)).willReturn(Mono.just(account));
				
		webClient.post().uri("/accounts")
					.accept(APPLICATION_JSON)
					.syncBody(accountDTO)
					.exchange()
					.expectStatus().isCreated()
					.expectBody(Account.class)
					.isEqualTo(account);
		
		then(customerRepository).should(times(1)).findById(customer.getId());
		then(accountRespository).should(times(1)).save(account);
	}
	
	@Test
	public void should_GetBadRequest_When_EmptyDescription() {
		final AccountDTO accountDTO = new AccountDTO(customer.getId(), "", new BigDecimal(2000.0));
				
		webClient.post().uri("/accounts")
					.accept(APPLICATION_JSON)
					.syncBody(accountDTO)
					.exchange()
					.expectStatus().isBadRequest()
					.expectBody()
					.isEmpty();
		
		then(customerRepository).should(never()).findById(anyString());
		then(accountRespository).should(never()).save(any(Account.class));
	}
	
	@Test
	public void should_GetBadRequest_When_EmptyInitialCredit() {
		final AccountDTO accountDTO = new AccountDTO(customer.getId(), "", null);
				
		webClient.post().uri("/accounts")
					.syncBody(accountDTO)
					.exchange()
					.expectStatus().isBadRequest()
					.expectBody()
					.isEmpty();
		
		then(customerRepository).should(never()).findById(anyString());
		then(accountRespository).should(never()).save(any(Account.class));
	}
	
	@Test
	public void should_GetBadRequest_When_NegativeInitialCredit() {
		final AccountDTO accountDTO = new AccountDTO(customer.getId(), "some desc", new BigDecimal(-2000.0));
				
		webClient.post().uri("/accounts")
					.syncBody(accountDTO)
					.exchange()
					.expectStatus().isBadRequest()
					.expectBody()
					.isEmpty();
		
		then(customerRepository).should(never()).findById(anyString());
		then(accountRespository).should(never()).save(any(Account.class));
	}
	
	@Test
	public void should_GetBadRequest_When_EmptyCustomerId() {
		final AccountDTO accountDTO = new AccountDTO("", "some desc", new BigDecimal(2000.0));
				
		webClient.post().uri("/accounts")
					.syncBody(accountDTO)
					.exchange()
					.expectStatus().isBadRequest()
					.expectBody()
					.isEmpty();
		
		then(customerRepository).should(never()).findById(anyString());
		then(accountRespository).should(never()).save(any(Account.class));
	}
	
	@Test
	public void should_GetBadRequest_When_CustomerIdDoesNotExist() {
		final AccountDTO accountDTO = new AccountDTO("0000", "some desc", new BigDecimal(2000.0));
				
		given(customerRepository.findById(accountDTO.getCustomerId())).willReturn(Mono.empty());
		given(accountRespository.save(account)).willReturn(Mono.just(account));
		
		webClient.post().uri("/accounts")
					.syncBody(accountDTO)
					.exchange()
					.expectStatus().isBadRequest()
					.expectBody()
					.isEmpty();
		
		then(customerRepository).should(times(1)).findById(accountDTO.getCustomerId());
		then(accountRespository).should(never()).save(any(Account.class));
	}
	
	@Test  
	public void should_GetCustomerAccounts_When_GivenValidCustomerId() throws Exception {
		final Mono<Customer> customerMono = Mono.just(customer);
		final PageRequest pagination = PageRequest.of(0, 100);
		
		given(customerRepository.findById(customer.getId())).willReturn(customerMono);
		given(accountRespository.findByCustomer(customerMono, pagination)).willReturn(Flux.just(account));
	    
		 webClient.get().uri("/accounts?customerId={customerId}", customer.getId())
						.accept(APPLICATION_JSON)
						.exchange()
						.expectStatus().isOk()
						.expectHeader().contentType(APPLICATION_JSON_UTF8)
						.expectBodyList(Account.class)
				        .contains(account);
		 
		 then(customerRepository).should(times(1)).findById(customer.getId());
		 then(accountRespository).should(times(1)).findByCustomer(customerMono, pagination);
	}
	
	@Test  
	public void should_GetEmptyAccountsList_When_CustomerIdDoesNotExist() throws Exception {
		final PageRequest pagination = PageRequest.of(0, 100);
		
		given(customerRepository.findById(customer.getId())).willReturn(Mono.empty());
		given(accountRespository.findByCustomer(Mono.empty(), pagination)).willReturn(Flux.empty());
	    
		 webClient.get().uri("/accounts?customerId={customerId}", customer.getId())
						.accept(APPLICATION_JSON)
						.exchange()
						.expectStatus().isOk()
						.expectHeader().contentType(APPLICATION_JSON_UTF8)
						.expectBodyList(Account.class)
				        .hasSize(0);
		 
		 then(customerRepository).should(times(1)).findById(customer.getId());
		 then(accountRespository).should(times(1)).findByCustomer(Mono.empty(), pagination);
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
	
	@Test  
	public void should_GetPageZero_When_OffsetIsZero_And_LimitIsOne() throws Exception {
		PageRequest pagination = PageRequest.of(0, 1);
		
		given(accountRespository.findAll(pagination))
		  .willReturn(Flux.just(account));
	    
		 webClient.get().uri("/accounts?limit={limit}&offset={offset}", 1, 0)
						.accept(APPLICATION_JSON)
						.exchange()
						.expectStatus().isOk()
						.expectHeader().contentType(APPLICATION_JSON_UTF8)
						.expectBodyList(Account.class)
				        .contains(account);
		 
		 then(accountRespository).should(times(1)).findAll(pagination);
	}
	
	@Test  
	public void should_GetPageTwo_When_OffsetIsTwo_And_LimitIsOne() throws Exception {
		PageRequest pagination = PageRequest.of(2, 1);
		
		given(accountRespository.findAll(pagination))
		  .willReturn(Flux.just(account));
	    
		 webClient.get().uri("/accounts?limit={limit}&offset={offset}", 1, 2)
						.accept(APPLICATION_JSON)
						.exchange()
						.expectStatus().isOk()
						.expectHeader().contentType(APPLICATION_JSON_UTF8)
						.expectBodyList(Account.class)
				        .contains(account);
		 
		 then(accountRespository).should(times(1)).findAll(pagination);
	}
}
