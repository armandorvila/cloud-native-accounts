package com.armandorvila.poc.transactions.repository;

import static java.util.Arrays.asList;

import java.math.BigDecimal;
import java.time.LocalDateTime;

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

	private static final String ACCOUNT_ID = "5ab698238d14c64fc85b3d38";

	@Autowired
	private TransactionRepository transactionRepository;
	
	private Flux<Transaction> transactions;
	
	@Before
	public void setUp() {
		transactions = this.transactionRepository.deleteAll()
				.thenMany(transactionRepository.saveAll(asList(
						new Transaction(ACCOUNT_ID, "Some transaction", new BigDecimal(-10.0), new BigDecimal(8000.00), LocalDateTime.now()),
						new Transaction(ACCOUNT_ID, "Some other transaction", new BigDecimal(-10.0), new BigDecimal(7900.00), LocalDateTime.now().plusMinutes(1)))));
		
		StepVerifier.create(transactions).expectNextCount(2).verifyComplete();
	}
	
	@Test
	public void should_GetLastAccountTransaction_When_GivenAccountId() {
		StepVerifier.create(transactionRepository.findFirstByAccountIdOrderByTimestampDesc(ACCOUNT_ID))
		.expectNext(transactions.blockLast())
		.expectNextCount(0)
		.verifyComplete();
	}
	
	@Test
	public void should_GetFirstPage_When_GivenPageOne() {
		StepVerifier.create(transactionRepository.findByAccountId(ACCOUNT_ID, PageRequest.of(0, 1)))
		.expectNext(transactions.blockFirst())
		.expectNextCount(0)
		.verifyComplete();
	}
	
	@Test
	public void should_GetOneElment_When_GivenPageSizeOne() {
		StepVerifier.create(transactionRepository.findByAccountId(ACCOUNT_ID, PageRequest.of(1, 1)))
		.expectNextCount(1)
		.verifyComplete();
	}
}
