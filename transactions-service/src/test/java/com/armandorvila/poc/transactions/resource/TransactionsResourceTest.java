package com.armandorvila.poc.transactions.resource;

import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8;

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

@RunWith(SpringRunner.class)
@WebFluxTest(TransactionResource.class)
@AutoConfigureWebTestClient
public class TransactionsResourceTest {

	private static final String ACCOUNT_ID = "5ab698238d14c64fc85b3d38";
	
	@MockBean
	private TransactionRepository transactionRepository;
	
	@Autowired
	private WebTestClient webClient;
	
	private Transaction transaction = new Transaction("5ab698238d14c64fc85b3d38", "Some transaction");
	
	@Test  
	public void should_GetPageZero_WhenOffsetIsZero_And_LimitIsOne() throws Exception {
		PageRequest pagination = PageRequest.of(0, 1);
		
		given(transactionRepository.findAll(pagination))
		  .willReturn(Flux.just(transaction));
	    
		 webClient.get().uri("/transactions?accountId={accountId}&limit={limit}&offset={offset}", ACCOUNT_ID, 1, 0)
						.accept(APPLICATION_JSON)
						.exchange()
						.expectStatus().isOk()
						.expectHeader().contentType(APPLICATION_JSON_UTF8)
						.expectBodyList(Transaction.class)
				        .contains(transaction);
		 
		 then(transactionRepository).should(times(1)).findAll(pagination);
	}
	
	@Test  
	public void should_GetPageTwo_WhenOffsetIsTwo_And_LimitIsOne() throws Exception {
		PageRequest pagination = PageRequest.of(2, 1);
		
		given(transactionRepository.findAll(pagination))
		  .willReturn(Flux.just(transaction));
	    
		 webClient.get().uri("/transactions?accountId={accountId}&limit={limit}&offset={offset}", ACCOUNT_ID, 1, 2)
						.accept(APPLICATION_JSON)
						.exchange()
						.expectStatus().isOk()
						.expectHeader().contentType(APPLICATION_JSON_UTF8)
						.expectBodyList(Transaction.class)
				        .contains(transaction);
		 
		 then(transactionRepository).should(times(1)).findAll(pagination);
	}
}
