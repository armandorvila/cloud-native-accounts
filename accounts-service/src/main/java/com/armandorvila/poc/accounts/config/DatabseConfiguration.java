package com.armandorvila.poc.accounts.config;

import java.util.Arrays;
import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.EnableMongoAuditing;
import org.springframework.data.mongodb.repository.config.EnableReactiveMongoRepositories;

import com.armandorvila.poc.accounts.domain.Account;
import com.armandorvila.poc.accounts.domain.Customer;
import com.armandorvila.poc.accounts.repository.AccountRepository;
import com.armandorvila.poc.accounts.repository.CustomerRepository;

@Configuration
@EnableMongoAuditing
@EnableReactiveMongoRepositories("com.armandorvila.poc.accounts.repository")
public class DatabseConfiguration {

	@Autowired
	private CustomerRepository customerRepository;

	@Autowired
	private AccountRepository accountRepository;

	/* This is going to get duplicated when running more than one instance.*/
	@PostConstruct
	public void initializeDatabase() {
		final Customer customer = new Customer("some@email.com", "some", "customer");

		customerRepository.save(customer)
				.doOnSuccess(c -> accountRepository.saveAll(accounts(customer)))
				.subscribe();
	}

	private List<Account> accounts(Customer customer) {
		return Arrays.asList(
				new Account("Some account", customer), 
				new Account("Some other account", customer));
	}
}
