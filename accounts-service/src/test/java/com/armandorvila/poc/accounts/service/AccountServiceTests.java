package com.armandorvila.poc.accounts.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;

import java.math.BigDecimal;

import org.junit.Before;
import org.junit.Test;
import org.springframework.data.domain.PageRequest;

import com.armandorvila.poc.accounts.domain.Account;
import com.armandorvila.poc.accounts.domain.AccountTransaction;
import com.armandorvila.poc.accounts.domain.Customer;
import com.armandorvila.poc.accounts.exception.CustomerNotFoundExeption;
import com.armandorvila.poc.accounts.repository.AccountRepository;
import com.armandorvila.poc.accounts.repository.CustomerRepository;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

public class AccountServiceTests {
	
	private static final BigDecimal POSITIVE_CREDIT = BigDecimal.valueOf(2000);
	private static final BigDecimal ZERO_CREDIT = BigDecimal.ZERO;
	
	private Customer customer = new Customer("57f4dadc6d138cf005711f4d", "some@mail.com", "some", "customer");
	private Account account = new Account("57f4dadc6d138cf005711f4d", "Some account");

	private AccountService accountService;
	
	private TransactionsService transactionsService;

	private CustomerRepository customerRepository;
	private AccountRepository accountRepository;
	
	@Before
	public void setUp() {
		transactionsService = mock(TransactionsService.class);
		customerRepository = mock(CustomerRepository.class);
		accountRepository = mock(AccountRepository.class);
		
		accountService = new AccountService(transactionsService, accountRepository, customerRepository);
	}
	
	@Test
	public void should_OpenAccount_When_ValidCustomer_Description_And_Credit() {
		final String description = "Some Account";
		
		final String customerId = customer.getId();
		final String accountId = account.getId();
		
		final AccountTransaction transaction = new AccountTransaction(accountId, POSITIVE_CREDIT, description);
		final Account account = new Account(accountId, description, customer, POSITIVE_CREDIT, null, null);
		
		given(customerRepository.findById(customerId)).willReturn(Mono.just(customer));
		given(accountRepository.save(any())).willReturn(Mono.just(account));
		
		given(transactionsService.registerTransaction(accountId, description, POSITIVE_CREDIT))
		.willReturn(Mono.just(transaction));
		
		StepVerifier.create(accountService.openCustomerAccount(customerId, description, POSITIVE_CREDIT))
		.expectNext(account)
		.verifyComplete();
		
		then(customerRepository).should(times(1)).findById(customerId);
		then(transactionsService).should(times(1)).registerTransaction(account.getId(), description, POSITIVE_CREDIT);
		then(accountRepository).should(times(2)).save(any());
	}
	
	@Test
	public void should_OpenAccountWithoutTransaction_When_InitialCreditZero() {
		final String description = "Some Account";
		
		final String customerId = customer.getId();
		final String accountId = account.getId();
		
		final Account account = new Account(accountId, description, customer, ZERO_CREDIT, null, null);
		
		given(customerRepository.findById(customerId)).willReturn(Mono.just(customer));
		given(accountRepository.save(any())).willReturn(Mono.just(account));
				
		StepVerifier.create(accountService.openCustomerAccount(customerId, description, ZERO_CREDIT))
		.expectNext(account)
		.verifyComplete();
		
		then(customerRepository).should(times(1)).findById(customer.getId());
		then(transactionsService).should(never()).registerTransaction(account.getId(), description, ZERO_CREDIT);
		then(accountRepository).should(times(1)).save(any());
	}
	
	@Test
	public void should_GetBadRequest_When_CustomerIdDoesNotExist() {
		final String description = "Some Account";
		
		final String customerId = "someId";
				
		given(customerRepository.findById(customerId)).willReturn(Mono.empty());
		
		StepVerifier.create(accountService.openCustomerAccount(customerId, description, ZERO_CREDIT))
		.expectError(CustomerNotFoundExeption.class);
		
		then(customerRepository).should(times(1)).findById(customerId);
		
		then(accountRepository).should(never()).save(any());
		then(transactionsService).should(never()).registerTransaction(anyString(), anyString(), any(BigDecimal.class));
	}
	
	@Test  
	public void should_GetCustomerAccounts_When_GivenValidCustomerId() throws Exception {
		final Mono<Customer> customerMono = Mono.just(customer);
		final PageRequest pagination = PageRequest.of(0, 100);
		
		given(customerRepository.findById(customer.getId())).willReturn(customerMono);
		given(accountRepository.findByCustomer(customerMono, pagination)).willReturn(Flux.just(account));
	    
		StepVerifier.create(accountService.getCustomerAccounts(customer.getId(), 100, 0))
		.expectNext(account)
		.verifyComplete();
		 
		 then(customerRepository).should(times(1)).findById(customer.getId());
		 then(accountRepository).should(times(1)).findByCustomer(customerMono, pagination);
	}
	
	@Test  
	public void should_GetEmptyAccountsList_When_CustomerIdDoesNotExist() throws Exception {
		final PageRequest pagination = PageRequest.of(0, 100);
		
		given(customerRepository.findById(customer.getId())).willReturn(Mono.empty());
		given(accountRepository.findByCustomer(Mono.empty(), pagination)).willReturn(Flux.empty());
	    
		StepVerifier.create(accountService.getCustomerAccounts(customer.getId(), 100, 0))
		.expectNextCount(0)
		.verifyComplete();
		 
		 then(customerRepository).should(times(1)).findById(customer.getId());
		 then(accountRepository).should(times(1)).findByCustomer(Mono.empty(), pagination);
	}
	
	@Test  
	public void should_GetPageZero_When_OffsetIsZero_And_LimitIsOne() throws Exception {
		PageRequest pagination = PageRequest.of(0, 1);
		
		given(accountRepository.findAll(pagination))
		  .willReturn(Flux.just(account));
	    
		StepVerifier.create(accountService.getCustomerAccounts(null, 1, 0))
		.expectNext(account)
		.verifyComplete();
		 
		 then(accountRepository).should(times(1)).findAll(pagination);
	}
	
	@Test  
	public void should_GetPageTwo_When_OffsetIsTwo_And_LimitIsOne() throws Exception {
		PageRequest pagination = PageRequest.of(2, 1);
		
		given(accountRepository.findAll(pagination))
		  .willReturn(Flux.just(account));
	    
		StepVerifier.create(accountService.getCustomerAccounts(null, 1, 2))
		.expectNext(account)
		.verifyComplete();
		 
		 then(accountRepository).should(times(1)).findAll(pagination);
	}
}
