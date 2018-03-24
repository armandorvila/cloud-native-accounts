package com.armandorvila.poc.transactions.repository;

import static java.util.Arrays.asList;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.junit4.SpringRunner;

import com.armandorvila.poc.transactions.domain.Transaction;

import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

@RunWith(SpringRunner.class)
@DataMongoTest
public class TransactionRepositoryTests {

	@Autowired
	private TransactionRepository transactionRepository;
	
	private Flux<Transaction> transactions;
	
	@Before
	public void setUp() {
		transactions = this.transactionRepository.deleteAll()
				.thenMany(transactionRepository.saveAll(asList(
						new Transaction("Some transaction"),
						new Transaction("Some other transaction"))));
		
		StepVerifier.create(transactions).expectNextCount(2).verifyComplete();
	}
	
	@Test
	public void should_GetFirstPage_When_GivenPageOne() {
		StepVerifier.create(transactionRepository.findAll(PageRequest.of(0, 1)))
		.expectNext(transactions.blockFirst())
		.expectNextCount(0)
		.verifyComplete();
	}
	
	@Test
	public void should_GetOneElment_When_GivenPageSizeOne() {
		StepVerifier.create(transactionRepository.findAll(PageRequest.of(1, 1)))
		.expectNextCount(1)
		.verifyComplete();
	}
}
