package com.armandorvila.poc.transactions.resource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
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
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.WebTestClient;

import com.armandorvila.poc.transactions.domain.Transaction;
import com.armandorvila.poc.transactions.repository.TransactionRepository;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RunWith(SpringRunner.class)
@WebFluxTest(TransactionResource.class)
@AutoConfigureWebTestClient
public class TransactionsResourceTest {

	private static final String ACCOUNT_ID = "5ab698238d14c64fc85b3d38";
	
	@MockBean
	private TransactionRepository transactionRepository;
	
	@Autowired
	private WebTestClient webClient;
	
	private Transaction transaction;
	
	@Before
	public void setUp() {
		this.transaction = new Transaction(ACCOUNT_ID, "Some transaction", 
				new BigDecimal(-10.0), new BigDecimal(8000.00));
	}
	
	@Test  
	public void should_RegisterNewTransaction_WhenTransactionIsValid() throws Exception {
		given(transactionRepository.findFirstByAccountIdOrderByTimestampDesc(transaction.getAccountId()))
		  .willReturn(Flux.just(transaction));
		
		given(transactionRepository.save(any(Transaction.class)))
		  .willAnswer(invocation -> Mono.just(invocation.getArguments()[0]));
		
		 Transaction result = webClient.post().uri("/transactions")
						.accept(APPLICATION_JSON)
						.syncBody(transaction)
						.exchange()
						.expectStatus().isCreated()
						.expectBody(Transaction.class)
						.returnResult().getResponseBody();
		 
		 assertThat(result.getBalance()).isEqualTo(transaction.getBalance().subtract(new BigDecimal(10)));
	}
	
	@Test  
	public void should_GetBadRequestn_WhenTransactionValueIsEmpty() throws Exception {	
		transaction.setValue(null);
		 webClient.post().uri("/transactions")
						.accept(APPLICATION_JSON)
						.syncBody(transaction)
						.exchange()
						.expectStatus().isBadRequest()
						.expectBody()
						.isEmpty();
	}
	
	@Test  
	public void should_GetBadRequestn_WhenTransactionDescriptionIsEmpty() throws Exception {	
		 transaction.setDescription("");
		 webClient.post().uri("/transactions")
						.accept(APPLICATION_JSON)
						.syncBody(transaction)
						.exchange()
						.expectStatus().isBadRequest()
						.expectBody()
						.isEmpty();
	}
	
	@Test  
	public void should_GetBadRequestn_WhenTransactionAccountIdIsEmpty() throws Exception {	
		 transaction.setAccountId("");
		 webClient.post().uri("/transactions")
						.accept(APPLICATION_JSON)
						.syncBody(transaction)
						.exchange()
						.expectStatus().isBadRequest()
						.expectBody()
						.isEmpty();
	}
	
	@Test  
	public void should_GetBadRequest_WhenAccountId_NotInformed() throws Exception {	    
		 webClient.get().uri("/transactions")
						.accept(APPLICATION_JSON)
						.exchange()
						.expectStatus().isBadRequest()
						.expectBody()
						.isEmpty();
	}
	
	@Test  
	public void should_GetPageZero_WhenOffsetIsZero_And_LimitIsOne() throws Exception {
		PageRequest pagination = PageRequest.of(0, 1);
		
		given(transactionRepository.findByAccountId(ACCOUNT_ID, pagination))
		  .willReturn(Flux.just(transaction));
	    
		 webClient.get().uri("/transactions?accountId={accountId}&limit={limit}&offset={offset}", ACCOUNT_ID, 1, 0)
						.accept(APPLICATION_JSON)
						.exchange()
						.expectStatus().isOk()
						.expectHeader().contentType(APPLICATION_JSON_UTF8)
						.expectBodyList(Transaction.class)
				        .contains(transaction);
		 
		 then(transactionRepository).should(times(1)).findByAccountId(ACCOUNT_ID, pagination);
	}
	
	@Test  
	public void should_GetPageTwo_WhenOffsetIsTwo_And_LimitIsOne() throws Exception {
		PageRequest pagination = PageRequest.of(2, 1);
		
		given(transactionRepository.findByAccountId(ACCOUNT_ID, pagination))
		  .willReturn(Flux.just(transaction));
	    
		 webClient.get().uri("/transactions?accountId={accountId}&limit={limit}&offset={offset}", ACCOUNT_ID, 1, 2)
						.accept(APPLICATION_JSON)
						.exchange()
						.expectStatus().isOk()
						.expectHeader().contentType(APPLICATION_JSON_UTF8)
						.expectBodyList(Transaction.class)
				        .contains(transaction);
		 
		 then(transactionRepository).should(times(1)).findByAccountId(ACCOUNT_ID, pagination);
	}
}
