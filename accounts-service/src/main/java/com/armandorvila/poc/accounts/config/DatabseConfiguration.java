package com.armandorvila.poc.accounts.config;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.EnableMongoAuditing;
import org.springframework.data.mongodb.repository.config.EnableReactiveMongoRepositories;

import com.armandorvila.poc.accounts.domain.Account;
import com.armandorvila.poc.accounts.domain.Customer;
import com.armandorvila.poc.accounts.repository.AccountRepository;
import com.armandorvila.poc.accounts.repository.CustomerRepository;

import reactor.core.publisher.Flux;

@Configuration
@EnableMongoAuditing
@EnableReactiveMongoRepositories("com.armandorvila.poc.accounts.repository")
public class DatabseConfiguration {

	private static final String DEFAULT_CUTOMER_ID = "5ab7b1e41fe5db3ac0945a10";

	@Autowired
	private CustomerRepository customerRepository;

	@Autowired
	private AccountRepository accountRepository;

	/*
	 * Development only.
	 */
	@PostConstruct
	public void initializeDatabase() {
		final Customer customer = new Customer(DEFAULT_CUTOMER_ID, "armando.ramirez.villa@email.com", "Armando", "Ramirez Vila");

		 customerRepository.deleteAll()
			.thenMany(customerRepository.save(customer).flux()
				.flatMap(initializeAccounts())).subscribe();
	}

	private Function<Customer,Flux<Account>> initializeAccounts() {
		return c -> accountRepository.deleteAll().thenMany(accountRepository.saveAll(accounts(c)));
	}

	private List<Account> accounts(Customer customer) {
		return Arrays.asList(
				new Account("5ab8086f1f11cd453ce85c23", "Personal Account", customer, BigDecimal.valueOf(20000), null, null),
				new Account("5ab8086f1f11cd453ce85c24", "Work Account", customer, BigDecimal.valueOf(30000), null, null));
	}
}
