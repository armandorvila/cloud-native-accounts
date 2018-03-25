package com.armandorvila.poc.transactions;

import static java.util.Arrays.asList;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8;

import java.math.BigDecimal;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.WebTestClient;

import com.armandorvila.poc.transactions.domain.Transaction;
import com.armandorvila.poc.transactions.repository.TransactionRepository;

import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class TransactionsApplicationTests {

	private static final String ACCOUNT_ID = "5ab698238d14c64fc85b3d38";
	
	@Autowired
	private TransactionRepository transactionRepository;
	
	@Autowired
	protected WebTestClient webClient;
	
	private Flux<Transaction> transactions;
	
	@Before
	public void setUp() {
		transactions = this.transactionRepository.deleteAll()
				.thenMany(transactionRepository.saveAll(asList(
						new Transaction(ACCOUNT_ID, "Some transaction", new BigDecimal(-10.0), new BigDecimal(8000.00)),
						new Transaction(ACCOUNT_ID, "Some other transaction", new BigDecimal(-10.0), new BigDecimal(8000.00)))));
		
		StepVerifier.create(transactions).expectNextCount(2).verifyComplete();
	}
	
	@Test  
	public void should_GetTransactions_WhenGivenValidAccountId() {
		 webClient.get().uri("transactions?accountId={accountId}", ACCOUNT_ID)
						.accept(APPLICATION_JSON)
						.exchange()
						.expectStatus().isOk()
						.expectHeader()
						.contentType(APPLICATION_JSON_UTF8)
						.expectBodyList(Transaction.class)
						.hasSize(2);
	}
	
	@Test  
	public void should_GetEmptyTransactions_WhenAccountIdDoesNotExist() {
		 webClient.get().uri("transactions?accountId={accountId}", "someId")
						.accept(APPLICATION_JSON)
						.exchange()
						.expectStatus().isOk()
						.expectHeader()
						.contentType(APPLICATION_JSON_UTF8)
						.expectBodyList(Transaction.class)
						.hasSize(0);
	}
	
	@Test  
	public void should_RegisterNewTransaction_WhenTransactionIsValid() throws Exception {	
		final Transaction transaction = new Transaction(ACCOUNT_ID, "Dinner and drinks", new BigDecimal(-250.90), null);
		final BigDecimal newBalance = new BigDecimal(8000.00).add(transaction.getValue());
		
		webClient.post().uri("/transactions")
						.accept(APPLICATION_JSON)
						.syncBody(transaction)
						.exchange()
						.expectStatus().isCreated()
						.expectBody()
						.jsonPath("$.id").isNotEmpty()
						.jsonPath("$.timestamp").isNotEmpty()
						.jsonPath("$.description").isEqualTo(transaction.getDescription())
						.jsonPath("$.accountId").isEqualTo(transaction.getAccountId())
						.jsonPath("$.value").isEqualTo(transaction.getValue().doubleValue())
						.jsonPath("$.balance").isEqualTo(newBalance.doubleValue());
	}
	
	@Test  
	public void should_SendBadRequest_WhenAccount() throws Exception {	
		final Transaction transaction = new Transaction(ACCOUNT_ID, "Dinner and drinks", new BigDecimal(-250.90), null);
		final BigDecimal newBalance = new BigDecimal(8000.00).add(transaction.getValue());
		
		webClient.post().uri("/transactions")
						.accept(APPLICATION_JSON)
						.syncBody(transaction)
						.exchange()
						.expectStatus().isCreated()
						.expectBody()
						.jsonPath("$.id").isNotEmpty()
						.jsonPath("$.timestamp").isNotEmpty()
						.jsonPath("$.description").isEqualTo(transaction.getDescription())
						.jsonPath("$.accountId").isEqualTo(transaction.getAccountId())
						.jsonPath("$.value").isEqualTo(transaction.getValue().doubleValue())
						.jsonPath("$.balance").isEqualTo(newBalance.doubleValue());
	}
}
