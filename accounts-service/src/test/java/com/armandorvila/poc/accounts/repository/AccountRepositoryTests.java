package com.armandorvila.poc.accounts.repository;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.junit4.SpringRunner;

import com.armandorvila.poc.accounts.domain.Account;
import com.armandorvila.poc.accounts.domain.Customer;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@RunWith(SpringRunner.class)
@DataMongoTest
public class AccountRepositoryTests {

	@Autowired
	private AccountRepository accountRepository;
	
	@Autowired
	private CustomerRepository customerRepository;
	
	private Flux<Account> accounts;
	
	private Mono<Customer> customer;
	
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
	public void should_GetFirstPage_When_GivenPageOne() {
		StepVerifier.create(accountRepository.findAll(PageRequest.of(0, 1)))
		.expectNext(accounts.blockFirst())
		.expectNextCount(0)
		.verifyComplete();
	}
	
	@Test
	public void should_GetOneAccount_When_GivenPageSizeOne() {
		StepVerifier.create(accountRepository.findAll(PageRequest.of(1, 1)))
		.expectNextCount(1)
		.verifyComplete();
	}
	
	@Test
	public void should_GetCustomer_When_GivenValidCustomer() {
		StepVerifier.create(accountRepository.findByCustomer(customer, PageRequest.of(0, 100)))
		.assertNext(account -> {
			assertThat(account).isNotNull();
			assertThat(account.getCustomer()).isNotNull();
		})
		.expectNextCount(1)
		.verifyComplete();
	}
}
