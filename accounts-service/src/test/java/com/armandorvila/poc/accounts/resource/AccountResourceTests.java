package com.armandorvila.poc.accounts.resource;

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
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.WebTestClient;

import com.armandorvila.poc.accounts.domain.Account;
import com.armandorvila.poc.accounts.repository.AccountRepository;
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
	private TransactionsService transactionsService;
	
	@Autowired
	private WebTestClient webClient;
	
	private Account account = new Account("57f4dadc6d138cf005711f4d", "Some account");
	
	@Test  
	public void should_GetPageZero_WhenOffsetIsZero_And_LimitIsOne() throws Exception {
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
	public void should_GetPageTwo_WhenOffsetIsTwo_And_LimitIsOne() throws Exception {
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
	
	@Test
	public void should_GetAccount_When_GivenExistentId() {
		given(accountRespository.findById(account.getId()))
		  .willReturn(Mono.just(account));
		
		webClient.get().uri("/accounts/{accountId}", account.getId())
					.accept(MediaType.APPLICATION_JSON)
					.exchange()
					.expectStatus().isOk()
					.expectHeader().contentType(MediaType.APPLICATION_JSON_UTF8)
					.expectBody(Account.class)
					.isEqualTo(account);
		
	 then(accountRespository).should(times(1)).findById(account.getId());
	}

	@Test
	public void should_GetNotFound_When_GivenNonExistentId() {
		given(accountRespository.findById("someid"))
		  .willReturn(Mono.empty());
		
		webClient.get().uri("/accounts/{accountId}", "someid")
					.accept(MediaType.APPLICATION_JSON)
					.exchange()
					.expectStatus().isNotFound()
					.expectBody()
					.isEmpty();
		
		then(accountRespository).should(times(1)).findById("someid");
	}
}
