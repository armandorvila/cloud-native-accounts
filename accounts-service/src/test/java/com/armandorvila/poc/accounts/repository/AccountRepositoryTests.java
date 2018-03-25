package com.armandorvila.poc.accounts.repository;

import static java.util.Arrays.asList;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.junit4.SpringRunner;

import com.armandorvila.poc.accounts.domain.Account;

import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

@RunWith(SpringRunner.class)
@DataMongoTest
public class AccountRepositoryTests {

	@Autowired
	private AccountRepository accountRepository;
	
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
	public void should_GetFirstPage_When_GivenPageOne() {
		StepVerifier.create(accountRepository.findAll(PageRequest.of(0, 1)))
		.expectNext(accounts.blockFirst())
		.expectNextCount(0)
		.verifyComplete();
	}
	
	@Test
	public void should_GetOneElment_When_GivenPageSizeOne() {
		StepVerifier.create(accountRepository.findAll(PageRequest.of(1, 1)))
		.expectNextCount(1)
		.verifyComplete();
	}
}
